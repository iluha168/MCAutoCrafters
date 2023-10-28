package com.iluha168.block_entity;

import java.util.List;

import com.iluha168.block.AutoLoomBlock;
import com.iluha168.screen_handler.AutoLoomScreenHandler;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoLoomBlockEntity extends BaseAutoBlockEntity {
    public static final int[] ALL_SLOTS = new int[]{0,1,2};

    public static final BlockEntityType<AutoLoomBlockEntity> BLOCK_ENTITY = FabricBlockEntityTypeBuilder
        .create(AutoLoomBlockEntity::new, AutoLoomBlock.BLOCK)
        .build();

    // Precise machine work can apply more layers than doing so by hand!
    // The default vanilla value is 6.
    public static final int MAX_DYE_LAYERS = 15;

    public AutoLoomBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY, pos, state, 3);
    }

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        private int patternIndex = -1; 

        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                return patternIndex;
                case 1:
                return world.getBlockState(pos).get(Properties.POWERED)? 1:0;
                default:
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        public void set(int index, int value) {
            if(index != 0) throw new ArrayIndexOutOfBoundsException();
            patternIndex = value;
            if(world != null)
                world.updateComparators(pos, world.getBlockState(pos).getBlock());
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public static boolean applyPatternNBT(ItemStack bannerStack, BannerPattern pattern, DyeColor dyeColor){
        NbtCompound bannerNbt = BlockItem.getBlockEntityNbt(bannerStack);
        if (bannerNbt == null)
            bannerNbt = new NbtCompound();
        NbtList nbtList = bannerNbt.contains("Patterns", NbtElement.LIST_TYPE)?
            bannerNbt.getList("Patterns", NbtElement.COMPOUND_TYPE) : new NbtList();
        if(nbtList.size() >= MAX_DYE_LAYERS) return false;

        NbtCompound newLayerNbt = new NbtCompound();
        newLayerNbt.putString("Pattern", pattern.getId());
        newLayerNbt.putInt("Color", dyeColor.getId());

        nbtList.add(newLayerNbt);
        bannerNbt.put("Patterns", nbtList);
        BlockItem.setBlockEntityNbt(bannerStack, BlockEntityType.BANNER, bannerNbt);
        return true;
    }

    @Override
    public ItemStack craft() {
        int selectionIndex = propertyDelegate.get(0);
        if(selectionIndex == -1) return ItemStack.EMPTY;
        List<RegistryEntry<BannerPattern>> bannerPatters = AutoLoomScreenHandler.getPatternsFor(getStack(2));
        if(selectionIndex >= 0 && selectionIndex < bannerPatters.size()) {
            BannerPattern pattern = bannerPatters.get(selectionIndex).value();
            ItemStack bannerStack = getStack(0);
            ItemStack dyeStack = getStack(1);
            if(bannerStack.isEmpty() || dyeStack.isEmpty()) 
                return ItemStack.EMPTY;
            ItemStack outputStack = bannerStack.split(1);
            if(!AutoLoomBlockEntity.applyPatternNBT(
                outputStack, pattern, ((DyeItem)dyeStack.split(1).getItem()).getColor()
            )) {
                dyeStack.increment(1);
            }
            return outputStack;
        } else {
            propertyDelegate.set(0, -1);
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("PatternIndex", propertyDelegate.get(0));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        propertyDelegate.set(0, nbt.getInt("PatternIndex")); 
        super.readNbt(nbt);
    }

    @Override
    public int getComparatorOutput() {
        if(propertyDelegate.get(0) != -1)
            return super.getComparatorOutput();
        return 0;
    }

    @Override
    public int[] getAvailableSlots(Direction side){
        return ALL_SLOTS;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        switch (slot) {
            case 0:
            return stack.getItem() instanceof BannerItem;
            case 1:
            return stack.getItem() instanceof DyeItem;
            default:
            return false;
        }
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AutoLoomScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
}
