package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.class_4187;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsButton;

@Environment(EnvType.CLIENT)
public class RealmsButtonWidget extends class_4185 implements class_4187<RealmsButton> {
	private final RealmsButton realmsButton;

	public RealmsButtonWidget(RealmsButton realmsButton, int i, int j, String string) {
		super(i, j, string);
		this.realmsButton = realmsButton;
	}

	public RealmsButtonWidget(RealmsButton realmsButton, int i, int j, String string, int k, int l) {
		super(i, j, k, l, string);
		this.realmsButton = realmsButton;
	}

	@Override
	public boolean method_2067() {
		return this.enabled;
	}

	@Override
	public void method_2062(boolean bl) {
		this.enabled = bl;
	}

	@Override
	public boolean method_19358() {
		return this.visible;
	}

	@Override
	public void method_19360(boolean bl) {
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
	public void method_1826() {
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

	public RealmsButton method_2064() {
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
