package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoLoomBlockEntity;
import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;

public class AutoLoomBlock extends BaseAutoBlock {
    public static final MapCodec<AutoLoomBlock> CODEC = createCodec(AutoLoomBlock::new);

    public static final Block BLOCK = new AutoLoomBlock(
        AbstractBlock.Settings.create()
        .strength(1.5f, 3.5f)
        .sounds(BlockSoundGroup.WOOD)
        .requiresTool()
    );

    public static final BlockItem BLOCK_ITEM = new BlockItem(BLOCK, new Item.Settings());

    public AutoLoomBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new AutoLoomBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}