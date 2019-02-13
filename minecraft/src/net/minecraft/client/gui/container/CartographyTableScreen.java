package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CartographyTableScreen extends ContainerScreen<CartographyTableContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/cartography_table.png");

	public CartographyTableScreen(CartographyTableContainer cartographyTableContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(cartographyTableContainer, playerInventory, textComponent);
	}

	@Override
	public void draw(int i, int j, float f) {
		super.draw(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 4.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		this.drawBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		Item item = this.container.getSlot(1).getStack().getItem();
		boolean bl = item == Items.field_8895;
		boolean bl2 = item == Items.field_8407;
		boolean bl3 = item == Items.field_8141;
		ItemStack itemStack = this.container.getSlot(0).getStack();
		boolean bl4 = false;
		MapState mapState;
		if (itemStack.getItem() == Items.field_8204) {
			mapState = FilledMapItem.method_7997(itemStack, this.client.world);
			if (mapState != null) {
				if (mapState.field_17403) {
					bl4 = true;
					if (bl2 || bl3) {
						this.drawTexturedRect(k + 35, l + 31, this.containerWidth + 50, 132, 28, 21);
					}
				}

				if (bl2 && mapState.scale >= 4) {
					bl4 = true;
					this.drawTexturedRect(k + 35, l + 31, this.containerWidth + 50, 132, 28, 21);
				}
			}
		} else {
			mapState = null;
		}

		this.drawMap(mapState, bl, bl2, bl3, bl4);
	}

	private void drawMap(@Nullable MapState mapState, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		int i = this.left;
		int j = this.top;
		if (bl2 && !bl4) {
			this.drawTexturedRect(i + 67, j + 13, this.containerWidth, 66, 66, 66);
			this.drawMap(mapState, i + 85, j + 31, 0.226F);
		} else if (bl) {
			this.drawTexturedRect(i + 67 + 16, j + 13, this.containerWidth, 132, 50, 66);
			this.drawMap(mapState, i + 86, j + 16, 0.34F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 1.0F);
			this.drawTexturedRect(i + 67, j + 13 + 16, this.containerWidth, 132, 50, 66);
			this.drawMap(mapState, i + 70, j + 32, 0.34F);
			GlStateManager.popMatrix();
		} else if (bl3) {
			this.drawTexturedRect(i + 67, j + 13, this.containerWidth, 0, 66, 66);
			this.drawMap(mapState, i + 71, j + 17, 0.45F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 1.0F);
			this.drawTexturedRect(i + 66, j + 12, 0, this.containerHeight, 66, 66);
			GlStateManager.popMatrix();
		} else {
			this.drawTexturedRect(i + 67, j + 13, this.containerWidth, 0, 66, 66);
			this.drawMap(mapState, i + 71, j + 17, 0.45F);
		}
	}

	private void drawMap(@Nullable MapState mapState, int i, int j, float f) {
		if (mapState != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)i, (float)j, 1.0F);
			GlStateManager.scalef(f, f, 1.0F);
			this.client.gameRenderer.getMapRenderer().draw(mapState, true);
			GlStateManager.popMatrix();
		}
	}
}
