package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RecipeBookGhostSlots {
	@Nullable
	private Recipe<?> recipe;
	private final List<RecipeBookGhostSlots.GhostInputSlot> slots = Lists.<RecipeBookGhostSlots.GhostInputSlot>newArrayList();
	float time;

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

	public void draw(DrawContext context, MinecraftClient client, int x, int y, boolean notInventory, float tickDelta) {
		if (!Screen.hasControlDown()) {
			this.time += tickDelta;
		}

		for (int i = 0; i < this.slots.size(); i++) {
			RecipeBookGhostSlots.GhostInputSlot ghostInputSlot = (RecipeBookGhostSlots.GhostInputSlot)this.slots.get(i);
			int j = ghostInputSlot.getX() + x;
			int k = ghostInputSlot.getY() + y;
			if (i == 0 && notInventory) {
				context.fill(j - 4, k - 4, j + 20, k + 20, 822018048);
			} else {
				context.fill(j, k, j + 16, k + 16, 822018048);
			}

			ItemStack itemStack = ghostInputSlot.getCurrentItemStack();
			context.drawItemWithoutEntity(itemStack, j, k);
			RenderSystem.depthFunc(516);
			context.fill(j, k, j + 16, k + 16, 822083583);
			RenderSystem.depthFunc(515);
			if (i == 0) {
				context.drawItemInSlot(client.textRenderer, itemStack, j, k);
			}
		}
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
			ItemStack[] itemStacks = this.ingredient.getMatchingStacks();
			return itemStacks.length == 0 ? ItemStack.EMPTY : itemStacks[MathHelper.floor(RecipeBookGhostSlots.this.time / 30.0F) % itemStacks.length];
		}
	}
}
