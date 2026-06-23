package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.CMEMechanicalParts;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record FilledToolSlot(
        @NotNull SlotId slot,
        @NotNull ResourceKey<MechanicalPart> part,
        @NotNull Optional<ResourceKey<MechanicalPart>> parent) {

    public record SlotId(@NonNull ResourceKey<MechanicalToolSlot> type, int ordinal) {

        public static final Codec<SlotId> CODEC = RecordCodecBuilder.create(i -> i.group(
                MechanicalToolSlot.CODEC.fieldOf("type").forGetter(SlotId::type),
                Codec.INT.fieldOf("ordinal").forGetter(SlotId::ordinal)
        ).apply(i, SlotId::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SlotId> STREAM_CODEC = StreamCodec.composite(
                MechanicalToolSlot.STREAM_CODEC, SlotId::type,
                ByteBufCodecs.VAR_INT, SlotId::ordinal,
                SlotId::new
        );
    }

    public static final Codec<FilledToolSlot> CODEC = RecordCodecBuilder.create(i -> i.group(
            SlotId.CODEC.fieldOf("id").forGetter(FilledToolSlot::slot),
            MechanicalPart.CODEC.fieldOf("part").forGetter(FilledToolSlot::part),
            MechanicalPart.CODEC.optionalFieldOf("parent").forGetter(FilledToolSlot::parent)
    ).apply(i, FilledToolSlot::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilledToolSlot> STREAM_CODEC = StreamCodec.composite(
            SlotId.STREAM_CODEC, FilledToolSlot::slot,
            MechanicalPart.STREAM_CODEC, FilledToolSlot::part,
            MechanicalPart.STREAM_CODEC.apply(ByteBufCodecs::optional), FilledToolSlot::parent,
            FilledToolSlot::new
    );

    public RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> getSlotType() {
        return MechanicalToolSlot.get(slot.type);
    }

    public RegistryEntry<MechanicalPart, MechanicalPart> getPartEntry() {
        return CMEMechanicalParts.get(this.part);
    }

    public MechanicalPart getPart() {
        return getPartEntry().get();
    }

    public RegistryEntry<Item, Item> getItem() {
        return this.getPartEntry().get().getItemRegistry();
    }

    public boolean isSlot(ResourceKey<MechanicalToolSlot> slot) {
        return this.slot.type.equals(slot);
    }
    public boolean isSlot(RegistryEntry<MechanicalToolSlot, MechanicalToolSlot> slot) {
        return isSlot(slot.getKey());
    }

    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn,
                                boolean isSelected, boolean isError) {
        var part = CMETranslations.Components.item(getPartEntry().get().getItemRegistry());

        var comp = getSlotType().get().lang().resolveComponentMutable();
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

    public static Optional<FilledToolSlot> getSlotOf(List<FilledToolSlot> filledSlots, SlotId slot, @Nullable ResourceKey<MechanicalPart> parent) {
        var optParent = Optional.ofNullable(parent);
        return filledSlots.stream().filter(s -> s.slot().equals(slot) && s.parent().equals(optParent)).findAny();
    }
}
