package com.mirandnyan.mired;

import com.mirandnyan.mired.util.LangMap;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

/** AKA Lang */
public class CVATranslations {
    public static final LangEntry CREATIVE_MODE_TAB = new LangEntry("itemGroup", CreateVariousAdditions.MOD_ID, CreateVariousAdditions.MOD_NAME);

    public static final LangEntry ANALOG_SCROLL_VALUE = new LangEntry("scroll_value_behaviour.analog_scoll_value", "Analog Value");


    public static class LangEntry extends LangMap {
        public LangEntry(String translationKey, String textEnglish) {
            this(CreateVariousAdditions.MOD_ID, translationKey, textEnglish);
        }
        public LangEntry(String prefix, String translationKey, String textEnglish) {
            super((prefix.isEmpty() ? "" : prefix + ".") + translationKey, textEnglish);
            CreateVariousAdditions.getRegistrate().addRawLang(this.translationKey, this.textEnglish);
        }

        public Component resolveComponent() {
            return Component.translatable(translationKey);
        }
        public String resolveString() {
            return Language.getInstance().getOrDefault(translationKey);
        }
    }

    public static void register() {
        // load class for datagen
    }
}
