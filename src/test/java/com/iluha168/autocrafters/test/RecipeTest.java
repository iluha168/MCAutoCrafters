package com.iluha168.autocrafters.test;

import com.iluha168.autocrafters.block.AutoCutterBlock;
import com.iluha168.autocrafters.block.AutoGrindstoneBlock;
import com.iluha168.autocrafters.block.AutoLoomBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class RecipeTest implements FabricGameTest {
	public final static String templateName = "autocrafters:recipe_test";

	private void testConversion(TestContext context, Item input, Item output) {
		BlockPos outputPos = new BlockPos(0, 0, 0);
		BlockPos crafterPos = outputPos.up();
		BlockPos powerPos =  crafterPos.up();

		context.expectEmptyContainer(outputPos);

		CrafterBlockEntity crafter = context.getBlockEntity(crafterPos);
		crafter.setStack(4, input.getDefaultStack());

		context.putAndRemoveRedstoneBlock(powerPos, 1);
		context.waitAndRun(10, () -> {
			context.expectContainerWith(outputPos, output);
			context.complete();
		});
	}

	@GameTest(templateName=templateName)
	public void testCrafter(TestContext context) {
		testConversion(context, Items.CRAFTING_TABLE, Items.CRAFTER);
	}

	@GameTest(templateName=templateName)
	public void testLoom(TestContext context) {
		testConversion(context, Items.LOOM, AutoLoomBlock.ITEM);
	}

	@GameTest(templateName=templateName)
	public void testGrindstone(TestContext context) {
		testConversion(context, Items.GRINDSTONE, AutoGrindstoneBlock.ITEM);
	}

	@GameTest(templateName=templateName)
	public void testStonecutter(TestContext context) {
		testConversion(context, Items.STONECUTTER, AutoCutterBlock.ITEM);
	}
}
