package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class class_344 extends ButtonWidget {
	private final Identifier field_2127;
	private final int u;
	private final int v;
	private final int hoverV;

	public class_344(int i, int j, int k, int l, int m, int n, int o, int p, Identifier identifier) {
		super(i, j, k, l, m, "");
		this.u = n;
		this.v = o;
		this.hoverV = p;
		this.field_2127 = identifier;
	}

	public void getPos(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.visible) {
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.getTextureManager().bindTexture(this.field_2127);
			GlStateManager.disableDepthTest();
			int k = this.v;
			if (this.hovered) {
				k += this.hoverV;
			}

			this.drawTexturedRect(this.x, this.y, this.u, k, this.width, this.height);
			GlStateManager.enableDepthTest();
		}
	}
}
