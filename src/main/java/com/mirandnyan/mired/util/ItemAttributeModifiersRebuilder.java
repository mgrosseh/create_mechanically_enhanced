package com.mirandnyan.mired.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Arrays;
import java.util.function.Predicate;

public class ItemAttributeModifiersRebuilder {
    private final ItemAttributeModifiers original;
    private final ItemAttributeModifiers.Builder builder;
    public ItemAttributeModifiersRebuilder(ItemAttributeModifiers from) {
        this.original = from;
        builder = ItemAttributeModifiers.builder();
    }
    public ItemAttributeModifiersRebuilder takeAll() {
        return this.filter(e -> true);
    }
    public ItemAttributeModifiersRebuilder filter(Predicate<ItemAttributeModifiers.Entry> filter) {
        for (var modifier : original.modifiers()) {
            if (filter.test(modifier))
                builder.add(modifier.attribute(), modifier.modifier(), modifier.slot());
        }
        return this;
    }
    public ItemAttributeModifiersRebuilder removing(ItemAttributeModifiers.Entry... entries) {
        return this.filter(e -> Arrays.stream(entries).noneMatch(e2 -> e2.equals(e)));
    }
    public ItemAttributeModifiersRebuilder add(ItemAttributeModifiers.Entry entry) {
        return this.add(entry.attribute(), entry.modifier(), entry.slot());
    }

    public ItemAttributeModifiersRebuilder add(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
        builder.add(attribute, modifier, slot);
        return this;
    }

    public ItemAttributeModifiers build() {
        return builder.build();
    }
}
