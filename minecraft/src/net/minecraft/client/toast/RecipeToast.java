package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(EnvType.CLIENT)
public class RecipeToast implements Toast {
	private final List<Recipe<?>> recipes = Lists.<Recipe<?>>newArrayList();
	private long field_2204;
	private boolean field_2203;

	public RecipeToast(Recipe<?> recipe) {
		this.recipes.add(recipe);
	}

	@Override
	public Toast.Visibility draw(ToastManager toastManager, long l) {
		if (this.field_2203) {
			this.field_2204 = l;
			this.field_2203 = false;
		}

		if (this.recipes.isEmpty()) {
			return Toast.Visibility.field_2209;
		} else {
			toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
			GlStateManager.color3f(1.0F, 1.0F, 1.0F);
			toastManager.drawTexturedRect(0, 0, 0, 32, 160, 32);
			toastManager.getGame().fontRenderer.draw(I18n.translate("recipe.toast.title"), 30.0F, 7.0F, -11534256);
			toastManager.getGame().fontRenderer.draw(I18n.translate("recipe.toast.description"), 30.0F, 18.0F, -16777216);
			GuiLighting.enableForItems();
			Recipe<?> recipe = (Recipe<?>)this.recipes.get((int)(l / (5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
			ItemStack itemStack = recipe.getRecipeKindIcon();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6F, 0.6F, 1.0F);
			toastManager.getGame().getItemRenderer().renderGuiItem(null, itemStack, 3, 3);
			GlStateManager.popMatrix();
			toastManager.getGame().getItemRenderer().renderGuiItem(null, recipe.getOutput(), 8, 8);
			return l - this.field_2204 >= 5000L ? Toast.Visibility.field_2209 : Toast.Visibility.field_2210;
		}
	}

	public void method_1984(Recipe<?> recipe) {
		if (this.recipes.add(recipe)) {
			this.field_2203 = true;
		}
	}

	public static void method_1985(ToastManager toastManager, Recipe<?> recipe) {
		RecipeToast recipeToast = toastManager.method_1997(RecipeToast.class, field_2208);
		if (recipeToast == null) {
			toastManager.add(new RecipeToast(recipe));
		} else {
			recipeToast.method_1984(recipe);
		}
	}
}
