package com.mirandnyan.cme;

import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalPart;
import com.mirandnyan.cme.content.equipment.mechanical_parts.MechanicalToolSlot;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(CreateMechanicallyEnhanced.MOD_ID)
public class CreateMechanicallyEnhanced {
    /*
    TODO:
    Maybe: Add way to load model with groups / parts into PartialModels each representing a group / part,
           to be able to have a single that can be rendered individually.

    Scanner: Scan mobs to be able to create mechanic versions of them

    Cat coins:
    - Part Trading villager (takes cat coins)
    - Coin Stack Block

    TODO: make cardboard tools obtainable with paper and wood or something for early game
    TODO: leftClick
    # TODO Registry of tool actions
    - grip handles triggering these
    - cardboard material action: changes tool with data if any part is cardboard
    - netherite material action: if any part is netherite, make unburnable
    - nether star material action: if any part is nether star, make unexplodable

    # remodels:
    - pumpkin automaton
    - blasting refiner may look too big, needs particles instead of bad fire graphic
    - grips

    # Mechanical Tool
    BUG: refilling air can cause use anim in offhand item (which isn't a tool)

    add slot tag system

    # Mechanical Parts:
    ## blasting refiner
    - add particles

    ## conduit
    - on R click boost forward

    ## mechanical cat:
    TODO:
    - range blessing broken

    ## Mechanical Chicken
    Gives slow falling
    Can eat grass to refill 1 hunger
    Occasionally drops wooden nuggets, craft 4 of them into a plank
    place down wooden nuggets, look like small dragon egg

    easy to make

    ## Mechanical Mooshroom
    give potion; it then gives arrows that potion effect
    maybe: apply short duration of effect on hit on entities

    ## Potion Launcher
    potion liquid into tank then as fuel
    launches potion effects

    addons:
    - lingering addon
    - slowly refill as long as not empty
    - chance for random effect?
    - mods to strenght, time, range, throw speed etc

    ## Guardian Beamer
    Beam attack enemies

    ## mechanical slime

    ## misc
    - wind charge launcher / also wind charge close range detonator (double jump but also down and sideways)
     */

    public static final String MOD_ID = "create_mechanically_enhanced";
    public static final String MOD_NAME = "Create: Mechanically Enhanced";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static IEventBus modEventBus;
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    public CreateMechanicallyEnhanced(IEventBus eventBus, ModContainer modContainer) {
        modEventBus = eventBus;
        REGISTRATE.registerEventListeners(modEventBus);

        REGISTRATE.setCreativeTab(CMEItems.MAIN);

        CMEAttributes.register(modEventBus);
        CMEBlocks.register();
        CMEItems.register(modEventBus);
        CMEDataComponents.register(modEventBus);

        CMETranslations.register();
        CMEMaterials.register();
        CMEMechanicalParts.register();
        MechanicalToolSlot.register();
        CMERecipeTypes.register(modEventBus);
        CMEMobEffects.register(eventBus);

        modEventBus.addListener(EventPriority.HIGHEST, CMEDatagen::gatherDataHighPriority);
        modEventBus.addListener(EventPriority.LOWEST, CMEDatagen::gatherData);
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }

    public static ResourceLocation asResource(String... pathParts) {
        return asResource(String.join("/", pathParts));

    }
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
