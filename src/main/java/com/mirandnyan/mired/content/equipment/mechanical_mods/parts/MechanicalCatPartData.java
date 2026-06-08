package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.mirandnyan.mired.CMEDataComponents;
import com.mirandnyan.mired.CMETags;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
import com.mirandnyan.mired.content.equipment.mechanical_mods.parts.mechanical_cat.MechanicalCatBonusType;
import com.mirandnyan.mired.util.AttributeHelpers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MechanicalCatPartData extends MechanicalPartData {
    private static int i = 0;

    private static final int CAT = i++;
    private static final int COG = i++;

    public MechanicalCatPartData() {
        super(0.3f);
    }


    public boolean tryHandlingStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                          @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (other.is(CMETags.Items.LOW_LIKED_MECHANICAL_CAT_FOOD)) {
            return tryEat(stack, other, player, 0);
        }

        if (other.is(CMETags.Items.MID_LIKED_MECHANICAL_CAT_FOOD)) {
            return tryEat(stack, other, player, 1);
        }

        if (other.is(CMETags.Items.HIGH_LIKED_MECHANICAL_CAT_FOOD)) {
            return tryEat(stack, other, player, 2);
        }

        return false;
    }

    @Override
    public void onInserted(ItemStack tool) {
        MechanicalPartUtil.addEnchantment(tool, Enchantments.FORTUNE, 2);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        MechanicalPartUtil.removeEnchantment(tool, Enchantments.FORTUNE);
        removeBonus(tool, null);
    }

    protected boolean tryEat(ItemStack stack, ItemStack food, Player player, int valueSkew) {
        var gameTime = player.level().getGameTime();
        if (hasBonus(stack, gameTime)) {
            //player.level().playLocalSound(player, SoundEvent.);
            // TODO play sound
            return true;
        }
        if (!player.isCreative())
            stack.shrink(1);
        var bonus = getRandomBonus(player.level().getRandom(), valueSkew);
        grantBonus(stack, player, bonus);

        // TODO: play sound
        return true;
    }

    protected void grantBonus(ItemStack tool, Player player, MechanicalCatBonusType bonus) {
        var newTime = player.level().getGameTime() + bonus.duration;
        switch (bonus) {
            case FORTUNE -> {
                //MechanicalPartUtil.removeEnchantment(tool, Enchantments.FORTUNE); // TODO: test
                MechanicalPartUtil.addEnchantment(tool, Enchantments.FORTUNE, bonus.amplitude);
            }
            case GLOWING, BLOCK_INTERACTION_RANGE, HUNGER_REGEN, HASTE -> {
                @SuppressWarnings("DataFlowIssue") // all these have a mob effect
                MobEffectInstance instance = new MobEffectInstance(
                        bonus.mobEffect, bonus.duration
                );
                player.addEffect(instance);
            }
            case NONE, GIFTS -> {} // handled elsewhere
            default -> throw new RuntimeException("Unknown MechanicalCatBonusType Value");
        }
        tool.set(CMEDataComponents.MECHANICAL_CAT_BONUS, bonus);
        tool.set(CMEDataComponents.MECHANICAL_CAT_BONUS_BLOCKED, newTime);
    }

    protected boolean hasBonus(ItemStack stack, long gameTime) {
        var time = stack.get(CMEDataComponents.MECHANICAL_CAT_BONUS_BLOCKED);
        return time != null && time <= gameTime;
    }

    protected void removeBonus(ItemStack tool, @Nullable LivingEntity entity) {
        var bonus = tool.get(CMEDataComponents.MECHANICAL_CAT_BONUS);
        if (bonus == null)
            return;
        switch (bonus) {
            case FORTUNE -> {
                MechanicalPartUtil.removeEnchantment(tool, Enchantments.FORTUNE);
                MechanicalPartUtil.addEnchantment(tool, Enchantments.FORTUNE, 2);
            }
            case GLOWING, BLOCK_INTERACTION_RANGE, HUNGER_REGEN, HASTE -> {
                if (entity == null)
                    break;
                //noinspection DataFlowIssue // all these have a mob effect
                entity.removeEffect(bonus.mobEffect);
                // if removed in creative, let the effect just run out
            }
            case NONE, GIFTS -> {} // handled elsewhere
            default -> throw new RuntimeException("Unknown MechanicalCatBonusType Value");
        }
        tool.remove(CMEDataComponents.MECHANICAL_CAT_BONUS);
        tool.remove(CMEDataComponents.MECHANICAL_CAT_BONUS_BLOCKED);
    }

    protected MechanicalCatBonusType getRandomBonus(RandomSource random, int valueSkew) {
        ArrayList<MechanicalCatBonusType> selection = new ArrayList<>(List.of(MechanicalCatBonusType.values()));
        if (valueSkew == 0 || valueSkew == 2) {
            var length = selection.size();
            for (int i = 0; i < length; i++) {
                if (selection.get(i).value != valueSkew)
                    continue;
                selection.add(selection.get(i));
                selection.add(selection.get(i));
                selection.add(selection.get(i));
            }
        }
        return selection.get(Mth.randomBetweenInclusive(random, 0, selection.size() - 1));
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        var gameTime = level.getGameTime();
        var bonusTime = stack.get(CMEDataComponents.MECHANICAL_CAT_BONUS_BLOCKED);
        if (bonusTime == null || bonusTime <= gameTime)
            return;
        removeBonus(stack, entity instanceof LivingEntity living ? living : null);
    }

    @Override
    public void brokeBlock(Player player, ItemStack item, BlockEvent.BreakEvent event) {
        var bonus = item.get(CMEDataComponents.MECHANICAL_CAT_BONUS);
        if (bonus == MechanicalCatBonusType.GIFTS) {
            if (Mth.randomBetweenInclusive(event.getLevel().getRandom(), 0, 100) < bonus.amplitude) {
                player.giveExperienceLevels(1); // TODO
            }
        }
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        ms.translate(0, 13 / 16f, -10 / 16f);

        // Cog
        ms.pushPose();
        float speedModifier = (float) (
                AttributeHelpers.calculateAttributeValue(stack, Attributes.MINING_EFFICIENCY, EquipmentSlot.MAINHAND)
                        * MechanicalPartUtil.MINING_EFFICIENCY_TO_COG_SPEED
        );

        float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f * speedModifier;
        angle %= 360;
        ms.mulPose(Axis.YP.rotationDegrees(angle));
        renderer.renderSolid(part.models[COG].get(), light);
        ms.popPose();

        // Head
        renderer.renderSolid(part.models[CAT].get(), light);

        ms.popPose();
    }
}
