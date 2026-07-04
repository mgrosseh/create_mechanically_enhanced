package com.mirandnyan.cme;

import com.mirandnyan.cme.content.blocks.part_crafter.PartCrafterMenu;
import com.mirandnyan.cme.content.blocks.part_crafter.PartCrafterScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

public class CMEMenus {
    public static final MenuEntry<PartCrafterMenu> PART_CRAFTER = REGISTRATE.object("part_crafter")
            .menu((type, windowId, inv) -> new PartCrafterMenu(type, windowId, inv), () -> PartCrafterScreen::new)
            .register();

    public static void register() {
        // load class
    }
}
