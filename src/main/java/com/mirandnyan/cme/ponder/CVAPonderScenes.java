package com.mirandnyan.cme.ponder;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CVAPonderScenes {

    public static void register(final PonderSceneRegistrationHelper<ResourceLocation> registry) {
        final PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> helper =
                registry.withKeyFunction(DeferredHolder::getId);

//        helper.forComponents(CVA.Blocks.BLOCK)
//                .addStoryBoard("analog_gate", AnalogGateScenes.SCENE::run);
    }
}
