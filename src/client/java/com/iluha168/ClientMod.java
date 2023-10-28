package com.iluha168;

import com.iluha168.screen_handler.*;
import com.iluha168.screens.*;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(AutoLoomScreenHandler.SCREEN_HANDLER, AutoLoomScreen::new);
		HandledScreens.register(AutoGrindstoneScreenHandler.SCREEN_HANDLER, AutoGrindstoneScreen::new);
	}
}