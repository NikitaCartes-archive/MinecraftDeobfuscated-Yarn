package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CartographyTableScreen extends AbstractContainerScreen<CartographyTableContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/cartography_table.png");

	public CartographyTableScreen(CartographyTableContainer atl2, PlayerInventory inventory, Text title) {
		super(atl2, inventory, title);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.font.draw(this.title.asFormattedString(), 8.0F, 4.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		this.renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = this.left;
		int j = this.top;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		Item item = this.container.getSlot(1).getStack().getItem();
		boolean bl = item == Items.MAP;
		boolean bl2 = item == Items.PAPER;
		boolean bl3 = item == Items.GLASS_PANE;
		ItemStack itemStack = this.container.getSlot(0).getStack();
		boolean bl4 = false;
		MapState mapState;
		if (itemStack.getItem() == Items.FILLED_MAP) {
			mapState = FilledMapItem.getMapState(itemStack, this.minecraft.world);
			if (mapState != null) {
				if (mapState.locked) {
					bl4 = true;
					if (bl2 || bl3) {
						this.blit(i + 35, j + 31, this.containerWidth + 50, 132, 28, 21);
					}
				}

				if (bl2 && mapState.scale >= 4) {
					bl4 = true;
					this.blit(i + 35, j + 31, this.containerWidth + 50, 132, 28, 21);
				}
			}
		} else {
			mapState = null;
		}

		this.drawMap(mapState, bl, bl2, bl3, bl4);
	}

	private void drawMap(@Nullable MapState mapState, boolean isMap, boolean isPaper, boolean isGlassPane, boolean bl) {
		int i = this.left;
		int j = this.top;
		if (isPaper && !bl) {
			this.blit(i + 67, j + 13, this.containerWidth, 66, 66, 66);
			this.drawMap(mapState, i + 85, j + 31, 0.226F);
		} else if (isMap) {
			this.blit(i + 67 + 16, j + 13, this.containerWidth, 132, 50, 66);
			this.drawMap(mapState, i + 86, j + 16, 0.34F);
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 0.0F, 1.0F);
			this.blit(i + 67, j + 13 + 16, this.containerWidth, 132, 50, 66);
			this.drawMap(mapState, i + 70, j + 32, 0.34F);
			RenderSystem.popMatrix();
		} else if (isGlassPane) {
			this.blit(i + 67, j + 13, this.containerWidth, 0, 66, 66);
			this.drawMap(mapState, i + 71, j + 17, 0.45F);
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 0.0F, 1.0F);
			this.blit(i + 66, j + 12, 0, this.containerHeight, 66, 66);
			RenderSystem.popMatrix();
		} else {
			this.blit(i + 67, j + 13, this.containerWidth, 0, 66, 66);
			this.drawMap(mapState, i + 71, j + 17, 0.45F);
		}
	}

	private void drawMap(@Nullable MapState state, int x, int y, float size) {
		if (state != null) {
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)x, (float)y, 1.0F);
			RenderSystem.scalef(size, size, 1.0F);
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			this.minecraft.gameRenderer.getMapRenderer().draw(new MatrixStack(), immediate, state, true, 15728880);
			immediate.draw();
			RenderSystem.popMatrix();
		}
	}
}
