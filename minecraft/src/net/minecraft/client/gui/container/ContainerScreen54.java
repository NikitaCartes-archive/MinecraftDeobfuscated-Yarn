package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ContainerScreen54 extends ContainerScreen<GenericContainer> implements ContainerProvider<GenericContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private final int rows;

	public ContainerScreen54(GenericContainer genericContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(genericContainer, playerInventory, textComponent);
		this.passEvents = false;
		int i = 222;
		int j = 114;
		this.rows = genericContainer.getRows();
		this.height = 114 + this.rows * 18;
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.screenWidth - this.width) / 2;
		int l = (this.screenHeight - this.height) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.rows * 18 + 17);
		this.drawTexturedRect(k, l + this.rows * 18 + 17, 0, 126, this.width, 96);
	}
}
