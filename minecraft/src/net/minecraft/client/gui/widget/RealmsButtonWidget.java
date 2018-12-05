package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsButton;

@Environment(EnvType.CLIENT)
public class RealmsButtonWidget extends ButtonWidget {
	private final RealmsButton realmsButton;

	public RealmsButtonWidget(RealmsButton realmsButton, int i, int j, int k, String string) {
		super(i, j, k, string);
		this.realmsButton = realmsButton;
	}

	public RealmsButtonWidget(RealmsButton realmsButton, int i, int j, int k, String string, int l, int m) {
		super(i, j, k, l, m, string);
		this.realmsButton = realmsButton;
	}

	public int getId() {
		return this.id;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean bl) {
		this.enabled = bl;
	}

	public void setText(String string) {
		super.text = string;
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	public int getY() {
		return this.y;
	}

	@Override
	public void onPressed(double d, double e) {
		this.realmsButton.onClick(d, e);
	}

	@Override
	public void onReleased(double d, double e) {
		this.realmsButton.onRelease(d, e);
	}

	@Override
	public void drawBackground(MinecraftClient minecraftClient, int i, int j) {
		this.realmsButton.renderBg(i, j);
	}

	public RealmsButton getRealmsButton() {
		return this.realmsButton;
	}

	@Override
	public int getTextureId(boolean bl) {
		return this.realmsButton.getYImage(bl);
	}

	public int getTexId(boolean bl) {
		return super.getTextureId(bl);
	}

	public int getHeight() {
		return this.height;
	}
}
