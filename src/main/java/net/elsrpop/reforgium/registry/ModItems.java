
package net.elsrpop.reforgium.registry;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.reforge.item.ReforgeHammerItem;
import net.elsrpop.reforgium.reforge.item.ReforgeTableItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Reforgium.MOD_ID);

    public static final DeferredItem<Item> REFORGING_MATERIAL_1 =
            ITEMS.register("reforging_material_1",
                    () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> REFORGING_MATERIAL_2 =
            ITEMS.register("reforging_material_2",
                    () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> REFORGING_MATERIAL_3 =
            ITEMS.register("reforging_material_3",
                    () -> new Item(new Item.Properties()));

    public static final DeferredItem<ReforgeHammerItem> REFORGING_HAMMER =
            ITEMS.register("reforging_hammer", ReforgeHammerItem::new);

    public static final DeferredItem<ReforgeTableItem> REFORGE_TABLE_ITEM =
            ITEMS.register("reforging_station",
                    () -> new ReforgeTableItem(
                            ModBlocks.REFORGE_TABLE.get(),
                            new Item.Properties()
                    ));
}
