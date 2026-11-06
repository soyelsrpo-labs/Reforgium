package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.GatherSkippedAttributeTooltipsEvent;

@EventBusSubscriber(modid = Reforgium.MOD_ID)
public class QualityTooltipFilter {

    @SubscribeEvent
    public static void onGatherSkippedAttributeTooltips(
            GatherSkippedAttributeTooltipsEvent event) {

        ItemStack stack = event.getStack();

        if (stack.isEmpty()) return;

        if (QualityHelper.getQuality(stack) == null) return;

        event.setSkipAll(true);
    }
}