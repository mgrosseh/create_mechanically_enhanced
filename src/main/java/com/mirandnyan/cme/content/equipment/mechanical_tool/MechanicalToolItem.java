package com.mirandnyan.cme.content.equipment.mechanical_tool;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMEItems;
import com.mirandnyan.cme.CMEMechanicalParts;
import com.mirandnyan.cme.CMETranslations;
import com.mirandnyan.cme.content.equipment.MechanicalItem;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MechanicalToolItem extends MechanicalItem implements CustomArmPoseItem {

    // TODO: pose is weird (crossing) when in offhand: extendo grip, crossbow, other mechanical tool
    // TODO: enchants: unbreaking (for when no air)
    // TODO: no anvil
    // TODO: has type: tag + if part has type then this has that type
    // TODO: parts: incompatible with tag
    // TODO: add right click (as opposed to right hold = refill) as an action parts can access
    // TODO: right / left click on drill in inventory
    // TODO: sound when air empty

    // TODO: head: explosion generator

    /*
    TODO: ECS / Rules:
    - Parts publish components / behaviors
    - Make fire_immune when there is at least one netherite item in the tool
    - Analyze tool structure to apply buffs / nerfs, e.g. if only saw, make it use a lot of durability when hitting an enemy to punish the fast speed etc.
     */

    /*
    TODO: remove since most has been done (not all hence this todo)
    TODO: reworking Slots / Parts / FilledSlots etc
    Tool has a type for Grip:
    Tag defines two slots on grip: cog + gearbox
    Each type stores position offset on tool

    On trying to rightClick, for each part try inserting a valid part onto it recursively, the tool facilitates figuring
    out if it is a MechanicalPart and then calls tryInsertingTool on gripPart.data.
    It sees if one of its slots fits the Part, if not for each part it calls part.data.tryInsertingTool.
     */

    @SafeVarargs
    public static ItemStack newStackWithParts(RegistryEntry<MechanicalPart, MechanicalPart>... parts) {
        var stack = CMEItems.MECHANICAL_TOOL.asStack();

        for (var part : parts) {
            tryInsertingMechanicalPart(stack, part);
        }
        recalculateTotalWeight(stack);
        var capacity = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR_CAPACITY, 0);
        stack.set(CMEDataComponents.PRESSURIZED_AIR, capacity);
        return stack;
    }

    public MechanicalToolItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON).stacksTo(1));
    }


    // Change parts
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot,
                                            @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (tryMechanicalPartsHandlingStackOnMe(stack, other, slot, action, player, access))
            return true;

        if (!slot.allowModification(player))
            return false;

        if (!AllKeys.ctrlDown() && action == ClickAction.SECONDARY)
            return tryAddingMechanicalPart(stack, other, player, access);

        if (!player.isCreative())
            return false;
        if (!AllKeys.ctrlDown()) {
            selectedToolSlot = -1;
            showErrorToolSlot = -1;
            return false;
        }
        if (action == ClickAction.SECONDARY && stack.getCount() != 1) {
            var out = tryAddingMechanicalPart(stack, other, player, access);
            if (!out)
                return false; // TODO: somehow show this
            selectedToolSlot = -1;
            return true;
        }
        if (action == ClickAction.SECONDARY && other.isEmpty()) {
            List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
            if (slots.isEmpty()) {
                selectedToolSlot = -1;
                return false; // make sure in bounds
            }
            selectedToolSlot = selectedToolSlot % slots.size();
            var out = tryRemovingMechanicalPart(stack, access, selectedToolSlot);
            switch (out.type) {
                case SUCCESS -> {
                    selectedToolSlot = -1;
                    showErrorToolSlot = -1;
                }
                case IS_PARENT_OF -> showErrorToolSlot = out.reason;
                case IMPOSSIBLE -> showErrorToolSlot = selectedToolSlot;
            }
            return true; // we don't want to mess up the selection, so true either way
        }
        if (action == ClickAction.PRIMARY) {
            List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
            if (!slots.isEmpty()) {
                selectedToolSlot = (selectedToolSlot + 1) % slots.size();
                showErrorToolSlot = -1;
            }
            return true;
        }

        return false;
    }

    protected static int selectedToolSlot = -1;
    protected static int showErrorToolSlot = -1;

    protected boolean tryMechanicalPartsHandlingStackOnMe(@NotNull ItemStack stack, @NotNull ItemStack other,
                                                          @NotNull Slot slot, @NotNull ClickAction action,
                                                          @NotNull Player player, @NotNull SlotAccess access) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var tool_slot : slots) {
            var part = tool_slot.getPartEntry();
            var handled = part.get().data.tryHandlingStackedOnMe(stack, other, slot, action, player, access);
            if (handled)
                return true;
        }

        return false;
    }

    protected boolean tryAddingMechanicalPart(@NotNull ItemStack stack, @NotNull ItemStack other,
                                              @NotNull Player player, @NotNull SlotAccess access) {
        if (!player.isCreative())
            return false;
        if (stack.getCount() != 1)
            return false;

        var maybe_part = CMEMechanicalParts.getOfItem(other.getItem());
        if (maybe_part.isEmpty())
            return false;
        var insertingPart = maybe_part.get();

        Optional<Boolean> override = Optional.empty();
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            var result = slot.getPartEntry().get().data.overrideInsertingPart(stack, other, player, access, insertingPart);
            if (result.isPresent()) {
                override = result;
                break;
            }
        }
        if (override.isPresent())
            return override.get();


        RegistryEntry<MechanicalPart, MechanicalPart> old = null;
        if (!tryInsertingMechanicalPart(stack, insertingPart)) {
            var out = MechanicalItem.tryReplacingMechanicalPart(stack, insertingPart);
            if (out.isEmpty())
                return false;
            old = out.get();
        }

        recalculateTotalWeight(stack);
        other.shrink(1);
        if (old == null)
            return true;
        var item = old.get().getItemRegistry().get().getDefaultInstance();
        access.set(item);

        return true;
    }



    // -- Visuals --
    @SuppressWarnings("removal")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new MechanicalToolRenderer()));
    }

    // prevent bobbing after mine
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack) && slotChanged;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.NONE;
    }

    // disable swing animation
    @Override
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        return true;
    }

    @Override
    @SuppressWarnings("removal")
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return true;
    }

    // make look nice in third person
    @OnlyIn(Dist.CLIENT)
    @Override
    @Nullable
    public HumanoidModel.ArmPose getArmPose(final ItemStack stack, final AbstractClientPlayer player, final InteractionHand hand) {
        return HumanoidModel.ArmPose.CROSSBOW_HOLD;
    }

    // -- Tooltips --
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());

        if (slots.isEmpty())
            tooltip.add(CMETranslations.TOOL_SLOTS_NONE.resolveComponent());
        else {
            if (!flagIn.hasControlDown())
                tooltip.add(CMETranslations.SHOW_SLOTS_TOOLTIP_INFO.resolveComponent());
            else {
                tooltip.add(CMETranslations.TOOL_SLOTS_TITLE.resolveComponent());
                var len = slots.size();
                for (int i = 0; i < len; i++) {
                    FilledToolSlot slot = slots.get(i);
                    slot.appendHoverText(stack, context, tooltip, flagIn, i == selectedToolSlot, i == showErrorToolSlot);
                }
            }
        }

        for (FilledToolSlot slot : slots) {
            var part = slot.getPartEntry();
            part.get().data.appendHoverText(stack, context, tooltip, flagIn);
        }

        // TODO: explain mechanic of removing / selecting parts (only if player in creative)
        boolean more = false;
        for (FilledToolSlot slot : slots) {
            more = more || slot.getPartEntry().get().data.hasExtraTooltip(stack, context, tooltip, flagIn);
        }
        if (more) {
            if (!flagIn.hasShiftDown())
                tooltip.add(CMETranslations.EXTRA_TOOLTIP_INFO.resolveComponent());
            else {
                for (FilledToolSlot slot : slots) {
                    slot.getPartEntry().get().data.appendExtraTooltip(stack, context, tooltip, flagIn);
                }
            }
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
