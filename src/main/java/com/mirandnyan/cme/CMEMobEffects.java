package com.mirandnyan.cme;

import com.mirandnyan.cme.content.equipment.mechanical_parts.parts.automaton.mechanical_cat.HungerRegenerationMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.function.Supplier;

public class CMEMobEffects {
    public static DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(
            BuiltInRegistries.MOB_EFFECT, CreateMechanicallyEnhanced.MOD_ID
    );

    public static HashMap<Holder<MobEffect>, CMETranslations.LangEntry> translations = new HashMap<>();

    public static final Holder<MobEffect> RANGE_BLESSING = register(
            "range_blessing", "Range Blessing",
            () -> new MyMobEffect(MobEffectCategory.BENEFICIAL, 3402751) // TODO color
                    .addAttributeModifier(
                            Attributes.BLOCK_INTERACTION_RANGE, CreateMechanicallyEnhanced.asResource("effect.range_blessing"),
                            0.2F, AttributeModifier.Operation.ADD_VALUE
                    )
    );
    public static final Holder<MobEffect> HUNGER_REGENERATION = register(
            "hunger_regeneration", "Hunger Regeneration",
            () -> new HungerRegenerationMobEffect(MobEffectCategory.BENEFICIAL, 3402751) // TODO color
    );


    private static Holder<MobEffect> register(String name, String lang, Supplier<MobEffect> effect) {
        var holder = REGISTRY.register(name, effect);
        translations.put(
                holder,
                new CMETranslations.LangEntry("effect." + CreateMechanicallyEnhanced.MOD_ID, name, lang)
        );
        return holder;
    }

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }

    public static class MyMobEffect extends MobEffect {
        public MyMobEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
