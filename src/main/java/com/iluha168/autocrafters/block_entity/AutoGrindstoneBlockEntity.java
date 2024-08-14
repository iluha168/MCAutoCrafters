package com.iluha168.autocrafters.block_entity;

import java.util.UUID;

import com.iluha168.autocrafters.block.AutoGrindstoneBlock;
import com.iluha168.autocrafters.screen_handler.AutoGrindstoneScreenHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AutoGrindstoneBlockEntity extends BaseAutoBlockEntity {
    public static final int[] ALL_SLOTS = new int[]{0,1};
    public static final BlockEntityType<AutoGrindstoneBlockEntity> BLOCK_ENTITY = BlockEntityType.Builder
        .create(AutoGrindstoneBlockEntity::new, AutoGrindstoneBlock.BLOCK)
        .build();

    public AutoGrindstoneBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY, pos, state, ALL_SLOTS.length);
    }

    public AutoGrindstoneBlockEntity(BlockPos pos, BlockState state, World world) {
        super(BLOCK_ENTITY, pos, state, ALL_SLOTS.length);
        this.setWorld(world);
    }

	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
			if(index != 0) throw new ArrayIndexOutOfBoundsException();
            return world.getBlockState(pos).get(Properties.TRIGGERED)? 1:0;
        }

        @Override
        public void set(int index, int value) {
        	throw new ArrayIndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 1;
        }
    };

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return (stack.isDamageable() || EnchantmentHelper.hasEnchantments(stack))
            && (slot == 0 || getStack(0).itemMatches(stack.getRegistryEntry()));
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return ALL_SLOTS;
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AutoGrindstoneScreenHandler(syncId, playerInventory, this, propertyDelegate);
	}

    public GrindstoneScreenHandler constructVirtualGSH(){
        PlayerEntity virtualPlayer = new PlayerEntity(
            world, pos, 0,
            new GameProfile(new UUID(0,0), "")
        ) {
            @Override
            public boolean isCreative() { return false; }
            @Override
            public boolean isSpectator() { return false; }
        };
        GrindstoneScreenHandler gsh = new GrindstoneScreenHandler(
            -1, new PlayerInventory(virtualPlayer),
            ScreenHandlerContext.create(world, pos.offset(getCachedState().get(Properties.ORIENTATION).getFacing()))
        );
        for(int slot : ALL_SLOTS)
            gsh.getSlot(slot).setStack(getStack(slot));
        return gsh;
    }

	@Override
	public ItemStack craft() {
        GrindstoneScreenHandler gsh = this.constructVirtualGSH();
        gsh.onSlotClick(2, 0, SlotActionType.PICKUP, ((PlayerInventory)gsh.getSlot(3).inventory).player);
        for(int slot : ALL_SLOTS)
            this.stacks.set(slot, gsh.getSlot(slot).getStack());
        return gsh.getCursorStack().copyAndEmpty();
	}
}
