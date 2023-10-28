package com.iluha168.block;

import com.iluha168.block_entity.AutoLoomBlockEntity;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class AutoLoomBlock extends BaseAutoBlock {
    public static final Block BLOCK = new AutoLoomBlock(
        FabricBlockSettings.create()
        .strength(1.5f, 3.5f)
        .sounds(BlockSoundGroup.WOOD)
        .requiresTool()
    );

    public static final BlockItem BLOCK_ITEM = new BlockItem(BLOCK,
        new FabricItemSettings()
    );

    public AutoLoomBlock(Settings settings){
        super(settings);
    }

    @Override
    public SoundEvent getCraftSound() {
        return SoundEvents.UI_LOOM_TAKE_RESULT;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new AutoLoomBlockEntity(pos, state);
    }
}