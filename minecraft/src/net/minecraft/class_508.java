package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_508 extends Drawable implements GuiEventListener {
	private static final Identifier field_3109 = new Identifier("textures/gui/recipe_book.png");
	private final List<class_508.class_509> field_3106 = Lists.<class_508.class_509>newArrayList();
	private boolean field_3107;
	private int field_3105;
	private int field_3103;
	private MinecraftClient field_3108;
	private class_516 field_3111;
	private Recipe field_3104;
	private float field_3110;
	private boolean field_3112;

	public void method_2617(MinecraftClient minecraftClient, class_516 arg, int i, int j, int k, int l, float f) {
		this.field_3108 = minecraftClient;
		this.field_3111 = arg;
		if (minecraftClient.player.container instanceof FurnaceContainer) {
			this.field_3112 = true;
		}

		boolean bl = minecraftClient.player.getRecipeBook().isFilteringCraftable((CraftingContainer)minecraftClient.player.container);
		List<Recipe> list = arg.method_2648(true);
		List<Recipe> list2 = bl ? Collections.emptyList() : arg.method_2648(false);
		int m = list.size();
		int n = m + list2.size();
		int o = n <= 16 ? 4 : 5;
		int p = (int)Math.ceil((double)((float)n / (float)o));
		this.field_3105 = i;
		this.field_3103 = j;
		int q = 25;
		float g = (float)(this.field_3105 + Math.min(n, o) * 25);
		float h = (float)(k + 50);
		if (g > h) {
			this.field_3105 = (int)((float)this.field_3105 - f * (float)((int)((g - h) / f)));
		}

		float r = (float)(this.field_3103 + p * 25);
		float s = (float)(l + 50);
		if (r > s) {
			this.field_3103 = (int)((float)this.field_3103 - f * (float)MathHelper.ceil((r - s) / f));
		}

		float t = (float)this.field_3103;
		float u = (float)(l - 100);
		if (t < u) {
			this.field_3103 = (int)((float)this.field_3103 - f * (float)MathHelper.ceil((t - u) / f));
		}

		this.field_3107 = true;
		this.field_3106.clear();

		for (int v = 0; v < n; v++) {
			boolean bl2 = v < m;
			Recipe recipe = bl2 ? (Recipe)list.get(v) : (Recipe)list2.get(v - m);
			int w = this.field_3105 + 4 + 25 * (v % o);
			int x = this.field_3103 + 5 + 25 * (v / o);
			if (this.field_3112) {
				this.field_3106.add(new class_508.class_511(w, x, recipe, bl2));
			} else {
				this.field_3106.add(new class_508.class_509(w, x, recipe, bl2));
			}
		}

		this.field_3104 = null;
	}

	public class_516 method_2614() {
		return this.field_3111;
	}

	public Recipe method_2615() {
		return this.field_3104;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i != 0) {
			return false;
		} else {
			for (class_508.class_509 lv : this.field_3106) {
				if (lv.mouseClicked(d, e, i)) {
					this.field_3104 = lv.field_3114;
					return true;
				}
			}

			return false;
		}
	}

	public void method_2612(int i, int j, float f) {
		if (this.field_3107) {
			this.field_3110 += f;
			class_308.method_1453();
			GlStateManager.enableBlend();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_3108.getTextureManager().bindTexture(field_3109);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 170.0F);
			int k = this.field_3106.size() <= 16 ? 4 : 5;
			int l = Math.min(this.field_3106.size(), k);
			int m = MathHelper.ceil((float)this.field_3106.size() / (float)k);
			int n = 24;
			int o = 4;
			int p = 82;
			int q = 208;
			this.method_2618(l, m, 24, 4, 82, 208);
			GlStateManager.disableBlend();
			class_308.method_1450();

			for (class_508.class_509 lv : this.field_3106) {
				lv.draw(i, j, f);
			}

			GlStateManager.popMatrix();
		}
	}

	private void method_2618(int i, int j, int k, int l, int m, int n) {
		this.drawTexturedRect(this.field_3105, this.field_3103, m, n, l, l);
		this.drawTexturedRect(this.field_3105 + l * 2 + i * k, this.field_3103, m + k + l, n, l, l);
		this.drawTexturedRect(this.field_3105, this.field_3103 + l * 2 + j * k, m, n + k + l, l, l);
		this.drawTexturedRect(this.field_3105 + l * 2 + i * k, this.field_3103 + l * 2 + j * k, m + k + l, n + k + l, l, l);

		for (int o = 0; o < i; o++) {
			this.drawTexturedRect(this.field_3105 + l + o * k, this.field_3103, m + l, n, k, l);
			this.drawTexturedRect(this.field_3105 + l + (o + 1) * k, this.field_3103, m + l, n, l, l);

			for (int p = 0; p < j; p++) {
				if (o == 0) {
					this.drawTexturedRect(this.field_3105, this.field_3103 + l + p * k, m, n + l, l, k);
					this.drawTexturedRect(this.field_3105, this.field_3103 + l + (p + 1) * k, m, n + l, l, l);
				}

				this.drawTexturedRect(this.field_3105 + l + o * k, this.field_3103 + l + p * k, m + l, n + l, k, k);
				this.drawTexturedRect(this.field_3105 + l + (o + 1) * k, this.field_3103 + l + p * k, m + l, n + l, l, k);
				this.drawTexturedRect(this.field_3105 + l + o * k, this.field_3103 + l + (p + 1) * k, m + l, n + l, k, l);
				this.drawTexturedRect(this.field_3105 + l + (o + 1) * k - 1, this.field_3103 + l + (p + 1) * k - 1, m + l, n + l, l + 1, l + 1);
				if (o == i - 1) {
					this.drawTexturedRect(this.field_3105 + l * 2 + i * k, this.field_3103 + l + p * k, m + k + l, n + l, l, k);
					this.drawTexturedRect(this.field_3105 + l * 2 + i * k, this.field_3103 + l + (p + 1) * k, m + k + l, n + l, l, l);
				}
			}

			this.drawTexturedRect(this.field_3105 + l + o * k, this.field_3103 + l * 2 + j * k, m + l, n + k + l, k, l);
			this.drawTexturedRect(this.field_3105 + l + (o + 1) * k, this.field_3103 + l * 2 + j * k, m + l, n + k + l, l, l);
		}
	}

	public void method_2613(boolean bl) {
		this.field_3107 = bl;
	}

	public boolean method_2616() {
		return this.field_3107;
	}

	@Environment(EnvType.CLIENT)
	class class_509 extends ButtonWidget implements class_2952<Ingredient> {
		private final Recipe field_3114;
		private final boolean field_3115;
		protected final List<class_508.class_509.class_510> field_3116 = Lists.<class_508.class_509.class_510>newArrayList();

		public class_509(int i, int j, Recipe recipe, boolean bl) {
			super(0, i, j, "");
			this.width = 24;
			this.height = 24;
			this.field_3114 = recipe;
			this.field_3115 = bl;
			this.method_2619(recipe);
		}

		protected void method_2619(Recipe recipe) {
			this.method_12816(3, 3, -1, recipe, recipe.getPreviewInputs().iterator(), 0);
		}

		@Override
		public void method_12815(Iterator<Ingredient> iterator, int i, int j, int k, int l) {
			ItemStack[] itemStacks = ((Ingredient)iterator.next()).getStackArray();
			if (itemStacks.length != 0) {
				this.field_3116.add(new class_508.class_509.class_510(3 + l * 7, 3 + k * 7, itemStacks));
			}
		}

		@Override
		public void draw(int i, int j, float f) {
			class_308.method_1453();
			GlStateManager.enableAlphaTest();
			class_508.this.field_3108.getTextureManager().bindTexture(class_508.field_3109);
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			int k = 152;
			if (!this.field_3115) {
				k += 26;
			}

			int l = class_508.this.field_3112 ? 130 : 78;
			if (this.hovered) {
				l += 26;
			}

			this.drawTexturedRect(this.x, this.y, k, l, this.width, this.height);

			for (class_508.class_509.class_510 lv : this.field_3116) {
				GlStateManager.pushMatrix();
				float g = 0.42F;
				int m = (int)((float)(this.x + lv.field_3119) / 0.42F - 3.0F);
				int n = (int)((float)(this.y + lv.field_3118) / 0.42F - 3.0F);
				GlStateManager.scalef(0.42F, 0.42F, 1.0F);
				GlStateManager.enableLighting();
				class_508.this.field_3108
					.getItemRenderer()
					.renderItemAndGlowInGui(lv.field_3120[MathHelper.floor(class_508.this.field_3110 / 30.0F) % lv.field_3120.length], m, n);
				GlStateManager.disableLighting();
				GlStateManager.popMatrix();
			}

			GlStateManager.disableAlphaTest();
			class_308.method_1450();
		}

		@Environment(EnvType.CLIENT)
		public class class_510 {
			public ItemStack[] field_3120;
			public int field_3119;
			public int field_3118;

			public class_510(int i, int j, ItemStack[] itemStacks) {
				this.field_3119 = i;
				this.field_3118 = j;
				this.field_3120 = itemStacks;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_511 extends class_508.class_509 {
		public class_511(int i, int j, Recipe recipe, boolean bl) {
			super(i, j, recipe, bl);
		}

		@Override
		protected void method_2619(Recipe recipe) {
			ItemStack[] itemStacks = recipe.getPreviewInputs().get(0).getStackArray();
			this.field_3116.add(new class_508.class_509.class_510(10, 10, itemStacks));
		}
	}
}
