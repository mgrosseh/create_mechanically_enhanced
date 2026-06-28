package com.mirandnyan.cme.content.equipment.mechanical_parts.parts;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPartData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.WeakHashMap;

public class SimpleMiningCheckPartData extends MechanicalPartData {
    // TODO: mining is considered happening when switching tools
    // TODO: armor stands may have mining animation when holding players tool

    protected SimpleMiningCheckPartData(float weight) {
        super(weight);
    }

    protected static class ClientData {
        public int lastAir = 0;
        public int lastDamage = 0;
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
    public void playerTick(Player player, ItemStack stack) {
        //noinspection DuplicatedCode
        if (!(player.level() instanceof ClientLevel clevel))
            return;

        var data = ClientData.of(player);


        var air = stack.getOrDefault(CMEDataComponents.PRESSURIZED_AIR, 0);
        var damage = stack.getOrDefault(DataComponents.DAMAGE, 0);
        var mining = clevel.levelRenderer.destroyingBlocks.containsKey(player.getId())
                || air < data.lastAir
                || damage > data.lastDamage;
        data.lastAir = air;
        data.lastDamage = damage;
        if (mining)
            data.active = 20;

        if (data.active > 0) {
            data.active--;
        }
    }
}
