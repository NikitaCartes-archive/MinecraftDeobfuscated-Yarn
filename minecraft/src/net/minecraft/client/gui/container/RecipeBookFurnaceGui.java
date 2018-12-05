package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_308;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class RecipeBookFurnaceGui extends RecipeBookGui {
	private Iterator<Item> field_3153;
	private Set<Item> field_3149;
	private Slot field_3150;
	private Item field_3152;
	private float field_3151;

	@Override
	protected boolean method_2589() {
		boolean bl = !this.recipeBook.isFurnaceFilteringCraftable();
		this.recipeBook.setFurnaceFilteringCraftable(bl);
		return bl;
	}

	@Override
	public boolean isOpen() {
		return this.recipeBook.isFurnaceGuiOpen();
	}

	@Override
	protected void method_2593(boolean bl) {
		this.recipeBook.setFurnaceGuiOpen(bl);
		if (!bl) {
			this.field_3086.method_2638();
		}

		this.method_2588();
	}

	@Override
	protected void method_2585() {
		this.field_3088.method_1962(152, 182, 28, 18, TEXTURE);
	}

	@Override
	protected String method_2599() {
		return I18n.translate(this.field_3088.method_1965() ? "gui.recipebook.toggleRecipes.smeltable" : "gui.recipebook.toggleRecipes.all");
	}

	@Override
	public void method_2600(@Nullable Slot slot) {
		super.method_2600(slot);
		if (slot != null && slot.id < this.field_3095.method_7658()) {
			this.field_3150 = null;
		}
	}

	@Override
	public void method_2596(Recipe recipe, List<Slot> list) {
		ItemStack itemStack = recipe.getOutput();
		this.field_3092.method_2565(recipe);
		this.field_3092.method_2569(Ingredient.ofStacks(itemStack), ((Slot)list.get(2)).xPosition, ((Slot)list.get(2)).yPosition);
		DefaultedList<Ingredient> defaultedList = recipe.getPreviewInputs();
		this.field_3150 = (Slot)list.get(1);
		if (this.field_3149 == null) {
			this.field_3149 = FurnaceBlockEntity.method_11196().keySet();
		}

		this.field_3153 = this.field_3149.iterator();
		this.field_3152 = null;
		Iterator<Ingredient> iterator = defaultedList.iterator();

		for (int i = 0; i < 2; i++) {
			if (!iterator.hasNext()) {
				return;
			}

			Ingredient ingredient = (Ingredient)iterator.next();
			if (!ingredient.method_8103()) {
				Slot slot = (Slot)list.get(i);
				this.field_3092.method_2569(ingredient, slot.xPosition, slot.yPosition);
			}
		}
	}

	@Override
	public void method_2581(int i, int j, boolean bl, float f) {
		super.method_2581(i, j, bl, f);
		if (this.field_3150 != null) {
			if (!Gui.isControlPressed()) {
				this.field_3151 += f;
			}

			class_308.method_1453();
			GlStateManager.disableLighting();
			int k = this.field_3150.xPosition + i;
			int l = this.field_3150.yPosition + j;
			Drawable.drawRect(k, l, k + 16, l + 16, 822018048);
			this.client.getItemRenderer().renderItemInGui(this.client.player, this.method_2658().getDefaultStack(), k, l);
			GlStateManager.depthFunc(516);
			Drawable.drawRect(k, l, k + 16, l + 16, 822083583);
			GlStateManager.depthFunc(515);
			GlStateManager.enableLighting();
			class_308.method_1450();
		}
	}

	private Item method_2658() {
		if (this.field_3152 == null || this.field_3151 > 30.0F) {
			this.field_3151 = 0.0F;
			if (this.field_3153 == null || !this.field_3153.hasNext()) {
				if (this.field_3149 == null) {
					this.field_3149 = FurnaceBlockEntity.method_11196().keySet();
				}

				this.field_3153 = this.field_3149.iterator();
			}

			this.field_3152 = (Item)this.field_3153.next();
		}

		return this.field_3152;
	}
}
