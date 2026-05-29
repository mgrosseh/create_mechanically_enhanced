package com.mirandnyan.mired;

import com.mirandnyan.mired.util.LangMap;
import com.tterrag.registrate.util.entry.BlockEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class CVATooltips {
    static List<TooltipEntry> tooltips = new ArrayList<>();


//    public static final TooltipEntry ANALOG_INVERTER = new TooltipBuilder(CVABlocks.ANALOG_INVERTER_BLOCK)
//            .summary("Inverts the input signal. Inverted here means 15 - _Input Signal_.")
//            .register();


    public static class TooltipEntry {
        List<LangMap> maps;
        public TooltipEntry() {
            maps = new ArrayList<>();
        }
    }

    public static class TooltipBuilder {
        TooltipEntry entry;
        String key;
        int conditions;
        int behaviours;

        public TooltipBuilder(String key) {
            this.entry = new TooltipEntry();
            this.key = key;
            this.conditions = 0;
            this.behaviours = 0;
        }

        public TooltipBuilder(BlockEntry<?> block) {
            this(getBlockName(block));
        }

        public TooltipBuilder summary(String text) {
            entry.maps.add(new LangMap(key + ".tooltip.summary", text));
            return this;
        }
        public TooltipConditionBuilder condition(String condition) {
            return new TooltipConditionBuilder(this, condition);
        }

        public TooltipEntry register() {
            tooltips.add(entry);
            return entry;
        }

        void addCondition(String condition, String behaviour) {
            entry.maps.add(new LangMap(key + ".tooltip.condition" + ++conditions, condition));
            entry.maps.add(new LangMap(key + ".tooltip.behaviour" + ++behaviours, behaviour));
        }

        static String getBlockName(BlockEntry<?> block) {
            var namespacedName = block.getRegisteredName();
            // Form: <namespace>:<name>
            if (namespacedName.equals("[unregistered]"))
                throw new RuntimeException("Trying to use unregistered block: " + block);
            return "block." + namespacedName.replace(':', '.');
        }
    }

    public static class TooltipConditionBuilder {
        TooltipBuilder parent;
        String condition;
        public TooltipConditionBuilder(TooltipBuilder parent, String text) {
            this.parent = parent;
            this.condition = text;
        }

        public TooltipBuilder behaviour(String text) {
            parent.addCondition(condition, text);
            return parent;
        }
    }

    public static void register(BiConsumer<String, String> consumer) {
        for (TooltipEntry tooltip : tooltips) {
            for (LangMap lang : tooltip.maps) {
                consumer.accept(lang.translationKey, lang.textEnglish);
            }
        }
    }
}
