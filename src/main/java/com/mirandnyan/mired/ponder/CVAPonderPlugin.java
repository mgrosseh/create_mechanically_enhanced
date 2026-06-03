package com.mirandnyan.mired.ponder;

import com.mirandnyan.mired.CreateMechanicallyEnhanced;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.IndexExclusionHelper;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CVAPonderPlugin extends CreatePonderPlugin {

    @Override
    public @NotNull String getModId() {
        return CreateMechanicallyEnhanced.MOD_ID;
    }

    @Override
    public void registerScenes(final @NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CVAPonderScenes.register(helper);
    }

    @Override
    public void registerTags(final @NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        CVAPonderTags.register(helper);
    }

    @Override
    public void registerSharedText(@NotNull final SharedTextRegistrationHelper helper) {
        //helper.registerSharedText("key", "text");
    }

    @Override
    public void onPonderLevelRestore(final @NotNull PonderLevel ponderLevel) {}

    @Override
    public void indexExclusions(@NotNull IndexExclusionHelper helper) {}
}
