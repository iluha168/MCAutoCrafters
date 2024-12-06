package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoFletchingTableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.iluha168.autocrafters.ServerMod.modId;

public class AutoFletchingTableBlock extends BaseAutoBlock {
    public static final Identifier ID = Identifier.of(modId, "autofletching");

    public static final Block BLOCK = Blocks.register(
        RegistryKey.of(RegistryKeys.BLOCK, ID),
        AutoFletchingTableBlock::new,
        Blocks.CRAFTER.getSettings().sounds(BlockSoundGroup.WOOD)
    );

    public AutoFletchingTableBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoFletchingTableBlockEntity(pos, state);
    }
}
