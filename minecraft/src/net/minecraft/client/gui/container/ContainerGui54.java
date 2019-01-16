package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ContainerGui54 extends ContainerGui<GenericContainer> implements ContainerProvider<GenericContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private final int rows;

	public ContainerGui54(GenericContainer genericContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(genericContainer, playerInventory, textComponent);
		this.field_2558 = false;
		int i = 222;
		int j = 114;
		this.rows = genericContainer.method_17388();
		this.containerHeight = 114 + this.rows * 18;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.rows * 18 + 17);
		this.drawTexturedRect(k, l + this.rows * 18 + 17, 0, 126, this.containerWidth, 96);
	}
}
