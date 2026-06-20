package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.CMETranslations;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record FilledToolSlot(
        @NonNull ResourceKey<MechanicalToolSlot> slot,
        @NotNull ResourceKey<MechanicalPart> part,
        @NotNull Optional<ResourceKey<MechanicalPart>> parent) {

    public static final Codec<FilledToolSlot> CODEC = RecordCodecBuilder.create(i -> i.group(
            MechanicalToolSlot.CODEC.fieldOf("slot").forGetter(FilledToolSlot::slot),
            MechanicalPart.CODEC.fieldOf("part").forGetter(FilledToolSlot::part),
            MechanicalPart.CODEC.optionalFieldOf("parent").forGetter(FilledToolSlot::parent)
    ).apply(i, FilledToolSlot::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilledToolSlot> STREAM_CODEC = StreamCodec.composite(
            MechanicalToolSlot.STREAM_CODEC, FilledToolSlot::slot,
            MechanicalPart.STREAM_CODEC, FilledToolSlot::part,
            MechanicalPart.STREAM_CODEC.apply(ByteBufCodecs::optional), FilledToolSlot::parent,
            FilledToolSlot::new
    );

    public RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> getSlot() {
        return MechanicalToolSlot.get(slot);
    }

    public RegistryEntry<MechanicalPart, MechanicalPart> getPartRegistry() {
        return MechanicalPart.get(this.part);
    }

    public RegistryEntry<Item, Item> getItem() {
        return this.getPartRegistry().get().getItemRegistry();
    }

    public boolean has(FilledToolSlot slot) {
        if (slot == null)
            return false;
        return getPartRegistry().get().slots().has(slot.slot());
    }

    public boolean isSlot(FilledToolSlot other) {
        return isSlot(other.slot());
    }
    public boolean isSlot(ResourceKey<MechanicalToolSlot> slot) {
        return this.slot.equals(slot);
    }
    public boolean isSlot(RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        return isSlot(slot.getKey());
    }

    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn,
                                boolean isSelected, boolean isError) {
        var part = CMETranslations.Components.item(getPartRegistry().get().getItemRegistry());

        var comp = getSlot().get().lang().resolveComponentMutable();
        if (isSelected)
            comp = comp.withStyle(ChatFormatting.UNDERLINE);
        if (isError)
            comp = comp.withStyle(ChatFormatting.RED);
        tooltip.add(CMETranslations.Components.line(
                comp,
                Component.literal(": "),
                part
        ));
    }
}
