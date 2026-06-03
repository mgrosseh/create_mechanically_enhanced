package com.mirandnyan.mired;

import com.mirandnyan.mired.ponder.CVAPonderPlugin;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.tags.TagBuilder;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CVADatagen {

    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (!event.getMods().contains(CreateVariousAdditions.MOD_ID))
            return;
        addExtraRegistrateData();
    }

    private static void addExtraRegistrateData() {
        CVATags.addGenerators();

        CreateVariousAdditions.getRegistrate().addDataGenerator(ProviderType.LANG, provider -> {
            // Register this since FMLClientSetupEvent does not run during datagen
            PonderIndex.addPlugin(new CVAPonderPlugin());

            CVATooltips.register(provider::add);

            PonderIndex.getLangAccess().provideLang(CreateVariousAdditions.MOD_ID, provider::add);
        });
    }
}
