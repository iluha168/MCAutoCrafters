package com.iluha168;

import com.iluha168.screen_handler.AutoLoomScreenHandler;
import com.iluha168.screens.AutoLoomScreen;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(AutoLoomScreenHandler.SCREEN_HANDLER, AutoLoomScreen::new);
	}
}