package net.elsrpop.reforgium.reforge.block;

import com.mojang.serialization.MapCodec;
import net.elsrpop.reforgium.reforge.menu.ReforgeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReforgeTableBlock extends BaseEntityBlock {

    public static final MapCodec<ReforgeTableBlock> CODEC =
            simpleCodec(ReforgeTableBlock::new);

    public static final DirectionProperty FACING =
            net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

    public ReforgeTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public ReforgeTableBlock() {
        // Se puede romper y dropear con la mano. Al estar en los tags
        // mineable/pickaxe y mineable/axe, con pico o hacha se rompe más rápido.
        this(BlockBehaviour.Properties.of()
                .strength(4.0F)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
                FACING,
                context.getHorizontalDirection().getOpposite()
        );
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReforgeTableBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getOcclusionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return Shapes.empty();
    }

    // Al romper el bloque (o si es reemplazado, por ejemplo por una explosión)
    // se sueltan los 4 slots: objeto, materiales y martillo.
    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ReforgeTableBlockEntity table) {
                Containers.dropContents(level, pos, table);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof ReforgeTableBlockEntity table
                && player instanceof ServerPlayer serverPlayer) {

            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("block.reforgium.reforging_station");
                }

                @Override
                public ReforgeMenu createMenu(
                        int containerId,
                        net.minecraft.world.entity.player.Inventory inventory,
                        Player player
                ) {
                    return new ReforgeMenu(containerId, inventory, table);
                }
            };

            serverPlayer.openMenu(provider, pos);
        }

        return InteractionResult.CONSUME;
    }
}