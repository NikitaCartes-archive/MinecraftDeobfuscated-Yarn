package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.RealmsButton;

@Environment(EnvType.CLIENT)
public class RealmsButtonWidget extends ButtonWidget implements RealmsButton<net.minecraft.realms.RealmsButton> {
	private final net.minecraft.realms.RealmsButton realmsButton;

	public RealmsButtonWidget(net.minecraft.realms.RealmsButton realmsButton, int i, int j, String string) {
		super(i, j, string);
		this.realmsButton = realmsButton;
	}

	public RealmsButtonWidget(net.minecraft.realms.RealmsButton realmsButton, int i, int j, String string, int k, int l) {
		super(i, j, k, l, string);
		this.realmsButton = realmsButton;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean bl) {
		this.enabled = bl;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(boolean bl) {
		this.visible = bl;
	}

	@Override
	public void setText(String string) {
		super.setText(string);
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	public int getHeight() {
		return this.y;
	}

	@Override
	public void onPressed() {
		this.realmsButton.onPress();
	}

	@Override
	public void onReleased(double d, double e) {
		this.realmsButton.onRelease(d, e);
	}

	@Override
	public void drawBackground(MinecraftClient minecraftClient, int i, int j) {
		this.realmsButton.renderBg(i, j);
	}

	public net.minecraft.realms.RealmsButton method_2064() {
		return this.realmsButton;
	}

	@Override
	public int getTextureId(boolean bl) {
		return this.realmsButton.getYImage(bl);
	}

	public int getTexId(boolean bl) {
		return super.getTextureId(bl);
	}

	public int getY() {
		return this.height;
	}
}
