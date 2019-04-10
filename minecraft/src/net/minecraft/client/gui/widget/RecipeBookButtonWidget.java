package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RecipeBookButtonWidget extends ButtonWidget {
	private final Identifier texture;
	private final int u;
	private final int v;
	private final int field_19079;
	private final int hoverVOffset;
	private final int field_19080;

	public RecipeBookButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier identifier, ButtonWidget.PressAction pressAction) {
		this(i, j, k, l, m, n, o, identifier, 256, 256, pressAction);
	}

	public RecipeBookButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier identifier, int p, int q, ButtonWidget.PressAction pressAction) {
		this(i, j, k, l, m, n, o, identifier, p, q, pressAction, "");
	}

	public RecipeBookButtonWidget(
		int i, int j, int k, int l, int m, int n, int o, Identifier identifier, int p, int q, ButtonWidget.PressAction pressAction, String string
	) {
		super(i, j, k, l, string, pressAction);
		this.hoverVOffset = p;
		this.field_19080 = q;
		this.u = m;
		this.v = n;
		this.field_19079 = o;
		this.texture = identifier;
	}

	public void setPos(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(this.texture);
		GlStateManager.disableDepthTest();
		int k = this.v;
		if (this.isHovered()) {
			k += this.field_19079;
		}

		blit(this.x, this.y, (float)this.u, (float)k, this.width, this.height, this.hoverVOffset, this.field_19080);
		GlStateManager.enableDepthTest();
	}
}
