package com.iluha168.autocrafters.screen_handler;

import com.iluha168.autocrafters.block_entity.AutoGrindstoneBlockEntity;

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
import net.minecraft.screen.slot.CrafterOutputSlot;
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

        class GrindSlot extends Slot {
            public GrindSlot(Inventory inventory, int index, int x, int y) {
                super(inventory, index, x, y);
            }
            
            @Override
            public boolean canInsert(ItemStack stack) {
                return inventory instanceof AutoGrindstoneBlockEntity 
                && ((AutoGrindstoneBlockEntity)inventory).canInsert(getIndex(), stack, null);
            }
        }

        this.addSlot(new GrindSlot(invInput, 0, 49, 19));
        this.addSlot(new GrindSlot(invInput, 1, 49, 40));
        outputSlot = this.addSlot(new CrafterOutputSlot(invOutput, 0, 129, 34));

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
    public ItemStack getOutputPreview() {
        if(this.invInput instanceof AutoGrindstoneBlockEntity){
            AutoGrindstoneBlockEntity grindstone = ((AutoGrindstoneBlockEntity)this.invInput);
            if(grindstone.hasWorld())
                return grindstone.constructVirtualGSH().getSlot(2).getStack();
        }
        return ItemStack.EMPTY;
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
