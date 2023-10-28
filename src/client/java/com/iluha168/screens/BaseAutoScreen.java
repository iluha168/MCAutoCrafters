package com.iluha168.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class BaseAutoScreen<T extends ScreenHandler> extends HandledScreen<T> {
    private final Identifier backgroundTextureId;
 
    public BaseAutoScreen(T handler, PlayerInventory inventory, Text title, Identifier backgroundTextureId) {
        super(handler, inventory, title);
        this.backgroundTextureId = backgroundTextureId;
    }
 
    @Override
    protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY) {
        matrices.drawTexture(backgroundTextureId, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}