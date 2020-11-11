/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen
extends RecipeBookWidget {
    @Nullable
    private Ingredient fuels;

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, TEXTURE);
    }

    @Override
    public void slotClicked(@Nullable Slot slot) {
        super.slotClicked(slot);
        if (slot != null && slot.id < this.craftingScreenHandler.getCraftingSlotCount()) {
            this.ghostSlots.reset();
        }
    }

    @Override
    public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
        ItemStack itemStack = recipe.getOutput();
        this.ghostSlots.setRecipe(recipe);
        this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), slots.get((int)2).x, slots.get((int)2).y);
        DefaultedList<Ingredient> defaultedList = recipe.getPreviewInputs();
        Slot slot = slots.get(1);
        if (slot.getStack().isEmpty()) {
            if (this.fuels == null) {
                this.fuels = Ingredient.ofStacks(this.getAllowedFuels().stream().map(ItemStack::new));
            }
            this.ghostSlots.addSlot(this.fuels, slot.x, slot.y);
        }
        Iterator iterator = defaultedList.iterator();
        for (int i = 0; i < 2; ++i) {
            if (!iterator.hasNext()) {
                return;
            }
            Ingredient ingredient = (Ingredient)iterator.next();
            if (ingredient.isEmpty()) continue;
            Slot slot2 = slots.get(i);
            this.ghostSlots.addSlot(ingredient, slot2.x, slot2.y);
        }
    }

    protected abstract Set<Item> getAllowedFuels();
}

