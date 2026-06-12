package com.mirandnyan.cme.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;

public class AttributeHelpers {
    public static double calculateAttributeValue(ItemStack stack, Holder<Attribute> attribute, EquipmentSlot slot) {
        var attributes = stack.getAttributeModifiers();
        ArrayList<Double> add_value = new ArrayList<>();
        ArrayList<Double> add_multiplied_base = new ArrayList<>();
        ArrayList<Double> add_multiplied_total = new ArrayList<>();
        for(ItemAttributeModifiers.Entry entry : attributes.modifiers()) {
            if (entry.slot().test(slot) && entry.attribute().equals(attribute)) {
                double amount = entry.modifier().amount();
                switch (entry.modifier().operation()) {
                    case ADD_VALUE -> add_value.add(amount);
                    case ADD_MULTIPLIED_BASE -> add_multiplied_base.add(amount);
                    case ADD_MULTIPLIED_TOTAL -> add_multiplied_total.add(amount);
                    default -> throw new MatchException(null, null);
                }
            }
        }

        double value = add_value.stream().reduce(0d, Double::sum);
        value = value * (1 + add_multiplied_base.stream().reduce(0d, Double::sum));
        value = add_multiplied_total.stream().reduce(value, (a, b) -> a * (1 + b));
        return value;
    }
}
