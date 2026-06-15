package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMEItems;
import com.mirandnyan.cme.CMETags;
import com.mirandnyan.cme.CMETranslations;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.mechanical_cat.MechanicalCatBonusType;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.mechanical_cat.MechanicalCatGiftType;
import com.mirandnyan.cme.util.AttributeHelpers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class MechanicalCatPartData extends MechanicalPartData {
    private static int i = 0;

    /*
    TODO:
    - BUG: effects overwrite other effects of same type
    - Model fix-up
    - Maybe make gifts effect probability depend on block strength of block
     */

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
    public void onRemoved(ItemStack tool) {
        MechanicalPartUtil.removeEnchantment(tool, MechanicalPartUtil.getLocalHolder(Enchantments.FORTUNE));
        removeBonus(tool, null);
    }

    protected static void playCatSound(Level level, Vec3 position, SoundEvent sound) {
        var newPos = position.add(0.5, 1, 0.5);
        var volume = .65f + level.random.nextFloat() * .125f;
        var pitch = .75f - level.random.nextFloat() * .25f;
        level.playSound(null, newPos.x, newPos.y, newPos.z, sound, SoundSource.PLAYERS, volume, pitch);
    }

    protected boolean tryEat(ItemStack stack, ItemStack food, Player player, int valueSkew) {
        var gameTime = player.level().getGameTime();

        var level = player.level();
        if (hasBonus(stack, gameTime)) {
            playCatSound(level, player.position(), SoundEvents.CAT_HISS);
            return true;
        }
        playCatSound(level, player.position(), SoundEvents.CAT_STRAY_AMBIENT);
        if (!player.isCreative())
            food.shrink(1);
        if (level.isClientSide)
            return true;
        var bonus = getRandomBonus(player.level().getRandom(), valueSkew);
        stack.set(CMEDataComponents.MECHANICAL_CAT_BONUS, bonus);
        stack.set(CMEDataComponents.MECHANICAL_CAT_APPLY_BONUS, Unit.INSTANCE);

        return true;
    }

    protected void grantBonus(Level level, ItemStack tool, LivingEntity entity, MechanicalCatBonusType bonus) {
        var newTime = level.getGameTime() + bonus.duration;
        switch (bonus) {
            case FORTUNE -> {
                if (level.isClientSide)
                    return;
                MechanicalPartUtil.addEnchantment(tool, MechanicalPartUtil.getHolder(Enchantments.FORTUNE, level), bonus.amplitude);
            }
            case GLOWING, BLOCK_INTERACTION_RANGE, HUNGER_REGEN, HASTE -> {
                @SuppressWarnings("DataFlowIssue") // all these have a mob effect
                MobEffectInstance instance = new MobEffectInstance(
                        bonus.mobEffect, bonus.duration
                );
                entity.addEffect(instance);
            }
            case NONE, GIFTS -> {} // handled elsewhere
            default -> throw new RuntimeException("Unknown MechanicalCatBonusType Value");
        }
        tool.set(CMEDataComponents.MECHANICAL_CAT_BONUS, bonus);
        tool.set(CMEDataComponents.MECHANICAL_CAT_BONUS_BLOCKED, newTime);
        if (bonus == MechanicalCatBonusType.FORTUNE || level.isClientSide)
            return;
        MechanicalPartUtil.addEnchantment(tool, MechanicalPartUtil.getHolder(Enchantments.FORTUNE, level), bonus.value + 1);
    }

    protected boolean hasBonus(ItemStack stack, long gameTime) {
        var time = stack.get(CMEDataComponents.MECHANICAL_CAT_BONUS_BLOCKED);
        return time != null && time > gameTime;
    }

    protected void removeBonus(ItemStack tool, @Nullable LivingEntity entity) {
        var bonus = tool.get(CMEDataComponents.MECHANICAL_CAT_BONUS);
        if (bonus == null)
            return;
        switch (bonus) {
            case FORTUNE -> {
                // now handled by general remove
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
        if (entity == null || entity.level().isClientSide)
            return;
        MechanicalPartUtil.removeEnchantment(tool, MechanicalPartUtil.getHolder(Enchantments.FORTUNE, entity.level()));
    }

    protected MechanicalCatBonusType getRandomBonus(RandomSource random, int valueSkew) {
        ArrayList<MechanicalCatBonusType> selection = new ArrayList<>(List.of(MechanicalCatBonusType.values()));
        selection.remove(MechanicalCatBonusType.NONE);
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
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        var gameTime = level.getGameTime();
        var bonusTime = stack.get(CMEDataComponents.MECHANICAL_CAT_BONUS_BLOCKED);

        if (stack.has(CMEDataComponents.MECHANICAL_CAT_APPLY_BONUS)) {
            var bonus = stack.get(CMEDataComponents.MECHANICAL_CAT_BONUS);
            if (bonus != null && entity instanceof LivingEntity living) {
                grantBonus(level, stack, living, bonus);
                stack.remove(CMEDataComponents.MECHANICAL_CAT_APPLY_BONUS);
            }
        }

        if (bonusTime == null || bonusTime > gameTime)
            return;
        playCatSound(level, entity.position(), SoundEvents.CAT_BEG_FOR_FOOD);
        removeBonus(stack, entity instanceof LivingEntity living ? living : null);
    }

    @Override
    public void brokeBlock(ServerPlayer player, ItemStack item, BlockEvent.BreakEvent event) {
        var bonus = item.get(CMEDataComponents.MECHANICAL_CAT_BONUS);
        if (bonus == MechanicalCatBonusType.GIFTS) {
            var speed = Math.clamp(event.getState().getDestroySpeed(event.getLevel(), event.getPos()), 0.5, 5);
            var mul = (11 - speed * 2) / 4f;
            if (Mth.randomBetweenInclusive(event.getLevel().getRandom(), 0, (int) (100 * mul)) < bonus.amplitude) {
                var gift = MechanicalCatGiftType.random(event.getLevel().getRandom());
                item.set(CMEDataComponents.MECHANICAL_CAT_GIVE_GIFT, gift);
            }
        }
    }

    @SubscribeEvent
    public static void modifyBlockDropsAfterBreak(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer player) || !player.getMainHandItem().has(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE))
            return;
        var item = player.getMainHandItem();

        if (!item.has(CMEDataComponents.MECHANICAL_CAT_GIVE_GIFT))
            return;

        List<FilledToolSlot> slots = item.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (FilledToolSlot slot : slots) {
            var part = slot.getPart();
            if (part.isEmpty() || part.get() != MechanicalPart.SMALL_MECHANICAL_CAT)
                continue;
            var data = (MechanicalCatPartData) part.get().get().data;
            //noinspection DataFlowIssue // cant be null since item.has guard
            data.handleGifts(player, item, item.get(CMEDataComponents.MECHANICAL_CAT_GIVE_GIFT), event);
        }
    }

    protected void handleGifts(@NotNull ServerPlayer player, @NotNull ItemStack item, @NotNull MechanicalCatGiftType gift,
                               BlockDropsEvent event) {
        var level = event.getLevel();
        switch (gift) {
            case EXPERIENCE -> {
                event.setDroppedExperience(2);
            } // handled above
            case DOUBLE_DROPS -> {
                // TODO: tag to forbid certain duplications
                var drops = event.getDrops();
                if (drops.isEmpty())
                    break;
                var other_drops = new ArrayList<>(drops);
                for (var drop : other_drops) {
                    var copy = drop.copy();
                    var pos = copy.position();

                    double d0 = (double) EntityType.ITEM.getHeight() / 2.0;
                    double d1 = pos.x() + Mth.nextDouble(level.getRandom(), -0.125, 0.125);
                    double d2 = pos.y() + 0.3 + Mth.nextDouble(level.getRandom(), -0.25, 0.2) - d0;
                    double d3 = pos.z() + Mth.nextDouble(level.getRandom(), -0.125, 0.125);
                    copy.setPos(d1, d2, d3);

                    drops.add(copy);
                }
                playCatSound(event.getLevel(), drops.getFirst().position(), SoundEvents.NOTE_BLOCK_BIT.value());
            }
            case CASHBACK -> {
                var drops = event.getDrops();
                if (drops.isEmpty())
                    break;
                var pos = drops.getFirst().position();
                double d1 = pos.x() + Mth.nextDouble(level.random, -0.25, 0.25);
                double d3 = pos.z() + Mth.nextDouble(level.random, -0.25, 0.25);
                var coin = new ItemEntity(event.getLevel(), d1, pos.y(), d3, CMEItems.MINTED_COPPER_COIN.asStack());
                drops.add(coin);
            }
            case JACKPOT -> {
                var drops = event.getDrops();
                if (drops.isEmpty())
                    break;
                var pos = drops.getFirst().position();
                var max = Mth.randomBetweenInclusive(level.getRandom(), 1, 5);
                ItemStack[] coins = new ItemStack[]{
                        CMEItems.MINTED_COPPER_COIN.asStack(),
                        CMEItems.MINTED_BRASS_COIN.asStack(),
                        CMEItems.MINTED_IRON_COIN.asStack(),
                        CMEItems.MINTED_IRON_COIN_DIAMOND.asStack(),
                        CMEItems.MINTED_IRON_COIN_AMETHYST.asStack(),
                        CMEItems.MINTED_BRASS_COIN_AMETHYST.asStack(),
                };
                for (int i = 0; i < max; i++) {
                    var coinStack = coins[Mth.randomBetweenInclusive(level.getRandom(), 0, coins.length - 1)];
                    double d1 = pos.x() + Mth.nextDouble(level.getRandom(), -0.25, 0.25);
                    double d3 = pos.z() + Mth.nextDouble(level.getRandom(), -0.25, 0.25);
                    var coin = new ItemEntity(event.getLevel(), d1, pos.y(), d3, coinStack);
                    drops.add(coin);
                }
            }
        }

        item.remove(CMEDataComponents.MECHANICAL_CAT_GIVE_GIFT); // always consume
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {

        var bonus = stack.get(CMEDataComponents.MECHANICAL_CAT_BONUS);
        if (bonus == null)
            return;
        var component = switch (bonus) {
            case NONE -> Component.empty(); // skip
            case FORTUNE -> CMETranslations.MECHANICAL_CAT_BONUS_FORTUNE.resolveComponentMutable();
            case GIFTS -> CMETranslations.MECHANICAL_CAT_BONUS_GIFTS.resolveComponentMutable();
            case GLOWING -> CMETranslations.MECHANICAL_CAT_BONUS_GLOWING.resolveComponentMutable();
            case HASTE -> CMETranslations.MECHANICAL_CAT_BONUS_HASTE.resolveComponentMutable();
            case HUNGER_REGEN -> CMETranslations.MECHANICAL_CAT_BONUS_HUNGER_REGEN.resolveComponentMutable();
            case BLOCK_INTERACTION_RANGE -> CMETranslations.MECHANICAL_CAT_BONUS_INTERACTION_RANGE.resolveComponentMutable();
        };
        tooltip.add(CMETranslations.MECHANICAL_CAT_ACTIVE_BONUS.resolveComponentMutable().append(component));
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        //ms.translate(0, 13 / 16f, -10 / 16f); // TODO

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
