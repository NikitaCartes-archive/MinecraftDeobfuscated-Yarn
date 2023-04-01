package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AnimatedResultButton extends ClickableWidget {
	private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private static final float field_32414 = 15.0F;
	private static final int field_32415 = 25;
	public static final int field_32413 = 30;
	private static final Text MORE_RECIPES_TEXT = Text.translatable("gui.recipebook.moreRecipes");
	private AbstractRecipeScreenHandler<?> craftingScreenHandler;
	private RecipeBook recipeBook;
	private RecipeResultCollection resultCollection;
	private float time;
	private float bounce;
	private int currentResultIndex;

	public AnimatedResultButton() {
		super(0, 0, 25, 25, ScreenTexts.EMPTY);
	}

	public void showResultCollection(RecipeResultCollection resultCollection, RecipeBookResults results) {
		this.resultCollection = resultCollection;
		this.craftingScreenHandler = (AbstractRecipeScreenHandler<?>)results.getClient().player.currentScreenHandler;
		this.recipeBook = results.getRecipeBook();
		List<Recipe<?>> list = resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler));

		for (Recipe<?> recipe : list) {
			if (this.recipeBook.shouldDisplay(recipe)) {
				results.onRecipesDisplayed(list);
				this.bounce = 15.0F;
				break;
			}
		}
	}

	public RecipeResultCollection getResultCollection() {
		return this.resultCollection;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!Screen.hasControlDown()) {
			this.time += delta;
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		int i = 29;
		if (!this.resultCollection.hasCraftableRecipes()) {
			i += 25;
		}

		int j = 206;
		if (this.resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
			j += 25;
		}

		boolean bl = this.bounce > 0.0F;
		if (bl) {
			float f = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float) Math.PI));
			matrices.push();
			matrices.translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
			matrices.scale(f, f, 1.0F);
			matrices.translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
			this.bounce -= delta;
		}

		drawTexture(matrices, this.getX(), this.getY(), i, j, this.width, this.height);
		List<Recipe<?>> list = this.getResults();
		this.currentResultIndex = MathHelper.floor(this.time / 30.0F) % list.size();
		ItemStack itemStack = ((Recipe)list.get(this.currentResultIndex)).method_50832(this.resultCollection.getRegistryManager());
		int k = 4;
		if (this.resultCollection.hasSingleOutput() && this.getResults().size() > 1) {
			minecraftClient.getItemRenderer().renderInGuiWithOverrides(matrices, itemStack, this.getX() + k + 1, this.getY() + k + 1, 0, 10);
			k--;
		}

		minecraftClient.getItemRenderer().renderInGui(matrices, itemStack, this.getX() + k, this.getY() + k);
		if (bl) {
			matrices.pop();
		}
	}

	private List<Recipe<?>> getResults() {
		List<Recipe<?>> list = this.resultCollection.getRecipes(true);
		if (!this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)) {
			list.addAll(this.resultCollection.getRecipes(false));
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

	public List<Text> getTooltip(Screen screen) {
		ItemStack itemStack = ((Recipe)this.getResults().get(this.currentResultIndex)).method_50832(this.resultCollection.getRegistryManager());
		List<Text> list = Lists.<Text>newArrayList(screen.getTooltipFromItem(itemStack));
		if (this.resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
			list.add(MORE_RECIPES_TEXT);
		}

		return list;
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		ItemStack itemStack = ((Recipe)this.getResults().get(this.currentResultIndex)).method_50832(this.resultCollection.getRegistryManager());
		builder.put(NarrationPart.TITLE, Text.translatable("narration.recipe", itemStack.getName()));
		if (this.resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
			builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"), Text.translatable("narration.recipe.usage.more"));
		} else {
			builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"));
		}
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
