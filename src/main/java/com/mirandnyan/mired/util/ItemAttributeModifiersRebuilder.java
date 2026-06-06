package com.mirandnyan.mired.util;

import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Predicate;

public class ItemAttributeModifiersRebuilder {
    ItemAttributeModifiers original;
    ItemAttributeModifiers.Builder builder;
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
    public ItemAttributeModifiersRebuilder add(ItemAttributeModifiers.Entry entry) {
        builder.add(entry.attribute(), entry.modifier(), entry.slot());
        return this;
    }

    public ItemAttributeModifiers build() {
        return builder.build();
    }
}
