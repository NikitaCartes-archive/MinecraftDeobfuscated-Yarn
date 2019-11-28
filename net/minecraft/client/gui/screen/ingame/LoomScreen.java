/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LoomScreen
extends AbstractContainerScreen<LoomContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
    private static final int PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
    private final ModelPart field_21694;
    @Nullable
    private BannerBlockEntity preview;
    private ItemStack banner = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean canApplySpecialPattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int firstPatternButtonId = 1;

    public LoomScreen(LoomContainer loomContainer, PlayerInventory playerInventory, Text text) {
        super(loomContainer, playerInventory, text);
        this.field_21694 = BannerBlockEntityRenderer.createField();
        loomContainer.setInventoryChangeListener(this::onInventoryChanged);
    }

    @Override
    public void render(int i, int j, float f) {
        super.render(i, j, f);
        this.drawMouseoverTooltip(i, j);
    }

    @Override
    protected void drawForeground(int i, int j) {
        this.font.draw(this.title.asFormattedString(), 8.0f, 4.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = this.x;
        int l = this.y;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        Slot slot = ((LoomContainer)this.container).getBannerSlot();
        Slot slot2 = ((LoomContainer)this.container).getDyeSlot();
        Slot slot3 = ((LoomContainer)this.container).getPatternSlot();
        Slot slot4 = ((LoomContainer)this.container).getOutputSlot();
        if (!slot.hasStack()) {
            this.blit(k + slot.xPosition, l + slot.yPosition, this.containerWidth, 0, 16, 16);
        }
        if (!slot2.hasStack()) {
            this.blit(k + slot2.xPosition, l + slot2.yPosition, this.containerWidth + 16, 0, 16, 16);
        }
        if (!slot3.hasStack()) {
            this.blit(k + slot3.xPosition, l + slot3.yPosition, this.containerWidth + 32, 0, 16, 16);
        }
        int m = (int)(41.0f * this.scrollPosition);
        this.blit(k + 119, l + 13 + m, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
        if (this.preview != null && !this.hasTooManyPatterns) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(k + 139, l + 52, 0.0f);
            RenderSystem.scalef(24.0f, -24.0f, 1.0f);
            this.preview.setPreview(true);
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.preview, new MatrixStack());
            this.preview.setPreview(false);
            RenderSystem.popMatrix();
        } else if (this.hasTooManyPatterns) {
            this.blit(k + slot4.xPosition - 2, l + slot4.yPosition - 2, this.containerWidth, 17, 17, 16);
        }
        if (this.canApplyDyePattern) {
            int n = k + 60;
            int o = l + 13;
            int p = this.firstPatternButtonId + 16;
            for (int q = this.firstPatternButtonId; q < p && q < BannerPattern.COUNT - 5; ++q) {
                int r = q - this.firstPatternButtonId;
                int s = n + r % 4 * 14;
                int t = o + r / 4 * 14;
                this.minecraft.getTextureManager().bindTexture(TEXTURE);
                int u = this.containerHeight;
                if (q == ((LoomContainer)this.container).getSelectedPattern()) {
                    u += 14;
                } else if (i >= s && j >= t && i < s + 14 && j < t + 14) {
                    u += 28;
                }
                this.blit(s, t, 0, u, 14, 14);
                this.method_22692(q, s, t);
            }
        } else if (this.canApplySpecialPattern) {
            int n = k + 60;
            int o = l + 13;
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.blit(n, o, 0, this.containerHeight, 14, 14);
            int p = ((LoomContainer)this.container).getSelectedPattern();
            this.method_22692(p, n, o);
        }
    }

    private void method_22692(int i, int j, int k) {
        BannerBlockEntity bannerBlockEntity = new BannerBlockEntity();
        bannerBlockEntity.setPreview(true);
        ItemStack itemStack = new ItemStack(Items.GRAY_BANNER);
        CompoundTag compoundTag = itemStack.getOrCreateSubTag("BlockEntityTag");
        ListTag listTag = new BannerPattern.Patterns().add(BannerPattern.BASE, DyeColor.GRAY).add(BannerPattern.values()[i], DyeColor.WHITE).toTag();
        compoundTag.put("Patterns", listTag);
        bannerBlockEntity.readFrom(itemStack, DyeColor.GRAY);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate((float)j + 0.5f, k + 16, 0.0);
        matrixStack.scale(6.0f, -6.0f, 1.0f);
        matrixStack.translate(0.5, 0.5, 0.0);
        float f = 0.6666667f;
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumerProvider.Immediate immediate = this.minecraft.getBufferBuilders().getEntityVertexConsumers();
        this.field_21694.pitch = 0.0f;
        this.field_21694.pivotY = -32.0f;
        BannerBlockEntityRenderer.method_23802(bannerBlockEntity, matrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, this.field_21694, ModelLoader.BANNER_BASE, true);
        matrixStack.pop();
        immediate.draw();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        this.scrollbarClicked = false;
        if (this.canApplyDyePattern) {
            int j = this.x + 60;
            int k = this.y + 13;
            int l = this.firstPatternButtonId + 16;
            for (int m = this.firstPatternButtonId; m < l; ++m) {
                int n = m - this.firstPatternButtonId;
                double f = d - (double)(j + n % 4 * 14);
                double g = e - (double)(k + n / 4 * 14);
                if (!(f >= 0.0) || !(g >= 0.0) || !(f < 14.0) || !(g < 14.0) || !((LoomContainer)this.container).onButtonClick(this.minecraft.player, m)) continue;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                this.minecraft.interactionManager.clickButton(((LoomContainer)this.container).syncId, m);
                return true;
            }
            j = this.x + 119;
            k = this.y + 9;
            if (d >= (double)j && d < (double)(j + 12) && e >= (double)k && e < (double)(k + 56)) {
                this.scrollbarClicked = true;
            }
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.scrollbarClicked && this.canApplyDyePattern) {
            int j = this.y + 13;
            int k = j + 56;
            this.scrollPosition = ((float)e - (float)j - 7.5f) / ((float)(k - j) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            int l = PATTERN_BUTTON_ROW_COUNT - 4;
            int m = (int)((double)(this.scrollPosition * (float)l) + 0.5);
            if (m < 0) {
                m = 0;
            }
            this.firstPatternButtonId = 1 + m * 4;
            return true;
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (this.canApplyDyePattern) {
            int i = PATTERN_BUTTON_ROW_COUNT - 4;
            this.scrollPosition = (float)((double)this.scrollPosition - f / (double)i);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.firstPatternButtonId = 1 + (int)((double)(this.scrollPosition * (float)i) + 0.5) * 4;
        }
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
        return d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
    }

    private void onInventoryChanged() {
        ItemStack itemStack = ((LoomContainer)this.container).getOutputSlot().getStack();
        if (itemStack.isEmpty()) {
            this.preview = null;
        } else {
            this.preview = new BannerBlockEntity();
            this.preview.readFrom(itemStack, ((BannerItem)itemStack.getItem()).getColor());
        }
        ItemStack itemStack2 = ((LoomContainer)this.container).getBannerSlot().getStack();
        ItemStack itemStack3 = ((LoomContainer)this.container).getDyeSlot().getStack();
        ItemStack itemStack4 = ((LoomContainer)this.container).getPatternSlot().getStack();
        CompoundTag compoundTag = itemStack2.getOrCreateSubTag("BlockEntityTag");
        boolean bl = this.hasTooManyPatterns = compoundTag.contains("Patterns", 9) && !itemStack2.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
        if (this.hasTooManyPatterns) {
            this.preview = null;
        }
        if (!(ItemStack.areEqualIgnoreDamage(itemStack2, this.banner) && ItemStack.areEqualIgnoreDamage(itemStack3, this.dye) && ItemStack.areEqualIgnoreDamage(itemStack4, this.pattern))) {
            this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.hasTooManyPatterns;
            this.canApplySpecialPattern = !this.hasTooManyPatterns && !itemStack4.isEmpty() && !itemStack2.isEmpty() && !itemStack3.isEmpty();
        }
        this.banner = itemStack2.copy();
        this.dye = itemStack3.copy();
        this.pattern = itemStack4.copy();
    }
}

