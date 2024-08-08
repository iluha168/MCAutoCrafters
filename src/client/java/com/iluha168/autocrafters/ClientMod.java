package com.iluha168.autocrafters;

import com.iluha168.autocrafters.screen_handler.*;
import com.iluha168.autocrafters.screens.*;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(AutoLoomScreenHandler.SCREEN_HANDLER, AutoLoomScreen::new);
		HandledScreens.register(AutoGrindstoneScreenHandler.SCREEN_HANDLER, AutoGrindstoneScreen::new);
	}
}