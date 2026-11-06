package net.elsrpop.reforgium.registry;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.reforge.block.ReforgeTableBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Reforgium.MOD_ID);

    public static final DeferredBlock<ReforgeTableBlock> REFORGE_TABLE =
            BLOCKS.register(
                    "reforging_station",
                    () -> new ReforgeTableBlock()
            );

    private ModBlocks() {
    }
}