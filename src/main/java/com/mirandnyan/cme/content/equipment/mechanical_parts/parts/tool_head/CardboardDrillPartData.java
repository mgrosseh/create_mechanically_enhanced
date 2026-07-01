package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.CMETranslations;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.util.neoforge_helpers.ItemAttributeModifiersRebuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.tool.CardboardSwordItem;
import com.simibubi.create.content.equipment.tool.KnockbackPacket;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.PhysicalFloat;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.WeakHashMap;


public class CardboardDrillPartData extends MechanicalDrillPartData implements CardboardHeadPartData {

    public CardboardDrillPartData(float attackDamage, Tool toolProperties) {
        super(0.8f, attackDamage, toolProperties);
    }

    @Override
    public void leftClick(FilledToolSlot.SlotId slot, Player entity, ItemStack item, boolean client, PlayerInteractEvent.LeftClickBlock event) {
        super.leftClick(slot, entity, item, client, event);
        doLeftClick(client, event);
    }

    @Override
    public void leftClickEntity(FilledToolSlot.SlotId slot, Player attacker, ItemStack item, LivingEntity target,
                                AttackEntityEvent event, boolean noDurabilityCancel) {
        super.leftClickEntity(slot, attacker, item, target, event, noDurabilityCancel);
        doLeftClickEntity(attacker, item, target, event, 1.7f);
    }
}
