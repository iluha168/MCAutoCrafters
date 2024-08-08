package com.iluha168.autocrafters.screens;

import java.util.List;

import com.iluha168.autocrafters.ServerMod;
import com.iluha168.autocrafters.screen_handler.AutoLoomScreenHandler;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AutoLoomScreen extends BaseAutoScreen<AutoLoomScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(ServerMod.modId, "textures/gui/container/autoloom.png");
    private static final Identifier[] SLOT_TEXTURES = {
        Identifier.ofVanilla("container/loom/banner_slot"),
        Identifier.ofVanilla("container/loom/dye_slot"),
        Identifier.ofVanilla("container/loom/pattern_slot")
    };
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/loom/scroller");
    private static final Identifier PATTERN_SELECTED_TEXTURE = Identifier.ofVanilla("container/loom/pattern_selected");
    private static final Identifier PATTERN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/loom/pattern_highlighted");
    private static final Identifier PATTERN_TEXTURE = Identifier.ofVanilla("container/loom/pattern");
    private static final int PATTERN_LIST_OFFSET_X = 60;
    private static final int PATTERN_LIST_OFFSET_Y = 13;
    private static final int SCROLLBAR_AREA_HEIGHT = 56;
    private static final int PATTERN_ENTRY_SIZE = 14;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private int visibleTopRow;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private ModelPart bannerField;

    public AutoLoomScreen(AutoLoomScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
    }
 
    @Override
    protected void init() {
        super.init();
        this.titleY -= 2;
        this.bannerField = this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BANNER).getChild("flag");
        this.bannerField.pitch = 0.0F;
        this.bannerField.pivotY = -32.0F;
    }

    private int getRows() {
        return MathHelper.ceilDiv(getPatterns().size(), 4);
    }

    private List<RegistryEntry<BannerPattern>> getPatterns(){
        return AutoLoomScreenHandler.getPatternsFor(handler.bannerPatternLookup, handler.getSlot(2).getStack());
    }

    @Override
    protected void drawBackground(DrawContext matrices, float delta, int mouseX, int mouseY) {
        super.drawBackground(matrices, delta, mouseX, mouseY);
        int l = x + PATTERN_LIST_OFFSET_X;
        int m = y + PATTERN_LIST_OFFSET_Y;
        List<RegistryEntry<BannerPattern>> list = getPatterns();

        for(int i = 0; i <= 2; i++){
            Slot slot = handler.getSlot(i);
            if(!slot.hasStack())
                matrices.drawGuiTexture(SLOT_TEXTURES[i], x + slot.x, y + slot.y, 0, 16, 16);
        }

        mouseScrolled(0d, 0d, 0d, 0d);
        matrices.drawGuiTexture(SCROLLER_TEXTURE, x+119, y+PATTERN_LIST_OFFSET_Y+(int)(41f*this.scrollPosition), 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
        if(handler.getRedstonePower() == 1)
            matrices.drawTexture(TEXTURE, x+9, y+24, 0, backgroundHeight, 44, 41);

        int selectedPatternIndex = handler.getSelectedPatternIndex();
        DiffuseLighting.disableGuiDepthLighting();
        loopBannerPatterns:
        for(int n = 0; n < 4; n++) {
            for(int o = 0; o < 4; o++) {
                int p = n + this.visibleTopRow;
                int q = p * 4 + o;
                if (q >= list.size())
                    break loopBannerPatterns;
                int px = l + o * PATTERN_ENTRY_SIZE;
                int py = m + n * PATTERN_ENTRY_SIZE;
                Identifier texture = 
                    q == selectedPatternIndex?
                        PATTERN_SELECTED_TEXTURE:
                    mouseX >= px && mouseY >= py && mouseX < px + PATTERN_ENTRY_SIZE && mouseY < py + PATTERN_ENTRY_SIZE?
                        PATTERN_HIGHLIGHTED_TEXTURE:
                        PATTERN_TEXTURE;
                matrices.drawGuiTexture(texture, px, py, 0, PATTERN_ENTRY_SIZE, PATTERN_ENTRY_SIZE);
                this.drawBanner(matrices, list.get(q), px, py);
            }
        }
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = this.getRows() - 4;
        if (this.scrollbarClicked && i > 0) {
            int j = y + PATTERN_LIST_OFFSET_Y;
            int k = j + SCROLLBAR_AREA_HEIGHT;
            this.scrollPosition = ((float)mouseY - (float)j - 7.5f) / (k - j - SCROLLBAR_HEIGHT);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0f, 1f);
            this.visibleTopRow = Math.max((int)(this.scrollPosition*i + 0.5), 0);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
  
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double verticalAmount) {
        int i = this.getRows() - 4;
        if (i > 0) {
            float f = (float)verticalAmount / (float)i;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0F, 1.0F);
        }
        this.visibleTopRow = Math.max((int)(this.scrollPosition*i + 0.5F), 0);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrollbarClicked = false;
        int i = this.x + PATTERN_LIST_OFFSET_X;
        int j = this.y + PATTERN_LIST_OFFSET_Y;

        for(int k = 0; k < 4; k++) {
            for(int l = 0; l < 4; l++) {
                double d = mouseX - (double)(i + l * PATTERN_ENTRY_SIZE);
                double e = mouseY - (double)(j + k * PATTERN_ENTRY_SIZE);
                int m = k + this.visibleTopRow;
                int n = m * 4 + l;
                if (d >= 0.0 && e >= 0.0 && d < PATTERN_ENTRY_SIZE && e < PATTERN_ENTRY_SIZE && handler.onButtonClick(this.client.player, n)) {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1f));
                    this.client.interactionManager.clickButton(handler.syncId, n);
                    return true;
                }
            }
        }
        i = this.x + 119;
        j = this.y + 9;
        if (mouseX >= i && mouseX < i + SCROLLBAR_WIDTH && mouseY >= j && mouseY < j + SCROLLBAR_AREA_HEIGHT)
            this.scrollbarClicked = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawBanner(DrawContext context, RegistryEntry<BannerPattern> pattern, int x, int y) {
        Item dyeItem = handler.getSlot(1).getStack().getItem();
        Item bannerItem = handler.getSlot(0).getStack().getItem();

        DyeColor dyeColor = DyeColor.WHITE;
        if(dyeItem instanceof DyeItem)
            dyeColor = ((DyeItem)dyeItem).getColor();

        DyeColor bannerColor = DyeColor.GRAY; 
        if(bannerItem instanceof BannerItem)
            bannerColor = ((BannerItem)bannerItem).getColor();

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate(x + 0.5f, y + 16f, 0.0f);
        matrixStack.scale(6f, -6f, 1f);
        matrixStack.translate(1f, 1f, 0.5f);
        matrixStack.scale(2f/3f, -2f/3f, -2f/3f);
        BannerPatternsComponent patternComponent = (new BannerPatternsComponent.Builder()).add(pattern, dyeColor).build();
        BannerBlockEntityRenderer.renderCanvas(matrixStack, context.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, bannerColor, patternComponent);
        matrixStack.pop();
        context.draw();
    }
}