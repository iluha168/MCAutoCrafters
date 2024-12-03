package com.iluha168.autocrafters.screens;

import com.iluha168.autocrafters.ServerMod;
import com.iluha168.autocrafters.screen_handler.AutoGrindstoneScreenHandler;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AutoGrindstoneScreen extends BaseAutoScreen<AutoGrindstoneScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(ServerMod.modId, "textures/gui/container/autogrindstone.png");

    public AutoGrindstoneScreen(AutoGrindstoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
    }

    @Override
    protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY) {
        super.drawBackground(matrices, delta, mouseX, mouseY);
        if(handler.getRedstonePower() == 1)
            matrices.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x+101, y+36, (float) backgroundWidth, 40f, 17, 11, 256, 256);
    }
}