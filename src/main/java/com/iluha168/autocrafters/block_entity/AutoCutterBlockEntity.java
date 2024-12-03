package com.iluha168.autocrafters.block_entity;

import com.iluha168.autocrafters.block.AutoCutterBlock;
import com.iluha168.autocrafters.screen_handler.AutoCutterScreenHandler;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AutoCutterBlockEntity extends BaseAutoBlockEntity {
    public static final int[] ALL_SLOTS = new int[]{0};
    public static final BlockEntityType<AutoCutterBlockEntity> BLOCK_ENTITY = FabricBlockEntityTypeBuilder
        .create(AutoCutterBlockEntity::new, AutoCutterBlock.BLOCK)
        .build();

    public AutoCutterBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY, pos, state, ALL_SLOTS.length);
    }

    public AutoCutterBlockEntity(BlockPos pos, BlockState state, World world) {
        super(BLOCK_ENTITY, pos, state, ALL_SLOTS.length);
        this.setWorld(world);
    }

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		assert world != null;
		return !getAvailableRecipes(new SingleStackRecipeInput(stack), world).isEmpty();
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return ALL_SLOTS;
	}

	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        private int recipeIndex = -1; 

        @Override
        public int get(int index) {
			assert index == 0;
            return recipeIndex;
        }

        @Override
        public void set(int index, int value) {
	        assert index == 0;
            recipeIndex = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AutoCutterScreenHandler(syncId, playerInventory, this, propertyDelegate);
	}

	public static CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getAvailableRecipes(SingleStackRecipeInput input, World world){
		return world.getRecipeManager().getStonecutterRecipes().filter(input.item());
	}

	public static ItemStack craftStatic(SingleStackRecipeInput input, World world, CuttingRecipeDisplay.Grouping<StonecuttingRecipe> availableRecipes, int recipeIndex){
		var recipes = availableRecipes.entries();
		if(recipeIndex <= 0 || recipeIndex > recipes.size())
			return ItemStack.EMPTY;
		var recipe = recipes.get(recipeIndex).recipe().recipe();
		if(recipe.isEmpty())
			return ItemStack.EMPTY;
		return recipe.get().value().craft(input, world.getRegistryManager());
	}

	@Override
	public ItemStack craft() {
		assert world != null;
		SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(getStack(0));
		ItemStack result = craftStatic(recipeInput, world, getAvailableRecipes(recipeInput, world), propertyDelegate.get(0));
		if(!result.isEmpty())
			getStack(0).decrement(1);
		return result;
	}
}
