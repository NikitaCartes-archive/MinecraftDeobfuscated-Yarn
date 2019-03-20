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
		return this.active;
	}

	@Override
	public void setEnabled(boolean bl) {
		this.active = bl;
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
	public void setMessage(String string) {
		super.setMessage(string);
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
	public void onRelease(double d, double e) {
		this.realmsButton.onRelease(d, e);
	}

	@Override
	public void renderBg(MinecraftClient minecraftClient, int i, int j) {
		this.realmsButton.renderBg(i, j);
	}

	public net.minecraft.realms.RealmsButton getButton() {
		return this.realmsButton;
	}

	@Override
	public int getYImage(boolean bl) {
		return this.realmsButton.getYImage(bl);
	}

	public int getTexId(boolean bl) {
		return super.getYImage(bl);
	}

	public int getY() {
		return this.height;
	}
}
