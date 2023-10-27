package com.iluha168.screen_handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public abstract class BaseAutoScreenHandler extends ScreenHandler {
    protected SidedInventory invInput;
    protected SimpleInventory invOutput = new SimpleInventory(1);
    protected Slot outputSlot;

    public BaseAutoScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, SidedInventory inventory){
        super(type, syncId);
        invInput = inventory;
        inventory.onOpen(playerInventory.player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.invInput.canPlayerUse(player);
    }

    public abstract boolean canInsert(int slot, ItemStack stack);
    public abstract ItemStack getMachineOutput();

    public void updateOutputSlot(){
        this.outputSlot.setStackNoCallbacks(getMachineOutput());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot == null || !slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();
        if (invSlot < invInput.size()) {
            if (!this.insertItem(originalStack, invInput.size(), this.slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            for(int i = 0; i < invInput.size(); i++)
                if(canInsert(i, originalStack))
                    if(this.insertItem(originalStack, i, i+1, false))
                        break;
            return ItemStack.EMPTY;
        } 

        if (originalStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        return newStack;
    }

    public class CheckingSlot extends Slot {
        BaseAutoScreenHandler bash;
        public CheckingSlot(BaseAutoScreenHandler bash /*XD*/, int index, int x, int y) {
            super(bash.invInput, index, x, y);
            this.bash = bash;
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return bash.canInsert(id, stack);
        }
    }
}
