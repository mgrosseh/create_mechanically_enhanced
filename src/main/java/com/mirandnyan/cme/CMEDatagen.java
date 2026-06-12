package com.mirandnyan.cme;

import com.mirandnyan.cme.recipes.coin_minting.CMECatCoinMintingRecipeGen;
import com.mirandnyan.cme.ponder.CVAPonderPlugin;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CMEDatagen {

    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (!event.getMods().contains(CreateMechanicallyEnhanced.MOD_ID))
            return;
        addExtraRegistrateData();
    }

    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreateMechanicallyEnhanced.MOD_ID))
            return;

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();


        if (event.includeServer()) {
            registerRecipes(generator, output, lookupProvider);
        }
    }

    static final List<ProcessingRecipeGen<?, ?, ?>> GENERATORS = new ArrayList<>();
    private static void registerRecipes(DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {

        GENERATORS.add(new CMECatCoinMintingRecipeGen(output, registries));

        generator.addProvider(true, new DataProvider() {

            @Override
            public @NotNull String getName() {
                return "Create Mechanically Enhanced Processing Recipes";
            }

            @Override
            public @NotNull CompletableFuture<?> run(@NotNull CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }

    private static void addExtraRegistrateData() {
        CMETags.addGenerators();

        CreateMechanicallyEnhanced.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            // Register this since FMLClientSetupEvent does not run during datagen
            PonderIndex.addPlugin(new CVAPonderPlugin());

            CMETooltips.register(provider::add);

            PonderIndex.getLangAccess().provideLang(CreateMechanicallyEnhanced.MOD_ID, provider::add);
        });
    }
}
