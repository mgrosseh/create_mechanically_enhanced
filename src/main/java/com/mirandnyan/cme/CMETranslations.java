package com.mirandnyan.cme;

import com.mirandnyan.cme.util.LangMap;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.function.Function;

/** AKA Lang */
public class CMETranslations {
    public static final LangEntry CREATIVE_MODE_TAB = new LangEntry("itemGroup", CreateMechanicallyEnhanced.MOD_ID, CreateMechanicallyEnhanced.MOD_NAME);

    public static final LangEntry ANALOG_SCROLL_VALUE = new LangEntry("scroll_value_behaviour.analog_scoll_value", "Analog Value");

    public static final LangEntry TOOL_SLOTS_TITLE = new StyledLangEntry("tool_slots.title", "Parts",
            component -> component.withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
    );
    public static final LangEntry TOOL_SLOTS_NONE = new StyledLangEntry("tool_slots.none", "No parts",
            component -> component.withStyle(ChatFormatting.ITALIC)
    );
    public static final LangEntry TOOL_SLOTS_EMPTY = new LangEntry("tool_slots.empty_slot", "Empty");
    public static final LangEntry TANK_TOOL_SLOT = toolSlot("tank", "Tank");
    public static final LangEntry GEARBOX_TOOL_SLOT = toolSlot("gearbox", "Gearbox");
    public static final LangEntry TIP_TOOL_SLOT = toolSlot("tip", "Tip");
    public static final LangEntry COG_TOOL_SLOT = toolSlot("cog", "Cog");
    public static final LangEntry GRIP_TOOL_SLOT = toolSlot("grip", "Grip");
    public static final LangEntry GEARED_TOP_TOOL_SLOT = toolSlot("geared_top", "Geared Top");

    public static final LangEntry MECHANICAL_TOOL_NO_AIR = tooltip("mechanical_tool.no_air", "No Tank");
    public static final LangEntry MECHANICAL_TOOL_AIR_LEVEL_PRE = tooltip("mechanical_tool.air.prefix", "Air: ");
    public static final LangEntry MECHANICAL_TOOL_AIR_LEVEL_IN = tooltip("mechanical_tool.air.infix", " / ");
    public static final LangEntry MECHANICAL_TOOL_AIR_LEVEL_POST = tooltip("mechanical_tool.air.postfix", "");

    // TODO: separate "Active Bonus" from bonus

    public static final LangEntry MECHANICAL_CAT_ACTIVE_BONUS = new StyledLangEntry("tooltip.mechanical_cat.bonus", "Active Bonus: ",
            component -> component.withStyle(ChatFormatting.DARK_PURPLE));
    public static final LangEntry MECHANICAL_CAT_BONUS_FORTUNE = affixTooltip("mechanical_cat.bonus.fortune", "Fortune");
    public static final LangEntry MECHANICAL_CAT_BONUS_GIFTS = affixTooltip("mechanical_cat.bonus.gifts", "Random Gifts");
    public static final LangEntry MECHANICAL_CAT_BONUS_GLOWING = affixTooltip("mechanical_cat.bonus.glowing", "Glowing!");
    public static final LangEntry MECHANICAL_CAT_BONUS_HASTE = affixTooltip("mechanical_cat.bonus.haste", "Haste");
    public static final LangEntry MECHANICAL_CAT_BONUS_HUNGER_REGEN = affixTooltip("mechanical_cat.bonus.hunger_regen", "Hunger Regen");
    public static final LangEntry MECHANICAL_CAT_BONUS_INTERACTION_RANGE = affixTooltip("mechanical_cat.bonus.interaction_range", "Extra Range");

    public static final LangEntry CAT_COIN_TIER_0 = new StyledLangEntry("cat_coin.tier_0", "For crafting only",
            c -> c.withStyle(ChatFormatting.GRAY));
    public static final LangEntry CAT_COIN_TIER_1 = new StyledLangEntry("cat_coin.tier_1", "Tier 1",
            c -> c.withStyle(ChatFormatting.GRAY));
    public static final LangEntry CAT_COIN_TIER_2 = new StyledLangEntry("cat_coin.tier_2", "Tier 2",
            c -> c.withStyle(ChatFormatting.GRAY));
    public static final LangEntry CAT_COIN_TIER_3 = new StyledLangEntry("cat_coin.tier_3", "Tier 3",
            c -> c.withStyle(ChatFormatting.GRAY));

    public static final LangEntry MECHANICAL_TOOL_DRILL_TYPE = new LangEntry("mechanical_tool.type.drill", " (Drill)");



    protected static LangEntry toolSlot(String key, String textEnglish) {
        return new StyledLangEntry(CreateMechanicallyEnhanced.MOD_ID + ".tool_slot", key, textEnglish,
                component -> component.withStyle(ChatFormatting.BLUE));
    }

    protected static LangEntry affixTooltip(String key, String textEnglish) {
        return new StyledLangEntry(CreateMechanicallyEnhanced.MOD_ID + ".tooltip", key, textEnglish,
                component -> component.withStyle(ChatFormatting.DARK_PURPLE));
    }

    protected static LangEntry tooltip(String key, String textEnglish) {
        return new LangEntry(CreateMechanicallyEnhanced.MOD_ID + ".tooltip", key, textEnglish);
    }

    public static String getKeyOf(RegistryEntry<Item, ? extends Item> item) {
        return "item." + item.getRegisteredName().replace(':', '.');
    }

    public static class Components {
        public static MutableComponent number(int number) {
            return Component.literal(number + "").withStyle(ChatFormatting.GOLD);
        }
        public static MutableComponent item(RegistryEntry<Item, ? extends Item> item) {
            return Component.translatable(getKeyOf(item));
        }

        public static MutableComponent line(Component... components) {
            MutableComponent out = Component.empty();
            for (Component component : components) {
                out.append(component);
            }
            return out;
        }
    }

    public static class StyledLangEntry extends LangEntry {

        Function<MutableComponent, MutableComponent> styler;

        public StyledLangEntry(String prefix, String translationKey, String textEnglish,
                               Function<MutableComponent, MutableComponent> styler) {
            super(prefix, translationKey, textEnglish);
            this.styler = styler;
        }
        public StyledLangEntry(String translationKey, String textEnglish,
                               Function<MutableComponent, MutableComponent> styler) {
            super(translationKey, textEnglish);
            this.styler = styler;
        }

        @Override
        public MutableComponent resolveComponentMutable() {
            return styler.apply(super.resolveComponentMutable());
        }
    }

    public static class LangEntry extends LangMap {
        public LangEntry(String translationKey, String textEnglish) {
            this(CreateMechanicallyEnhanced.MOD_ID, translationKey, textEnglish);
        }
        public LangEntry(String prefix, String translationKey, String textEnglish) {
            super((prefix.isEmpty() ? "" : prefix + ".") + translationKey, textEnglish);
            CreateMechanicallyEnhanced.REGISTRATE.addRawLang(this.translationKey, this.textEnglish);
        }

        public MutableComponent resolveComponentMutable() {
            return Component.translatable(translationKey);
        }
        public Component resolveComponent() {
            return resolveComponentMutable();
        }
        public String resolveString() {
            return Language.getInstance().getOrDefault(translationKey);
        }
    }

    public static void register() {
        // load class for datagen
    }
}
