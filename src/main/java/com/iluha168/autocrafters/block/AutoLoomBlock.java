package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoLoomBlockEntity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.iluha168.autocrafters.ServerMod.modId;

public class AutoLoomBlock extends BaseAutoBlock {
    public static final Identifier ID = Identifier.of(modId, "autoloom");

    public static final Block BLOCK = Blocks.register(
        RegistryKey.of(RegistryKeys.BLOCK, ID),
        AutoLoomBlock::new,
        AbstractBlock.Settings.create()
            .strength(1.5f, 3.5f)
            .sounds(BlockSoundGroup.WOOD)
            .requiresTool()
    );

    public static final Item ITEM = Items.register(BLOCK);

    public AutoLoomBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new AutoLoomBlockEntity(pos, state);
    }
}