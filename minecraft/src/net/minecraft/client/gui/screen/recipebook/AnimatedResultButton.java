package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AnimatedResultButton extends ClickableWidget {
	private static final Identifier SLOT_MANY_CRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_many_craftable");
	private static final Identifier SLOT_CRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_craftable");
	private static final Identifier SLOT_MANY_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_many_uncraftable");
	private static final Identifier SLOT_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_uncraftable");
	private static final float field_32414 = 15.0F;
	private static final int field_32415 = 25;
	private static final Text MORE_RECIPES_TEXT = Text.translatable("gui.recipebook.moreRecipes");
	private RecipeResultCollection resultCollection;
	private List<RecipeEntry<?>> results = List.of();
	private final CurrentIndexProvider currentIndexProvider;
	private float bounce;

	public AnimatedResultButton(CurrentIndexProvider currentIndexProvider) {
		super(0, 0, 25, 25, ScreenTexts.EMPTY);
		this.currentIndexProvider = currentIndexProvider;
	}

	public void showResultCollection(RecipeResultCollection resultCollection, boolean filteringCraftable, RecipeBookResults results) {
		this.resultCollection = resultCollection;
		this.results = resultCollection.filter(filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY);

		for (RecipeEntry<?> recipeEntry : this.results) {
			if (results.getRecipeBook().shouldDisplay(recipeEntry)) {
				results.onRecipesDisplayed(this.results);
				this.bounce = 15.0F;
				break;
			}
		}
	}

	public RecipeResultCollection getResultCollection() {
		return this.resultCollection;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		Identifier identifier;
		if (this.resultCollection.hasCraftableRecipes()) {
			if (this.hasMultipleResults()) {
				identifier = SLOT_MANY_CRAFTABLE_TEXTURE;
			} else {
				identifier = SLOT_CRAFTABLE_TEXTURE;
			}
		} else if (this.hasMultipleResults()) {
			identifier = SLOT_MANY_UNCRAFTABLE_TEXTURE;
		} else {
			identifier = SLOT_UNCRAFTABLE_TEXTURE;
		}

		boolean bl = this.bounce > 0.0F;
		if (bl) {
			float f = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float) Math.PI));
			context.getMatrices().push();
			context.getMatrices().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
			context.getMatrices().scale(f, f, 1.0F);
			context.getMatrices().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
			this.bounce -= delta;
		}

		context.drawGuiTexture(RenderLayer::getGuiTextured, identifier, this.getX(), this.getY(), this.width, this.height);
		ItemStack itemStack = this.currentRecipe().value().getResult(this.resultCollection.getRegistryManager());
		int i = 4;
		if (this.resultCollection.hasSingleOutput() && this.hasMultipleResults()) {
			context.drawItem(itemStack, this.getX() + i + 1, this.getY() + i + 1, 0, 10);
			i--;
		}

		context.drawItemWithoutEntity(itemStack, this.getX() + i, this.getY() + i);
		if (bl) {
			context.getMatrices().pop();
		}
	}

	private boolean hasMultipleResults() {
		return this.results.size() > 1;
	}

	public boolean hasSingleResult() {
		return this.results.size() == 1;
	}

	public RecipeEntry<?> currentRecipe() {
		int i = this.currentIndexProvider.currentIndex() % this.results.size();
		return (RecipeEntry<?>)this.results.get(i);
	}

	public List<Text> getTooltip() {
		ItemStack itemStack = this.currentRecipe().value().getResult(this.resultCollection.getRegistryManager());
		List<Text> list = Lists.<Text>newArrayList(Screen.getTooltipFromItem(MinecraftClient.getInstance(), itemStack));
		if (this.hasMultipleResults()) {
			list.add(MORE_RECIPES_TEXT);
		}

		return list;
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		ItemStack itemStack = this.currentRecipe().value().getResult(this.resultCollection.getRegistryManager());
		builder.put(NarrationPart.TITLE, Text.translatable("narration.recipe", itemStack.getName()));
		if (this.hasMultipleResults()) {
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
