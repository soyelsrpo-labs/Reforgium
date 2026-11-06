
package net.elsrpop.reforgium.quality;

import net.elsrpop.reforgium.Reforgium;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Reforgium.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class QualityCraftingHandler {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        var stack = event.getCrafting();

        if (stack.isEmpty()) return;
        if (QualityHelper.hasQuality(stack)) return;

        EquipmentType type = EquipmentTypeHelper.getType(stack);

        if (type == null) return;

        QualityDefinition quality = QualityRandomizer.getRandomQuality(type);

        if (quality == null) return;

        QualityHelper.setQuality(stack, quality.id());
    }
}
