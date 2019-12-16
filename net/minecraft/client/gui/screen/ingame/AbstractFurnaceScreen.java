/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
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
extends AbstractContainerScreen<T>
implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    public final AbstractFurnaceRecipeBookScreen recipeBook;
    private boolean narrow;
    private final Identifier background;

    public AbstractFurnaceScreen(T container, AbstractFurnaceRecipeBookScreen recipeBook, PlayerInventory inventory, Text title, Identifier background) {
        super(container, inventory, title);
        this.recipeBook = recipeBook;
        this.background = background;
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
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(delta, mouseX, mouseY);
            this.recipeBook.render(mouseX, mouseY, delta);
        } else {
            this.recipeBook.render(mouseX, mouseY, delta);
            super.render(mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(this.x, this.y, true, delta);
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
        this.recipeBook.drawTooltip(this.x, this.y, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        String string = this.title.asFormattedString();
        this.font.draw(string, this.containerWidth / 2 - this.font.getStringWidth(string) / 2, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        int k;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(this.background);
        int i = this.x;
        int j = this.y;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        if (((AbstractFurnaceContainer)this.container).isBurning()) {
            k = ((AbstractFurnaceContainer)this.container).getFuelProgress();
            this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }
        k = ((AbstractFurnaceContainer)this.container).getCookProgress();
        this.blit(i + 79, j + 34, 176, 14, k + 1, 16);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.narrow && this.recipeBook.isOpen()) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        super.onMouseClick(slot, invSlot, button, slotActionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.recipeBook.keyPressed(keyCode, scanCode, modifiers)) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.containerWidth) || mouseY >= (double)(top + this.containerHeight);
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.containerWidth, this.containerHeight, button) && bl;
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (this.recipeBook.charTyped(chr, keyCode)) {
            return true;
        }
        return super.charTyped(chr, keyCode);
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

