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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AnimatedResultButton
extends AbstractButtonWidget {
    private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
    private AbstractRecipeScreenHandler<?> craftingScreenHandler;
    private RecipeBook recipeBook;
    private RecipeResultCollection results;
    private float time;
    private float bounce;
    private int currentResultIndex;

    public AnimatedResultButton() {
        super(0, 0, 25, 25, LiteralText.EMPTY);
    }

    public void showResultCollection(RecipeResultCollection recipeResultCollection, RecipeBookResults recipeBookResults) {
        this.results = recipeResultCollection;
        this.craftingScreenHandler = (AbstractRecipeScreenHandler)recipeBookResults.getMinecraftClient().player.currentScreenHandler;
        this.recipeBook = recipeBookResults.getRecipeBook();
        List<Recipe<?>> list = recipeResultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler));
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

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int i, int j, float f) {
        boolean bl;
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BG_TEX);
        int k = 29;
        if (!this.results.hasCraftableRecipes()) {
            k += 25;
        }
        int l = 206;
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
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
        this.drawTexture(matrixStack, this.x, this.y, k, l, this.width, this.height);
        List<Recipe<?>> list = this.getResults();
        this.currentResultIndex = MathHelper.floor(this.time / 30.0f) % list.size();
        ItemStack itemStack = list.get(this.currentResultIndex).getOutput();
        int m = 4;
        if (this.results.hasSingleOutput() && this.getResults().size() > 1) {
            minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + m + 1, this.y + m + 1);
            --m;
        }
        minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + m, this.y + m);
        if (bl) {
            RenderSystem.popMatrix();
        }
    }

    private List<Recipe<?>> getResults() {
        List<Recipe<?>> list = this.results.getRecipes(true);
        if (!this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)) {
            list.addAll(this.results.getRecipes(false));
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

    public List<Text> getTooltip(Screen screen) {
        ItemStack itemStack = this.getResults().get(this.currentResultIndex).getOutput();
        List<Text> list = screen.getTooltipFromItem(itemStack);
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
            list.add(new TranslatableText("gui.recipebook.moreRecipes"));
        }
        return list;
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == 0 || button == 1;
    }
}

