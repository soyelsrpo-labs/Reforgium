package net.elsrpop.reforgium.registry;

import com.mojang.serialization.MapCodec;
import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.QualityContainerLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
                    Reforgium.MOD_ID
            );

    public static final Supplier<MapCodec<QualityContainerLootModifier>> QUALITY_CONTAINER =
            GLOBAL_LOOT_MODIFIERS.register(
                    "quality_container",
                    () -> QualityContainerLootModifier.CODEC
            );
}