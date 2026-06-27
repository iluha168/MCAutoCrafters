package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoCutterBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import static com.iluha168.autocrafters.ServerMod.modId;

public class AutoCutterBlock extends BaseAutoBlock {
    protected static final VoxelShape SHAPE_UP    = Block.createCuboidShape(0, 0, 0, 16, 9, 16);
    protected static final VoxelShape SHAPE_EAST  = Block.createCuboidShape(16-9, 0, 0, 16, 16, 16);
    protected static final VoxelShape SHAPE_WEST  = Block.createCuboidShape(0, 0, 0, 9, 16, 16);
    protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0, 0, 0, 16, 16, 9);
    protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0, 0, 16-9, 16, 16, 16);

    public static final Identifier ID = new Identifier(modId, "autocutter");

    public static final Block BLOCK = Blocks.register(
        ID.toString(),
        new AutoCutterBlock(
            AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY)
            .strength(1.5f, 3.5f)
            .sounds(BlockSoundGroup.STONE)
            .nonOpaque()
        )
    );

    public static final Item ITEM = Items.register(BLOCK);

    public AutoCutterBlock(Settings settings){
        super(settings);
    }

    @Override
    public SoundEvent getCraftSound() {
        return SoundEvents.UI_STONECUTTER_TAKE_RESULT;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoCutterBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(Properties.ORIENTATION)) {
            case EAST_UP, NORTH_UP, SOUTH_UP, WEST_UP -> SHAPE_UP;
            case UP_EAST, DOWN_EAST -> SHAPE_WEST;
            case UP_NORTH, DOWN_NORTH -> SHAPE_SOUTH;
            case UP_SOUTH, DOWN_SOUTH -> SHAPE_NORTH;
            case UP_WEST, DOWN_WEST -> SHAPE_EAST;
        };
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }
}
