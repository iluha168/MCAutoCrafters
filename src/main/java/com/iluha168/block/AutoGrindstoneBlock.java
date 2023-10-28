package com.iluha168.block;

import com.iluha168.block_entity.AutoGrindstoneBlockEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AutoGrindstoneBlock extends BaseAutoBlock {
    public static final Block BLOCK = new AutoGrindstoneBlock(
        FabricBlockSettings.create()
        .strength(1.5f, 3.5f)
        .sounds(BlockSoundGroup.STONE)
        .nonOpaque()
        .requiresTool()
    );

    public static final BlockItem BLOCK_ITEM = new BlockItem(BLOCK,
        new FabricItemSettings()
    );

    public AutoGrindstoneBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoGrindstoneBlockEntity(pos, state);
    }

    @Override
    public SoundEvent getCraftSound() {
        return SoundEvents.BLOCK_GRINDSTONE_USE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.125f,0.125f,0.125f, 0.875f,0.875f,0.875f);
    }
}
