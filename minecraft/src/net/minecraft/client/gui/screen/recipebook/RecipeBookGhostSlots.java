package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RecipeBookGhostSlots {
	private Recipe<?> recipe;
	private final List<RecipeBookGhostSlots.GhostInputSlot> slots = Lists.<RecipeBookGhostSlots.GhostInputSlot>newArrayList();
	private float time;

	public void reset() {
		this.recipe = null;
		this.slots.clear();
		this.time = 0.0F;
	}

	public void addSlot(Ingredient ingredient, int x, int y) {
		this.slots.add(new RecipeBookGhostSlots.GhostInputSlot(ingredient, x, y));
	}

	public RecipeBookGhostSlots.GhostInputSlot getSlot(int index) {
		return (RecipeBookGhostSlots.GhostInputSlot)this.slots.get(index);
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

	public void draw(MinecraftClient client, int x, int y, boolean isBig, float lastFrameDuration) {
		if (!Screen.hasControlDown()) {
			this.time += lastFrameDuration;
		}

		DiffuseLighting.enableForItems();
		GlStateManager.disableLighting();

		for (int i = 0; i < this.slots.size(); i++) {
			RecipeBookGhostSlots.GhostInputSlot ghostInputSlot = (RecipeBookGhostSlots.GhostInputSlot)this.slots.get(i);
			int j = ghostInputSlot.getX() + x;
			int k = ghostInputSlot.getY() + y;
			if (i == 0 && isBig) {
				DrawableHelper.fill(j - 4, k - 4, j + 20, k + 20, 822018048);
			} else {
				DrawableHelper.fill(j, k, j + 16, k + 16, 822018048);
			}

			ItemStack itemStack = ghostInputSlot.getCurrentItemStack();
			ItemRenderer itemRenderer = client.getItemRenderer();
			itemRenderer.renderGuiItem(client.player, itemStack, j, k);
			GlStateManager.depthFunc(516);
			DrawableHelper.fill(j, k, j + 16, k + 16, 822083583);
			GlStateManager.depthFunc(515);
			if (i == 0) {
				itemRenderer.renderGuiItemOverlay(client.textRenderer, itemStack, j, k);
			}

			GlStateManager.enableLighting();
		}

		DiffuseLighting.disable();
	}

	@Environment(EnvType.CLIENT)
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
			return itemStacks[MathHelper.floor(RecipeBookGhostSlots.this.time / 30.0F) % itemStacks.length];
		}
	}
}
