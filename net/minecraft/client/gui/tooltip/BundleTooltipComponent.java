/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BundleTooltipComponent
implements TooltipComponent {
    private final DefaultedList<ItemStack> inventory;
    private final boolean hasSpace;

    public BundleTooltipComponent(BundleTooltipData bundleTooltipData) {
        this.inventory = bundleTooltipData.getInventory();
        this.hasSpace = bundleTooltipData.hasSpace();
    }

    @Override
    public int getHeight() {
        return 18 * (1 + (this.getStackCount() - 1) / this.getDisplayColumns()) + 4;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.getDisplayColumns() * 18;
    }

    private int getStackCount() {
        return this.inventory.size() + (this.hasSpace ? 1 : 0);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
        int i = 0;
        int j = 0;
        int k = this.getDisplayColumns();
        for (ItemStack itemStack : this.inventory) {
            this.drawSlot(matrices, i + x - 1, j + y - 1, z, textureManager, false);
            itemRenderer.renderInGuiWithOverrides(itemStack, x + i, y + j);
            itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x + i, y + j);
            if ((i += 18) < 18 * k) continue;
            i = 0;
            j += 18;
        }
        if (this.hasSpace) {
            this.drawSlot(matrices, i + x - 1, j + y - 1, z, textureManager, true);
        }
    }

    private void drawSlot(MatrixStack matrices, int x, int y, int z, TextureManager textureManager, boolean plusSlot) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        textureManager.bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
        DrawableHelper.drawTexture(matrices, x, y, z, 0.0f, plusSlot ? 36.0f : 0.0f, 18, 18, 128, 128);
    }

    private int getDisplayColumns() {
        return MathHelper.ceil(Math.sqrt(this.getStackCount()));
    }
}

