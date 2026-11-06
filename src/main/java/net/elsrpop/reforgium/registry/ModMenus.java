package net.elsrpop.reforgium.registry;

import net.elsrpop.reforgium.Reforgium;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.elsrpop.reforgium.reforge.menu.ReforgeMenu;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    net.minecraft.core.registries.Registries.MENU,
                    Reforgium.MOD_ID
            );

    public static final net.neoforged.neoforge.registries.DeferredHolder<
            MenuType<?>, MenuType<ReforgeMenu>
            > REFORGE_MENU = MENUS.register(
            "reforging_station",
            () -> IMenuTypeExtension.create(ReforgeMenu::new)
    );

    private ModMenus() {
    }
}