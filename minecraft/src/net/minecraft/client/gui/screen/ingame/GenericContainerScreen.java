package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GenericContainerScreen extends HandledScreen<GenericContainerScreenHandler> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
	private final int rows;

	public GenericContainerScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		int i = 222;
		int j = 114;
		this.rows = handler.getRows();
		this.backgroundHeight = 114 + this.rows * 18;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.rows * 18 + 17, 256, 256);
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j + this.rows * 18 + 17, 0.0F, 126.0F, this.backgroundWidth, 96, 256, 256);
	}
}
