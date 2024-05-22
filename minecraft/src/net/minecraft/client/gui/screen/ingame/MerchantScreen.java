package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;

@Environment(EnvType.CLIENT)
public class MerchantScreen extends HandledScreen<MerchantScreenHandler> {
	private static final Identifier OUT_OF_STOCK_TEXTURE = Identifier.ofVanilla("container/villager/out_of_stock");
	private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("container/villager/experience_bar_background");
	private static final Identifier EXPERIENCE_BAR_CURRENT_TEXTURE = Identifier.ofVanilla("container/villager/experience_bar_current");
	private static final Identifier EXPERIENCE_BAR_RESULT_TEXTURE = Identifier.ofVanilla("container/villager/experience_bar_result");
	private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/villager/scroller");
	private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/villager/scroller_disabled");
	private static final Identifier TRADE_ARROW_OUT_OF_STOCK_TEXTURE = Identifier.ofVanilla("container/villager/trade_arrow_out_of_stock");
	private static final Identifier TRADE_ARROW_TEXTURE = Identifier.ofVanilla("container/villager/trade_arrow");
	private static final Identifier DISCOUNT_STRIKETHROUGH_TEXTURE = Identifier.ofVanilla("container/villager/discount_strikethrough");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/villager.png");
	private static final int TEXTURE_WIDTH = 512;
	private static final int TEXTURE_HEIGHT = 256;
	private static final int field_32356 = 99;
	private static final int XP_BAR_X_OFFSET = 136;
	private static final int TRADE_LIST_AREA_Y_OFFSET = 16;
	private static final int FIRST_BUY_ITEM_X_OFFSET = 5;
	private static final int SECOND_BUY_ITEM_X_OFFSET = 35;
	private static final int SOLD_ITEM_X_OFFSET = 68;
	private static final int field_32362 = 6;
	private static final int MAX_TRADE_OFFERS = 7;
	private static final int field_32364 = 5;
	private static final int TRADE_OFFER_BUTTON_HEIGHT = 20;
	private static final int TRADE_OFFER_BUTTON_WIDTH = 88;
	private static final int SCROLLBAR_HEIGHT = 27;
	private static final int SCROLLBAR_WIDTH = 6;
	private static final int SCROLLBAR_AREA_HEIGHT = 139;
	private static final int SCROLLBAR_OFFSET_Y = 18;
	private static final int SCROLLBAR_OFFSET_X = 94;
	private static final Text TRADES_TEXT = Text.translatable("merchant.trades");
	private static final Text DEPRECATED_TEXT = Text.translatable("merchant.deprecated");
	private int selectedIndex;
	private final MerchantScreen.WidgetButtonPage[] offers = new MerchantScreen.WidgetButtonPage[7];
	int indexStartOffset;
	private boolean scrolling;

