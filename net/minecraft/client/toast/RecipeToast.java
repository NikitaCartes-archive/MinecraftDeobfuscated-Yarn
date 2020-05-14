/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(value=EnvType.CLIENT)
public class RecipeToast
implements Toast {
    private final List<Recipe<?>> recipes = Lists.newArrayList();
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
        }
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        manager.drawTexture(matrices, 0, 0, 0, 32, 160, 32);
        manager.getGame().textRenderer.draw(matrices, I18n.translate("recipe.toast.title", new Object[0]), 30.0f, 7.0f, -11534256);
        manager.getGame().textRenderer.draw(matrices, I18n.translate("recipe.toast.description", new Object[0]), 30.0f, 18.0f, -16777216);
        Recipe<?> recipe = this.recipes.get((int)(startTime / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
        ItemStack itemStack = recipe.getRecipeKindIcon();
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.6f, 0.6f, 1.0f);
        manager.getGame().getItemRenderer().method_27953(itemStack, 3, 3);
        RenderSystem.popMatrix();
        manager.getGame().getItemRenderer().method_27953(recipe.getOutput(), 8, 8);
        return startTime - this.startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
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

