package com.mirandnyan.mired.content.equipment.mechanical_mods.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class MechanicalPartUtil {
    public static final double MINING_EFFICIENCY_TO_COG_SPEED = 1d / 8d;


    public static Holder<Enchantment> getHolder(ResourceKey<Enchantment> enchantment) {
        if (Minecraft.getInstance().level == null)
            return null;
        return Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantment);
    }

    public static void addEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment, int level) {
        ItemEnchantments enchs = stack.get(DataComponents.ENCHANTMENTS);
        if (enchs == null)
            enchs = ItemEnchantments.EMPTY;
        ItemEnchantments.Mutable mutEnchs = new ItemEnchantments.Mutable(enchs);
        var enchant = MechanicalPartUtil.getHolder(enchantment);
        if (enchant == null)
            return;
        mutEnchs.upgrade(enchant, level);
        stack.set(DataComponents.ENCHANTMENTS, mutEnchs.toImmutable());
    }

    public static void removeEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        ItemEnchantments enchs = stack.get(DataComponents.ENCHANTMENTS);
        if (enchs == null)
            enchs = ItemEnchantments.EMPTY;
        ItemEnchantments.Mutable mutEnchs = new ItemEnchantments.Mutable(enchs);
        var enchant = MechanicalPartUtil.getHolder(enchantment);
        if (enchant == null)
            return;
        mutEnchs.removeIf(x -> x.equals(enchant));
        stack.set(DataComponents.ENCHANTMENTS, mutEnchs.toImmutable());
    }

}
