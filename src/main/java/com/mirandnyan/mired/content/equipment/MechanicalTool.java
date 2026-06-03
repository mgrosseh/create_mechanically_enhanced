package com.mirandnyan.mired.content.equipment;

import com.mirandnyan.mired.CVADataComponents;
import com.mirandnyan.mired.content.equipment.mechanical_mods.FilledToolSlot;
import com.mirandnyan.mired.content.equipment.mechanical_mods.MechanicalToolSlot;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MechanicalTool extends Item {

    public MechanicalTool(Properties properties) {
        super(properties);
    }

    protected static @Nullable FilledToolSlot getToolSlot(ItemStack stack, RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        List<FilledToolSlot> slots = stack.get(CVADataComponents.TOOL_SLOTS_COMPONENT_TYPE);
        if (slots == null)
            return null;
        for (FilledToolSlot toolSlot : slots) {
            if (toolSlot.isSlot(slot))
                return toolSlot;
        }
        return null;
    }
    protected static @Nullable FilledToolSlot insertFilledToolSlot(ItemStack stack, FilledToolSlot slot) {
        List<FilledToolSlot> slots = stack.get(CVADataComponents.TOOL_SLOTS_COMPONENT_TYPE);
        ArrayList<FilledToolSlot> newSlots = slots == null ? new ArrayList<>() : new ArrayList<>(slots);

        FilledToolSlot removed = null;
        for (int i = 0; i < newSlots.size(); i++) {
            if (newSlots.get(i).isSlot(slot)) {
                removed = newSlots.remove(i);
                removed.getPart().ifPresent(part -> part.get().data.onRemoved(stack));
                break;
            }
        }
        slot.getPart().ifPresent(part -> part.get().data.onInserted(stack));
        newSlots.add(slot);
        stack.set(CVADataComponents.TOOL_SLOTS_COMPONENT_TYPE, List.copyOf(newSlots));
        return removed;
    }
}
