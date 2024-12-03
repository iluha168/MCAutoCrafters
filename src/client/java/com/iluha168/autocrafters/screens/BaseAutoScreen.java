package com.iluha168.autocrafters.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class BaseAutoScreen<T extends ScreenHandler> extends HandledScreen<T> {
    private final Identifier backgroundTextureId;

    public BaseAutoScreen(
        T handler,
        PlayerInventory inventory,
        Text title,
        Identifier backgroundTextureId
    ) {
        super(handler, inventory, title);
        this.backgroundTextureId = backgroundTextureId;
    }
 
    @Override
    protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY) {
        matrices.drawTexture(RenderLayer::getGuiTextured, backgroundTextureId, x, y, 0f, 0f, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices, mouseX, mouseY, delta);
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