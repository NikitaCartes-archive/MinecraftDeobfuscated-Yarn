package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AnimatedResultButton extends ButtonWidget {
	private static final Identifier field_3143 = new Identifier("textures/gui/recipe_book.png");
	private CraftingContainer<?> craftingContainer;
	private RecipeBook field_3138;
	private RecipeResultCollection field_3142;
	private float time;
	private float bounce;
	private int currentResultIndex;

	public AnimatedResultButton() {
		super(0, 0, 25, 25, "");
	}

	public void method_2640(RecipeResultCollection recipeResultCollection, RecipeBookGuiResults recipeBookGuiResults) {
		this.field_3142 = recipeResultCollection;
		this.craftingContainer = (CraftingContainer<?>)recipeBookGuiResults.getMinecraftClient().field_1724.field_7512;
		this.field_3138 = recipeBookGuiResults.method_2633();
		List<Recipe<?>> list = recipeResultCollection.getResults(this.field_3138.isFilteringCraftable(this.craftingContainer));

		for (Recipe<?> recipe : list) {
			if (this.field_3138.shouldDisplay(recipe)) {
				recipeBookGuiResults.onRecipesDisplayed(list);
				this.bounce = 15.0F;
				break;
			}
		}
	}

	public RecipeResultCollection method_2645() {
		return this.field_3142;
	}

	public void setPos(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void drawButton(int i, int j, float f) {
		if (!Screen.isControlPressed()) {
			this.time += f;
		}

		GuiLighting.enableForItems();
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.method_1531().method_4618(field_3143);
		GlStateManager.disableLighting();
		int k = 29;
		if (!this.field_3142.hasCraftableResults()) {
			k += 25;
		}

		int l = 206;
		if (this.field_3142.getResults(this.field_3138.isFilteringCraftable(this.craftingContainer)).size() > 1) {
			l += 25;
		}

		boolean bl = this.bounce > 0.0F;
		if (bl) {
			float g = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float) Math.PI));
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
			GlStateManager.scalef(g, g, 1.0F);
			GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
			this.bounce -= f;
		}

		this.drawTexturedRect(this.x, this.y, k, l, this.width, this.height);
		List<Recipe<?>> list = this.getResults();
		this.currentResultIndex = MathHelper.floor(this.time / 30.0F) % list.size();
		ItemStack itemStack = ((Recipe)list.get(this.currentResultIndex)).getOutput();
		int m = 4;
		if (this.field_3142.method_2656() && this.getResults().size() > 1) {
			minecraftClient.method_1480().renderGuiItem(itemStack, this.x + m + 1, this.y + m + 1);
			m--;
		}

		minecraftClient.method_1480().renderGuiItem(itemStack, this.x + m, this.y + m);
		if (bl) {
			GlStateManager.popMatrix();
		}

		GlStateManager.enableLighting();
		GuiLighting.disable();
	}

	private List<Recipe<?>> getResults() {
		List<Recipe<?>> list = this.field_3142.getResultsExclusive(true);
		if (!this.field_3138.isFilteringCraftable(this.craftingContainer)) {
			list.addAll(this.field_3142.getResultsExclusive(false));
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

	public List<String> method_2644(Screen screen) {
		ItemStack itemStack = ((Recipe)this.getResults().get(this.currentResultIndex)).getOutput();
		List<String> list = screen.getStackTooltip(itemStack);
		if (this.field_3142.getResults(this.field_3138.isFilteringCraftable(this.craftingContainer)).size() > 1) {
			list.add(I18n.translate("gui.recipebook.moreRecipes"));
		}

		return list;
	}

	@Override
	public int getWidth() {
		return 25;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0 || i == 1) {
			boolean bl = this.isSelected(d, e);
			if (bl) {
				this.method_1832(MinecraftClient.getInstance().method_1483());
				if (i == 0) {
					this.method_19347(d, e);
				}

				return true;
			}
		}

		return false;
	}
}
