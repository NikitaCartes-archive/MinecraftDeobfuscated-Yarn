package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RecipeToast implements Toast {
	private static final Text field_26533 = new TranslatableText("recipe.toast.title");
	private static final Text field_26534 = new TranslatableText("recipe.toast.description");
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
			manager.getGame().getTextureManager().bindTexture(TEXTURE);
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			manager.drawTexture(matrices, 0, 0, 0, 32, this.getWidth(), this.getHeight());
			manager.getGame().textRenderer.draw(matrices, field_26533, 30.0F, 7.0F, -11534256);
			manager.getGame().textRenderer.draw(matrices, field_26534, 30.0F, 18.0F, -16777216);
			Recipe<?> recipe = (Recipe<?>)this.recipes.get((int)(startTime / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
			ItemStack itemStack = recipe.getRecipeKindIcon();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.6F, 0.6F, 1.0F);
			manager.getGame().getItemRenderer().renderInGui(itemStack, 3, 3);
			RenderSystem.popMatrix();
			manager.getGame().getItemRenderer().renderInGui(recipe.getOutput(), 8, 8);
			return startTime - this.startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}
	}

	private void addRecipes(Recipe<?> recipes) {
		this.recipes.add(recipes);
		this.justUpdated = true;
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
