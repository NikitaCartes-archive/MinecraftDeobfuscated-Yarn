package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StonecutterScreen extends HandledScreen<StonecutterScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/stonecutter.png");
	private float scrollAmount;
	private boolean mouseClicked;
	private int scrollOffset;
	private boolean canCraft;

	public StonecutterScreen(StonecutterScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		handler.setContentsChangedListener(this::onInventoryChange);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int j) {
		this.textRenderer.draw(matrixStack, this.title, 8.0F, 4.0F, 4210752);
		this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 94), 4210752);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {
		this.renderBackground(matrixStack);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int j = this.x;
		int k = this.y;
		this.drawTexture(matrixStack, j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
		int l = (int)(41.0F * this.scrollAmount);
		this.drawTexture(matrixStack, j + 119, k + 15 + l, 176 + (this.shouldScroll() ? 0 : 12), 0, 12, 15);
		int m = this.x + 52;
		int n = this.y + 14;
		int o = this.scrollOffset + 12;
		this.renderRecipeBackground(matrixStack, mouseY, i, m, n, o);
		this.renderRecipeIcons(m, n, o);
	}

	@Override
	protected void drawMouseoverTooltip(MatrixStack matrixStack, int i, int j) {
		super.drawMouseoverTooltip(matrixStack, i, j);
		if (this.canCraft) {
			int k = this.x + 52;
			int l = this.y + 14;
			int m = this.scrollOffset + 12;
			List<StonecuttingRecipe> list = this.handler.getAvailableRecipes();

			for (int n = this.scrollOffset; n < m && n < this.handler.getAvailableRecipeCount(); n++) {
				int o = n - this.scrollOffset;
				int p = k + o % 4 * 16;
				int q = l + o / 4 * 18 + 2;
				if (i >= p && i < p + 16 && j >= q && j < q + 18) {
					this.renderTooltip(matrixStack, ((StonecuttingRecipe)list.get(n)).getOutput(), i, j);
				}
			}
		}
	}

	private void renderRecipeBackground(MatrixStack matrixStack, int i, int j, int k, int l, int m) {
		for (int n = this.scrollOffset; n < m && n < this.handler.getAvailableRecipeCount(); n++) {
			int o = n - this.scrollOffset;
			int p = k + o % 4 * 16;
			int q = o / 4;
			int r = l + q * 18 + 2;
			int s = this.backgroundHeight;
			if (n == this.handler.getSelectedRecipe()) {
				s += 18;
			} else if (i >= p && j >= r && i < p + 16 && j < r + 18) {
				s += 36;
			}

			this.drawTexture(matrixStack, p, r - 1, 0, s, 16, 18);
		}
	}

	private void renderRecipeIcons(int x, int y, int scrollOffset) {
		List<StonecuttingRecipe> list = this.handler.getAvailableRecipes();

		for (int i = this.scrollOffset; i < scrollOffset && i < this.handler.getAvailableRecipeCount(); i++) {
			int j = i - this.scrollOffset;
			int k = x + j % 4 * 16;
			int l = j / 4;
			int m = y + l * 18 + 2;
			this.client.getItemRenderer().renderGuiItem(((StonecuttingRecipe)list.get(i)).getOutput(), k, m);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.mouseClicked = false;
		if (this.canCraft) {
			int i = this.x + 52;
			int j = this.y + 14;
			int k = this.scrollOffset + 12;

			for (int l = this.scrollOffset; l < k; l++) {
				int m = l - this.scrollOffset;
				double d = mouseX - (double)(i + m % 4 * 16);
				double e = mouseY - (double)(j + m / 4 * 18);
				if (d >= 0.0 && e >= 0.0 && d < 16.0 && e < 18.0 && this.handler.onButtonClick(this.client.player, l)) {
					MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
					this.client.interactionManager.clickButton(this.handler.syncId, l);
					return true;
				}
			}

			i = this.x + 119;
			j = this.y + 9;
			if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54)) {
				this.mouseClicked = true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.mouseClicked && this.shouldScroll()) {
			int i = this.y + 14;
			int j = i + 54;
			this.scrollAmount = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
			this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
			this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * 4;
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (this.shouldScroll()) {
			int i = this.getMaxScroll();
			this.scrollAmount = (float)((double)this.scrollAmount - amount / (double)i);
			this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
			this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5) * 4;
		}

		return true;
	}

	private boolean shouldScroll() {
		return this.canCraft && this.handler.getAvailableRecipeCount() > 12;
	}

	protected int getMaxScroll() {
		return (this.handler.getAvailableRecipeCount() + 4 - 1) / 4 - 3;
	}

	private void onInventoryChange() {
		this.canCraft = this.handler.canCraft();
		if (!this.canCraft) {
			this.scrollAmount = 0.0F;
			this.scrollOffset = 0;
		}
	}
}
