package net.elsrpop.reforgium;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.elsrpop.reforgium.quality.QualityRegistry;
import net.elsrpop.reforgium.reforge.ReforgeMaterialRegistry;
import net.elsrpop.reforgium.registry.ModBlocks;
import net.elsrpop.reforgium.registry.ModDataComponents;
import net.elsrpop.reforgium.registry.ModItems;
import net.elsrpop.reforgium.registry.ModLootModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.elsrpop.reforgium.registry.ModBlockEntities;
import net.elsrpop.reforgium.registry.ModMenus;
import net.elsrpop.reforgium.registry.ModAttributes;

@Mod(Reforgium.MOD_ID)
public class Reforgium {

    public static final String MOD_ID = "reforgium";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Reforgium(IEventBus modEventBus) {

        ModItems.ITEMS.register(modEventBus);

        ModBlocks.BLOCKS.register(modEventBus);

        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);

        ModMenus.MENUS.register(modEventBus);

        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        ModLootModifiers.GLOBAL_LOOT_MODIFIERS.register(modEventBus);

        ModAttributes.ATTRIBUTES.register(modEventBus);

        QualityRegistry.registerAll();

        ReforgeMaterialRegistry.register(ModItems.REFORGING_MATERIAL_1.getId(), 1);
        ReforgeMaterialRegistry.register(ModItems.REFORGING_MATERIAL_2.getId(), 2);
        ReforgeMaterialRegistry.register(ModItems.REFORGING_MATERIAL_3.getId(), 3);
    }
}