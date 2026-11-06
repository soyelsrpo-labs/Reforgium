package net.elsrpop.reforgium.registry;

import net.elsrpop.reforgium.Reforgium;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, Reforgium.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> JUMP_HEIGHT =
            ATTRIBUTES.register("jump_height", () ->
                    new RangedAttribute(
                            "attribute.reforgium.jump_height",
                            0.0D,
                            -1024.0D,
                            1024.0D
                    ).setSyncable(true)
            );
}