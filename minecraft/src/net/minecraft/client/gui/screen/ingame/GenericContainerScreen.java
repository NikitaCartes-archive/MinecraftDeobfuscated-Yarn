package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GenericContainerScreen extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private final int rows;

	public GenericContainerScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.passEvents = false;
		int i = 222;
		int j = 114;
		this.rows = handler.getRows();
		this.backgroundHeight = 114 + this.rows * 18;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(this.title.asFormattedString(), 8.0F, 6.0F, 4210752);
		this.textRenderer.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
		this.drawTexture(i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
	}
}
