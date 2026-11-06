package net.elsrpop.reforgium.registry;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.reforge.block.ReforgeTableBlock;
import net.elsrpop.reforgium.reforge.block.ReforgeTableBlockEntity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(
                    net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE,
                    Reforgium.MOD_ID
            );

    public static final net.neoforged.neoforge.registries.DeferredHolder<
            BlockEntityType<?>, BlockEntityType<ReforgeTableBlockEntity>
            > REFORGE_TABLE = BLOCK_ENTITY_TYPES.register(
            "reforging_station",
            () -> BlockEntityType.Builder.of(
                    ReforgeTableBlockEntity::new,
                    ModBlocks.REFORGE_TABLE.get()
            ).build(null)
    );

    private ModBlockEntities() {
    }
}