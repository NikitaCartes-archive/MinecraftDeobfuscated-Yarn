package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_514 extends ButtonWidget {
	private static final Identifier field_3143 = new Identifier("textures/gui/recipe_book.png");
	private CraftingContainer field_3137;
	private RecipeBook field_3138;
	private class_516 field_3142;
	private float field_3140;
	private float field_3139;
	private int field_3141;

	public class_514() {
		super(0, 0, 0, 25, 25, "");
	}

	public void method_2640(class_516 arg, class_513 arg2) {
		this.field_3142 = arg;
		this.field_3137 = (CraftingContainer)arg2.method_2637().player.container;
		this.field_3138 = arg2.method_2633();
		List<Recipe> list = arg.method_2651(this.field_3138.isFilteringCraftable(this.field_3137));

		for (Recipe recipe : list) {
			if (this.field_3138.method_14883(recipe)) {
				arg2.method_2629(list);
				this.field_3139 = 15.0F;
				break;
			}
		}
	}

	public class_516 method_2645() {
		return this.field_3142;
	}

	public void method_2641(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.visible) {
			if (!Gui.isControlPressed()) {
				this.field_3140 += f;
			}

			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			class_308.method_1453();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.getTextureManager().bindTexture(field_3143);
			GlStateManager.disableLighting();
			int k = 29;
			if (!this.field_3142.method_2655()) {
				k += 25;
			}

			int l = 206;
			if (this.field_3142.method_2651(this.field_3138.isFilteringCraftable(this.field_3137)).size() > 1) {
				l += 25;
			}

			boolean bl = this.field_3139 > 0.0F;
			if (bl) {
				float g = 1.0F + 0.1F * (float)Math.sin((double)(this.field_3139 / 15.0F * (float) Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
				GlStateManager.scalef(g, g, 1.0F);
				GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
				this.field_3139 -= f;
			}

			this.drawTexturedRect(this.x, this.y, k, l, this.width, this.height);
			List<Recipe> list = this.method_2639();
			this.field_3141 = MathHelper.floor(this.field_3140 / 30.0F) % list.size();
			ItemStack itemStack = ((Recipe)list.get(this.field_3141)).getOutput();
			int m = 4;
			if (this.field_3142.method_2656() && this.method_2639().size() > 1) {
				minecraftClient.getItemRenderer().renderItemAndGlowInGui(itemStack, this.x + m + 1, this.y + m + 1);
				m--;
			}

			minecraftClient.getItemRenderer().renderItemAndGlowInGui(itemStack, this.x + m, this.y + m);
			if (bl) {
				GlStateManager.popMatrix();
			}

			GlStateManager.enableLighting();
			class_308.method_1450();
		}
	}

	private List<Recipe> method_2639() {
		List<Recipe> list = this.field_3142.method_2648(true);
		if (!this.field_3138.isFilteringCraftable(this.field_3137)) {
			list.addAll(this.field_3142.method_2648(false));
		}

		return list;
	}

	public boolean method_2642() {
		return this.method_2639().size() == 1;
	}

	public Recipe method_2643() {
		List<Recipe> list = this.method_2639();
		return (Recipe)list.get(this.field_3141);
	}

	public List<String> method_2644(Gui gui) {
		ItemStack itemStack = ((Recipe)this.method_2639().get(this.field_3141)).getOutput();
		List<String> list = gui.getStackTooltip(itemStack);
		if (this.field_3142.method_2651(this.field_3138.isFilteringCraftable(this.field_3137)).size() > 1) {
			list.add(I18n.translate("gui.recipebook.moreRecipes"));
		}

		return list;
	}

	@Override
	public int getWidth() {
		return 25;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0 || i == 1) {
			boolean bl = this.method_1829(d, e);
			if (bl) {
				this.onPressed(MinecraftClient.getInstance().getSoundLoader());
				if (i == 0) {
					this.onPressed(d, e);
				}

				return true;
			}
		}

		return false;
	}
}
