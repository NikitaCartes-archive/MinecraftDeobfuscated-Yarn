/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceContainer>
extends ContainerScreen<T>
implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    public final AbstractFurnaceRecipeBookScreen recipeBook;
    private boolean narrow;
    private final Identifier field_18975;

    public AbstractFurnaceScreen(T abstractFurnaceContainer, AbstractFurnaceRecipeBookScreen abstractFurnaceRecipeBookScreen, PlayerInventory playerInventory, Text text, Identifier identifier) {
        super(abstractFurnaceContainer, playerInventory, text);
        this.recipeBook = abstractFurnaceRecipeBookScreen;
        this.field_18975 = identifier;
    }

    @Override
    public void init() {
        super.init();
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.minecraft, this.narrow, (CraftingContainer)this.container);
        this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.containerWidth);
        this.addButton(new TexturedButtonWidget(this.x + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, buttonWidget -> {
            this.recipeBook.reset(this.narrow);
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.containerWidth);
            ((TexturedButtonWidget)buttonWidget).setPos(this.x + 20, this.height / 2 - 49);
        }));
    }

    @Override
    public void tick() {
        super.tick();
        this.recipeBook.update();
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(f, i, j);
            this.recipeBook.render(i, j, f);
        } else {
            this.recipeBook.render(i, j, f);
            super.render(i, j, f);
            this.recipeBook.drawGhostSlots(this.x, this.y, true, f);
        }
        this.drawMouseoverTooltip(i, j);
        this.recipeBook.drawTooltip(this.x, this.y, i, j);
    }

    @Override
    protected void drawForeground(int i, int j) {
        String string = this.title.asFormattedString();
        this.font.draw(string, this.containerWidth / 2 - this.font.getStringWidth(string) / 2, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        int m;
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(this.field_18975);
        int k = this.x;
        int l = this.y;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        if (((AbstractFurnaceContainer)this.container).isBurning()) {
            m = ((AbstractFurnaceContainer)this.container).getFuelProgress();
            this.blit(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
        }
        m = ((AbstractFurnaceContainer)this.container).getCookProgress();
        this.blit(k + 79, l + 34, 176, 14, m + 1, 16);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.recipeBook.mouseClicked(d, e, i)) {
            return true;
        }
        if (this.narrow && this.recipeBook.isOpen()) {
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
        super.onMouseClick(slot, i, j, slotActionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.recipeBook.keyPressed(i, j, k)) {
            return false;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
        return this.recipeBook.isClickOutsideBounds(d, e, this.x, this.y, this.containerWidth, this.containerHeight, k) && bl;
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.recipeBook.charTyped(c, i)) {
            return true;
        }
        return super.charTyped(c, i);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }

    @Override
    public void removed() {
        this.recipeBook.close();
        super.removed();
    }
}

