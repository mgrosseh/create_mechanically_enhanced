package com.mirandnyan.cme.content.items;

import com.mirandnyan.cme.CMETranslations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class TooltipItem extends Item {
    Supplier<Component> hoverTextSupplier;
    public TooltipItem(Properties properties, Supplier<Component> supplier) {
        super(properties);
        hoverTextSupplier = supplier;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(hoverTextSupplier.get());
    }

}
