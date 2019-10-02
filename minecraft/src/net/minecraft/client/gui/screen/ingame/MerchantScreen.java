package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.MerchantContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;

@Environment(EnvType.CLIENT)
public class MerchantScreen extends AbstractContainerScreen<MerchantContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
	private int selectedIndex;
	private final MerchantScreen.WidgetButtonPage[] offers = new MerchantScreen.WidgetButtonPage[7];
	private int indexStartOffset;
	private boolean scrolling;

	public MerchantScreen(MerchantContainer merchantContainer, PlayerInventory playerInventory, Text text) {
		super(merchantContainer, playerInventory, text);
		this.containerWidth = 276;
	}

	private void syncRecipeIndex() {
		this.container.setRecipeIndex(this.selectedIndex);
		this.container.switchTo(this.selectedIndex);
		this.minecraft.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.selectedIndex));
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
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
	protected void drawForeground(int i, int j) {
		int k = this.container.getLevelProgress();
		int l = this.containerHeight - 94;
		if (k > 0 && k <= 5 && this.container.isLevelled()) {
			String string = this.title.asFormattedString();
			String string2 = "- " + I18n.translate("merchant.level." + k);
			int m = this.font.getStringWidth(string);
			int n = this.font.getStringWidth(string2);
			int o = m + n + 3;
			int p = 49 + this.containerWidth / 2 - o / 2;
			this.font.draw(string, (float)p, 6.0F, 4210752);
			this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0F, (float)l, 4210752);
			this.font.draw(string2, (float)(p + m + 3), 6.0F, 4210752);
		} else {
			String string = this.title.asFormattedString();
			this.font.draw(string, (float)(49 + this.containerWidth / 2 - this.font.getStringWidth(string) / 2), 6.0F, 4210752);
			this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0F, (float)l, 4210752);
		}

		String string = I18n.translate("merchant.trades");
		int q = this.font.getStringWidth(string);
		this.font.draw(string, (float)(5 - q / 2 + 48), 6.0F, 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		blit(k, l, this.getBlitOffset(), 0.0F, 0.0F, this.containerWidth, this.containerHeight, 256, 512);
		TraderOfferList traderOfferList = this.container.getRecipes();
		if (!traderOfferList.isEmpty()) {
			int m = this.selectedIndex;
			if (m < 0 || m >= traderOfferList.size()) {
				return;
			}

			TradeOffer tradeOffer = (TradeOffer)traderOfferList.get(m);
			if (tradeOffer.isDisabled()) {
				this.minecraft.getTextureManager().bindTexture(TEXTURE);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				blit(this.left + 83 + 99, this.top + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
			}
		}
	}

	private void drawLevelInfo(int i, int j, TradeOffer tradeOffer) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = this.container.getLevelProgress();
		int l = this.container.getExperience();
		if (k < 5) {
			blit(i + 136, j + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 256, 512);
			int m = VillagerData.getLowerLevelExperience(k);
			if (l >= m && VillagerData.canLevelUp(k)) {
				int n = 100;
				float f = (float)(100 / (VillagerData.getUpperLevelExperience(k) - m));
				int o = MathHelper.floor(f * (float)(l - m));
				blit(i + 136, j + 16, this.getBlitOffset(), 0.0F, 191.0F, o + 1, 5, 256, 512);
				int p = this.container.getTraderRewardedExperience();
				if (p > 0) {
					int q = Math.min(MathHelper.floor((float)p * f), 100 - o);
					blit(i + 136 + o + 1, j + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, q, 3, 256, 512);
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

			blit(i + 94, j + 18 + o, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
		} else {
			blit(i + 94, j + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		TraderOfferList traderOfferList = this.container.getRecipes();
		if (!traderOfferList.isEmpty()) {
			int k = (this.width - this.containerWidth) / 2;
			int l = (this.height - this.containerHeight) / 2;
			int m = l + 16 + 1;
			int n = k + 5 + 5;
			RenderSystem.pushMatrix();
			RenderSystem.enableRescaleNormal();
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.method_20221(k, l, traderOfferList);
			int o = 0;

			for (TradeOffer tradeOffer : traderOfferList) {
				if (!this.canScroll(traderOfferList.size()) || o >= this.indexStartOffset && o < 7 + this.indexStartOffset) {
					ItemStack itemStack = tradeOffer.getOriginalFirstBuyItem();
					ItemStack itemStack2 = tradeOffer.getAdjustedFirstBuyItem();
					ItemStack itemStack3 = tradeOffer.getSecondBuyItem();
					ItemStack itemStack4 = tradeOffer.getMutableSellItem();
					this.itemRenderer.zOffset = 100.0F;
					int p = m + 2;
					this.method_20222(itemStack2, itemStack, n, p);
					if (!itemStack3.isEmpty()) {
						this.itemRenderer.renderGuiItem(itemStack3, k + 5 + 35, p);
						this.itemRenderer.renderGuiItemOverlay(this.font, itemStack3, k + 5 + 35, p);
					}

					this.method_20223(tradeOffer, k, p);
					this.itemRenderer.renderGuiItem(itemStack4, k + 5 + 68, p);
					this.itemRenderer.renderGuiItemOverlay(this.font, itemStack4, k + 5 + 68, p);
					this.itemRenderer.zOffset = 0.0F;
					m += 20;
					o++;
				} else {
					o++;
				}
			}

			int q = this.selectedIndex;
			TradeOffer tradeOfferx = (TradeOffer)traderOfferList.get(q);
			if (this.container.isLevelled()) {
				this.drawLevelInfo(k, l, tradeOfferx);
			}

			if (tradeOfferx.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, (double)i, (double)j) && this.container.canRefreshTrades()) {
				this.renderTooltip(I18n.translate("merchant.deprecated"), i, j);
			}

			for (MerchantScreen.WidgetButtonPage widgetButtonPage : this.offers) {
				if (widgetButtonPage.isHovered()) {
					widgetButtonPage.renderToolTip(i, j);
				}

				widgetButtonPage.visible = widgetButtonPage.index < this.container.getRecipes().size();
			}

			RenderSystem.popMatrix();
			RenderSystem.enableDepthTest();
		}

		this.drawMouseoverTooltip(i, j);
	}

	private void method_20223(TradeOffer tradeOffer, int i, int j) {
		RenderSystem.enableBlend();
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		if (tradeOffer.isDisabled()) {
			blit(i + 5 + 35 + 20, j + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
		} else {
			blit(i + 5 + 35 + 20, j + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
		}
	}

	private void method_20222(ItemStack itemStack, ItemStack itemStack2, int i, int j) {
		this.itemRenderer.renderGuiItem(itemStack, i, j);
		if (itemStack2.getCount() == itemStack.getCount()) {
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i, j);
		} else {
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack2, i, j, itemStack2.getCount() == 1 ? "1" : null);
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i + 14, j, itemStack.getCount() == 1 ? "1" : null);
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.setBlitOffset(this.getBlitOffset() + 300);
			blit(i + 7, j + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
			this.setBlitOffset(this.getBlitOffset() - 300);
		}
	}

	private boolean canScroll(int i) {
		return i > 7;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		int i = this.container.getRecipes().size();
		if (this.canScroll(i)) {
			int j = i - 7;
			this.indexStartOffset = (int)((double)this.indexStartOffset - f);
			this.indexStartOffset = MathHelper.clamp(this.indexStartOffset, 0, j);
		}

		return true;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		int j = this.container.getRecipes().size();
		if (this.scrolling) {
			int k = this.top + 18;
			int l = k + 139;
			int m = j - 7;
			float h = ((float)e - (float)k - 13.5F) / ((float)(l - k) - 27.0F);
			h = h * (float)m + 0.5F;
			this.indexStartOffset = MathHelper.clamp((int)h, 0, m);
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.scrolling = false;
		int j = (this.width - this.containerWidth) / 2;
		int k = (this.height - this.containerHeight) / 2;
		if (this.canScroll(this.container.getRecipes().size())
			&& d > (double)(j + 94)
			&& d < (double)(j + 94 + 6)
			&& e > (double)(k + 18)
			&& e <= (double)(k + 18 + 139 + 1)) {
			this.scrolling = true;
		}

		return super.mouseClicked(d, e, i);
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
		public void renderToolTip(int i, int j) {
			if (this.isHovered && MerchantScreen.this.container.getRecipes().size() > this.index + MerchantScreen.this.indexStartOffset) {
				if (i < this.x + 20) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.container.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset))
						.getAdjustedFirstBuyItem();
					MerchantScreen.this.renderTooltip(itemStack, i, j);
				} else if (i < this.x + 50 && i > this.x + 30) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.container.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSecondBuyItem();
					if (!itemStack.isEmpty()) {
						MerchantScreen.this.renderTooltip(itemStack, i, j);
					}
				} else if (i > this.x + 65) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.container.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getMutableSellItem();
					MerchantScreen.this.renderTooltip(itemStack, i, j);
				}
			}
		}
	}
}
