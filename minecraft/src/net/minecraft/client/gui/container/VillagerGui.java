package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.VillagerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.SelectVillagerTradeServerPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerRecipe;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class VillagerGui extends ContainerGui {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager.png");
	private final Villager villager;
	private VillagerGui.WidgetButtonPage buttonPageNext;
	private VillagerGui.WidgetButtonPage buttonPagePrevious;
	private int field_2947;
	private final TextComponent villagerName;
	private final PlayerInventory playerInventory;

	public VillagerGui(PlayerInventory playerInventory, Villager villager, World world) {
		super(new VillagerContainer(playerInventory, villager, world));
		this.villager = villager;
		this.villagerName = villager.getDisplayName();
		this.playerInventory = playerInventory;
	}

	private void method_2496() {
		((VillagerContainer)this.container).setRecipeIndex(this.field_2947);
		this.client.getNetworkHandler().sendPacket(new SelectVillagerTradeServerPacket(this.field_2947));
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.buttonPageNext = this.addButton(new VillagerGui.WidgetButtonPage(1, i + 120 + 27, j + 24 - 1, true) {
			@Override
			public void onPressed(double d, double e) {
				VillagerGui.this.field_2947++;
				VillagerRecipeList villagerRecipeList = VillagerGui.this.villager.getRecipes();
				if (villagerRecipeList != null && VillagerGui.this.field_2947 >= villagerRecipeList.size()) {
					VillagerGui.this.field_2947 = villagerRecipeList.size() - 1;
				}

				VillagerGui.this.method_2496();
			}
		});
		this.buttonPagePrevious = this.addButton(new VillagerGui.WidgetButtonPage(2, i + 36 - 19, j + 24 - 1, false) {
			@Override
			public void onPressed(double d, double e) {
				VillagerGui.this.field_2947--;
				if (VillagerGui.this.field_2947 < 0) {
					VillagerGui.this.field_2947 = 0;
				}

				VillagerGui.this.method_2496();
			}
		});
		this.buttonPageNext.enabled = false;
		this.buttonPagePrevious.enabled = false;
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = this.villagerName.getFormattedText();
		this.fontRenderer.draw(string, (float)(this.containerWidth / 2 - this.fontRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	public void update() {
		super.update();
		VillagerRecipeList villagerRecipeList = this.villager.getRecipes();
		this.buttonPageNext.enabled = this.field_2947 < villagerRecipeList.size() - 1;
		this.buttonPagePrevious.enabled = this.field_2947 > 0;
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		VillagerRecipeList villagerRecipeList = this.villager.getRecipes();
		if (!villagerRecipeList.isEmpty()) {
			int m = this.field_2947;
			if (m < 0 || m >= villagerRecipeList.size()) {
				return;
			}

			VillagerRecipe villagerRecipe = (VillagerRecipe)villagerRecipeList.get(m);
			if (villagerRecipe.isDisabled()) {
				this.client.getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				this.drawTexturedRect(this.left + 83, this.top + 21, 212, 0, 28, 21);
				this.drawTexturedRect(this.left + 83, this.top + 51, 212, 0, 28, 21);
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		VillagerRecipeList villagerRecipeList = this.villager.getRecipes();
		if (!villagerRecipeList.isEmpty()) {
			int k = (this.width - this.containerWidth) / 2;
			int l = (this.height - this.containerHeight) / 2;
			int m = this.field_2947;
			VillagerRecipe villagerRecipe = (VillagerRecipe)villagerRecipeList.get(m);
			ItemStack itemStack = villagerRecipe.getBuyItem();
			ItemStack itemStack2 = villagerRecipe.getSecondBuyItem();
			ItemStack itemStack3 = villagerRecipe.getSellItem();
			GlStateManager.pushMatrix();
			GuiLighting.enableForItems();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.itemRenderer.zOffset = 100.0F;
			this.itemRenderer.renderItemAndGlowInGui(itemStack, k + 36, l + 24);
			this.itemRenderer.renderItemOverlaysInGUI(this.fontRenderer, itemStack, k + 36, l + 24);
			if (!itemStack2.isEmpty()) {
				this.itemRenderer.renderItemAndGlowInGui(itemStack2, k + 62, l + 24);
				this.itemRenderer.renderItemOverlaysInGUI(this.fontRenderer, itemStack2, k + 62, l + 24);
			}

			this.itemRenderer.renderItemAndGlowInGui(itemStack3, k + 120, l + 24);
			this.itemRenderer.renderItemOverlaysInGUI(this.fontRenderer, itemStack3, k + 120, l + 24);
			this.itemRenderer.zOffset = 0.0F;
			GlStateManager.disableLighting();
			if (this.isPointWithinBounds(36, 24, 16, 16, (double)i, (double)j) && !itemStack.isEmpty()) {
				this.drawStackTooltip(itemStack, i, j);
			} else if (!itemStack2.isEmpty() && this.isPointWithinBounds(62, 24, 16, 16, (double)i, (double)j)) {
				this.drawStackTooltip(itemStack2, i, j);
			} else if (!itemStack3.isEmpty() && this.isPointWithinBounds(120, 24, 16, 16, (double)i, (double)j)) {
				this.drawStackTooltip(itemStack3, i, j);
			} else if (villagerRecipe.isDisabled()
				&& (this.isPointWithinBounds(83, 21, 28, 21, (double)i, (double)j) || this.isPointWithinBounds(83, 51, 28, 21, (double)i, (double)j))) {
				this.drawTooltip(I18n.translate("merchant.deprecated"), i, j);
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			GuiLighting.enable();
		}

		this.drawMousoverTooltip(i, j);
	}

	public Villager method_2495() {
		return this.villager;
	}

	@Environment(EnvType.CLIENT)
	abstract static class WidgetButtonPage extends ButtonWidget {
		private final boolean next;

		public WidgetButtonPage(int i, int j, int k, boolean bl) {
			super(i, j, k, 12, 19, "");
			this.next = bl;
		}

		@Override
		public void draw(int i, int j, float f) {
			if (this.visible) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(VillagerGui.TEXTURE);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
				int k = 0;
				int l = 176;
				if (!this.enabled) {
					l += this.width * 2;
				} else if (bl) {
					l += this.width;
				}

				if (!this.next) {
					k += this.height;
				}

				this.drawTexturedRect(this.x, this.y, l, k, this.width, this.height);
			}
		}
	}
}
