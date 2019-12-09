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
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(value=EnvType.CLIENT)
public class RecipeToast
implements Toast {
    private final List<Recipe<?>> recipes = Lists.newArrayList();
    private long startTime;
    private boolean justUpdated;

    public RecipeToast(Recipe<?> recipe) {
        this.recipes.add(recipe);
    }

    @Override
    public Toast.Visibility draw(ToastManager manager, long currentTime) {
        if (this.justUpdated) {
            this.startTime = currentTime;
            this.justUpdated = false;
        }
        if (this.recipes.isEmpty()) {
            return Toast.Visibility.HIDE;
        }
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        manager.blit(0, 0, 0, 32, 160, 32);
        manager.getGame().textRenderer.draw(I18n.translate("recipe.toast.title", new Object[0]), 30.0f, 7.0f, -11534256);
        manager.getGame().textRenderer.draw(I18n.translate("recipe.toast.description", new Object[0]), 30.0f, 18.0f, -16777216);
        Recipe<?> recipe = this.recipes.get((int)(currentTime / (5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
        ItemStack itemStack = recipe.getRecipeKindIcon();
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.6f, 0.6f, 1.0f);
        manager.getGame().getItemRenderer().renderGuiItem(null, itemStack, 3, 3);
        RenderSystem.popMatrix();
        manager.getGame().getItemRenderer().renderGuiItem(null, recipe.getOutput(), 8, 8);
        return currentTime - this.startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public void addRecipe(Recipe<?> recipe) {
        if (this.recipes.add(recipe)) {
            this.justUpdated = true;
        }
    }

    public static void show(ToastManager toastManager, Recipe<?> recipe) {
        RecipeToast recipeToast = toastManager.getToast(RecipeToast.class, field_2208);
        if (recipeToast == null) {
            toastManager.add(new RecipeToast(recipe));
        } else {
            recipeToast.addRecipe(recipe);
        }
    }
}

