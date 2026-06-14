package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CreateMechanicallyEnhanced;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.util.AttributeHelpers;
import com.mirandnyan.cme.util.ItemAttributeModifiersRebuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.datamaps.BlazeBurnerFuel;
import com.simibubi.create.api.registry.CreateDataMaps;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.WeakHashMap;

public class MechanicalBlazePartData extends MechanicalPartData {
    private static int i = 0;
    private static final int BASE_INERT = i++;
    private static final int BASE_IDLE = i++;
    private static final int BASE_WORKING = i++;
    private static final int BASE_IDLE_SUPERHEATED = i++;
    private static final int BASE_WORKING_SUPERHEATED = i++;
    private static final int SMALL_RODS = i++;
    private static final int LARGE_RODS = i++;
    private static final int SMALL_RODS_SUPERHEATED = i++;
    private static final int LARGE_RODS_SUPERHEATED = i++;
    private static final int COG = i++;

    private static final AttributeModifier superheatedBoostModifier =
            new AttributeModifier(CreateMechanicallyEnhanced.asResource("superheated_mining_boost"), 2.7,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final AttributeModifier heatedBoostModifier =
            new AttributeModifier(CreateMechanicallyEnhanced.asResource("heated_mining_boost"), 1.6,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    private static final ItemAttributeModifiers.Entry superheatedBoost = new ItemAttributeModifiers.Entry(
            Attributes.MINING_EFFICIENCY,
            superheatedBoostModifier,
            EquipmentSlotGroup.MAINHAND
    );
    private static final ItemAttributeModifiers.Entry heatedBoost = new ItemAttributeModifiers.Entry(
            Attributes.MINING_EFFICIENCY,
            heatedBoostModifier,
            EquipmentSlotGroup.MAINHAND
    );

    public MechanicalBlazePartData() {
        super(0.3f);
    }


    protected static void playBlazeSound(Level level, Vec3 position, SoundEvent sound, float base_volume, float base_pitch) {
        var newPos = position.add(0.5, 1, 0.5);
        var volume = base_volume + level.random.nextFloat() * .125f;
        var pitch = base_pitch - level.random.nextFloat() * .25f;
        level.playSound(null, newPos.x, newPos.y, newPos.z, sound, SoundSource.PLAYERS, volume, pitch);
        // TODO: make sound more robotic
    }
    protected static void playBlazeSound(Level level, Vec3 position, SoundEvent sound) {
        playBlazeSound(level, position, sound, .125f, 1.55f);
    }

    @Override
    public boolean tryHandlingStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                          @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        var gameTime = player.level().getGameTime();
        if (AllItems.CREATIVE_BLAZE_CAKE.isIn(other)) {
            var infinite = stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE);
            var superheated = stack.has(CMEDataComponents.BLAZE_BURNING_SUPER);
            stack.remove(CMEDataComponents.BLAZE_BURNING_TIME);

            if (infinite && superheated) {
                stack.remove(CMEDataComponents.BLAZE_BURNING_INFINITE);
                stack.remove(CMEDataComponents.BLAZE_BURNING_SUPER);
                playBlazeSound(player.level(), player.position(), SoundEvents.FIRE_EXTINGUISH);
            }
            else if (!infinite) {
                stack.remove(CMEDataComponents.BLAZE_BURNING_SUPER);
                stack.set(CMEDataComponents.BLAZE_BURNING_INFINITE, Unit.INSTANCE);
                playBlazeSound(player.level(), player.position(), SoundEvents.BLAZE_SHOOT);
            }
            else {
                stack.set(CMEDataComponents.BLAZE_BURNING_SUPER, Unit.INSTANCE);
                playBlazeSound(player.level(), player.position(), SoundEvents.BLAZE_SHOOT);
            }

            updateHeatAttribute(player, stack);
            return true;
        }
        if (stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE))
            return false;

        Holder<Item> holder = other.getItemHolder();
        BlazeBurnerFuel superheatedFuel = holder.getData(CreateDataMaps.SUPERHEATED_BLAZE_BURNER_FUELS);
        BlazeBurnerFuel normalFuel = holder.getData(CreateDataMaps.REGULAR_BLAZE_BURNER_FUELS);

        if (superheatedFuel != null) {
            stack.set(CMEDataComponents.BLAZE_BURNING_TIME, gameTime + superheatedFuel.burnTime());
            stack.set(CMEDataComponents.BLAZE_BURNING_SUPER, Unit.INSTANCE);
            playBlazeSound(player.level(), player.position(), SoundEvents.BLAZE_SHOOT);

            if (!player.isCreative())
                other.shrink(1);

            updateHeatAttribute(player, stack);
            return true;
        }
        if (stack.has(CMEDataComponents.BLAZE_BURNING_SUPER))
            return false;


        if (normalFuel == null) {
            var burnTime = other.getBurnTime(null);
            if (burnTime > 0) {
                normalFuel = new BlazeBurnerFuel(burnTime);
            }
        }

        if (normalFuel != null) {
            long time = stack.getOrDefault(CMEDataComponents.BLAZE_BURNING_TIME, gameTime);
            stack.set(CMEDataComponents.BLAZE_BURNING_TIME, time + normalFuel.burnTime());
            playBlazeSound(player.level(), player.position(), SoundEvents.BLAZE_SHOOT);

            if (!player.isCreative())
                other.shrink(1);
            updateHeatAttribute(player, stack);
            return true;
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        var infinite = stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE);
        if (infinite)
            return;

