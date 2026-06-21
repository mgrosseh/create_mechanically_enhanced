package com.mirandnyan.cme;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mirandnyan.cme.CreateMechanicallyEnhanced.REGISTRATE;

@EventBusSubscriber
public class CMEAttributes {

    public static DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(
            BuiltInRegistries.ATTRIBUTE, CreateMechanicallyEnhanced.MOD_ID
    );

    public static HashMap<Holder<Attribute>, CMETranslations.LangEntry> translations = new HashMap<>();

    public static DeferredHolder<Attribute, Attribute> COYOTE_TIME_ATTRIBUTE = register("player.coyote_time", "Coyote Time",
            s -> new RangedAttribute(s, 0, 0, 60).setSyncable(true));




    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, CMEAttributes.COYOTE_TIME_ATTRIBUTE, 0.0);
    }


    private static DeferredHolder<Attribute, Attribute> register(String name, String lang, Function<String, Attribute> attribute) {
        var entry = new CMETranslations.LangEntry("attribute." + CreateMechanicallyEnhanced.MOD_ID, name, lang);
        var holder = REGISTRY.register(name, () -> attribute.apply(entry.translationKey));
        translations.put(
                holder,
                entry
        );
        return holder;
    }


    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }

}
