package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.FilledToolSlot;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.tool_head.MechanicalDrillPartData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Optional;
import java.util.WeakHashMap;

public class SimpleMiningCheckPartData extends MechanicalPartData {
    // TODO: use custom package to e.g. make breaking block work
    // TODO: maybe find system to not double run this if multiple parts all inherit from this
    // TODO: armor stands may have mining animation when holding players tool

    protected SimpleMiningCheckPartData(float weight) {
        super(weight);
    }

    protected static class ClientData {
        public int active;
        static WeakHashMap<String, ClientData> clientData = new WeakHashMap<>();

        public static ClientData of(String name) {
            return clientData.computeIfAbsent(name, s -> new ClientData());
        }

        public static ClientData of(Player player) {
            return of(player.getName().getString());
        }
        public static Optional<ClientData> of(ItemStack stack) {
            var name = stack.get(CMEDataComponents.LAST_TOOL_HOLDER_NAME);
            if (name == null)
                return Optional.empty();
            return Optional.of(of(name));
        }
    }

    public boolean getActive(ItemStack stack, ItemDisplayContext transformType) {
        return switch (transformType) {
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, // can only be mining in someone's hand
                 FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> ClientData.of(stack).map(d -> d.active).orElse(0);
            case NONE, HEAD, GUI, GROUND, FIXED -> 0;
        } > 0;
    }

    @Override
    public void leftClickBlock(FilledToolSlot.SlotId slot, Player entity, ItemStack item, boolean client, PlayerInteractEvent.LeftClickBlock event) {
        super.leftClickBlock(slot, entity, item, client, event);
        if (!client)
            return;
        var data = ClientData.of(entity);
        data.active = 20;
    }


    @Override
    public void leftClickEmpty(FilledToolSlot.SlotId slot, Player entity, ItemStack item, boolean client, PlayerInteractEvent.LeftClickEmpty event) {
        super.leftClickEmpty(slot, entity, item, client, event);
        if (!client)
            return;
        var data = ClientData.of(entity);
        data.active = 20;
    }

    @Override
    public void playerTick(Player player, ItemStack stack) {
        if (!(player.level() instanceof ClientLevel clevel))
            return;

        var data = ClientData.of(player);

        var mining = clevel.levelRenderer.destroyingBlocks.containsKey(player.getId());
        if (mining)
            data.active = 20;

        if (data.active > 0) {
            data.active--;
        }
    }
}
