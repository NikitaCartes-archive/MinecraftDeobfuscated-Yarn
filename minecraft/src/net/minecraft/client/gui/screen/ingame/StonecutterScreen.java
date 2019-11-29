package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StonecutterScreen extends AbstractContainerScreen<StonecutterContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/stonecutter.png");
	private float scrollAmount;
	private boolean mouseClicked;
	private int scrollOffset;
	private boolean canCraft;

	public StonecutterScreen(StonecutterContainer stonecutterContainer, PlayerInventory playerInventory, Text text) {
		super(stonecutterContainer, playerInventory, text);
		stonecutterContainer.setContentsChangedListener(this::onInventoryChange);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.font.draw(this.title.asFormattedString(), 8.0F, 4.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 94), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		this.renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = this.left;
		int j = this.top;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		int k = (int)(41.0F * this.scrollAmount);
		this.blit(i + 119, j + 15 + k, 176 + (this.shouldScroll() ? 0 : 12), 0, 12, 15);
		int l = this.left + 52;
		int m = this.top + 14;
		int n = this.scrollOffset + 12;
		this.method_17952(mouseX, mouseY, l, m, n);
		this.method_17951(l, m, n);
	}

	private void method_17952(int i, int j, int k, int l, int m) {
		for (int n = this.scrollOffset; n < m && n < this.container.getAvailableRecipeCount(); n++) {
			int o = n - this.scrollOffset;
			int p = k + o % 4 * 16;
			int q = o / 4;
			int r = l + q * 18 + 2;
			int s = this.containerHeight;
			if (n == this.container.getSelectedRecipe()) {
				s += 18;
			} else if (i >= p && j >= r && i < p + 16 && j < r + 18) {
				s += 36;
			}

			this.blit(p, r - 1, 0, s, 16, 18);
		}
	}

	private void method_17951(int i, int j, int k) {
		List<StonecuttingRecipe> list = this.container.getAvailableRecipes();

		for (int l = this.scrollOffset; l < k && l < this.container.getAvailableRecipeCount(); l++) {
			int m = l - this.scrollOffset;
			int n = i + m % 4 * 16;
			int o = m / 4;
			int p = j + o * 18 + 2;
			this.minecraft.getItemRenderer().renderGuiItem(((StonecuttingRecipe)list.get(l)).getOutput(), n, p);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.mouseClicked = false;
		if (this.canCraft) {
			int i = this.left + 52;
			int j = this.top + 14;
			int k = this.scrollOffset + 12;

			for (int l = this.scrollOffset; l < k; l++) {
				int m = l - this.scrollOffset;
				double d = mouseX - (double)(i + m % 4 * 16);
				double e = mouseY - (double)(j + m / 4 * 18);
				if (d >= 0.0 && e >= 0.0 && d < 16.0 && e < 18.0 && this.container.onButtonClick(this.minecraft.player, l)) {
					MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
					this.minecraft.interactionManager.clickButton(this.container.syncId, l);
					return true;
				}
			}

			i = this.left + 119;
			j = this.top + 9;
			if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54)) {
				this.mouseClicked = true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.mouseClicked && this.shouldScroll()) {
			int i = this.top + 14;
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
	public boolean mouseScrolled(double d, double e, double amount) {
		if (this.shouldScroll()) {
			int i = this.getMaxScroll();
			this.scrollAmount = (float)((double)this.scrollAmount - amount / (double)i);
			this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
			this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5) * 4;
		}

		return true;
	}

	private boolean shouldScroll() {
		return this.canCraft && this.container.getAvailableRecipeCount() > 12;
	}

	protected int getMaxScroll() {
		return (this.container.getAvailableRecipeCount() + 4 - 1) / 4 - 3;
	}

	private void onInventoryChange() {
		this.canCraft = this.container.canCraft();
		if (!this.canCraft) {
			this.scrollAmount = 0.0F;
			this.scrollOffset = 0;
		}
	}
}
