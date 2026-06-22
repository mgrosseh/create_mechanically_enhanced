package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.accelerator;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMEMechanicalParts;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class MechanicalSimpleAcceleratorPartData extends MechanicalAcceleratorPartData {
    public MechanicalSimpleAcceleratorPartData(int speedModifier) {
        super(-0.8f, speedModifier);
    }

    @Override
    public void onInserted(ItemStack tool) {
        super.onInserted(tool);
        tool.set(CMEDataComponents.EXPLOSION_IMMUNE, Unit.INSTANCE);
    }

    @Override
    public void onRemoved(ItemStack tool) {
        super.onRemoved(tool);
        tool.remove(CMEDataComponents.EXPLOSION_IMMUNE);
    }

    @Override
    public Optional<Boolean> overrideInsertingPart(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Player player, @NotNull SlotAccess access, @NotNull FilledToolSlot insertingPart) {
        List<FilledToolSlot> slots = stack.getOrDefault(CMEDataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.of());
        for (var slot : slots) {
            if (slot.parent().isEmpty() || slot.parent().get() != CMEMechanicalParts.SIMPLE_ANDESITE_ACCELERATOR.getKey())
                continue;
            if (slot.isSlot(insertingPart.slot()))
                return Optional.empty(); // let item handle swapping parts
            return Optional.of(false);
        }

        return super.overrideInsertingPart(stack, other, player, access, insertingPart);
    }
}
