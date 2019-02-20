package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.MerchantContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.village.TraderRecipe;
import net.minecraft.village.TraderRecipeList;

@Environment(EnvType.CLIENT)
public class VillagerScreen extends ContainerScreen<MerchantContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager.png");
	private VillagerScreen.WidgetButtonPage buttonPageNext;
	private VillagerScreen.WidgetButtonPage buttonPagePrevious;
	private int recipeIndex;

	public VillagerScreen(MerchantContainer merchantContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(merchantContainer, playerInventory, textComponent);
	}

	private void syncRecipeIndex() {
		this.container.setRecipeIndex(this.recipeIndex);
		this.client.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.recipeIndex));
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.buttonPageNext = this.addButton(new VillagerScreen.WidgetButtonPage(i + 120 + 27, j + 24 - 1, true) {
			@Override
			public void onPressed(double d, double e) {
				VillagerScreen.this.recipeIndex++;
				TraderRecipeList traderRecipeList = VillagerScreen.this.container.method_17438();
				if (traderRecipeList != null && VillagerScreen.this.recipeIndex >= traderRecipeList.size()) {
					VillagerScreen.this.recipeIndex = traderRecipeList.size() - 1;
				}

				VillagerScreen.this.syncRecipeIndex();
			}
		});
		this.buttonPagePrevious = this.addButton(new VillagerScreen.WidgetButtonPage(i + 36 - 19, j + 24 - 1, false) {
			@Override
			public void onPressed(double d, double e) {
				VillagerScreen.this.recipeIndex--;
				if (VillagerScreen.this.recipeIndex < 0) {
					VillagerScreen.this.recipeIndex = 0;
				}

				VillagerScreen.this.syncRecipeIndex();
			}
		});
		this.buttonPageNext.enabled = false;
		this.buttonPagePrevious.enabled = false;
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = this.name.getFormattedText();
		this.fontRenderer.draw(string, (float)(this.containerWidth / 2 - this.fontRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	public void update() {
		super.update();
		TraderRecipeList traderRecipeList = this.container.method_17438();
		this.buttonPageNext.enabled = this.recipeIndex < traderRecipeList.size() - 1;
		this.buttonPagePrevious.enabled = this.recipeIndex > 0;
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		TraderRecipeList traderRecipeList = this.container.method_17438();
		if (!traderRecipeList.isEmpty()) {
			int m = this.recipeIndex;
			if (m < 0 || m >= traderRecipeList.size()) {
				return;
			}

			TraderRecipe traderRecipe = (TraderRecipe)traderRecipeList.get(m);
			if (traderRecipe.isDisabled()) {
				this.client.getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				this.drawTexturedRect(this.left + 83, this.top + 21, 212, 0, 28, 21);
				this.drawTexturedRect(this.left + 83, this.top + 51, 212, 0, 28, 21);
			}
		}
	}

	@Override
	public void method_18326(int i, int j, float f) {
		this.drawBackground();
		super.method_18326(i, j, f);
		TraderRecipeList traderRecipeList = this.container.method_17438();
		if (!traderRecipeList.isEmpty()) {
			int k = (this.width - this.containerWidth) / 2;
			int l = (this.height - this.containerHeight) / 2;
			int m = this.recipeIndex;
			TraderRecipe traderRecipe = (TraderRecipe)traderRecipeList.get(m);
			ItemStack itemStack = traderRecipe.getFirstBuyItem();
			ItemStack itemStack2 = traderRecipe.getSecondBuyItem();
			ItemStack itemStack3 = traderRecipe.getModifiableSellItem();
			GlStateManager.pushMatrix();
			GuiLighting.enableForItems();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.itemRenderer.zOffset = 100.0F;
			this.itemRenderer.renderGuiItem(itemStack, k + 36, l + 24);
			this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack, k + 36, l + 24);
			if (!itemStack2.isEmpty()) {
				this.itemRenderer.renderGuiItem(itemStack2, k + 62, l + 24);
				this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack2, k + 62, l + 24);
			}

			this.itemRenderer.renderGuiItem(itemStack3, k + 120, l + 24);
			this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack3, k + 120, l + 24);
			this.itemRenderer.zOffset = 0.0F;
			GlStateManager.disableLighting();
			if (this.isPointWithinBounds(36, 24, 16, 16, (double)i, (double)j) && !itemStack.isEmpty()) {
				this.drawStackTooltip(itemStack, i, j);
			} else if (!itemStack2.isEmpty() && this.isPointWithinBounds(62, 24, 16, 16, (double)i, (double)j)) {
				this.drawStackTooltip(itemStack2, i, j);
			} else if (!itemStack3.isEmpty() && this.isPointWithinBounds(120, 24, 16, 16, (double)i, (double)j)) {
				this.drawStackTooltip(itemStack3, i, j);
			} else if (traderRecipe.isDisabled()
				&& (this.isPointWithinBounds(83, 21, 28, 21, (double)i, (double)j) || this.isPointWithinBounds(83, 51, 28, 21, (double)i, (double)j))) {
				this.drawTooltip(I18n.translate("merchant.deprecated"), i, j);
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			GuiLighting.enable();
		}

		this.drawMouseoverTooltip(i, j);
	}

	@Environment(EnvType.CLIENT)
	abstract static class WidgetButtonPage extends ButtonWidget {
		private final boolean next;

		public WidgetButtonPage(int i, int j, boolean bl) {
			super(i, j, 12, 19, "");
			this.next = bl;
		}

		@Override
		public void draw(int i, int j, float f) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(VillagerScreen.TEXTURE);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = 0;
			int l = 176;
			if (!this.enabled) {
				l += this.width * 2;
			} else if (this.isHovered()) {
				l += this.width;
			}

			if (!this.next) {
				k += this.height;
			}

			this.drawTexturedRect(this.x, this.y, l, k, this.width, this.height);
		}
	}
}
