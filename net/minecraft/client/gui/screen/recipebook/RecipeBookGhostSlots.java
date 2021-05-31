/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RecipeBookGhostSlots {
    @Nullable
    private Recipe<?> recipe;
    private final List<GhostInputSlot> slots = Lists.newArrayList();
    float time;

    public void reset() {
        this.recipe = null;
        this.slots.clear();
        this.time = 0.0f;
    }

    public void addSlot(Ingredient ingredient, int x, int y) {
        this.slots.add(new GhostInputSlot(ingredient, x, y));
    }

    public GhostInputSlot getSlot(int index) {
        return this.slots.get(index);
    }

    public int getSlotCount() {
        return this.slots.size();
    }

    @Nullable
    public Recipe<?> getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe<?> recipe) {
        this.recipe = recipe;
    }

    public void draw(MatrixStack matrices, MinecraftClient client, int i, int j, boolean bl, float f) {
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        for (int k = 0; k < this.slots.size(); ++k) {
            GhostInputSlot ghostInputSlot = this.slots.get(k);
            int l = ghostInputSlot.getX() + i;
            int m = ghostInputSlot.getY() + j;
            if (k == 0 && bl) {
                DrawableHelper.fill(matrices, l - 4, m - 4, l + 20, m + 20, 0x30FF0000);
            } else {
                DrawableHelper.fill(matrices, l, m, l + 16, m + 16, 0x30FF0000);
            }
            ItemStack itemStack = ghostInputSlot.getCurrentItemStack();
            ItemRenderer itemRenderer = client.getItemRenderer();
            itemRenderer.renderInGui(itemStack, l, m);
            RenderSystem.depthFunc(516);
            DrawableHelper.fill(matrices, l, m, l + 16, m + 16, 0x30FFFFFF);
            RenderSystem.depthFunc(515);
            if (k != 0) continue;
            itemRenderer.renderGuiItemOverlay(client.textRenderer, itemStack, l, m);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class GhostInputSlot {
        private final Ingredient ingredient;
        private final int x;
        private final int y;

        public GhostInputSlot(Ingredient ingredient, int x, int y) {
            this.ingredient = ingredient;
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getCurrentItemStack() {
            ItemStack[] itemStacks = this.ingredient.getMatchingStacksClient();
            if (itemStacks.length == 0) {
                return ItemStack.EMPTY;
            }
            return itemStacks[MathHelper.floor(RecipeBookGhostSlots.this.time / 30.0f) % itemStacks.length];
        }
    }
}

