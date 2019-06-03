/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CartographyTableScreen
extends AbstractContainerScreen<CartographyTableContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/cartography_table.png");

    public CartographyTableScreen(CartographyTableContainer cartographyTableContainer, PlayerInventory playerInventory, Text text) {
        super(cartographyTableContainer, playerInventory, text);
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
        MapState mapState;
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = this.left;
        int l = this.top;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        Item item = ((CartographyTableContainer)this.container).getSlot(1).getStack().getItem();
        boolean bl = item == Items.MAP;
        boolean bl2 = item == Items.PAPER;
        boolean bl3 = item == Items.GLASS_PANE;
        ItemStack itemStack = ((CartographyTableContainer)this.container).getSlot(0).getStack();
        boolean bl4 = false;
        if (itemStack.getItem() == Items.FILLED_MAP) {
            mapState = FilledMapItem.getMapState(itemStack, this.minecraft.world);
            if (mapState != null) {
                if (mapState.locked) {
                    bl4 = true;
                    if (bl2 || bl3) {
                        this.blit(k + 35, l + 31, this.containerWidth + 50, 132, 28, 21);
                    }
                }
                if (bl2 && mapState.scale >= 4) {
                    bl4 = true;
                    this.blit(k + 35, l + 31, this.containerWidth + 50, 132, 28, 21);
                }
            }
        } else {
            mapState = null;
        }
        this.drawMap(mapState, bl, bl2, bl3, bl4);
    }

    private void drawMap(@Nullable MapState mapState, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = this.left;
        int j = this.top;
        if (bl2 && !bl4) {
            this.blit(i + 67, j + 13, this.containerWidth, 66, 66, 66);
            this.drawMap(mapState, i + 85, j + 31, 0.226f);
        } else if (bl) {
            this.blit(i + 67 + 16, j + 13, this.containerWidth, 132, 50, 66);
            this.drawMap(mapState, i + 86, j + 16, 0.34f);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, 1.0f);
            this.blit(i + 67, j + 13 + 16, this.containerWidth, 132, 50, 66);
            this.drawMap(mapState, i + 70, j + 32, 0.34f);
            GlStateManager.popMatrix();
        } else if (bl3) {
            this.blit(i + 67, j + 13, this.containerWidth, 0, 66, 66);
            this.drawMap(mapState, i + 71, j + 17, 0.45f);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, 1.0f);
            this.blit(i + 66, j + 12, 0, this.containerHeight, 66, 66);
            GlStateManager.popMatrix();
        } else {
            this.blit(i + 67, j + 13, this.containerWidth, 0, 66, 66);
            this.drawMap(mapState, i + 71, j + 17, 0.45f);
        }
    }

    private void drawMap(@Nullable MapState mapState, int i, int j, float f) {
        if (mapState != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(i, j, 1.0f);
            GlStateManager.scalef(f, f, 1.0f);
            this.minecraft.gameRenderer.getMapRenderer().draw(mapState, true);
            GlStateManager.popMatrix();
        }
    }
}

