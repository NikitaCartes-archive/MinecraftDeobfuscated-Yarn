package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CartographyTableScreen extends HandledScreen<CartographyTableScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/cartography_table.png");

	public CartographyTableScreen(CartographyTableScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.titleY -= 2;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		this.renderBackground(matrices);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = this.x;
		int j = this.y;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		ItemStack itemStack = this.handler.getSlot(1).getStack();
		boolean bl = itemStack.isOf(Items.MAP);
		boolean bl2 = itemStack.isOf(Items.PAPER);
		boolean bl3 = itemStack.isOf(Items.GLASS_PANE);
		ItemStack itemStack2 = this.handler.getSlot(0).getStack();
		boolean bl4 = false;
		MapState mapState;
		if (itemStack2.isOf(Items.FILLED_MAP)) {
			mapState = FilledMapItem.getMapState(itemStack2, this.client.world);
			if (mapState != null) {
				if (mapState.locked) {
					bl4 = true;
					if (bl2 || bl3) {
						this.drawTexture(matrices, i + 35, j + 31, this.backgroundWidth + 50, 132, 28, 21);
					}
				}

				if (bl2 && mapState.scale >= 4) {
					bl4 = true;
					this.drawTexture(matrices, i + 35, j + 31, this.backgroundWidth + 50, 132, 28, 21);
				}
			}
		} else {
			mapState = null;
		}

		this.drawMap(matrices, mapState, bl, bl2, bl3, bl4);
	}

	private void drawMap(MatrixStack matrixStack, @Nullable MapState mapState, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		int i = this.x;
		int j = this.y;
		if (bl2 && !bl4) {
			this.drawTexture(matrixStack, i + 67, j + 13, this.backgroundWidth, 66, 66, 66);
			this.drawMap(mapState, i + 85, j + 31, 0.226F);
		} else if (bl) {
			this.drawTexture(matrixStack, i + 67 + 16, j + 13, this.backgroundWidth, 132, 50, 66);
			this.drawMap(mapState, i + 86, j + 16, 0.34F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 0.0F, 1.0F);
			this.drawTexture(matrixStack, i + 67, j + 13 + 16, this.backgroundWidth, 132, 50, 66);
			this.drawMap(mapState, i + 70, j + 32, 0.34F);
			RenderSystem.popMatrix();
		} else if (bl3) {
			this.drawTexture(matrixStack, i + 67, j + 13, this.backgroundWidth, 0, 66, 66);
			this.drawMap(mapState, i + 71, j + 17, 0.45F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 0.0F, 1.0F);
			this.drawTexture(matrixStack, i + 66, j + 12, 0, this.backgroundHeight, 66, 66);
			RenderSystem.popMatrix();
		} else {
			this.drawTexture(matrixStack, i + 67, j + 13, this.backgroundWidth, 0, 66, 66);
			this.drawMap(mapState, i + 71, j + 17, 0.45F);
		}
	}

	private void drawMap(@Nullable MapState state, int x, int y, float size) {
		if (state != null) {
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)x, (float)y, 1.0F);
			RenderSystem.scalef(size, size, 1.0F);
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			this.client.gameRenderer.getMapRenderer().draw(new MatrixStack(), immediate, state, true, 15728880);
			immediate.draw();
			RenderSystem.popMatrix();
		}
	}
}
