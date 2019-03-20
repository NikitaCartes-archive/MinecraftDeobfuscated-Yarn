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
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TraderRecipe;
import net.minecraft.village.TraderRecipeList;
import net.minecraft.village.VillagerData;

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
		int i = (this.screenWidth - this.width) / 2;
		int j = (this.screenHeight - this.height) / 2;
		this.buttonPageNext = this.addButton(new VillagerScreen.WidgetButtonPage(i + 120 + 27, j + 24 - 1, true) {
			@Override
			public void onPressed() {
				VillagerScreen.this.recipeIndex++;
				TraderRecipeList traderRecipeList = VillagerScreen.this.container.getRecipes();
				if (traderRecipeList != null && VillagerScreen.this.recipeIndex >= traderRecipeList.size()) {
					VillagerScreen.this.recipeIndex = traderRecipeList.size() - 1;
				}

				VillagerScreen.this.syncRecipeIndex();
			}
		});
		this.buttonPagePrevious = this.addButton(new VillagerScreen.WidgetButtonPage(i + 36 - 19, j + 24 - 1, false) {
			@Override
			public void onPressed() {
				VillagerScreen.this.recipeIndex--;
				if (VillagerScreen.this.recipeIndex < 0) {
					VillagerScreen.this.recipeIndex = 0;
				}

				VillagerScreen.this.syncRecipeIndex();
			}
		});
		this.buttonPageNext.active = false;
		this.buttonPagePrevious.active = false;
	}

	@Override
	protected void drawForeground(int i, int j) {
		int k = this.container.method_19258();
		if (k > 0 && k <= 5 && this.container.canLevel()) {
			String string = this.name.getFormattedText();
			String string2 = "- " + I18n.translate("merchant.level." + k);
			int l = this.fontRenderer.getStringWidth(string);
			int m = this.fontRenderer.getStringWidth(string2);
			int n = l + m + 3;
			int o = this.width / 2 - n / 2;
			this.fontRenderer.draw(string, (float)o, 6.0F, 4210752);
			this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
			this.fontRenderer.draw(string2, (float)(o + l + 3), 6.0F, 4210752);
		} else {
			String string = this.name.getFormattedText();
			this.fontRenderer.draw(string, (float)(this.width / 2 - this.fontRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
			this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
		}
	}

	@Override
	public void update() {
		super.update();
		TraderRecipeList traderRecipeList = this.container.getRecipes();
		this.buttonPageNext.active = this.recipeIndex < traderRecipeList.size() - 1;
		this.buttonPagePrevious.active = this.recipeIndex > 0;
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.screenWidth - this.width) / 2;
		int l = (this.screenHeight - this.height) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		TraderRecipeList traderRecipeList = this.container.getRecipes();
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

	private void method_19413(int i, int j, TraderRecipe traderRecipe) {
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = this.container.method_19258();
		int l = this.container.getExperience();
		if (k < 5) {
			this.drawTexturedRect(i + 37, j + 16, 0, 186, 102, 5);
			int m = VillagerData.getLowerLevelExperience(k);
			if (l >= m && VillagerData.isLevelValid(k)) {
				int n = 100;
				float f = (float)(100 / (VillagerData.getUpperLevelExperience(k) - m));
				int o = MathHelper.floor(f * (float)(l - m));
				this.drawTexturedRect(i + 37, j + 16, 0, 191, o + 1, 5);
				int p = this.container.method_19256();
				if (p > 0) {
					int q = Math.min(MathHelper.floor((float)p * f), 100 - o);
					this.drawTexturedRect(i + 37 + o + 1, j + 16 + 1, 2, 182, q, 3);
				}
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		super.render(i, j, f);
		TraderRecipeList traderRecipeList = this.container.getRecipes();
		if (!traderRecipeList.isEmpty()) {
			int k = (this.screenWidth - this.width) / 2;
			int l = (this.screenHeight - this.height) / 2;
			int m = this.recipeIndex;
			TraderRecipe traderRecipe = (TraderRecipe)traderRecipeList.get(m);
			ItemStack itemStack = traderRecipe.getFirstBuyItem();
			ItemStack itemStack2 = traderRecipe.getDiscountedFirstBuyItem();
			ItemStack itemStack3 = traderRecipe.getSecondBuyItem();
			ItemStack itemStack4 = traderRecipe.getModifiableSellItem();
			GlStateManager.pushMatrix();
			GuiLighting.enableForItems();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.itemRenderer.zOffset = 100.0F;
			this.itemRenderer.renderGuiItem(itemStack2, k + 36, l + 24);
			if (itemStack.getAmount() == itemStack2.getAmount()) {
				this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack2, k + 36, l + 24);
			} else {
				this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack, k + 36, l + 24, itemStack.getAmount() == 1 ? "1" : null);
				this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack2, k + 36 + 14, l + 24, itemStack2.getAmount() == 1 ? "1" : null);
				this.client.getTextureManager().bindTexture(TEXTURE);
				this.zOffset += 300.0F;
				this.drawTexturedRect(k + 36 + 7, l + 24 + 13, 0, 176, 9, 2);
				this.zOffset -= 300.0F;
			}

			if (!itemStack3.isEmpty()) {
				this.itemRenderer.renderGuiItem(itemStack3, k + 62, l + 24);
				this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack3, k + 62, l + 24);
			}

			this.itemRenderer.renderGuiItem(itemStack4, k + 120, l + 24);
			this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack4, k + 120, l + 24);
			this.itemRenderer.zOffset = 0.0F;
			GlStateManager.disableLighting();
			if (this.container.canLevel()) {
				this.method_19413(k, l, traderRecipe);
			}

			if (this.isPointWithinBounds(36, 24, 16, 16, (double)i, (double)j) && !itemStack2.isEmpty()) {
				this.drawStackTooltip(itemStack2, i, j);
			} else if (!itemStack3.isEmpty() && this.isPointWithinBounds(62, 24, 16, 16, (double)i, (double)j)) {
				this.drawStackTooltip(itemStack3, i, j);
			} else if (!itemStack4.isEmpty() && this.isPointWithinBounds(120, 24, 16, 16, (double)i, (double)j)) {
				this.drawStackTooltip(itemStack4, i, j);
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
		public void renderButton(int i, int j, float f) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(VillagerScreen.TEXTURE);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = 0;
			int l = 176;
			if (!this.active) {
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
