package com.iluha168.autocrafters.screen_handler;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.iluha168.autocrafters.block.AutoLoomBlock;
import com.iluha168.autocrafters.block_entity.AutoLoomBlockEntity;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BannerPatternTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CrafterOutputSlot;
import net.minecraft.screen.slot.Slot;

public class AutoLoomScreenHandler extends BaseAutoScreenHandler {
    public static final ScreenHandlerType<AutoLoomScreenHandler> SCREEN_HANDLER = new ScreenHandlerType<>(
	    AutoLoomScreenHandler::new,
        FeatureFlags.VANILLA_FEATURES
    );

    private final Slot bannerSlot;
    private final Slot dyeSlot;
    private final Slot patternSlot;
    private final PropertyDelegate propertyDelegate;
    public final RegistryEntryLookup<BannerPattern> bannerPatternLookup;

    //Client constructor
    public AutoLoomScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new AutoLoomBlockEntity(
                playerInventory.player.getBlockPos(),
                AutoLoomBlock.BLOCK.getDefaultState()
            ),
            new ArrayPropertyDelegate(2)
        );
    }

    public AutoLoomScreenHandler(int syncId, PlayerInventory playerInventory, AutoLoomBlockEntity inventory, PropertyDelegate propertyDelegate) {
        super(SCREEN_HANDLER, syncId, playerInventory, inventory);
        checkSize(inventory, 3);
        checkDataCount(propertyDelegate, 2);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);

        this.bannerSlot = this.addSlot(new Slot(invInput, 0, 13, 26) {
            @Override
            public boolean canInsert(ItemStack stack){
                return stack.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(invInput, 1, 33, 26) {
            @Override
            public boolean canInsert(ItemStack stack){
                return stack.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(invInput, 2, 23, 45) {
            @Override
            public boolean canInsert(ItemStack stack){
                return stack.getItem() instanceof BannerPatternItem;
            }
        });
        this.outputSlot = this.addSlot(new CrafterOutputSlot(invOutput, 0, 143, 33));
        this.bannerPatternLookup = playerInventory.player.getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN);

        for(int i = 0; i < 3; ++i)
            for(int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for(int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));

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

    public static List<RegistryEntry<BannerPattern>> getPatternsFor(RegistryEntryLookup<BannerPattern> bannerPatternLookup, ItemStack stack) {
        TagKey<BannerPattern> pattern;
        if(stack.isEmpty())
            pattern = BannerPatternTags.NO_ITEM_REQUIRED;
        else {
            Item item = stack.getItem();
            if (item instanceof BannerPatternItem)
                pattern = ((BannerPatternItem)item).getPattern();
            else return List.of();
        }
        return bannerPatternLookup.getOptional(pattern).map(ImmutableList::copyOf).orElse(ImmutableList.of());
    }

    public RegistryEntry<BannerPattern> getSelectedPattern(){
        int selectionIndex = getSelectedPatternIndex();
        if(selectionIndex == -1) return null;
        if(isPatternIndexValid(selectionIndex))
            return getPatternsFor(bannerPatternLookup, patternSlot.getStack()).get(selectionIndex);
        this.setProperty(0, -1);
        return null;
    }

    private boolean isPatternIndexValid(int index) {
        return index >= 0 && index < getPatternsFor(bannerPatternLookup, patternSlot.getStack()).size();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (this.getSelectedPatternIndex() != id && isPatternIndexValid(id)) {
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
        if (getPatternsFor(bannerPatternLookup, patternStack).size() == 1) { //Select the only banner pattern for the user automatically
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
    public ItemStack getOutputPreview(){
        RegistryEntry<BannerPattern> pattern = getSelectedPattern();
        if(pattern == null) return ItemStack.EMPTY;
        ItemStack bannerStack = this.bannerSlot.getStack();
        ItemStack dyeStack = this.dyeSlot.getStack();
        if(bannerStack.isEmpty() || dyeStack.isEmpty()) 
            return ItemStack.EMPTY;
        ItemStack outputStack = bannerStack.copyWithCount(1);
        AutoLoomBlockEntity.applyPatternComponent(
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
