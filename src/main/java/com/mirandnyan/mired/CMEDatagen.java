package com.mirandnyan.mired;

import com.mirandnyan.mired.ponder.CVAPonderPlugin;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CMEDatagen {

    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (!event.getMods().contains(CreateMechanicallyEnhanced.MOD_ID))
            return;
        addExtraRegistrateData();
    }

    private static void addExtraRegistrateData() {
        CMETags.addGenerators();

        CreateMechanicallyEnhanced.getRegistrate().addDataGenerator(ProviderType.LANG, provider -> {
            // Register this since FMLClientSetupEvent does not run during datagen
            PonderIndex.addPlugin(new CVAPonderPlugin());

            CMETooltips.register(provider::add);

            PonderIndex.getLangAccess().provideLang(CreateMechanicallyEnhanced.MOD_ID, provider::add);
        });
    }
}
