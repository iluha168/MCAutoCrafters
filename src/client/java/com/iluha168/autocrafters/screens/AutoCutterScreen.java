package com.iluha168.autocrafters.screens;

import com.iluha168.autocrafters.ServerMod;
import com.iluha168.autocrafters.screen_handler.AutoCutterScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;

public class AutoCutterScreen extends BaseAutoScreen<AutoCutterScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(ServerMod.modId, "textures/gui/container/autocutter.png");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/stonecutter/scroller");
    private static final Identifier RECIPE_SELECTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_selected");
    private static final Identifier RECIPE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_highlighted");
    private static final Identifier RECIPE_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe");
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int RECIPE_LIST_COLUMNS = 4;
    private static final int RECIPE_LIST_ROWS = 3;
    private static final int RECIPE_ENTRY_WIDTH = 16;
    private static final int RECIPE_ENTRY_HEIGHT = 18;
    private static final int SCROLLBAR_AREA_HEIGHT = 54;
    private static final int RECIPE_LIST_OFFSET_X = 52;
    private static final int RECIPE_LIST_OFFSET_Y = 14;
    private int scrollOffset;
    private float scrollAmount;
    private boolean scrollbarClicked;
    private Item lastRecipeInput = Items.AIR;

    public AutoCutterScreen(AutoCutterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
    }
 
    @Override
    protected void init() {
        super.init();
        this.titleY--;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Item currentRecipeInput = handler.getSlot(0).getStack().getItem();
        if(currentRecipeInput != lastRecipeInput){
            handler.updateOutputSlot();
            lastRecipeInput = currentRecipeInput;
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY) {
        assert this.client != null;
        assert this.client.world != null;
        super.drawBackground(matrices, delta, mouseX, mouseY);
        int l = x + RECIPE_LIST_OFFSET_X;
        int m = y + RECIPE_LIST_OFFSET_Y;

        mouseScrolled(0d, 0d, 0d, 0d);
        matrices.drawGuiTexture(RenderLayer::getGuiTextured, SCROLLER_TEXTURE, x+119, y+RECIPE_LIST_OFFSET_Y+(int)(41f*this.scrollAmount), SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);

        int selectedRecipeIndex = handler.getSelectedRecipeIndex();
	    ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(this.client.world);
        if(this.scrollOffset < 0)
            this.scrollOffset = 0;
        for(int i = this.scrollOffset; i < RECIPE_LIST_COLUMNS*RECIPE_LIST_ROWS && i < handler.getAvailableRecipeCount(); i++) {
            int j = i - this.scrollOffset;
            int iX = l + j%RECIPE_LIST_COLUMNS * RECIPE_ENTRY_WIDTH;
            int iY = m + j/RECIPE_LIST_COLUMNS * RECIPE_ENTRY_HEIGHT + 2;

            matrices.drawGuiTexture(
                RenderLayer::getGuiTextured,
                i == selectedRecipeIndex? RECIPE_SELECTED_TEXTURE:
                mouseX >= iX && mouseY >= iY && mouseX < iX + RECIPE_ENTRY_WIDTH && mouseY < iY + RECIPE_ENTRY_HEIGHT? RECIPE_HIGHLIGHTED_TEXTURE:
                RECIPE_TEXTURE,
                iX, iY-1, RECIPE_ENTRY_WIDTH, RECIPE_ENTRY_HEIGHT
            );
            matrices.drawItem(handler.getAvailableRecipes().entries().get(i).recipe().optionDisplay().getFirst(contextParameterMap), iX, iY);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(this.scrollbarClicked) {
            int yStart = this.y + RECIPE_LIST_OFFSET_Y;
            int yEnd = yStart + SCROLLBAR_AREA_HEIGHT;
            this.scrollAmount = MathHelper.clamp((float)(mouseY - yStart - 7.5f) / (yEnd - yStart - 15.0f), 0f, 1f);
            this.scrollOffset = (int)(this.scrollAmount * this.getMaxScroll() + 0.5) * RECIPE_LIST_COLUMNS;
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }
  
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double verticalAmount) {
        int i = this.getMaxScroll();
        this.scrollAmount = MathHelper.clamp(this.scrollAmount - (float)verticalAmount / (float)i, 0F, 1F);
        this.scrollOffset = (int)(this.scrollAmount * i + 0.5) * 4;
        return true;
    }

    protected int getMaxScroll() {
        return (handler.getAvailableRecipeCount() + RECIPE_LIST_COLUMNS - 1) / RECIPE_LIST_COLUMNS - RECIPE_LIST_ROWS;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        assert this.client != null;
        assert this.client.interactionManager != null;
        int x = this.x + RECIPE_LIST_OFFSET_X;
        int y = this.y + RECIPE_LIST_OFFSET_Y;

        for(int recipeIndex = this.scrollOffset; recipeIndex < this.scrollOffset + RECIPE_LIST_COLUMNS*RECIPE_LIST_ROWS; recipeIndex++) {
            int visibleRecipeIndex = recipeIndex - this.scrollOffset;
            double recipeMouseX = mouseX - (double)(x + visibleRecipeIndex % RECIPE_LIST_COLUMNS * RECIPE_ENTRY_WIDTH);
            double recipeMouseY = mouseY - (double)(y + visibleRecipeIndex / RECIPE_LIST_COLUMNS * RECIPE_ENTRY_HEIGHT);
            if (recipeMouseX >= 0 && recipeMouseY >= 0 && recipeMouseX < RECIPE_ENTRY_WIDTH && recipeMouseY < RECIPE_ENTRY_HEIGHT && this.handler.onButtonClick(this.client.player, recipeIndex)) {
               MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1));
	            this.client.interactionManager.clickButton(this.handler.syncId, recipeIndex);
               return true;
            }
        }

        x = this.x + 119;
        y = this.y + 9;
        this.scrollbarClicked = mouseX >= x && mouseX < x + SCROLLBAR_WIDTH && mouseY >= y && mouseY < y + SCROLLBAR_AREA_HEIGHT;

        return super.mouseClicked(mouseX, mouseY, button);
    }
}