package com.mirandnyan.mired;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import static net.createmod.catnip.lang.LangBuilder.resolveBuilders;

public class CVALang extends Lang {

    public static LangBuilder builder() {
        return new LangBuilder(CreateVariousAdditions.MOD_ID);
    }

    public static LangBuilder blockName(BlockState state) {
        return builder().add(state.getBlock()
                .getName());
    }

    public static LangBuilder itemName(ItemStack stack) {
        return builder().add(stack.getHoverName()
                .copy());
    }

    public static LangBuilder fluidName(FluidStack stack) {
        return builder().add(stack.getHoverName()
                .copy());
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder translate(CVATranslations.LangEntry entry, Object... args) {
        Object[] args1 = resolveBuilders(args);
        return builder().add(Component.translatable(entry.translationKey, args1));
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }

    @Deprecated // Use while implementing and replace all references with Lang.translate
    public static LangBuilder temporaryText(String text) {
        return builder().text(text);
    }

}
