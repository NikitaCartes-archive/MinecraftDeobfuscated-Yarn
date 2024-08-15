package net.minecraft.client.gui.screen.ingame;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
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
	private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/cartography_table/error");
	private static final Identifier SCALED_MAP_TEXTURE = Identifier.ofVanilla("container/cartography_table/scaled_map");
	private static final Identifier DUPLICATED_MAP_TEXTURE = Identifier.ofVanilla("container/cartography_table/duplicated_map");
	private static final Identifier MAP_TEXTURE = Identifier.ofVanilla("container/cartography_table/map");
	private static final Identifier LOCKED_TEXTURE = Identifier.ofVanilla("container/cartography_table/locked");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/cartography_table.png");
	private final MapRenderState mapRenderState = new MapRenderState();

	public CartographyTableScreen(CartographyTableScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.titleY -= 2;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
		ItemStack itemStack = this.handler.getSlot(1).getStack();
		boolean bl = itemStack.isOf(Items.MAP);
		boolean bl2 = itemStack.isOf(Items.PAPER);
		boolean bl3 = itemStack.isOf(Items.GLASS_PANE);
		ItemStack itemStack2 = this.handler.getSlot(0).getStack();
		MapIdComponent mapIdComponent = itemStack2.get(DataComponentTypes.MAP_ID);
		boolean bl4 = false;
		MapState mapState;
		if (mapIdComponent != null) {
			mapState = FilledMapItem.getMapState(mapIdComponent, this.client.world);
			if (mapState != null) {
				if (mapState.locked) {
					bl4 = true;
					if (bl2 || bl3) {
						context.drawGuiTexture(RenderLayer::getGuiTextured, ERROR_TEXTURE, i + 35, j + 31, 28, 21);
					}
				}

				if (bl2 && mapState.scale >= 4) {
					bl4 = true;
					context.drawGuiTexture(RenderLayer::getGuiTextured, ERROR_TEXTURE, i + 35, j + 31, 28, 21);
				}
			}
		} else {
			mapState = null;
		}

		this.drawMap(context, mapIdComponent, mapState, bl, bl2, bl3, bl4);
	}

	private void drawMap(
		DrawContext context,
		@Nullable MapIdComponent mapId,
		@Nullable MapState mapState,
		boolean cloneMode,
		boolean expandMode,
		boolean lockMode,
		boolean cannotExpand
	) {
		int i = this.x;
		int j = this.y;
		if (expandMode && !cannotExpand) {
			context.drawGuiTexture(RenderLayer::getGuiTextured, SCALED_MAP_TEXTURE, i + 67, j + 13, 66, 66);
			this.drawMap(context, mapId, mapState, i + 85, j + 31, 0.226F);
		} else if (cloneMode) {
			context.drawGuiTexture(RenderLayer::getGuiTextured, DUPLICATED_MAP_TEXTURE, i + 67 + 16, j + 13, 50, 66);
			this.drawMap(context, mapId, mapState, i + 86, j + 16, 0.34F);
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, 1.0F);
			context.drawGuiTexture(RenderLayer::getGuiTextured, DUPLICATED_MAP_TEXTURE, i + 67, j + 13 + 16, 50, 66);
			this.drawMap(context, mapId, mapState, i + 70, j + 32, 0.34F);
			context.getMatrices().pop();
		} else if (lockMode) {
			context.drawGuiTexture(RenderLayer::getGuiTextured, MAP_TEXTURE, i + 67, j + 13, 66, 66);
			this.drawMap(context, mapId, mapState, i + 71, j + 17, 0.45F);
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, 1.0F);
			context.drawGuiTexture(RenderLayer::getGuiTextured, LOCKED_TEXTURE, i + 118, j + 60, 10, 14);
			context.getMatrices().pop();
		} else {
			context.drawGuiTexture(RenderLayer::getGuiTextured, MAP_TEXTURE, i + 67, j + 13, 66, 66);
			this.drawMap(context, mapId, mapState, i + 71, j + 17, 0.45F);
		}
	}

	private void drawMap(DrawContext context, @Nullable MapIdComponent mapId, @Nullable MapState mapState, int x, int y, float scale) {
		if (mapId != null && mapState != null) {
			context.getMatrices().push();
			context.getMatrices().translate((float)x, (float)y, 1.0F);
			context.getMatrices().scale(scale, scale, 1.0F);
			MapRenderer mapRenderer = this.client.getMapRenderer();
			mapRenderer.update(mapId, mapState, this.mapRenderState);
			mapRenderer.draw(this.mapRenderState, context.getMatrices(), context.getVertexConsumers(), true, 15728880);
			context.getMatrices().pop();
		}
	}
}
