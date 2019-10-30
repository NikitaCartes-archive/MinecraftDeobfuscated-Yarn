package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AnimatedResultButton extends AbstractButtonWidget {
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
		this.craftingContainer = (CraftingContainer<?>)recipeBookResults.getMinecraftClient().player.container;
		this.recipeBook = recipeBookResults.getRecipeBook();
		List<Recipe<?>> list = recipeResultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer));

		for (Recipe<?> recipe : list) {
			if (this.recipeBook.shouldDisplay(recipe)) {
				recipeBookResults.onRecipesDisplayed(list);
				this.bounce = 15.0F;
				break;
			}
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
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (!Screen.hasControlDown()) {
			this.time += delta;
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(BG_TEX);
		int i = 29;
		if (!this.results.hasCraftableRecipes()) {
			i += 25;
		}

		int j = 206;
		if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
			j += 25;
		}

		boolean bl = this.bounce > 0.0F;
		if (bl) {
			float f = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float) Math.PI));
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
			RenderSystem.scalef(f, f, 1.0F);
			RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
			this.bounce -= delta;
		}

		this.blit(this.x, this.y, i, j, this.width, this.height);
		List<Recipe<?>> list = this.getResults();
		this.currentResultIndex = MathHelper.floor(this.time / 30.0F) % list.size();
		ItemStack itemStack = ((Recipe)list.get(this.currentResultIndex)).getOutput();
		int k = 4;
		if (this.results.hasSingleOutput() && this.getResults().size() > 1) {
			minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + k + 1, this.y + k + 1);
			k--;
		}

		minecraftClient.getItemRenderer().renderGuiItem(itemStack, this.x + k, this.y + k);
		if (bl) {
			RenderSystem.popMatrix();
		}
	}

	private List<Recipe<?>> getResults() {
		List<Recipe<?>> list = this.results.getRecipes(true);
		if (!this.recipeBook.isFilteringCraftable(this.craftingContainer)) {
			list.addAll(this.results.getRecipes(false));
		}

		return list;
	}

	public boolean hasResults() {
		return this.getResults().size() == 1;
	}

	public Recipe<?> currentRecipe() {
		List<Recipe<?>> list = this.getResults();
		return (Recipe<?>)list.get(this.currentResultIndex);
	}

	public List<String> getTooltip(Screen screen) {
		ItemStack itemStack = ((Recipe)this.getResults().get(this.currentResultIndex)).getOutput();
		List<String> list = screen.getTooltipFromItem(itemStack);
		if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
			list.add(I18n.translate("gui.recipebook.moreRecipes"));
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
