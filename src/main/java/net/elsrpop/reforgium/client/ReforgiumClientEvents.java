package net.elsrpop.reforgium.client;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.client.screen.ReforgeScreen;
import net.elsrpop.reforgium.registry.ModMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(
        modid = Reforgium.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD
)
public final class ReforgiumClientEvents {

    private ReforgiumClientEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(
                ModMenus.REFORGE_MENU.get(),
                ReforgeScreen::new
        );
    }
}