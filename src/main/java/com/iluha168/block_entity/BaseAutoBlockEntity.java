package com.iluha168.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public abstract class BaseAutoBlockEntity extends BlockEntity implements SidedInventory, NamedScreenHandlerFactory {
    public DefaultedList<ItemStack> stacks;
    public BaseAutoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize) {
        super(type, pos, state);
        stacks = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, stacks);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, stacks);
    }

    public abstract ItemStack craft();
    
    public int getComparatorOutput(){
        int stacksFilled = 0;
        for(int i = 0; i < size(); i++)
            stacksFilled += getStack(i).isEmpty()? 0:1;
        return stacksFilled;
    }

//NAMED SCREEN FACTORY IMPLEMENTATION
    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

//SIDED INVENTORY IMPLEMENTATION
    @Override
    public void clear() {
        stacks.clear();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack stack : stacks)
            if(!stack.isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(stacks, slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(stacks, slot, amount);
        if (!result.isEmpty())
            markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) {
            stack.setCount(stack.getMaxCount());
        }
    }

    @Override
    public int size() {
        return stacks.size();
    }
}
