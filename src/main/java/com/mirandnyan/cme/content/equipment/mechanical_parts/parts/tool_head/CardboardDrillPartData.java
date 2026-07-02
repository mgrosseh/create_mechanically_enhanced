package com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head;

import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


public class CardboardDrillPartData extends MechanicalDrillPartData implements CardboardHeadPartData {

    public CardboardDrillPartData(float attackDamage, Tool toolProperties) {
        super(0.8f, attackDamage, toolProperties);
    }

    @Override
    public void leftClickBlock(FilledToolSlot.SlotId slot, Player entity, ItemStack item, boolean client, PlayerInteractEvent.LeftClickBlock event) {
        super.leftClickBlock(slot, entity, item, client, event);
        doLeftClick(client, event);
    }

    @Override
    public void leftClickEntity(FilledToolSlot.SlotId slot, Player attacker, ItemStack item, LivingEntity target,
                                AttackEntityEvent event, boolean noDurabilityCancel) {
        super.leftClickEntity(slot, attacker, item, target, event, noDurabilityCancel);
        doLeftClickEntity(attacker, item, target, event, 1.7f);
    }
}
