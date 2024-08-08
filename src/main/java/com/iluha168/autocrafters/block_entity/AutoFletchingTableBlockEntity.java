package com.iluha168.autocrafters.block_entity;

import com.iluha168.autocrafters.block.AutoFletchingTableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoFletchingTableBlockEntity extends BaseAutoBlockEntity {
    public static final int[] ALL_SLOTS = new int[]{};
    public static final BlockEntityType<AutoFletchingTableBlockEntity> BLOCK_ENTITY = BlockEntityType.Builder
        .create(AutoFletchingTableBlockEntity::new,  AutoFletchingTableBlock.BLOCK)
        .build();

    public  AutoFletchingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY, pos, state, 0);
    }

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return ALL_SLOTS;
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
	}

	@Override
	public ItemStack craft() {
        return ItemStack.EMPTY;
	}
}
