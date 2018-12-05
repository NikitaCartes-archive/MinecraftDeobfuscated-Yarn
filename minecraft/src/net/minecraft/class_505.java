package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_505 {
	private Recipe field_3079;
	private final List<class_505.class_506> field_3081 = Lists.<class_505.class_506>newArrayList();
	private float field_3080;

	public void method_2571() {
		this.field_3079 = null;
		this.field_3081.clear();
		this.field_3080 = 0.0F;
	}

	public void method_2569(Ingredient ingredient, int i, int j) {
		this.field_3081.add(new class_505.class_506(ingredient, i, j));
	}

	public class_505.class_506 method_2570(int i) {
		return (class_505.class_506)this.field_3081.get(i);
	}

	public int method_2572() {
		return this.field_3081.size();
	}

	@Nullable
	public Recipe method_2566() {
		return this.field_3079;
	}

	public void method_2565(Recipe recipe) {
		this.field_3079 = recipe;
	}

	public void method_2567(MinecraftClient minecraftClient, int i, int j, boolean bl, float f) {
		if (!Gui.isControlPressed()) {
			this.field_3080 += f;
		}

		class_308.method_1453();
		GlStateManager.disableLighting();

		for (int k = 0; k < this.field_3081.size(); k++) {
			class_505.class_506 lv = (class_505.class_506)this.field_3081.get(k);
			int l = lv.method_2574() + i;
			int m = lv.method_2575() + j;
			if (k == 0 && bl) {
				Drawable.drawRect(l - 4, m - 4, l + 20, m + 20, 822018048);
			} else {
				Drawable.drawRect(l, m, l + 16, m + 16, 822018048);
			}

			ItemStack itemStack = lv.method_2573();
			ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
			itemRenderer.renderItemInGui(minecraftClient.player, itemStack, l, m);
			GlStateManager.depthFunc(516);
			Drawable.drawRect(l, m, l + 16, m + 16, 822083583);
			GlStateManager.depthFunc(515);
			if (k == 0) {
				itemRenderer.renderItemOverlaysInGUI(minecraftClient.fontRenderer, itemStack, l, m);
			}

			GlStateManager.enableLighting();
		}

		class_308.method_1450();
	}

	@Environment(EnvType.CLIENT)
	public class class_506 {
		private final Ingredient field_3082;
		private final int field_3084;
		private final int field_3083;

		public class_506(Ingredient ingredient, int i, int j) {
			this.field_3082 = ingredient;
			this.field_3084 = i;
			this.field_3083 = j;
		}

		public int method_2574() {
			return this.field_3084;
		}

		public int method_2575() {
			return this.field_3083;
		}

		public ItemStack method_2573() {
			ItemStack[] itemStacks = this.field_3082.getStackArray();
			return itemStacks[MathHelper.floor(class_505.this.field_3080 / 30.0F) % itemStacks.length];
		}
	}
}
