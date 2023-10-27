package com.iluha168.screen_handler;

import java.util.List;
import com.google.common.collect.ImmutableList;

import com.iluha168.block_entity.AutoLoomBlockEntity;
import com.iluha168.slots.PreviewSlot;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BannerPatternTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class AutoLoomScreenHandler extends BaseAutoScreenHandler {
    public static final ScreenHandlerType<AutoLoomScreenHandler> SCREEN_HANDLER = new ScreenHandlerType<>(
        (syncId, inventory) -> new AutoLoomScreenHandler(syncId, inventory),
        FeatureFlags.VANILLA_FEATURES
    );

    private final Slot bannerSlot;
    private final Slot dyeSlot;
    private final Slot patternSlot;
    private final PropertyDelegate propertyDelegate;

    //Client constructor
    public AutoLoomScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new AutoLoomBlockEntity(playerInventory.player.getBlockPos(), null), new ArrayPropertyDelegate(2));
    }

    public AutoLoomScreenHandler(int syncId, PlayerInventory playerInventory, AutoLoomBlockEntity inventory, PropertyDelegate propertyDelegate) {
        super(SCREEN_HANDLER, syncId, playerInventory, inventory);
        checkSize(inventory, 3);
        checkDataCount(propertyDelegate, 2);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);

        this.bannerSlot = this.addSlot(new CheckingSlot(this, 0, 13, 26));
        this.dyeSlot = this.addSlot(new CheckingSlot(this, 1, 33, 26));
        this.patternSlot = this.addSlot(new CheckingSlot(this, 2, 23, 45));
        this.outputSlot = this.addSlot(new PreviewSlot(invOutput, 0, 143, 33));

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
    public boolean canInsert(int slot, ItemStack stack){
        switch (slot) {
            case 0:
            return stack.getItem() instanceof BannerItem;
            case 1:
            return stack.getItem() instanceof DyeItem;
            case 2:
            return stack.getItem() instanceof BannerPatternItem;
            default:
            return false;
        }
    }

    public static List<RegistryEntry<BannerPattern>> getPatternsFor(ItemStack stack) {
        if(!stack.isEmpty() && !(stack.getItem() instanceof BannerPatternItem))
            return List.of();
        return
            Registries.BANNER_PATTERN.getEntryList(
                stack.isEmpty() ?
                BannerPatternTags.NO_ITEM_REQUIRED:
                ((BannerPatternItem)stack.getItem()).getPattern()
            )
            .map(ImmutableList::copyOf)
            .orElse(ImmutableList.of());
    }

    public BannerPattern getSelectedPattern(){
        int selectionIndex = getSelectedPatternIndex();
        if(selectionIndex == -1) return null;
        if(isPatternIndexValid(selectionIndex))
            return getPatternsFor(patternSlot.getStack()).get(selectionIndex).value();
        this.setProperty(0, -1);
        return null;
    }

    private boolean isPatternIndexValid(int index) {
        return index >= 0 && index < getPatternsFor(patternSlot.getStack()).size();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (isPatternIndexValid(id)) {
            this.setProperty(0, id);
            this.updateOutputSlot();
            return true;
        }
        return false;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack bannerStack = this.bannerSlot.getStack();
        ItemStack dyeStack = this.dyeSlot.getStack();
        ItemStack patternStack = this.patternSlot.getStack();
        int selectedPatternIndex = getSelectedPatternIndex();
        if (getPatternsFor(patternStack).size() == 1) { //Select the only banner pattern for the user automatically
           this.setProperty(0, 0);
        } else if (!this.isPatternIndexValid(selectedPatternIndex)) { //Previous selection index no longer valid
           this.setProperty(0, -1);
        }
        if (bannerStack.isEmpty() || dyeStack.isEmpty())
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        else this.updateOutputSlot();
        super.onContentChanged(inventory);
    }

    @Override
    public ItemStack getMachineOutput(){
        BannerPattern pattern = getSelectedPattern();
        if(pattern == null) return ItemStack.EMPTY;
        ItemStack bannerStack = this.bannerSlot.getStack();
        ItemStack dyeStack = this.dyeSlot.getStack();
        if(bannerStack.isEmpty() || dyeStack.isEmpty()) 
            return ItemStack.EMPTY;
        ItemStack outputStack = bannerStack.copyWithCount(1);
        AutoLoomBlockEntity.applyPatternNBT(
            outputStack, pattern, ((DyeItem)dyeStack.getItem()).getColor()
        );
        return outputStack;
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        this.sendContentUpdates();
    }

    public int getSelectedPatternIndex(){
        return this.propertyDelegate.get(0);
    }
    public int getRedstonePower(){
        return this.propertyDelegate.get(1);
    }
}
