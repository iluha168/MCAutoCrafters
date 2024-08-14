package com.iluha168.autocrafters.block_entity;

import java.util.List;

import com.iluha168.autocrafters.block.AutoCutterBlock;
import com.iluha168.autocrafters.screen_handler.AutoCutterScreenHandler;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AutoCutterBlockEntity extends BaseAutoBlockEntity {
    public static final int[] ALL_SLOTS = new int[]{0};
    public static final BlockEntityType<AutoCutterBlockEntity> BLOCK_ENTITY = BlockEntityType.Builder
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
        return world.getRecipeManager().getFirstMatch(RecipeType.STONECUTTING, new SingleStackRecipeInput(stack), world).isPresent();
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
			if(index != 0) throw new ArrayIndexOutOfBoundsException();
            return recipeIndex;
        }

        @Override
        public void set(int index, int value) {
            if(index != 0) throw new ArrayIndexOutOfBoundsException();
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

	public static List<RecipeEntry<StonecuttingRecipe>> getAvailableRecipes(SingleStackRecipeInput input, World world){
		return world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING, input, world);
	}

	public static ItemStack craftStatic(SingleStackRecipeInput input, World world, List<RecipeEntry<StonecuttingRecipe>> availableRecipes, int recipeIndex){
        return (recipeIndex >= 0 && recipeIndex < availableRecipes.size())?
            availableRecipes.get(recipeIndex).value().craft(input, world.getRegistryManager())
            : ItemStack.EMPTY;
	}

	@Override
	public ItemStack craft() {
		SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(getStack(0));
        ItemStack result = craftStatic(recipeInput, world, getAvailableRecipes(recipeInput, world),  propertyDelegate.get(0));
		if(!result.isEmpty())
			getStack(0).decrement(1);
		return result;
	}
}
