package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mirandnyan.mired.CMEDataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.FilledToolSlot;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPart;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalPartData;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Supplier;

@EventBusSubscriber
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


    public static final String MECH_BLAZE_MARKER = "mechanicallyEnhancedBlaze";


    public static final AttributeModifier superheatedBoostModifier =
            new AttributeModifier(ResourceLocation.withDefaultNamespace("player.mining_efficiency"), 1.8,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    public static final AttributeModifier heatedBoostModifier =
            new AttributeModifier(ResourceLocation.withDefaultNamespace("player.mining_efficiency"), 1.3,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);


    private static final Supplier<Multimap<Holder<Attribute>, AttributeModifier>> superheatedBoost = Suppliers.memoize(() ->
            ImmutableMultimap.of(Attributes.MINING_EFFICIENCY, superheatedBoostModifier));
    private static final Supplier<Multimap<Holder<Attribute>, AttributeModifier>> heatedBoost = Suppliers.memoize(() ->
            ImmutableMultimap.of(Attributes.MINING_EFFICIENCY, heatedBoostModifier));

    @Override
    public boolean tryHandlingStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                          @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        var gameTime = player.level().getGameTime();
        if (AllItems.CREATIVE_BLAZE_CAKE.isIn(other)) {
            var infinite = stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE);
            var superheated = stack.has(CMEDataComponents.BLAZE_BURNING_SUPER);

            if (infinite && superheated) {
                stack.remove(CMEDataComponents.BLAZE_BURNING_INFINITE);
                stack.remove(CMEDataComponents.BLAZE_BURNING_SUPER);
                return true;
            }
            if (!infinite) {
                stack.remove(CMEDataComponents.BLAZE_BURNING_SUPER);
                stack.set(CMEDataComponents.BLAZE_BURNING_INFINITE, Unit.INSTANCE);
                return true;
            }
            stack.set(CMEDataComponents.BLAZE_BURNING_SUPER, Unit.INSTANCE);
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

            if (!player.isCreative())
                other.shrink(1);
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

            if (!player.isCreative())
                other.shrink(1);
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
        }
    }

    private static class ClientData {
        boolean mining = false;
        int mining_visual = 0;
        float smallRodPos = 0;
        float largeRodPos = 0;
        static WeakHashMap<String, ClientData> clientData = new WeakHashMap<>();

        static ClientData of(String name) {
            return clientData.computeIfAbsent(name, s -> new ClientData());
        }
    }
    @Override
    public void playerTick(Player player, ItemStack stack) {
        if (!(player.level() instanceof ClientLevel clevel))
            return;
        // animation
        var data = ClientData.of(player.getName().getString());
        data.mining = clevel.levelRenderer.destroyingBlocks.containsKey(player.getId());
        if (data.mining) {
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

    private static void updateHeatAttribute(Player player, ItemStack stack) {

    }

    @SubscribeEvent
    public static void stopHoldingMechanicalTool(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        boolean has = false;
        ItemStack stack = player.getMainHandItem();
        @Nullable MechanicalBlazePartData data = null;
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            if (slot.part() == null || slot.part().compareTo(MechanicalPart.SMALL_MECHANICAL_BLAZE.getKey()) == 0)
                continue;
            has = true;
            //noinspection OptionalGetWithoutIsPresent // isPresent === slot.part() != null
            data = (MechanicalBlazePartData) slot.getPart().get().get().data;
        }

        // TODO:
        // effect
        CompoundTag persistentData = player.getPersistentData();
        var had = persistentData.contains(MECH_BLAZE_MARKER);

        if (has != had) {
            if (has) {
                updateHeatAttribute(player, stack);
                persistentData.putBoolean(MECH_BLAZE_MARKER, true);
            }
            else {
                player.getAttributes()
                        .removeAttributeModifiers(superheatedBoost.get());
                player.getAttributes()
                        .removeAttributeModifiers(heatedBoost.get());
                persistentData.remove(MECH_BLAZE_MARKER);
            }
        }

    }


    private Optional<ClientData> getData(ItemStack stack) {
        var name = stack.get(CMEDataComponents.LAST_TOOL_HOLDER_NAME);
        if (name == null)
            return Optional.empty();
        return Optional.of(ClientData.of(name));
    }


    @Override
    public void render(ItemStack stack, MechanicalPart part, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        long time = stack.getOrDefault(CMEDataComponents.BLAZE_BURNING_TIME, 0L);
        if (stack.has(CMEDataComponents.BLAZE_BURNING_INFINITE))
            time = 1;
        var supercharged = stack.has(CMEDataComponents.BLAZE_BURNING_SUPER);

        var data = getData(stack);

        ms.pushPose();
        ms.translate(0, 13 / 16f, -10 / 16f);

        // cog
        ms.pushPose();
        int speedModifier = Math.max(stack.getOrDefault(CMEDataComponents.SPEED_MODIFIER, 100), 0);
        float angle = AnimationTickHolder.getRenderTime() * -1 * 2.5f * (speedModifier / 100f);
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
