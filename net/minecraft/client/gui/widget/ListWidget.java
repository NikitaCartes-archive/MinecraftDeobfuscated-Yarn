/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class ListWidget
extends AbstractParentElement
implements Drawable {
    protected final MinecraftClient client;
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected final int itemHeight;
    protected boolean centerListVertically = true;
    protected int yDrag = -2;
    protected double scrollAmount;
    protected boolean visible = true;
    protected boolean renderSelection = true;
    protected boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;

    public ListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.itemHeight = itemHeight;
        this.left = 0;
        this.right = width;
    }

    public boolean isVisible() {
        return this.visible;
    }

    protected abstract int getItemCount();

    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }

    protected boolean selectItem(int index, int button, double mouseX, double mouseY) {
        return true;
    }

    protected abstract boolean isSelectedItem(int var1);

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected abstract void renderBackground();

    protected void updateItemPosition(int index, int x, int y, float delta) {
    }

    protected abstract void renderItem(int var1, int var2, int var3, int var4, int var5, int var6, float var7);

    protected void renderHeader(int x, int y, Tessellator tessellator) {
    }

    protected void clickedHeader(int i, int j) {
    }

    protected void renderDecorations(int mouseX, int mouseY) {
    }

    public int getItemAtPosition(double mouseX, double mouseY) {
        int i = this.left + this.width / 2 - this.getRowWidth() / 2;
        int j = this.left + this.width / 2 + this.getRowWidth() / 2;
        int k = MathHelper.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.scrollAmount - 4;
        int l = k / this.itemHeight;
        if (mouseX < (double)this.getScrollbarPosition() && mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
            return l;
        }
        return -1;
    }

    protected void capYPosition() {
        this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    public boolean isMouseInList(double mouseX, double mouseY) {
        return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        this.renderBackground();
        int i = this.getScrollbarPosition();
        int j = i + 6;
        this.capYPosition();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.bottom, 0.0).texture((float)this.left / 32.0f, (float)(this.bottom + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.right, this.bottom, 0.0).texture((float)this.right / 32.0f, (float)(this.bottom + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.right, this.top, 0.0).texture((float)this.right / 32.0f, (float)(this.top + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.left, this.top, 0.0).texture((float)this.left / 32.0f, (float)(this.top + (int)this.scrollAmount) / 32.0f).color(32, 32, 32, 255).next();
        tessellator.draw();
        int k = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
        int l = this.top + 4 - (int)this.scrollAmount;
        if (this.renderHeader) {
            this.renderHeader(k, l, tessellator);
        }
        this.renderList(k, l, mouseX, mouseY, delta);
        RenderSystem.disableDepthTest();
        this.renderHoleBackground(0, this.top, 255, 255);
        this.renderHoleBackground(this.bottom, this.height, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        int m = 4;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.top + 4, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top + 4, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.left, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
        tessellator.draw();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom - 4, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.left, this.bottom - 4, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 0).next();
        tessellator.draw();
        int n = this.getMaxScroll();
        if (n > 0) {
            int o = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            int p = (int)this.scrollAmount * (this.bottom - this.top - (o = MathHelper.clamp(o, 32, this.bottom - this.top - 8))) / n + this.top;
            if (p < this.top) {
                p = this.top;
            }
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(i, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(j, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(j, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(i, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
            tessellator.draw();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(i, p + o, 0.0).texture(0.0f, 1.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, p + o, 0.0).texture(1.0f, 1.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, p, 0.0).texture(1.0f, 0.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i, p, 0.0).texture(0.0f, 0.0f).color(128, 128, 128, 255).next();
            tessellator.draw();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(i, p + o - 1, 0.0).texture(0.0f, 1.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, p + o - 1, 0.0).texture(1.0f, 1.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, p, 0.0).texture(1.0f, 0.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(i, p, 0.0).texture(0.0f, 0.0f).color(192, 192, 192, 255).next();
            tessellator.draw();
        }
        this.renderDecorations(mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPosition() && mouseX < (double)(this.getScrollbarPosition() + 6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isVisible() || !this.isMouseInList(mouseX, mouseY)) {
            return false;
        }
        int i = this.getItemAtPosition(mouseX, mouseY);
        if (i == -1 && button == 0) {
            this.clickedHeader((int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.scrollAmount - 4);
            return true;
        }
        if (i != -1 && this.selectItem(i, button, mouseX, mouseY)) {
            if (this.children().size() > i) {
                this.setFocused(this.children().get(i));
            }
            this.setDragging(true);
            return true;
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (!this.isVisible() || button != 0 || !this.scrolling) {
            return false;
        }
        if (mouseY < (double)this.top) {
            this.scrollAmount = 0.0;
        } else if (mouseY > (double)this.bottom) {
            this.scrollAmount = this.getMaxScroll();
        } else {
            double d = this.getMaxScroll();
            if (d < 1.0) {
                d = 1.0;
            }
            int i = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            double e = d / (double)(this.bottom - this.top - (i = MathHelper.clamp(i, 32, this.bottom - this.top - 8)));
            if (e < 1.0) {
                e = 1.0;
            }
            this.scrollAmount += deltaY * e;
            this.capYPosition();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.isVisible()) {
            return false;
        }
        this.scrollAmount -= amount * (double)this.itemHeight / 2.0;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isVisible()) {
            return false;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 264) {
            this.moveSelection(1);
            return true;
        }
        if (keyCode == 265) {
            this.moveSelection(-1);
            return true;
        }
        return false;
    }

    protected void moveSelection(int by) {
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (!this.isVisible()) {
            return false;
        }
        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isMouseInList(mouseX, mouseY);
    }

    public int getRowWidth() {
        return 220;
    }

    protected void renderList(int x, int y, int mouseX, int mouseY, float delta) {
        int i = this.getItemCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int j = 0; j < i; ++j) {
            int k = y + j * this.itemHeight + this.headerHeight;
            int l = this.itemHeight - 4;
            if (k > this.bottom || k + l < this.top) {
                this.updateItemPosition(j, x, k, delta);
            }
            if (this.renderSelection && this.isSelectedItem(j)) {
                int m = this.left + this.width / 2 - this.getRowWidth() / 2;
                int n = this.left + this.width / 2 + this.getRowWidth() / 2;
                RenderSystem.disableTexture();
                float f = this.isFocused() ? 1.0f : 0.5f;
                RenderSystem.color4f(f, f, f, 1.0f);
                bufferBuilder.begin(7, VertexFormats.POSITION);
                bufferBuilder.vertex(m, k + l + 2, 0.0).next();
                bufferBuilder.vertex(n, k + l + 2, 0.0).next();
                bufferBuilder.vertex(n, k - 2, 0.0).next();
                bufferBuilder.vertex(m, k - 2, 0.0).next();
                tessellator.draw();
                RenderSystem.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                bufferBuilder.begin(7, VertexFormats.POSITION);
                bufferBuilder.vertex(m + 1, k + l + 1, 0.0).next();
                bufferBuilder.vertex(n - 1, k + l + 1, 0.0).next();
                bufferBuilder.vertex(n - 1, k - 1, 0.0).next();
                bufferBuilder.vertex(m + 1, k - 1, 0.0).next();
                tessellator.draw();
                RenderSystem.enableTexture();
            }
            this.renderItem(j, x, k, l, mouseX, mouseY, delta);
        }
    }

    protected boolean isFocused() {
        return false;
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    protected void renderHoleBackground(int top, int bottom, int topAlpha, int bottomAlpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, bottom, 0.0).texture(0.0f, (float)bottom / 32.0f).color(64, 64, 64, bottomAlpha).next();
        bufferBuilder.vertex(this.left + this.width, bottom, 0.0).texture((float)this.width / 32.0f, (float)bottom / 32.0f).color(64, 64, 64, bottomAlpha).next();
        bufferBuilder.vertex(this.left + this.width, top, 0.0).texture((float)this.width / 32.0f, (float)top / 32.0f).color(64, 64, 64, topAlpha).next();
        bufferBuilder.vertex(this.left, top, 0.0).texture(0.0f, (float)top / 32.0f).color(64, 64, 64, topAlpha).next();
        tessellator.draw();
    }
}

