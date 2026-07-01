package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head;

import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.tool.CardboardSwordItem;
import com.simibubi.create.content.equipment.tool.KnockbackPacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public interface CardboardHeadPartData {
    float KNOCKUP_BASE = 0.2f;

    default void doLeftClick(boolean client, PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START)
            return;
        if (client)
            AllSoundEvents.CARDBOARD_SWORD.playAt(event.getLevel(), event.getPos(), 0.5f, 1.85f, false);
        else
            AllSoundEvents.CARDBOARD_SWORD.play(event.getLevel(), event.getEntity(), event.getPos(), 0.5f, 1.85f);
    }


    default void doLeftClickEntity(Player attacker, ItemStack item, LivingEntity target,
                                   AttackEntityEvent event, float knockback) {
        doLeftClickEntity(attacker, item, target, event, knockback, 0);
    }

    default void doLeftClickEntity(Player attacker, ItemStack item, LivingEntity target,
                                   AttackEntityEvent event, float knockback, float knockup) {
        // adaptation of Create's CardboardSwordItem.cardboardSwordsCannotHurtYou adding mechanic to knock targets upwards

        if (target.getType().is(EntityTypeTags.ARTHROPOD))
            return;

        AllSoundEvents.CARDBOARD_SWORD.playFrom(attacker, 0.75f, 1.85f);

        event.setCanceled(true);

        // Reference player.attack()
        // This section replicates knockback behavior without hurting the target

        float knockbackStrength = (float) (attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + knockback);
        if (attacker.level() instanceof ServerLevel serverLevel)
            knockbackStrength = EnchantmentHelper.modifyKnockback(serverLevel, item, target, serverLevel.damageSources().playerAttack(attacker), knockbackStrength);
        if (attacker.isSprinting() && attacker.getAttackStrengthScale(0.5f) > 0.9f)
            ++knockbackStrength;

        if (knockbackStrength <= 0)
            return;

        float yRot = attacker.getYRot();
        CardboardSwordItem.knockback(target, knockbackStrength, yRot);

        boolean targetIsPlayer = target instanceof Player;
        MobCategory targetType = target.getClassification(false);

        if (target instanceof ServerPlayer sp)
            CatnipServices.NETWORK.sendToClient(sp, new KnockbackPacket(yRot, knockbackStrength));

        if ((targetType == MobCategory.MISC || targetType == MobCategory.CREATURE) && !targetIsPlayer)
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 9, true, false, false));

        target.setDeltaMovement(target.getDeltaMovement().add(0, knockup * KNOCKUP_BASE, 0));

        attacker.setDeltaMovement(attacker.getDeltaMovement()
                .multiply(0.6D, 1.0D, 0.6D));
        attacker.setSprinting(false);
    }
}
