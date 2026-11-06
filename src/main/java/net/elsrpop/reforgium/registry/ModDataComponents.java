package net.elsrpop.reforgium.registry;

import com.mojang.serialization.Codec;
import net.elsrpop.reforgium.Reforgium;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(
                    Reforgium.MOD_ID
            );

    public static final Supplier<DataComponentType<String>> QUALITY =

            DATA_COMPONENTS.registerComponentType(
                    "quality",
                    builder -> builder.persistent(Codec.STRING)
            );

    public static final Supplier<DataComponentType<Integer>> ORIGINAL_MAX_DAMAGE =
            DATA_COMPONENTS.registerComponentType(
                    "original_max_damage",
                    builder -> builder.persistent(Codec.INT)
            );
}