package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoFletchingTableBlockEntity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.iluha168.autocrafters.ServerMod.modId;

public class AutoFletchingTableBlock extends BaseAutoBlock {
    public static final Identifier ID = new Identifier(modId, "autofletching");

    public static final Block BLOCK = Blocks.register(
        ID.toString(),
        new AutoFletchingTableBlock(
            AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY)
            .strength(1.5f, 3.5f)
            .sounds(BlockSoundGroup.WOOD)
        )
    );

    public AutoFletchingTableBlock(Settings settings){
        super(settings);
    }

    @Override
    public SoundEvent getCraftSound() {
        // The fletching table is an easter egg that never produces output, so this
        // sound is never actually played; it only satisfies the abstract contract.
        return SoundEvents.UI_LOOM_TAKE_RESULT;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoFletchingTableBlockEntity(pos, state);
    }
}
