package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.MerchantContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;

@Environment(EnvType.CLIENT)
public class MerchantScreen extends ContainerScreen<MerchantContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
	private int field_19161;
	private final MerchantScreen.WidgetButtonPage[] field_19162 = new MerchantScreen.WidgetButtonPage[7];
	private int field_19163;
	private boolean field_19164;

	public MerchantScreen(MerchantContainer merchantContainer, PlayerInventory playerInventory, Text text) {
		super(merchantContainer, playerInventory, text);
		this.containerWidth = 276;
	}

	private void syncRecipeIndex() {
		this.container.setRecipeIndex(this.field_19161);
		this.container.switchTo(this.field_19161);
		this.minecraft.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.field_19161));
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		int k = j + 16 + 2;

		for (int l = 0; l < 7; l++) {
			this.field_19162[l] = this.addButton(new MerchantScreen.WidgetButtonPage(i + 5, k, l, buttonWidget -> {
				if (buttonWidget instanceof MerchantScreen.WidgetButtonPage) {
					this.field_19161 = ((MerchantScreen.WidgetButtonPage)buttonWidget).method_20228() + this.field_19163;
					this.syncRecipeIndex();
				}
			}));
			k += 20;
		}
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		int i = this.container.getLevelProgress();
		int j = this.containerHeight - 94;
		if (i > 0 && i <= 5 && this.container.isLevelled()) {
			String string = this.title.asFormattedString();
			String string2 = "- " + I18n.translate("merchant.level." + i);
			int k = this.font.getStringWidth(string);
			int l = this.font.getStringWidth(string2);
			int m = k + l + 3;
			int n = 49 + this.containerWidth / 2 - m / 2;
			this.font.draw(string, (float)n, 6.0F, 4210752);
			this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0F, (float)j, 4210752);
			this.font.draw(string2, (float)(n + k + 3), 6.0F, 4210752);
		} else {
			String string = this.title.asFormattedString();
			this.font.draw(string, (float)(49 + this.containerWidth / 2 - this.font.getStringWidth(string) / 2), 6.0F, 4210752);
			this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0F, (float)j, 4210752);
		}

		String string = I18n.translate("merchant.trades");
		int o = this.font.getStringWidth(string);
		this.font.draw(string, (float)(5 - o / 2 + 48), 6.0F, 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		blit(i, j, this.blitOffset, 0.0F, 0.0F, this.containerWidth, this.containerHeight, 256, 512);
		TraderOfferList traderOfferList = this.container.getRecipes();
		if (!traderOfferList.isEmpty()) {
			int k = this.field_19161;
			if (k < 0 || k >= traderOfferList.size()) {
				return;
			}

			TradeOffer tradeOffer = (TradeOffer)traderOfferList.get(k);
			if (tradeOffer.isDisabled()) {
				this.minecraft.getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				blit(this.x + 83 + 99, this.y + 35, this.blitOffset, 311.0F, 0.0F, 28, 21, 256, 512);
			}
		}
	}

	private void method_19413(int i, int j, TradeOffer tradeOffer) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = this.container.getLevelProgress();
		int l = this.container.getExperience();
		if (k < 5) {
			blit(i + 136, j + 16, this.blitOffset, 0.0F, 186.0F, 102, 5, 256, 512);
			int m = VillagerData.getLowerLevelExperience(k);
			if (l >= m && VillagerData.canLevelUp(k)) {
				int n = 100;
				float f = (float)(100 / (VillagerData.getUpperLevelExperience(k) - m));
				int o = MathHelper.floor(f * (float)(l - m));
				blit(i + 136, j + 16, this.blitOffset, 0.0F, 191.0F, o + 1, 5, 256, 512);
				int p = this.container.getTraderRewardedExperience();
				if (p > 0) {
					int q = Math.min(MathHelper.floor((float)p * f), 100 - o);
					blit(i + 136 + o + 1, j + 16 + 1, this.blitOffset, 2.0F, 182.0F, q, 3, 256, 512);
				}
			}
		}
	}

	private void method_20221(int i, int j, TraderOfferList traderOfferList) {
		DiffuseLighting.disable();
		int k = traderOfferList.size() + 1 - 7;
		if (k > 1) {
			int l = 139 - (27 + (k - 1) * 139 / k);
			int m = 1 + l / k + 139 / k;
			int n = 113;
			int o = Math.min(113, this.field_19163 * m);
			if (this.field_19163 == k - 1) {
				o = 113;
			}

			blit(i + 94, j + 18 + o, this.blitOffset, 0.0F, 199.0F, 6, 27, 256, 512);
		} else {
			blit(i + 94, j + 18, this.blitOffset, 6.0F, 199.0F, 6, 27, 256, 512);
		}

		DiffuseLighting.enableForItems();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		TraderOfferList traderOfferList = this.container.getRecipes();
		if (!traderOfferList.isEmpty()) {
			int i = (this.width - this.containerWidth) / 2;
			int j = (this.height - this.containerHeight) / 2;
			int k = j + 16 + 1;
			int l = i + 5 + 5;
			GlStateManager.pushMatrix();
			DiffuseLighting.enableForItems();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.method_20221(i, j, traderOfferList);
			int m = 0;

			for (TradeOffer tradeOffer : traderOfferList) {
				if (!this.method_20220(traderOfferList.size()) || m >= this.field_19163 && m < 7 + this.field_19163) {
					ItemStack itemStack = tradeOffer.getOriginalFirstBuyItem();
					ItemStack itemStack2 = tradeOffer.getAdjustedFirstBuyItem();
					ItemStack itemStack3 = tradeOffer.getSecondBuyItem();
					ItemStack itemStack4 = tradeOffer.getMutableSellItem();
					this.itemRenderer.zOffset = 100.0F;
					int n = k + 2;
					this.method_20222(itemStack2, itemStack, l, n);
					if (!itemStack3.isEmpty()) {
						this.itemRenderer.renderGuiItem(itemStack3, i + 5 + 35, n);
						this.itemRenderer.renderGuiItemOverlay(this.font, itemStack3, i + 5 + 35, n);
					}

					this.method_20223(tradeOffer, i, n);
					this.itemRenderer.renderGuiItem(itemStack4, i + 5 + 68, n);
					this.itemRenderer.renderGuiItemOverlay(this.font, itemStack4, i + 5 + 68, n);
					this.itemRenderer.zOffset = 0.0F;
					k += 20;
					m++;
				} else {
					m++;
				}
			}

			int o = this.field_19161;
			TradeOffer tradeOfferx = (TradeOffer)traderOfferList.get(o);
			GlStateManager.disableLighting();
			if (this.container.isLevelled()) {
				this.method_19413(i, j, tradeOfferx);
			}

			if (tradeOfferx.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, (double)mouseX, (double)mouseY) && this.container.canRefreshTrades()) {
				this.renderTooltip(I18n.translate("merchant.deprecated"), mouseX, mouseY);
			}

			for (MerchantScreen.WidgetButtonPage widgetButtonPage : this.field_19162) {
				if (widgetButtonPage.isHovered()) {
					widgetButtonPage.renderToolTip(mouseX, mouseY);
				}

				widgetButtonPage.visible = widgetButtonPage.field_19165 < this.container.getRecipes().size();
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			DiffuseLighting.enable();
		}

		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	private void method_20223(TradeOffer tradeOffer, int i, int j) {
		DiffuseLighting.disable();
		GlStateManager.enableBlend();
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		if (tradeOffer.isDisabled()) {
			blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 25.0F, 171.0F, 10, 9, 256, 512);
		} else {
			blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 15.0F, 171.0F, 10, 9, 256, 512);
		}

		DiffuseLighting.enableForItems();
	}

	private void method_20222(ItemStack itemStack, ItemStack itemStack2, int i, int j) {
		this.itemRenderer.renderGuiItem(itemStack, i, j);
		if (itemStack2.getCount() == itemStack.getCount()) {
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i, j);
		} else {
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack2, i, j, itemStack2.getCount() == 1 ? "1" : null);
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i + 14, j, itemStack.getCount() == 1 ? "1" : null);
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.blitOffset += 300;
			DiffuseLighting.disable();
			blit(i + 7, j + 12, this.blitOffset, 0.0F, 176.0F, 9, 2, 256, 512);
			DiffuseLighting.enableForItems();
			this.blitOffset -= 300;
		}
	}

	private boolean method_20220(int i) {
		return i > 7;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		int i = this.container.getRecipes().size();
		if (this.method_20220(i)) {
			int j = i - 7;
			this.field_19163 = (int)((double)this.field_19163 - amount);
			this.field_19163 = MathHelper.clamp(this.field_19163, 0, j);
		}

		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		int i = this.container.getRecipes().size();
		if (this.field_19164) {
			int j = this.y + 18;
			int k = j + 139;
			int l = i - 7;
			float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
			f = f * (float)l + 0.5F;
			this.field_19163 = MathHelper.clamp((int)f, 0, l);
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.field_19164 = false;
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		if (this.method_20220(this.container.getRecipes().size())
			&& mouseX > (double)(i + 94)
			&& mouseX < (double)(i + 94 + 6)
			&& mouseY > (double)(j + 18)
			&& mouseY <= (double)(j + 18 + 139 + 1)) {
			this.field_19164 = true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonPage extends ButtonWidget {
		final int field_19165;

		public WidgetButtonPage(int i, int j, int k, ButtonWidget.PressAction pressAction) {
			super(i, j, 89, 20, "", pressAction);
			this.field_19165 = k;
			this.visible = false;
		}

		public int method_20228() {
			return this.field_19165;
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			if (this.isHovered && MerchantScreen.this.container.getRecipes().size() > this.field_19165 + MerchantScreen.this.field_19163) {
				if (mouseX < this.x + 20) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.container.getRecipes().get(this.field_19165 + MerchantScreen.this.field_19163))
						.getAdjustedFirstBuyItem();
					MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
				} else if (mouseX < this.x + 50 && mouseX > this.x + 30) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.container.getRecipes().get(this.field_19165 + MerchantScreen.this.field_19163)).getSecondBuyItem();
					if (!itemStack.isEmpty()) {
						MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
					}
				} else if (mouseX > this.x + 65) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.container.getRecipes().get(this.field_19165 + MerchantScreen.this.field_19163))
						.getMutableSellItem();
					MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
				}
			}
		}
	}
}
