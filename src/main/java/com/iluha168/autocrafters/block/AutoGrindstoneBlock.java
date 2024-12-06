package com.iluha168.autocrafters.block;

import com.iluha168.autocrafters.block_entity.AutoGrindstoneBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.iluha168.autocrafters.ServerMod.modId;

public class AutoGrindstoneBlock extends BaseAutoBlock {
    public static final Identifier ID = Identifier.of(modId, "autogrindstone");

    public static final Block BLOCK = Blocks.register(
        RegistryKey.of(RegistryKeys.BLOCK, ID),
        AutoGrindstoneBlock::new,
        Blocks.CRAFTER.getSettings()
    );

    public static final Item ITEM = Items.register(BLOCK);

    public AutoGrindstoneBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoGrindstoneBlockEntity(pos, state);
    }
}
