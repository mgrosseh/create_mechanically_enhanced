package com.mirandnyan.mired.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static net.createmod.ponder.PonderClient.isGameActive;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onTickPre(ClientTickEvent.Pre event) {
        onTick(true);
    }

    @SubscribeEvent
    public static void onTickPost(ClientTickEvent.Post event) {
        onTick(false);
    }

    public static void onTick(boolean isPreEvent) {
        if (!isGameActive())
            return;

        Level world = Minecraft.getInstance().level;
        if (isPreEvent)
            return;

        //CVAClient.EXAMPLE_RENDER_HANDLER.tick();
    }
}
