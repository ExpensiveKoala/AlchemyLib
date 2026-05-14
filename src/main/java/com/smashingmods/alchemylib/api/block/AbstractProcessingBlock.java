package com.smashingmods.alchemylib.api.block;

import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * AbstractProcessingBlock extends BaseEntityBlock to add helpful
 * methods for simplifying creating a Block for a BlockEntity.
 *
 * @see BaseEntityBlock
 */
@SuppressWarnings("unused")
public abstract class AbstractProcessingBlock extends BaseEntityBlock {

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFunction;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    /**
     * The block needs to have a reference to its block entity so that it can return that
     * reference in {@link #newBlockEntity}. The block entity's BlockPos and BlockState can't be known
     * in advance, pass a function that can apply the BlockPos and BlockState at runtime.
     *
     * @param blockEntity takes a BiFunction that requires a BlockPos and BlockState and returns a BlockEntity.
     *
     * @see BlockPos
     * @see BlockState
     * @see BlockEntity
     */
    public AbstractProcessingBlock(BiFunction<BlockPos, BlockState, BlockEntity> blockEntity) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
        blockEntityFunction = blockEntity;
    }

    /**
     * Uses the BlockPlaceContext parameter to determine the opposite facing direction.
     * This means that when you place the block, it's forward face will be looking at you
     * as expected.
     *
     * @param context {@link BlockPlaceContext}
     * @return {@link BlockState}
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * Handles rotation for this block using the {@link BlockStateProperties#HORIZONTAL_FACING FACING} property.
     *
     * @see BaseEntityBlock#rotate(BlockState, LevelAccessor, BlockPos, Rotation)
     */
    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /**
     * Add {@link BlockStateProperties#HORIZONTAL_FACING FACING} to this block's default block state definition.
     *
     * @see BaseEntityBlock#createBlockStateDefinition(StateDefinition.Builder)
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    /**
     * This method is called whenever a block is broken in the world by a player or anything else. The first priority
     * is to make sure that if this block's block entity is an instance of {@link InventoryBlockEntity} that the item contents
     * of its container are dropped into the world and not deleted.
     */

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
                inventoryBlockEntity.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /**
     * This method is called by the game when the block is placed in the world to create its
     * BlockEntity. Apply the {@link BlockPos} and {@link BlockState} parameters to {@link AbstractProcessingBlock#blockEntityFunction blockEntityFunction}.
     *
     * @return a new BlockEntity by applying BlockPos and BlockState to blockEntityFunction.
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFunction.apply(pos, state);
    }
}
