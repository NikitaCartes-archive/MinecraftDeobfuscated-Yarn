package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;

@Environment(EnvType.CLIENT)
public class VillagerScreen extends ContainerScreen<MerchantContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
	private int field_19161;
	private final VillagerScreen.WidgetButtonPage[] field_19162 = new VillagerScreen.WidgetButtonPage[7];
	private int field_19163;
	private boolean field_19164;

	public VillagerScreen(MerchantContainer merchantContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(merchantContainer, playerInventory, textComponent);
		this.containerWidth = 275;
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
			this.field_19162[l] = this.addButton(new VillagerScreen.WidgetButtonPage(i + 5, k, l, buttonWidget -> {
				if (buttonWidget instanceof VillagerScreen.WidgetButtonPage) {
					this.field_19161 = ((VillagerScreen.WidgetButtonPage)buttonWidget).method_20228() + this.field_19163;
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
			String string = this.title.getFormattedText();
			String string2 = "- " + I18n.translate("merchant.level." + k);
			int m = this.font.getStringWidth(string);
			int n = this.font.getStringWidth(string2);
			int o = m + n + 3;
			int p = 49 + this.containerWidth / 2 - o / 2;
			this.font.draw(string, (float)p, 6.0F, 4210752);
			this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 107.0F, (float)l, 4210752);
			this.font.draw(string2, (float)(p + m + 3), 6.0F, 4210752);
		} else {
			String string = this.title.getFormattedText();
			this.font.draw(string, (float)(49 + this.containerWidth / 2 - this.font.getStringWidth(string) / 2), 6.0F, 4210752);
			this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 107.0F, (float)l, 4210752);
		}

		String string = I18n.translate("merchant.trades");
		int q = this.font.getStringWidth(string);
		this.font.draw(string, (float)(5 - q / 2 + 48), 6.0F, 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		blit(k, l, this.blitOffset, 0.0F, 0.0F, this.containerWidth, this.containerHeight, 256, 512);
		TraderOfferList traderOfferList = this.container.getRecipes();
		if (!traderOfferList.isEmpty()) {
			int m = this.field_19161;
			if (m < 0 || m >= traderOfferList.size()) {
				return;
			}

			TradeOffer tradeOffer = (TradeOffer)traderOfferList.get(m);
			if (tradeOffer.isDisabled()) {
				this.minecraft.getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				blit(this.left + 83 + 99, this.top + 35, this.blitOffset, 311.0F, 0.0F, 28, 21, 256, 512);
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
		GuiLighting.disable();
		int k = traderOfferList.size() + 1 - 7;
		if (k > 1) {
			int l = 139 - (27 + (k - 1) * 139 / k);
			int m = 1 + l / k + 139 / k;
			int n = this.field_19163 * m;
			if (this.field_19163 == k - 1) {
				n = 113;
			}

			blit(i + 94, j + 18 + n, this.blitOffset, 0.0F, 199.0F, 6, 27, 256, 512);
		} else {
			blit(i + 94, j + 18, this.blitOffset, 6.0F, 199.0F, 6, 27, 256, 512);
		}

		GuiLighting.enableForItems();
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
			GlStateManager.pushMatrix();
			GuiLighting.enableForItems();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.method_20221(k, l, traderOfferList);
			int o = 0;

			for (TradeOffer tradeOffer : traderOfferList) {
				if (!this.method_20220(traderOfferList.size()) || o >= this.field_19163 && o < 7 + this.field_19163) {
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

			int q = this.field_19161;
			TradeOffer tradeOfferx = (TradeOffer)traderOfferList.get(q);
			GlStateManager.disableLighting();
			if (this.container.isLevelled()) {
				this.method_19413(k, l, tradeOfferx);
			}

			if (tradeOfferx.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, (double)i, (double)j)) {
				this.renderTooltip(I18n.translate("merchant.deprecated"), i, j);
			}

			for (VillagerScreen.WidgetButtonPage widgetButtonPage : this.field_19162) {
				if (widgetButtonPage.isHovered()) {
					widgetButtonPage.renderToolTip(i, j);
				}

				widgetButtonPage.visible = widgetButtonPage.field_19165 < this.container.getRecipes().size();
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			GuiLighting.enable();
		}

		this.drawMouseoverTooltip(i, j);
	}

	private void method_20223(TradeOffer tradeOffer, int i, int j) {
		GuiLighting.disable();
		GlStateManager.enableBlend();
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		if (tradeOffer.isDisabled()) {
			blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 25.0F, 171.0F, 10, 9, 256, 512);
		} else {
			blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 15.0F, 171.0F, 10, 9, 256, 512);
		}

		GuiLighting.enableForItems();
	}

	private void method_20222(ItemStack itemStack, ItemStack itemStack2, int i, int j) {
		this.itemRenderer.renderGuiItem(itemStack, i, j);
		if (itemStack2.getAmount() == itemStack.getAmount()) {
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i, j);
		} else {
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack2, i, j, itemStack2.getAmount() == 1 ? "1" : null);
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i + 14, j, itemStack.getAmount() == 1 ? "1" : null);
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.blitOffset += 300;
			GuiLighting.disable();
			blit(i + 7, j + 12, this.blitOffset, 0.0F, 176.0F, 9, 2, 256, 512);
			GuiLighting.enableForItems();
			this.blitOffset -= 300;
		}
	}

	private boolean method_20220(int i) {
		return i > 7;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		int i = this.container.getRecipes().size();
		if (this.method_20220(i)) {
			int j = i - 7;
			this.field_19163 = (int)((double)this.field_19163 - f);
			this.field_19163 = MathHelper.clamp(this.field_19163, 0, j);
		}

		return true;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		int j = this.container.getRecipes().size();
		if (this.field_19164) {
			int k = this.top + 18;
			int l = k + 139;
			int m = j - 7;
			float h = ((float)e - (float)k - 13.5F) / ((float)(l - k) - 27.0F);
			h = h * (float)m + 0.5F;
			this.field_19163 = MathHelper.clamp((int)h, 0, m);
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.field_19164 = false;
		int j = (this.width - this.containerWidth) / 2;
		int k = (this.height - this.containerHeight) / 2;
		if (this.method_20220(this.container.getRecipes().size())
			&& d > (double)(j + 94)
			&& d < (double)(j + 94 + 6)
			&& e > (double)(k + 18)
			&& e <= (double)(k + 18 + 139 + 1)) {
			this.field_19164 = true;
		}

		return super.mouseClicked(d, e, i);
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
		public void renderToolTip(int i, int j) {
			if (this.isHovered && VillagerScreen.this.container.getRecipes().size() > this.field_19165 + VillagerScreen.this.field_19163) {
				if (i < this.x + 20) {
					ItemStack itemStack = ((TradeOffer)VillagerScreen.this.container.getRecipes().get(this.field_19165 + VillagerScreen.this.field_19163))
						.getAdjustedFirstBuyItem();
					VillagerScreen.this.renderTooltip(itemStack, i, j);
				} else if (i < this.x + 50 && i > this.x + 30) {
					ItemStack itemStack = ((TradeOffer)VillagerScreen.this.container.getRecipes().get(this.field_19165 + VillagerScreen.this.field_19163)).getSecondBuyItem();
					if (!itemStack.isEmpty()) {
						VillagerScreen.this.renderTooltip(itemStack, i, j);
					}
				} else if (i > this.x + 65) {
					ItemStack itemStack = ((TradeOffer)VillagerScreen.this.container.getRecipes().get(this.field_19165 + VillagerScreen.this.field_19163))
						.getMutableSellItem();
					VillagerScreen.this.renderTooltip(itemStack, i, j);
				}
			}
		}
	}
}