	public MerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 276;
		this.playerInventoryTitleX = 107;
	}

	private void syncRecipeIndex() {
		this.handler.setRecipeIndex(this.selectedIndex);
		this.handler.switchTo(this.selectedIndex);
		this.client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(this.selectedIndex));
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		int k = j + 16 + 2;

		for (int l = 0; l < 7; l++) {
			this.offers[l] = this.addDrawableChild(new MerchantScreen.WidgetButtonPage(i + 5, k, l, button -> {
				if (button instanceof MerchantScreen.WidgetButtonPage) {
					this.selectedIndex = ((MerchantScreen.WidgetButtonPage)button).getIndex() + this.indexStartOffset;
					this.syncRecipeIndex();
				}
			}));
			k += 20;
		}
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		int i = this.handler.getLevelProgress();
		if (i > 0 && i <= 5 && this.handler.isLeveled()) {
			Text text = Text.translatable("merchant.title", this.title, Text.translatable("merchant.level." + i));
			int j = this.textRenderer.getWidth(text);
			int k = 49 + this.backgroundWidth / 2 - j / 2;
			context.drawText(this.textRenderer, text, k, 6, 4210752, false);
		} else {
			context.drawText(this.textRenderer, this.title, 49 + this.backgroundWidth / 2 - this.textRenderer.getWidth(this.title) / 2, 6, 4210752, false);
		}

		context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
		int l = this.textRenderer.getWidth(TRADES_TEXT);
		context.drawText(this.textRenderer, TRADES_TEXT, 5 - l / 2 + 48, 6, 4210752, false);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(TEXTURE, i, j, 0, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 512, 256);
		TradeOfferList tradeOfferList = this.handler.getRecipes();
		if (!tradeOfferList.isEmpty()) {
			int k = this.selectedIndex;
			if (k < 0 || k >= tradeOfferList.size()) {
				return;
			}

			TradeOffer tradeOffer = (TradeOffer)tradeOfferList.get(k);
			if (tradeOffer.isDisabled()) {
				context.drawGuiTexture(OUT_OF_STOCK_TEXTURE, this.x + 83 + 99, this.y + 35, 0, 28, 21);
			}
		}
	}

	private void drawLevelInfo(DrawContext context, int x, int y, TradeOffer tradeOffer) {
		int i = this.handler.getLevelProgress();
		int j = this.handler.getExperience();
		if (i < 5) {
			context.drawGuiTexture(EXPERIENCE_BAR_BACKGROUND_TEXTURE, x + 136, y + 16, 0, 102, 5);
			int k = VillagerData.getLowerLevelExperience(i);
			if (j >= k && VillagerData.canLevelUp(i)) {
				int l = 102;
				float f = 102.0F / (float)(VillagerData.getUpperLevelExperience(i) - k);
				int m = Math.min(MathHelper.floor(f * (float)(j - k)), 102);
				context.drawGuiTexture(EXPERIENCE_BAR_CURRENT_TEXTURE, 102, 5, 0, 0, x + 136, y + 16, 0, m, 5);
				int n = this.handler.getMerchantRewardedExperience();
				if (n > 0) {
					int o = Math.min(MathHelper.floor((float)n * f), 102 - m);
					context.drawGuiTexture(EXPERIENCE_BAR_RESULT_TEXTURE, 102, 5, m, 0, x + 136 + m, y + 16, 0, o, 5);
				}
			}
		}
	}

	private void renderScrollbar(DrawContext context, int x, int y, TradeOfferList tradeOffers) {
		int i = tradeOffers.size() + 1 - 7;
		if (i > 1) {
			int j = 139 - (27 + (i - 1) * 139 / i);
			int k = 1 + j / i + 139 / i;
			int l = 113;
			int m = Math.min(113, this.indexStartOffset * k);
			if (this.indexStartOffset == i - 1) {
				m = 113;
			}

			context.drawGuiTexture(SCROLLER_TEXTURE, x + 94, y + 18 + m, 0, 6, 27);
		} else {
			context.drawGuiTexture(SCROLLER_DISABLED_TEXTURE, x + 94, y + 18, 0, 6, 27);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		TradeOfferList tradeOfferList = this.handler.getRecipes();
		if (!tradeOfferList.isEmpty()) {
			int i = (this.width - this.backgroundWidth) / 2;
			int j = (this.height - this.backgroundHeight) / 2;
			int k = j + 16 + 1;
			int l = i + 5 + 5;
			this.renderScrollbar(context, i, j, tradeOfferList);
			int m = 0;

			for (TradeOffer tradeOffer : tradeOfferList) {
				if (!this.canScroll(tradeOfferList.size()) || m >= this.indexStartOffset && m < 7 + this.indexStartOffset) {
					ItemStack itemStack = tradeOffer.getOriginalFirstBuyItem();
					ItemStack itemStack2 = tradeOffer.getDisplayedFirstBuyItem();
					ItemStack itemStack3 = tradeOffer.getDisplayedSecondBuyItem();
					ItemStack itemStack4 = tradeOffer.getSellItem();
					context.getMatrices().push();
					context.getMatrices().translate(0.0F, 0.0F, 100.0F);
					int n = k + 2;
					this.renderFirstBuyItem(context, itemStack2, itemStack, l, n);
					if (!itemStack3.isEmpty()) {
						context.drawItemWithoutEntity(itemStack3, i + 5 + 35, n);
						context.drawItemInSlot(this.textRenderer, itemStack3, i + 5 + 35, n);
					}

					this.renderArrow(context, tradeOffer, i, n);
					context.drawItemWithoutEntity(itemStack4, i + 5 + 68, n);
					context.drawItemInSlot(this.textRenderer, itemStack4, i + 5 + 68, n);
					context.getMatrices().pop();
					k += 20;
					m++;
				} else {
					m++;
				}
			}

			int o = this.selectedIndex;
			TradeOffer tradeOfferx = (TradeOffer)tradeOfferList.get(o);
			if (this.handler.isLeveled()) {
				this.drawLevelInfo(context, i, j, tradeOfferx);
			}

			if (tradeOfferx.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, (double)mouseX, (double)mouseY) && this.handler.canRefreshTrades()) {
				context.drawTooltip(this.textRenderer, DEPRECATED_TEXT, mouseX, mouseY);
			}

			for (MerchantScreen.WidgetButtonPage widgetButtonPage : this.offers) {
				if (widgetButtonPage.isSelected()) {
					widgetButtonPage.renderTooltip(context, mouseX, mouseY);
				}

				widgetButtonPage.visible = widgetButtonPage.index < this.handler.getRecipes().size();
			}

			RenderSystem.enableDepthTest();
		}

		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	private void renderArrow(DrawContext context, TradeOffer tradeOffer, int x, int y) {
		RenderSystem.enableBlend();
		if (tradeOffer.isDisabled()) {
			context.drawGuiTexture(TRADE_ARROW_OUT_OF_STOCK_TEXTURE, x + 5 + 35 + 20, y + 3, 0, 10, 9);
		} else {
			context.drawGuiTexture(TRADE_ARROW_TEXTURE, x + 5 + 35 + 20, y + 3, 0, 10, 9);
		}
	}

	private void renderFirstBuyItem(DrawContext context, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, int x, int y) {
		context.drawItemWithoutEntity(adjustedFirstBuyItem, x, y);
		if (originalFirstBuyItem.getCount() == adjustedFirstBuyItem.getCount()) {
			context.drawItemInSlot(this.textRenderer, adjustedFirstBuyItem, x, y);
		} else {
			context.drawItemInSlot(this.textRenderer, originalFirstBuyItem, x, y, originalFirstBuyItem.getCount() == 1 ? "1" : null);
			context.drawItemInSlot(this.textRenderer, adjustedFirstBuyItem, x + 14, y, adjustedFirstBuyItem.getCount() == 1 ? "1" : null);
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, 300.0F);
			context.drawGuiTexture(DISCOUNT_STRIKETHROUGH_TEXTURE, x + 7, y + 12, 0, 9, 2);
			context.getMatrices().pop();
		}
	}

	private boolean canScroll(int listSize) {
		return listSize > 7;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		int i = this.handler.getRecipes().size();
		if (this.canScroll(i)) {
			int j = i - 7;
			this.indexStartOffset = MathHelper.clamp((int)((double)this.indexStartOffset - verticalAmount), 0, j);
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

		public WidgetButtonPage(final int x, final int y, final int index, final ButtonWidget.PressAction onPress) {
			super(x, y, 88, 20, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
			this.index = index;
			this.visible = false;
		}

		public int getIndex() {
			return this.index;
		}

		public void renderTooltip(DrawContext context, int x, int y) {
			if (this.hovered && MerchantScreen.this.handler.getRecipes().size() > this.index + MerchantScreen.this.indexStartOffset) {
				if (x < this.getX() + 20) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.handler.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset))
						.getDisplayedFirstBuyItem();
					context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
				} else if (x < this.getX() + 50 && x > this.getX() + 30) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.handler.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset))
						.getDisplayedSecondBuyItem();
					if (!itemStack.isEmpty()) {
						context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
					}
				} else if (x > this.getX() + 65) {
					ItemStack itemStack = ((TradeOffer)MerchantScreen.this.handler.getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSellItem();
					context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
				}
			}
		}
	}
}
