package com.mirandnyan.mired;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.mirandnyan.mired.CreateVariousAdditions.MOD_ID;

public class MiredCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
        CREATIVE_MODE_TABS.register("mired_tab",
                () -> CreativeModeTab.builder()
                        .title(CVATranslations.CREATIVE_MODE_TAB.resolveComponent())
                        .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
//                        .icon(() -> CVABlocks.getItem(CVABlocks.BRASS_ENCASED_REDSTONE).asStack())
//                        .displayItems((parameters, output) -> {
//                            output.accept(CVABlocks.getItem(CVABlocks.ANDESITE_ENCASED_REDSTONE));
//                            output.accept(CVABlocks.getItem(CVABlocks.COPPER_ENCASED_REDSTONE));
//                            output.accept(CVABlocks.getItem(CVABlocks.BRASS_ENCASED_REDSTONE));
//                            output.accept(CVABlocks.getItem(CVABlocks.ANALOG_INVERTER_BLOCK));
//                            output.accept(CVABlocks.getItem(CVABlocks.ANALOG_SR_LATCH_BLOCK));
//                            output.accept(CVABlocks.getItem(CVABlocks.COMPUTATOR));
//                            output.accept(CVABlocks.getItem(CVABlocks.ANALOG_GATE_BLOCK));
//                            output.accept(CVABlocks.getItem(CVABlocks.MEASURING_REDSTONE_LINK));
//                            output.accept(MiredItems.COMPUTATION_CIRCUIT);
//                            output.accept(MiredItems.DIODE_BASE);
//                        })
                        .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
