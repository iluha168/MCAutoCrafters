package com.iluha168.autocrafters;

import com.iluha168.autocrafters.block.AutoCutterBlock;
import com.iluha168.autocrafters.screen_handler.*;
import com.iluha168.autocrafters.screens.*;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class ClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(AutoLoomScreenHandler.SCREEN_HANDLER, AutoLoomScreen::new);
		HandledScreens.register(AutoGrindstoneScreenHandler.SCREEN_HANDLER, AutoGrindstoneScreen::new);
		HandledScreens.register(AutoCutterScreenHandler.SCREEN_HANDLER, AutoCutterScreen::new);

		BlockRenderLayerMap.INSTANCE.putBlock(AutoCutterBlock.BLOCK, RenderLayer.getCutout());
	}
}