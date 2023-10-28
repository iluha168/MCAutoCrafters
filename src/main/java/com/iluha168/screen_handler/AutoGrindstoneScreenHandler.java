package com.iluha168.screen_handler;

import com.iluha168.block_entity.AutoGrindstoneBlockEntity;
import com.iluha168.slots.CheckingSlot;
import com.iluha168.slots.PreviewSlot;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class AutoGrindstoneScreenHandler extends BaseAutoScreenHandler {
    public static final ScreenHandlerType<AutoGrindstoneScreenHandler> SCREEN_HANDLER = new ScreenHandlerType<>(
        (syncId, inventory) -> new AutoGrindstoneScreenHandler(syncId, inventory),
        FeatureFlags.VANILLA_FEATURES
    );

    private final PropertyDelegate propertyDelegate;

    //Client constructor
    public AutoGrindstoneScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new AutoGrindstoneBlockEntity(playerInventory.player.getBlockPos(), null), new ArrayPropertyDelegate(1));
    }

    public AutoGrindstoneScreenHandler(int syncId, PlayerInventory playerInventory, SidedInventory inventory, PropertyDelegate propertyDelegate) {
        super(SCREEN_HANDLER, syncId, playerInventory, inventory);
        checkSize(inventory, 2);
        checkDataCount(propertyDelegate, 1);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);

        this.addSlot(new CheckingSlot(this, invInput, 0, 49, 19));
        this.addSlot(new CheckingSlot(this, invInput, 1, 49, 40));
        outputSlot = this.addSlot(new PreviewSlot(invOutput, 0, 129, 34));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                onContentChanged(null);
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
            }
        });
        this.onContentChanged(inventory);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        if(slot < 2)
            return AutoGrindstoneBlockEntity.canBeGrinded(stack);
        return true;
    }

    @Override
    public ItemStack getOutputPreview() {
        return AutoGrindstoneBlockEntity.grind(
            invInput.getStack(0).copyWithCount(1),
            invInput.getStack(1).copyWithCount(1)
        );
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.updateOutputSlot();
        super.onContentChanged(inventory);
    }

    public int getRedstonePower(){
        return this.propertyDelegate.get(0);
    }
}
