package net.elsrpop.reforgium.registry;

import net.elsrpop.reforgium.Reforgium;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(
        modid = Reforgium.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD
)
public class ModAttributeEvents {

    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {

        event.add(
                EntityType.PLAYER,
                ModAttributes.JUMP_HEIGHT
        );
    }
}