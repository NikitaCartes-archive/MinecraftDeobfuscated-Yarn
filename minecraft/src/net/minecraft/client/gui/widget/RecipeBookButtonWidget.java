package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class RecipeBookButtonWidget extends class_4185 {
	private final Identifier field_2127;
	private final int u;
	private final int v;
	private final int hoverVOffset;

	public RecipeBookButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier identifier) {
		super(i, j, k, l, "");
		this.u = m;
		this.v = n;
		this.hoverVOffset = o;
		this.field_2127 = identifier;
	}

	public void setPos(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void drawButton(int i, int j, float f) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.method_1531().method_4618(this.field_2127);
		GlStateManager.disableDepthTest();
		int k = this.v;
		if (this.isHovered()) {
			k += this.hoverVOffset;
		}

		this.drawTexturedRect(this.x, this.y, this.u, k, this.width, this.height);
		GlStateManager.enableDepthTest();
	}
}
