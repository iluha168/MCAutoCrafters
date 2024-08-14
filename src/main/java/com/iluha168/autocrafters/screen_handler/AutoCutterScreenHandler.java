package com.iluha168.autocrafters.screen_handler;

import java.util.List;

import com.iluha168.autocrafters.block.AutoCutterBlock;
import com.iluha168.autocrafters.block_entity.AutoCutterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CrafterOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class AutoCutterScreenHandler extends BaseAutoScreenHandler {
    public static final ScreenHandlerType<AutoCutterScreenHandler> SCREEN_HANDLER = new ScreenHandlerType<>(
        (syncId, inventory) -> new AutoCutterScreenHandler(syncId, inventory),
        FeatureFlags.VANILLA_FEATURES
    );

    private final Slot inputSlot;
    private final PropertyDelegate propertyDelegate;
    private final World world;
    private List<RecipeEntry<StonecuttingRecipe>> recipesCache;

    //Client constructor
    public AutoCutterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new AutoCutterBlockEntity(
            playerInventory.player.getBlockPos(),
            AutoCutterBlock.BLOCK.getDefaultState(),
            playerInventory.player.getWorld()
        ), new ArrayPropertyDelegate(1));
    }

    public AutoCutterScreenHandler(int syncId, PlayerInventory playerInventory, AutoCutterBlockEntity inventory, PropertyDelegate propertyDelegate) {
        super(SCREEN_HANDLER, syncId, playerInventory, inventory);
        checkSize(inventory, 1);
        checkDataCount(propertyDelegate, 1);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);

        this.world = playerInventory.player.getWorld();

        this.inputSlot = this.addSlot(new Slot(invInput, 0, 20, 33){
            @Override
            public boolean canInsert(ItemStack stack){
                return ((AutoCutterBlockEntity)inventory).canInsert(0, stack, null);
            }
        });
        this.outputSlot = this.addSlot(new CrafterOutputSlot(invOutput, 0, 143, 33));

        for(int i = 0; i < 3; ++i)
            for(int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for(int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));

        addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                onContentChanged(slotId == 0? invInput : null);
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
            }
        });
        this.onContentChanged(invInput);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (this.getSelectedRecipeIndex() != id) {
            this.setProperty(0, id);
            this.updateOutputSlot();
            this.sendContentUpdates();
            return true;
        }
        return super.onButtonClick(player, id);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if(inventory == invInput)
            this.updateOutputSlot();
        super.onContentChanged(inventory);
    }

    @Override
    public ItemStack getOutputPreview(){
        SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(this.inputSlot.getStack());
        this.recipesCache = AutoCutterBlockEntity.getAvailableRecipes(recipeInput, world);
        return AutoCutterBlockEntity.craftStatic(recipeInput, world, this.recipesCache, getSelectedRecipeIndex());
    }

    public int getSelectedRecipeIndex(){
        return this.propertyDelegate.get(0);
    }

    public List<RecipeEntry<StonecuttingRecipe>> getAvailableRecipes() {
        return this.recipesCache;
    }

    public int getAvailableRecipeCount() {
        return this.recipesCache.size();
    }
}
