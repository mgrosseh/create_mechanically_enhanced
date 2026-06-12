package com.mirandnyan.cme;

import com.mirandnyan.cme.ponder.CVAPonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CreateMechanicallyEnhanced.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateMechanicallyEnhanced.MOD_ID, value = Dist.CLIENT)
public class CMEClient {
    //public static final RenderHandler EXAMPLE_RENDER_HANDLER = new RenderHandler() {};
    // check ClientEvents

    public CMEClient(final IEventBus modEventBus, final ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the old_en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        registerRenderHandlers(modEventBus);

        PonderIndex.addPlugin(new CVAPonderPlugin());
    }

    public void registerRenderHandlers(IEventBus modEventBus) {
        //EXAMPLE_RENDER_HANDLER.registerListeners(modEventBus);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CreateMechanicallyEnhanced.LOGGER.info("HELLO FROM CLIENT SETUP Create Various Additions");
    }
}
