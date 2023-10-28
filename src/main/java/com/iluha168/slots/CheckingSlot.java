package com.iluha168.slots;

import com.iluha168.screen_handler.BaseAutoScreenHandler;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class CheckingSlot extends Slot {
    BaseAutoScreenHandler bash;
    public CheckingSlot(BaseAutoScreenHandler bash /*XD*/, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.bash = bash;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        return bash.canInsert(id, stack);
    }
}