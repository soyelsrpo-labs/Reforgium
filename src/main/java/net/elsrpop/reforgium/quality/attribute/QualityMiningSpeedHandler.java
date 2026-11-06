
package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber(
        modid = Reforgium.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public class QualityMiningSpeedHandler {

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        double bonus = getMiningSpeedBonus(stack);

        if (bonus <= 0.0) {
            return;
        }

        event.setNewSpeed((float) (event.getNewSpeed() * (1.0 + bonus)));
    }

    public static double getMiningSpeedBonus(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0;
        }

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) {
            return 0.0;
        }

        for (QualityModifier modifier : quality.modifiers()) {
            if (modifier.attribute() == QualityAttribute.MINING_SPEED) {
                return modifier.percentage()
                        ? modifier.value() / 100.0
                        : modifier.value();
            }
        }

        return 0.0;
    }
}
