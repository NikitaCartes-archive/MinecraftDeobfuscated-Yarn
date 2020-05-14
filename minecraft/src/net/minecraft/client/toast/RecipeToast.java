package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(EnvType.CLIENT)
public class RecipeToast implements Toast {
	private final List<Recipe<?>> recipes = Lists.<Recipe<?>>newArrayList();
	private long startTime;
	private boolean justUpdated;

	public RecipeToast(Recipe<?> recipes) {
		this.recipes.add(recipes);
	}

	@Override
	public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
		if (this.justUpdated) {
			this.startTime = startTime;
			this.justUpdated = false;
		}

		if (this.recipes.isEmpty()) {
			return Toast.Visibility.HIDE;
		} else {
			manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			manager.drawTexture(matrices, 0, 0, 0, 32, 160, 32);
			manager.getGame().textRenderer.draw(matrices, I18n.translate("recipe.toast.title"), 30.0F, 7.0F, -11534256);
			manager.getGame().textRenderer.draw(matrices, I18n.translate("recipe.toast.description"), 30.0F, 18.0F, -16777216);
			Recipe<?> recipe = (Recipe<?>)this.recipes.get((int)(startTime / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
			ItemStack itemStack = recipe.getRecipeKindIcon();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.6F, 0.6F, 1.0F);
			manager.getGame().getItemRenderer().method_27953(itemStack, 3, 3);
			RenderSystem.popMatrix();
			manager.getGame().getItemRenderer().method_27953(recipe.getOutput(), 8, 8);
			return startTime - this.startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}
	}

	public void addRecipes(Recipe<?> recipes) {
		if (this.recipes.add(recipes)) {
			this.justUpdated = true;
		}
	}

	public static void show(ToastManager manager, Recipe<?> recipes) {
		RecipeToast recipeToast = manager.getToast(RecipeToast.class, TYPE);
		if (recipeToast == null) {
			manager.add(new RecipeToast(recipes));
		} else {
			recipeToast.addRecipes(recipes);
		}
	}
}
