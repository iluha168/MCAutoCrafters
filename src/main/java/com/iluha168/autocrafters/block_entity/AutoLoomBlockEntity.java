package com.iluha168.autocrafters.block_entity;

import java.util.List;

import com.iluha168.autocrafters.block.AutoLoomBlock;
import com.iluha168.autocrafters.screen_handler.AutoLoomScreenHandler;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoLoomBlockEntity extends BaseAutoBlockEntity {
    public static final int[] ALL_SLOTS = new int[]{0,1,2};

    public static final BlockEntityType<AutoLoomBlockEntity> BLOCK_ENTITY = BlockEntityType.Builder
        .create(AutoLoomBlockEntity::new, AutoLoomBlock.BLOCK)
        .build();

    // Precise machine work can apply more layers than doing so by hand!
    // The default vanilla value is 6.
    public static final int MAX_DYE_LAYERS = 15;

    private RegistryEntryLookup<BannerPattern> bannerPatternLookup = null;

    public AutoLoomBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY, pos, state, ALL_SLOTS.length);
    }

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        private int patternIndex = -1; 

        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                return patternIndex;
                case 1:
                return world.getBlockState(pos).get(Properties.TRIGGERED)? 1:0;
                default:
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        public void set(int index, int value) {
            if(index != 0) throw new ArrayIndexOutOfBoundsException();
            patternIndex = value;
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public static boolean applyPatternComponent(ItemStack bannerStack, RegistryEntry<BannerPattern> pattern, DyeColor dyeColor){
        if(bannerStack.get(DataComponentTypes.BANNER_PATTERNS).layers().size() >= MAX_DYE_LAYERS) return false;
        bannerStack.apply(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT, (components) ->
            (new BannerPatternsComponent.Builder()).addAll(components).add(pattern, dyeColor).build()
        );
        return true;
    }

    @Override
    public ItemStack craft() {
        int selectionIndex = propertyDelegate.get(0);
        if(selectionIndex == -1) return ItemStack.EMPTY;
        if(bannerPatternLookup == null)
            this.bannerPatternLookup = world.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN);
        List<RegistryEntry<BannerPattern>> bannerPatters = AutoLoomScreenHandler.getPatternsFor(bannerPatternLookup, getStack(2));
        if(selectionIndex >= 0 && selectionIndex < bannerPatters.size()) {
            ItemStack bannerStack = getStack(0);
            ItemStack dyeStack = getStack(1);
            if(bannerStack.isEmpty() || dyeStack.isEmpty()) 
                return ItemStack.EMPTY;
            ItemStack outputStack = bannerStack.split(1);
            if(!AutoLoomBlockEntity.applyPatternComponent(
                outputStack, bannerPatters.get(selectionIndex), ((DyeItem)dyeStack.split(1).getItem()).getColor()
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
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("PatternIndex", propertyDelegate.get(0));
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        propertyDelegate.set(0, nbt.getInt("PatternIndex")); 
        super.readNbt(nbt, registryLookup);
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
