package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ToggleButtonWidget extends AbstractButtonWidget {
	protected Identifier texture;
	protected boolean toggled;
	protected int u;
	protected int v;
	protected int pressedUOffset;
	protected int hoverVOffset;

	public ToggleButtonWidget(int i, int j, int k, int l, boolean bl) {
		super(i, j, k, l, "");
		this.toggled = bl;
	}

	public void setTextureUV(int i, int j, int k, int l, Identifier identifier) {
		this.u = i;
		this.v = j;
		this.pressedUOffset = k;
		this.hoverVOffset = l;
		this.texture = identifier;
	}

	public void setToggled(boolean bl) {
		this.toggled = bl;
	}

	public boolean isToggled() {
		return this.toggled;
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
		int k = this.u;
		int l = this.v;
		if (this.toggled) {
			k += this.pressedUOffset;
		}

		if (this.isHovered()) {
			l += this.hoverVOffset;
		}

		this.drawTexturedRect(this.x, this.y, k, l, this.width, this.height);
		GlStateManager.enableDepthTest();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.active && this.visible) {
			if (i == 0) {
				boolean bl = this.clicked(d, e);
				if (bl) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}
}
