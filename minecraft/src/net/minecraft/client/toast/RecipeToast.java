package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RecipeToast implements Toast {
	private static final Identifier TEXTURE = Identifier.ofVanilla("toast/recipe");
	private static final long DEFAULT_DURATION_MS = 5000L;
	private static final Text TITLE = Text.translatable("recipe.toast.title");
	private static final Text DESCRIPTION = Text.translatable("recipe.toast.description");
	private final List<RecipeToast.DisplayItems> displayItems = Lists.<RecipeToast.DisplayItems>newArrayList();
	private long startTime;
	private boolean justUpdated;
	private Toast.Visibility visibility = Toast.Visibility.HIDE;
	private int currentItemsDisplayed;

	public RecipeToast(ItemStack categoryItem, ItemStack unlockedItem) {
		this.displayItems.add(new RecipeToast.DisplayItems(categoryItem, unlockedItem));
	}

	@Override
	public Toast.Visibility getVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager manager, long time) {
		if (this.justUpdated) {
			this.startTime = time;
			this.justUpdated = false;
		}

		if (this.displayItems.isEmpty()) {
			this.visibility = Toast.Visibility.HIDE;
		} else {
			this.visibility = (double)(time - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}

		this.currentItemsDisplayed = (int)(
			(double)time / Math.max(1.0, 5000.0 * manager.getNotificationDisplayTimeMultiplier() / (double)this.displayItems.size()) % (double)this.displayItems.size()
		);
	}

	@Override
	public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
		context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
		context.drawText(textRenderer, TITLE, 30, 7, Colors.PURPLE, false);
		context.drawText(textRenderer, DESCRIPTION, 30, 18, Colors.BLACK, false);
		RecipeToast.DisplayItems displayItems = (RecipeToast.DisplayItems)this.displayItems.get(this.currentItemsDisplayed);
		context.getMatrices().push();
		context.getMatrices().scale(0.6F, 0.6F, 1.0F);
		context.drawItemWithoutEntity(displayItems.categoryItem(), 3, 3);
		context.getMatrices().pop();
		context.drawItemWithoutEntity(displayItems.unlockedItem(), 8, 8);
	}

	private void addRecipes(ItemStack categoryItem, ItemStack unlockedItem) {
		this.displayItems.add(new RecipeToast.DisplayItems(categoryItem, unlockedItem));
		this.justUpdated = true;
	}

	public static void show(ToastManager manager, RecipeEntry<?> recipe) {
		RecipeToast recipeToast = manager.getToast(RecipeToast.class, TYPE);
		ItemStack itemStack = recipe.value().createIcon();
		ItemStack itemStack2 = recipe.value().getResult(manager.getClient().world.getRegistryManager());
		if (recipeToast == null) {
			manager.add(new RecipeToast(itemStack, itemStack2));
		} else {
			recipeToast.addRecipes(itemStack, itemStack2);
		}
	}

	@Environment(EnvType.CLIENT)
	static record DisplayItems(ItemStack categoryItem, ItemStack unlockedItem) {
	}
}
