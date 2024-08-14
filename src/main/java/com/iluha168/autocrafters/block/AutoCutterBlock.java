package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoCutterBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class AutoCutterBlock extends BaseAutoBlock {
    protected static final VoxelShape SHAPE_UP    = Block.createCuboidShape(0, 0, 0, 16, 9, 16);
    protected static final VoxelShape SHAPE_EAST  = Block.createCuboidShape(16-9, 0, 0, 16, 16, 16);
    protected static final VoxelShape SHAPE_WEST  = Block.createCuboidShape(0, 0, 0, 9, 16, 16);
    protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0, 0, 0, 16, 16, 9);
    protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0, 0, 16-9, 16, 16, 16);

    public static final Block BLOCK = new AutoCutterBlock(
        AbstractBlock.Settings.create()
        .strength(1.5f, 3.5f)
        .sounds(BlockSoundGroup.STONE)
        .requiresTool()
        .nonOpaque()
    );

    public static final BlockItem BLOCK_ITEM = new BlockItem(BLOCK, new Item.Settings());

    public AutoCutterBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoCutterBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(Properties.ORIENTATION)){
            case EAST_UP:
            case NORTH_UP:
            case SOUTH_UP:
            case WEST_UP:
                return SHAPE_UP;
            case UP_EAST:
            case DOWN_EAST:
                return SHAPE_WEST;
            case UP_NORTH:
            case DOWN_NORTH:
                return SHAPE_SOUTH;
            case UP_SOUTH:
            case DOWN_SOUTH:
                return SHAPE_NORTH;
            case UP_WEST:
            case DOWN_WEST:
                return SHAPE_EAST;
            default:
                return null;
        }
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }
}
