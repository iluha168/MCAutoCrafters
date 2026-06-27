package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoGrindstoneBlockEntity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.iluha168.autocrafters.ServerMod.modId;

public class AutoGrindstoneBlock extends BaseAutoBlock {
    public static final Identifier ID = new Identifier(modId, "autogrindstone");

    public static final Block BLOCK = Blocks.register(
        ID.toString(),
        new AutoGrindstoneBlock(
            AbstractBlock.Settings.create()
            .strength(1.5f, 3.5f)
            .sounds(BlockSoundGroup.STONE)
            .requiresTool()
        )
    );

    public static final Item ITEM = Items.register(BLOCK);

    public AutoGrindstoneBlock(Settings settings){
        super(settings);
    }

    @Override
    public SoundEvent getCraftSound() {
        return SoundEvents.BLOCK_GRINDSTONE_USE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoGrindstoneBlockEntity(pos, state);
    }
}
