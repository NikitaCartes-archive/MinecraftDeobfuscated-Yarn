package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.GuiLighting;
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

	public void addSlot(Ingredient ingredient, int i, int j) {
		this.slots.add(new RecipeBookGhostSlots.GhostInputSlot(ingredient, i, j));
	}

	public RecipeBookGhostSlots.GhostInputSlot getSlot(int i) {
		return (RecipeBookGhostSlots.GhostInputSlot)this.slots.get(i);
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

	public void draw(MinecraftClient minecraftClient, int i, int j, boolean bl, float f) {
		if (!Screen.isControlPressed()) {
			this.time += f;
		}

		GuiLighting.enableForItems();
		GlStateManager.disableLighting();

		for (int k = 0; k < this.slots.size(); k++) {
			RecipeBookGhostSlots.GhostInputSlot ghostInputSlot = (RecipeBookGhostSlots.GhostInputSlot)this.slots.get(k);
			int l = ghostInputSlot.getX() + i;
			int m = ghostInputSlot.getY() + j;
			if (k == 0 && bl) {
				DrawableHelper.drawRect(l - 4, m - 4, l + 20, m + 20, 822018048);
			} else {
				DrawableHelper.drawRect(l, m, l + 16, m + 16, 822018048);
			}

			ItemStack itemStack = ghostInputSlot.getCurrentItemStack();
			ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
			itemRenderer.renderGuiItem(minecraftClient.player, itemStack, l, m);
			GlStateManager.depthFunc(516);
			DrawableHelper.drawRect(l, m, l + 16, m + 16, 822083583);
			GlStateManager.depthFunc(515);
			if (k == 0) {
				itemRenderer.renderGuiItemOverlay(minecraftClient.textRenderer, itemStack, l, m);
			}

			GlStateManager.enableLighting();
		}

		GuiLighting.disable();
	}

	@Environment(EnvType.CLIENT)
	public class GhostInputSlot {
		private final Ingredient ingredient;
		private final int x;
		private final int y;

		public GhostInputSlot(Ingredient ingredient, int i, int j) {
			this.ingredient = ingredient;
			this.x = i;
			this.y = j;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public ItemStack getCurrentItemStack() {
			ItemStack[] itemStacks = this.ingredient.getStackArray();
			return itemStacks[MathHelper.floor(RecipeBookGhostSlots.this.time / 30.0F) % itemStacks.length];
		}
	}
}
