package com.mirandnyan.mired.content.equipment.mechanical_mods;

import com.mirandnyan.mired.CMETranslations;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public record FilledToolSlot(
        @NonNull ResourceKey<MechanicalToolSlot> slot,
        @Nullable ResourceKey<MechanicalPart> part) {

    public static final Codec<FilledToolSlot> CODEC = RecordCodecBuilder.create(i -> i.group(
            MechanicalToolSlot.CODEC.fieldOf("slot").forGetter(FilledToolSlot::slot),
            MechanicalPart.CODEC.fieldOf("part").forGetter(FilledToolSlot::part)
    ).apply(i, FilledToolSlot::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilledToolSlot> STREAM_CODEC = StreamCodec.composite(
            MechanicalToolSlot.STREAM_CODEC, FilledToolSlot::slot,
            MechanicalPart.STREAM_CODEC, FilledToolSlot::part,
            FilledToolSlot::new
    );

    public boolean isEmpty() {
        return part == null;
    }

    public RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> getSlot() {
        return MechanicalToolSlot.get(slot);
    }

    public Optional<RegistryEntry<MechanicalPart, MechanicalPart>> getPart() {
        if (this.part == null)
            return Optional.empty();
        return Optional.of(MechanicalPart.get(this.part));
    }

    public Optional<RegistryEntry<Item, Item>> getItem() {
        return this.getPart().map(p -> p.get().getItem());
    }

    public static Optional<RegistryEntry<MechanicalPart, MechanicalPart>> getPartOf(@Nullable FilledToolSlot slot) {
        if (slot == null)
            return Optional.empty();
        return slot.getPart();
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
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        var maybe_part = getPart();
        var part = maybe_part.isEmpty()
                ? CMETranslations.TOOL_SLOTS_EMPTY.resolveComponent()
                : CMETranslations.Components.item(maybe_part.get().get().getItem());

        tooltip.add(CMETranslations.Components.line(
                getSlot().get().lang().resolveComponent(),
                Component.literal(": "),
                part
        ));
    }
}
