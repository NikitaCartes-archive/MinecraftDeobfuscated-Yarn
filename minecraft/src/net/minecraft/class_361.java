package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_361 extends ButtonWidget {
	protected Identifier field_2193;
	protected boolean field_2194;
	protected int field_2192;
	protected int field_2191;
	protected int field_2190;
	protected int field_2189;

	public class_361(int i, int j, int k, int l, int m, boolean bl) {
		super(i, j, k, l, m, "");
		this.field_2194 = bl;
	}

	public void method_1962(int i, int j, int k, int l, Identifier identifier) {
		this.field_2192 = i;
		this.field_2191 = j;
		this.field_2190 = k;
		this.field_2189 = l;
		this.field_2193 = identifier;
	}

	public void method_1964(boolean bl) {
		this.field_2194 = bl;
	}

	public boolean method_1965() {
		return this.field_2194;
	}

	public void setPos(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.visible) {
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.getTextureManager().bindTexture(this.field_2193);
			GlStateManager.disableDepthTest();
			int k = this.field_2192;
			int l = this.field_2191;
			if (this.field_2194) {
				k += this.field_2190;
			}

			if (this.hovered) {
				l += this.field_2189;
			}

			this.drawTexturedRect(this.x, this.y, k, l, this.width, this.height);
			GlStateManager.enableDepthTest();
		}
	}
}
