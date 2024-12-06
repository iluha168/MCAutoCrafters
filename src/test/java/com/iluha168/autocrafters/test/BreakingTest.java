package com.iluha168.autocrafters.test;

import com.iluha168.autocrafters.block.AutoCutterBlock;
import com.iluha168.autocrafters.block.AutoGrindstoneBlock;
import com.iluha168.autocrafters.block.AutoLoomBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.*;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class BreakingTest implements FabricGameTest {
	public final static String templateName = "autocrafters:thin_box";

	private void testBreaking(TestContext context, Item autocrafter) {
		context.setBlockState(BlockPos.ORIGIN, ((BlockItem) autocrafter).getBlock());
		context.getWorld().breakBlock(
			context.getAbsolutePos(BlockPos.ORIGIN),
			true
		);

		context.expectItem(autocrafter);
		context.complete();
	}

	@GameTest(templateName=templateName)
	public void testLoom(TestContext context) {
		testBreaking(context, AutoLoomBlock.ITEM);
	}

	@GameTest(templateName=templateName)
	public void testGrindstone(TestContext context) {
		testBreaking(context, AutoGrindstoneBlock.ITEM);
	}

	@GameTest(templateName=templateName)
	public void testStonecutter(TestContext context) {
		testBreaking(context, AutoCutterBlock.ITEM);
	}
}
