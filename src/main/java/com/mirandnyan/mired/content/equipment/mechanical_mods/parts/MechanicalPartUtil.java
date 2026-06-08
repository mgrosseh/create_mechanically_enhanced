package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MechanicalPartUtil {
    public static final double MINING_EFFICIENCY_TO_COG_SPEED = 1d / 8d;


    public static Holder<Enchantment> getHolder(ResourceKey<Enchantment> enchantment) {
        if (Minecraft.getInstance().level == null)
            return null;
        return getHolder(enchantment, Minecraft.getInstance().level);
    }

    public static Holder<Enchantment> getHolder(ResourceKey<Enchantment> enchantment, @NotNull Level level) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantment);
    }

    public static void addEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment, int level) {
        addEnchantment(stack, getHolder(enchantment), level);
    }

    public static void addEnchantment(ItemStack stack, Holder<Enchantment> enchantment, int level) {
        ItemEnchantments enchs = stack.get(DataComponents.ENCHANTMENTS);
        if (enchs == null)
            enchs = ItemEnchantments.EMPTY;
        ItemEnchantments.Mutable mutEnchs = new ItemEnchantments.Mutable(enchs);
        if (enchantment == null)
            return;
        mutEnchs.upgrade(enchantment, level);
        stack.set(DataComponents.ENCHANTMENTS, mutEnchs.toImmutable());
    }

    public static void removeEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        removeEnchantment(stack, getHolder(enchantment));
    }
    public static void removeEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ItemEnchantments enchs = stack.get(DataComponents.ENCHANTMENTS);
        if (enchs == null)
            enchs = ItemEnchantments.EMPTY;
        ItemEnchantments.Mutable mutEnchs = new ItemEnchantments.Mutable(enchs);
        if (enchantment == null)
            return;
        mutEnchs.removeIf(x -> x.equals(enchantment));
        stack.set(DataComponents.ENCHANTMENTS, mutEnchs.toImmutable());
    }
}
