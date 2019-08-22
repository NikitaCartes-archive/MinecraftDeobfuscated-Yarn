/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AnimatedResultButton
extends AbstractButtonWidget {
    private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
    private CraftingContainer<?> craftingContainer;
    private RecipeBook recipeBook;
    private RecipeResultCollection results;
    private float time;
    private float bounce;
    private int currentResultIndex;

    public AnimatedResultButton() {
        super(0, 0, 25, 25, "");
    }

    public void showResultCollection(RecipeResultCollection recipeResultCollection, RecipeBookResults recipeBookResults) {
        this.results = recipeResultCollection;
        this.craftingContainer = (CraftingContainer)recipeBookResults.getMinecraftClient().player.container;
        this.recipeBook = recipeBookResults.getRecipeBook();
        List<Recipe<?>> list = recipeResultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer));
        for (Recipe<?> recipe : list) {
            if (!this.recipeBook.shouldDisplay(recipe)) continue;
            recipeBookResults.onRecipesDisplayed(list);
            this.bounce = 15.0f;
            break;
        }
    }

    public RecipeResultCollection getResultCollection() {
        return this.results;
    }

    public void setPos(int i, int j) {
        this.x = i;
        this.y = j;
    }

    @Override
    public void renderButton(int i, int j, float f) {
        boolean bl;
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        GuiLighting.enableForItems();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BG_TEX);
        RenderSystem.disableLighting();
        int k = 29;
        if (!this.results.hasCraftableResults()) {
            k += 25;
        }
        int l = 206;
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
            l += 25;
        }
        boolean bl2 = bl = this.bounce > 0.0f;
        if (bl) {
            float g = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.x + 8, this.y + 12, 0.0f);
            RenderSystem.scalef(g, g, 1.0f);
            RenderSystem.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
            this.bounce -= f;
        }
        this.blit(this.x, this.y, k, l, this.width, this.height);
        List<Recipe<?>> list = this.getResults();
        this.currentResultIndex = MathHelper.floor(this.time / 30.0f) % list.size();
        ItemStack itemStack = list.get(this.currentResultIndex).getOutput();
        int m = 4;
        if (this.results.method_2656() && this.getResults().size() > 1) {
            minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + m + 1, this.y + m + 1);
            --m;
        }
        minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + m, this.y + m);
        if (bl) {
            RenderSystem.popMatrix();
        }
        RenderSystem.enableLighting();
        GuiLighting.disable();
    }

    private List<Recipe<?>> getResults() {
        List<Recipe<?>> list = this.results.getResultsExclusive(true);
        if (!this.recipeBook.isFilteringCraftable(this.craftingContainer)) {
            list.addAll(this.results.getResultsExclusive(false));
        }
        return list;
    }

    public boolean hasResults() {
        return this.getResults().size() == 1;
    }

    public Recipe<?> currentRecipe() {
        List<Recipe<?>> list = this.getResults();
        return list.get(this.currentResultIndex);
    }

    public List<String> method_2644(Screen screen) {
        ItemStack itemStack = this.getResults().get(this.currentResultIndex).getOutput();
        List<String> list = screen.getTooltipFromItem(itemStack);
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
            list.add(I18n.translate("gui.recipebook.moreRecipes", new Object[0]));
        }
        return list;
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int i) {
        return i == 0 || i == 1;
    }
}

