package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoFletchingTableBlockEntity;
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

public class AutoFletchingTableBlock extends BaseAutoBlock {
    public static final MapCodec<AutoFletchingTableBlock> CODEC = createCodec(AutoFletchingTableBlock::new);

    public static final Block BLOCK = new AutoFletchingTableBlock(
        AbstractBlock.Settings.create()
        .strength(1.5f, 3.5f)
        .sounds(BlockSoundGroup.WOOD)
        .requiresTool()
    );

    public static final BlockItem BLOCK_ITEM = new BlockItem(BLOCK, new Item.Settings());

    public AutoFletchingTableBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoFletchingTableBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}