        long time = stack.getOrDefault(CMEDataComponents.BLAZE_BURNING_TIME, 0L);
        var superheated = stack.has(CMEDataComponents.BLAZE_BURNING_SUPER);
        if (time != 0 && time <= level.getGameTime()) {
            if (superheated)
                stack.remove(CMEDataComponents.BLAZE_BURNING_SUPER);
            stack.remove(CMEDataComponents.BLAZE_BURNING_TIME);
            if (entity instanceof Player player)
                updateHeatAttribute(player, stack);
        }

        if ((time - level.getGameTime()) == 30 * 20) {
            playBlazeSound(level, entity.position(), SoundEvents.BLAZE_AMBIENT);
        }
    }

    private static class ClientData {
        int lastAir = 0;
        int lastDamage = 0;
        int mining_visual = 0;
        float smallRodPos = 0;
        float largeRodPos = 0;
        static WeakHashMap<String, ClientData> clientData = new WeakHashMap<>();

        static ClientData of(String name) {
            return clientData.computeIfAbsent(name, s -> new ClientData());
        }
        static ClientData of(Player player) {
            return of(player.getName().getString());
        }
        static Optional<ClientData> of(ItemStack stack) {
            var name = stack.get(CMEDataComponents.LAST_TOOL_HOLDER_NAME);
            if (name == null)
                return Optional.empty();
            return Optional.of(ClientData.of(name));
        }
    }
    @Override
    public void playerTick(Player player, ItemStack stack) {
        //noinspection DuplicatedCode
        if (!(player.level() instanceof ClientLevel clevel))
            return;
        // animation
        var data = ClientData.of(player);
        var air = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        var damage = stack.getOrDefault(DataComponents.DAMAGE, 0);
        var mining = clevel.levelRenderer.destroyingBlocks.containsKey(player.getId())
                || air < data.lastAir
                || damage > data.lastDamage;
        data.lastAir = air;
        data.lastDamage = damage;
        if (mining) {
            data.mining_visual = 20;
        }
        if (data.mining_visual > 0) {
            data.mining_visual--;
            data.smallRodPos += 0.95f;
            data.largeRodPos += 0.7f;
        }

        if (data.smallRodPos > 0)
            data.smallRodPos -= 0.25f;
        if (data.largeRodPos > 0)
            data.largeRodPos -= 0.2f;

        if (data.smallRodPos > 5)
            data.smallRodPos = 5f;
        if (data.largeRodPos > 5)
            data.largeRodPos = 5f;
    }

    @Override
    public void onRemoved(ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(tool.getAttributeModifiers())
                .removing(superheatedBoost, heatedBoost)
                .build()
        );
        tool.remove(CMEDataComponents.BLAZE_BURNING_TIME);
        tool.remove(CMEDataComponents.BLAZE_BURNING_SUPER);
        tool.remove(CMEDataComponents.BLAZE_BURNING_INFINITE);
    }

    private static void updateHeatAttribute(Player player, ItemStack stack) {
        long time = stack.getOrDefault(CMEDataComponents.BLAZE_BURNING_TIME, 0L);
        var infinite = stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE);
        var superheated = stack.has(CMEDataComponents.BLAZE_BURNING_SUPER);
        var active = infinite
                || time >= player.level().getGameTime()
                || player.getMainHandItem() != stack;

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(stack.getAttributeModifiers())
                .removing(superheatedBoost, heatedBoost)
                .build()
        );
        if (!active) {
            return;
        }
        if (superheated)
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(stack.getAttributeModifiers())
                    .takeAll()
                    .add(superheatedBoost)
                    .build()
            );
        else
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiersRebuilder(stack.getAttributeModifiers())
                    .takeAll()
                    .add(heatedBoost)
                    .build()
            );
    }

    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        long time = stack.getOrDefault(CMEDataComponents.BLAZE_BURNING_TIME, 0L);
        if (stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE))
            time = 1;
        var supercharged = stack.has(CMEDataComponents.BLAZE_BURNING_SUPER);

        var data = ClientData.of(stack);

        ms.pushPose();
        ms.translate(0, 13 / 16f, -10 / 16f);

        // cog
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

        // base
        var working = data.map(d -> d.mining_visual).orElse(0) > 0 && time != 0;
        if (working && supercharged)
            renderer.renderSolid(part.models[BASE_WORKING_SUPERHEATED].get(), light);
        else if (working)
            renderer.renderSolid(part.models[BASE_WORKING].get(), light);
        else if (supercharged)
            renderer.renderSolid(part.models[BASE_IDLE_SUPERHEATED].get(), light);
        else if (time != 0)
            renderer.renderSolid(part.models[BASE_IDLE].get(), light);
        else {
            renderer.renderSolid(part.models[BASE_INERT].get(), light);
            ms.popPose();
            return;
        }

        // rods
        var smallRodPos = rodPos(-1, data.map(d -> d.smallRodPos).orElse(0f), 40f, 23);
        var smallRods = part.models[supercharged ? SMALL_RODS_SUPERHEATED : SMALL_RODS];

        ms.pushPose();
        ms.translate(0, smallRodPos / 16f, 0);
        renderer.renderSolidGlowing(smallRods.get(), light);
        ms.popPose();

        var largeRodPos = rodPos(-1, data.map(d -> d.largeRodPos).orElse(0f), 25f, 10);
        var largeRods = part.models[supercharged ? LARGE_RODS_SUPERHEATED : LARGE_RODS];

        ms.pushPose();
        ms.translate(0, largeRodPos / 16f, 0);
        renderer.renderSolidGlowing(largeRods.get(), light);
        ms.popPose();

        ms.popPose();
    }

    private float rodPos(float base, float offset, float variance, float period) {
        var jitterX = AnimationTickHolder.getTicks() % (2 * variance);
        var jitter = (jitterX < variance ? jitterX : (2 * variance) - jitterX) - (variance / 2f);
        jitter = jitter / period;
        return base - offset + jitter;
    }
}
