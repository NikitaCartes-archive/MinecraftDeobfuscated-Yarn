package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StonecutterGui extends ContainerGui<StonecutterContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/stonecutter.png");
	private float scrollAmount;
	private boolean mouseClicked;
	private int scrollOffset;
	private boolean canCraft;

	public StonecutterGui(StonecutterContainer stonecutterContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(stonecutterContainer, playerInventory, textComponent);
		stonecutterContainer.setContentsChangedListener(this::onInventoryChange);
	}

	@Override
	public void draw(int i, int j, float f) {
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 4.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 94), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		this.drawBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		int m = (int)(41.0F * this.scrollAmount);
		this.drawTexturedRect(k + 119, l + 15 + m, 176 + (this.shouldScroll() ? 0 : 12), 0, 12, 15);
		int n = this.left + 52;
		int o = this.top + 14;
		int p = this.scrollOffset + 12;
		this.method_17952(i, j, n, o, p);
		this.method_17951(n, o, p);
	}

	private void method_17952(int i, int j, int k, int l, int m) {
		for (int n = this.scrollOffset; n < m && n < this.container.getAvailableRecipeCount(); n++) {
			int o = n - this.scrollOffset;
			int p = k + o % 4 * 16;
			int q = o / 4;
			int r = l + q * 18 + 2;
			int s = this.containerHeight;
			if (n == this.container.method_17862()) {
				s += 18;
			} else if (i >= p && j >= r && i < p + 16 && j < r + 18) {
				s += 36;
			}

			this.drawTexturedRect(p, r - 1, 0, s, 16, 18);
		}
	}

	private void method_17951(int i, int j, int k) {
		GuiLighting.enableForItems();
		List<StonecuttingRecipe> list = this.container.getAvailableRecipes();

		for (int l = this.scrollOffset; l < k && l < this.container.getAvailableRecipeCount(); l++) {
			int m = l - this.scrollOffset;
			int n = i + m % 4 * 16;
			int o = m / 4;
			int p = j + o * 18 + 2;
			this.client.getItemRenderer().renderItemAndGlowInGui(((StonecuttingRecipe)list.get(l)).getOutput(), n, p);
		}

		GuiLighting.disable();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.mouseClicked = false;
		if (this.canCraft) {
			int j = this.left + 52;
			int k = this.top + 14;
			int l = this.scrollOffset + 12;

			for (int m = this.scrollOffset; m < l; m++) {
				int n = m - this.scrollOffset;
				double f = d - (double)(j + n % 4 * 16);
				double g = e - (double)(k + n / 4 * 18);
				if (f >= 0.0 && g >= 0.0 && f < 16.0 && g < 18.0 && this.container.onButtonClick(this.client.player, m)) {
					MinecraftClient.getInstance().getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_17711, 1.0F));
					this.client.interactionManager.clickButton(this.container.syncId, m);
					return true;
				}
			}

			j = this.left + 119;
			k = this.top + 9;
			if (d >= (double)j && d < (double)(j + 12) && e >= (double)k && e < (double)(k + 54)) {
				this.mouseClicked = true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (this.mouseClicked && this.shouldScroll()) {
			int j = this.top + 14;
			int k = j + 54;
			this.scrollAmount = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
			this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * 4;
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (this.shouldScroll()) {
			int i = this.getMaxScroll();
			this.scrollAmount = (float)((double)this.scrollAmount - d / (double)i);
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
