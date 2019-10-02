/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen
extends RecipeBookWidget {
    private Iterator<Item> fuelIterator;
    private Set<Item> fuels;
    private Slot outputSlot;
    private Item currentItem;
    private float frameTime;

    @Override
    protected boolean toggleFilteringCraftable() {
        boolean bl = !this.isFilteringCraftable();
        this.setFilteringCraftable(bl);
        return bl;
    }

    protected abstract boolean isFilteringCraftable();

    protected abstract void setFilteringCraftable(boolean var1);

    @Override
    public boolean isOpen() {
        return this.isGuiOpen();
    }

    protected abstract boolean isGuiOpen();

    @Override
    protected void setOpen(boolean bl) {
        this.setGuiOpen(bl);
        if (!bl) {
            this.recipesArea.hideAlternates();
        }
        this.sendBookDataPacket();
    }

    protected abstract void setGuiOpen(boolean var1);

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, TEXTURE);
    }

    @Override
    protected String getCraftableButtonText() {
        return I18n.translate(this.toggleCraftableButton.isToggled() ? this.getToggleCraftableButtonText() : "gui.recipebook.toggleRecipes.all", new Object[0]);
    }

    protected abstract String getToggleCraftableButtonText();

    @Override
    public void slotClicked(@Nullable Slot slot) {
        super.slotClicked(slot);
        if (slot != null && slot.id < this.craftingContainer.getCraftingSlotCount()) {
            this.outputSlot = null;
        }
    }

    @Override
    public void showGhostRecipe(Recipe<?> recipe, List<Slot> list) {
        ItemStack itemStack = recipe.getOutput();
        this.ghostSlots.setRecipe(recipe);
        this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), list.get((int)2).xPosition, list.get((int)2).yPosition);
        DefaultedList<Ingredient> defaultedList = recipe.getPreviewInputs();
        this.outputSlot = list.get(1);
        if (this.fuels == null) {
            this.fuels = this.getAllowedFuels();
        }
        this.fuelIterator = this.fuels.iterator();
        this.currentItem = null;
        Iterator iterator = defaultedList.iterator();
        for (int i = 0; i < 2; ++i) {
            if (!iterator.hasNext()) {
                return;
            }
            Ingredient ingredient = (Ingredient)iterator.next();
            if (ingredient.isEmpty()) continue;
            Slot slot = list.get(i);
            this.ghostSlots.addSlot(ingredient, slot.xPosition, slot.yPosition);
        }
    }

    protected abstract Set<Item> getAllowedFuels();

    @Override
    public void drawGhostSlots(int i, int j, boolean bl, float f) {
        super.drawGhostSlots(i, j, bl, f);
        if (this.outputSlot == null) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.frameTime += f;
        }
        int k = this.outputSlot.xPosition + i;
        int l = this.outputSlot.yPosition + j;
        DrawableHelper.fill(k, l, k + 16, l + 16, 0x30FF0000);
        this.client.getItemRenderer().renderGuiItem(this.client.player, this.getItem().getStackForRender(), k, l);
        RenderSystem.depthFunc(516);
        DrawableHelper.fill(k, l, k + 16, l + 16, 0x30FFFFFF);
        RenderSystem.depthFunc(515);
    }

    private Item getItem() {
        if (this.currentItem == null || this.frameTime > 30.0f) {
            this.frameTime = 0.0f;
            if (this.fuelIterator == null || !this.fuelIterator.hasNext()) {
                if (this.fuels == null) {
                    this.fuels = this.getAllowedFuels();
                }
                this.fuelIterator = this.fuels.iterator();
            }
            this.currentItem = this.fuelIterator.next();
        }
        return this.currentItem;
    }
}

