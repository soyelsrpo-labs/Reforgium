
package net.elsrpop.reforgium.client;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.client.screen.ReforgeScreen;
import net.elsrpop.reforgium.client.renderer.ReforgeTableRenderer;
import net.elsrpop.reforgium.registry.ModMenus;
import net.elsrpop.reforgium.registry.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

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

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.REFORGE_TABLE.get(),
                ReforgeTableRenderer::new
        );
    }
}
