package com.iluha168.screens;

import java.util.List;

import com.iluha168.ServerMod;
import com.iluha168.screen_handler.AutoLoomScreenHandler;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AutoLoomScreen extends BaseAutoScreen<AutoLoomScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ServerMod.modId, "textures/gui/container/autoloom.png");
    private static final Identifier TEXTURE_VANILLA = new Identifier("textures/gui/container/loom.png");
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
        return AutoLoomScreenHandler.getPatternsFor(handler.getSlot(2).getStack());
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
                matrices.drawTexture(TEXTURE_VANILLA, x + slot.x, y + slot.y, this.backgroundWidth+i*16, 0, 16, 16);
        }

        mouseScrolled(0d, 0d, 0d);
        matrices.drawTexture(TEXTURE_VANILLA, x+119, y+PATTERN_LIST_OFFSET_Y+(int)(41f*this.scrollPosition), 232, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
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
                int t;
                if (q == selectedPatternIndex) {
                    t = this.backgroundHeight + PATTERN_ENTRY_SIZE;
                } else if (mouseX >= px && mouseY >= py && mouseX < px + PATTERN_ENTRY_SIZE && mouseY < py + PATTERN_ENTRY_SIZE) {
                    t = this.backgroundHeight + 28;
                } else {
                    t = this.backgroundHeight;
                }

                matrices.drawTexture(TEXTURE_VANILLA, px, py, 0, t, PATTERN_ENTRY_SIZE, PATTERN_ENTRY_SIZE);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = this.getRows() - 4;
        if (i > 0) {
            float f = (float)amount / (float)i;
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
        NbtCompound nbtCompound = new NbtCompound();
        NbtList nbtList = (new BannerPattern.Patterns()).add(BannerPatterns.BASE, DyeColor.GRAY).add(pattern, DyeColor.WHITE).toNbt();
        nbtCompound.put("Patterns", nbtList);
        ItemStack itemStack = new ItemStack(Items.GRAY_BANNER);
        BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.BANNER, nbtCompound);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate(x + 0.5f, y + 16f, 0.0f);
        matrixStack.scale(6f, -6f, 1f);
        matrixStack.translate(1f, 1f, 0.5f);
        matrixStack.scale(2f/3f, -2f/3f, -2f/3f);
        List<Pair<RegistryEntry<BannerPattern>, DyeColor>> list = BannerBlockEntity.getPatternsFromNbt(DyeColor.GRAY, BannerBlockEntity.getPatternListNbt(itemStack));
        BannerBlockEntityRenderer.renderCanvas(matrixStack, context.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, list);
        matrixStack.pop();
        context.draw();
    }
}