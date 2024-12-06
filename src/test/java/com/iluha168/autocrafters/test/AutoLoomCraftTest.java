package com.iluha168.autocrafters.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class AutoLoomCraftTest implements FabricGameTest {
	public static final int PULSES = 16+5;
	public static final int PULSE_DELAY = 10;

	@GameTest(
		templateName="autocrafters:autoloom_craft_test",
		tickLimit = PULSES*PULSE_DELAY
	)
	public void testLoom(TestContext context) {
		for (int i = 0; i < PULSES; i++) {
			context.waitAndRun(i*PULSE_DELAY, () ->
				context.putAndRemoveRedstoneBlock(new BlockPos(0, 1, 0), 1)
			);
		}

		context.waitAndRun(PULSES*PULSE_DELAY, () -> {
			BlockPos outputPos = new BlockPos(7, 0, 1);
			ChestBlockEntity output = context.getBlockEntity(outputPos);
			ChestBlockEntity check = context.getBlockEntity(outputPos.south());

			for(int i = 0; i < check.size(); i++) {
				context.assertTrue(
					ItemStack.areEqual(output.getStack(i), check.getStack(i)),
					"Slots "+i+" in target and check chests are different"
				);
			}

			context.complete();
		});
	}
}
