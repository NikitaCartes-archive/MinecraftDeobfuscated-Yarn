package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;

@Environment(EnvType.CLIENT)
public class MerchantScreen extends HandledScreen<MerchantScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
	private int selectedIndex;
	private final MerchantScreen.WidgetButtonPage[] offers = new MerchantScreen.WidgetButtonPage[7];
	private int indexStartOffset;
	private boolean scrolling;

	public MerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 276;
	}

	private void syncRecipeIndex() {
		this.handler.setRecipeIndex(this.selectedIndex);
		this.handler.switchTo(this.selectedIndex);
		this.client.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.selectedIndex));
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		int k = j + 16 + 2;

		for (int l = 0; l < 7; l++) {
			this.offers[l] = this.addButton(new MerchantScreen.WidgetButtonPage(i + 5, k, l, buttonWidget -> {
				if (buttonWidget instanceof MerchantScreen.WidgetButtonPage) {
					this.selectedIndex = ((MerchantScreen.WidgetButtonPage)buttonWidget).getIndex() + this.indexStartOffset;
					this.syncRecipeIndex();
				}
			}));
			k += 20;
		}
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		int i = this.handler.getLevelProgress();
		int j = this.backgroundHeight - 94;
		if (i > 0 && i <= 5 && this.handler.isLevelled()) {
			String string = this.title.asFormattedString();
			String string2 = "- " + I18n.translate("merchant.level." + i);
			int k = this.textRenderer.getStringWidth(string);
			int l = this.textRenderer.getStringWidth(string2);
			int m = k + l + 3;
			int n = 49 + this.backgroundWidth / 2 - m / 2;
			this.textRenderer.draw(string, (float)n, 6.0F, 4210752);
			this.textRenderer.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0F, (float)j, 4210752);
			this.textRenderer.draw(string2, (float)(n + k + 3), 6.0F, 4210752);
		} else {
			String string = this.title.asFormattedString();
			this.textRenderer.draw(string, (float)(49 + this.backgroundWidth / 2 - this.textRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
			this.textRenderer.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0F, (float)j, 4210752);
		}

		String string = I18n.translate("merchant.trades");
		int o = this.textRenderer.getStringWidth(string);
		this.textRenderer.draw(string, (float)(5 - o / 2 + 48), 6.0F, 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		drawTexture(i, j, this.getZOffset(), 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 512);
		TraderOfferList traderOfferList = this.handler.getRecipes();
		if (!traderOfferList.isEmpty()) {
			int k = this.selectedIndex;
			if (k < 0 || k >= traderOfferList.size()) {
				return;
			}

			TradeOffer tradeOffer = (TradeOffer)traderOfferList.get(k);
			if (tradeOffer.isDisabled()) {
				this.client.getTextureManager().bindTexture(TEXTURE);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				drawTexture(this.x + 83 + 99, this.y + 35, this.getZOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
			}
		}
	}

	private void drawLevelInfo(int i, int j, TradeOffer tradeOffer) {
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = this.handler.getLevelProgress();
		int l = this.handler.getExperience();
		if (k < 5) {
			drawTexture(i + 136, j + 16, this.getZOffset(), 0.0F, 186.0F, 102, 5, 256, 512);
			int m = VillagerData.getLowerLevelExperience(k);
			if (l >= m && VillagerData.canLevelUp(k)) {
				int n = 100;
				float f = (float)(100 / (VillagerData.getUpperLevelExperience(k) - m));
				int o = Math.min(MathHelper.floor(f * (float)(l - m)), 100);
				drawTexture(i + 136, j + 16, this.getZOffset(), 0.0F, 191.0F, o + 1, 5, 256, 512);
				int p = this.handler.getTraderRewardedExperience();
				if (p > 0) {
					int q = Math.min(MathHelper.floor((float)p * f), 100 - o);
					drawTexture(i + 136 + o + 1, j + 16 + 1, this.getZOffset(), 2.0F, 182.0F, q, 3, 256, 512);
				}
			}
		}
	}

	private void method_20221(int i, int j, TraderOfferList traderOfferList) {
		int k = traderOfferList.size() + 1 - 7;
		if (k > 1) {
			int l = 139 - (27 + (k - 1) * 139 / k);
			int m = 1 + l / k + 139 / k;
			int n = 113;
			int o = Math.min(113, this.indexStartOffset * m);
			if (this.indexStartOffset == k - 1) {
				o = 113;
			}

			drawTexture(i + 94, j + 18 + o, this.getZOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
		} else {
			drawTexture(i + 94, j + 18, this.getZOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		TraderOfferList traderOfferList = this.handler.getRecipes();
		if (!traderOfferList.isEmpty()) {
			int i = (this.width - this.backgroundWidth) / 2;
			int j = (this.height - this.backgroundHeight) / 2;
			int k = j + 16 + 1;
			int l = i + 5 + 5;
			RenderSystem.pushMatrix();
			RenderSystem.enableRescaleNormal();
			this.client.getTextureManager().bindTexture(TEXTURE);
			this.method_20221(i, j, traderOfferList);
			int m = 0;

			for (TradeOffer tradeOffer : traderOfferList) {
				if (!this.canScroll(traderOfferList.size()) || m >= this.indexStartOffset && m < 7 + this.indexStartOffset) {
					ItemStack itemStack = tradeOffer.getOriginalFirstBuyItem();
					ItemStack itemStack2 = tradeOffer.getAdjustedFirstBuyItem();
					ItemStack itemStack3 = tradeOffer.getSecondBuyItem();
					ItemStack itemStack4 = tradeOffer.getMutableSellItem();
					this.itemRenderer.zOffset = 100.0F;
					int n = k + 2;
					this.method_20222(itemStack2, itemStack, l, n);
					if (!itemStack3.isEmpty()) {
						this.itemRenderer.renderGuiItem(itemStack3, i + 5 + 35, n);
						this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack3, i + 5 + 35, n);
					}

					this.method_20223(tradeOffer, i, n);
					this.itemRenderer.renderGuiItem(itemStack4, i + 5 + 68, n);
					this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack4, i + 5 + 68, n);
					this.itemRenderer.zOffset = 0.0F;
					k += 20;
					m++;
				} else {
					m++;
				}
			}

			int o = this.selectedIndex;
			TradeOffer tradeOfferx = (TradeOffer)traderOfferList.get(o);
			if (this.handler.isLevelled()) {
				this.drawLevelInfo(i, j, tradeOfferx);
			}

			if (tradeOfferx.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, (double)mouseX, (double)mouseY) && this.handler.canRefreshTrades()) {
				this.renderTooltip(I18n.translate("merchant.deprecated"), mouseX, mouseY);
			}

			for (MerchantScreen.WidgetButtonPage widgetButtonPage : this.offers) {
				if (widgetButtonPage.isHovered()) {
					widgetButtonPage.renderToolTip(mouseX, mouseY);
				}

				widgetButtonPage.visible = widgetButtonPage.index < this.handler.getRecipes().size();
			}

			RenderSystem.popMatrix();
			RenderSystem.enableDepthTest();
		}

		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	private void method_20223(TradeOffer tradeOffer, int i, int j) {
		RenderSystem.enableBlend();
		this.client.getTextureManager().bindTexture(TEXTURE);
		if (tradeOffer.isDisabled()) {
			drawTexture(i + 5 + 35 + 20, j + 3, this.getZOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
		} else {
			drawTexture(i + 5 + 35 + 20, j + 3, this.getZOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
		}
	}

	private void method_20222(ItemStack itemStack, ItemStack itemStack2, int i, int j) {
		this.itemRenderer.renderGuiItem(itemStack, i, j);
		if (itemStack2.getCount() == itemStack.getCount()) {
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, i, j);
		} else {
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack2, i, j, itemStack2.getCount() == 1 ? "1" : null);
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, i + 14, j, itemStack.getCount() == 1 ? "1" : null);
			this.client.getTextureManager().bindTexture(TEXTURE);
			this.setZOffset(this.getZOffset() + 300);
			drawTexture(i + 7, j + 12, this.getZOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
			this.setZOffset(this.getZOffset() - 300);
		}
	}

	private boolean canScroll(int listSize) {
		return listSize > 7;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		int i = this.handler.getRecipes().size();
		if (this.canScroll(i)) {
			int j = i - 7;
			this.indexStartOffset = (int)((double)this.indexStartOffset - amount);
			this.indexStartOffset = MathHelper.clamp(this.indexStartOffset, 0, j);
		}

		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		int i = this.handler.getRecipes().size();
		if (this.scrolling) {
			int j = this.y + 18;
			int k = j + 139;
			int l = i - 7;
			float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
			f = f * (float)l + 0.5F;
			this.indexStartOffset = MathHelper.clamp((int)f, 0, l);
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.scrolling = false;
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		if (this.canScroll(this.handler.getRecipes().size())
			&& mouseX > (double)(i + 94)
			&& mouseX < (double)(i + 94 + 6)
			&& mouseY > (double)(j + 18)
			&& mouseY <= (double)(j + 18 + 139 + 1)) {
			this.scrolling = true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonPage extends ButtonWidget {
		final int index;

		public WidgetButtonPage(int i, int j, int k, ButtonWidget.PressAction pressAction) {
			super(i, j, 89, 20, "", pressAction);
			this.index = k;
			this.visible = false;
		}

		public int getIndex() {
			return this.index;
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			if (this.hovered && MerchantScreen.this.handler.getRecipes().size() > this.index + MerchantScreen.this.indexStartOffset) {
				if (mouseX < this.x + 20) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.handler.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset))
						.getAdjustedFirstBuyItem();
					MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
				} else if (mouseX < this.x + 50 && mouseX > this.x + 30) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.handler.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSecondBuyItem();
					if (!itemStack.isEmpty()) {
						MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
					}
				} else if (mouseX > this.x + 65) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.handler.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getMutableSellItem();
					MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
				}
			}
		}
	}
}
