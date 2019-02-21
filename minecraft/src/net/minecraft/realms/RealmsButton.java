package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.RealmsButtonWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class RealmsButton {
	protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	private final int field_18237;
	private final RealmsButtonWidget proxy;

	public RealmsButton(int i, int j, int k, String string) {
		this.field_18237 = i;
		this.proxy = new RealmsButtonWidget(this, j, k, string) {
			@Override
			public void onPressed(double d, double e) {
				RealmsButton.this.onClick(d, e);
			}
		};
	}

	public RealmsButton(int i, int j, int k, int l, int m, String string) {
		this.field_18237 = i;
		this.proxy = new RealmsButtonWidget(this, j, k, string, l, m) {
			@Override
			public void onPressed(double d, double e) {
				RealmsButton.this.onClick(d, e);
			}
		};
	}

	public ButtonWidget getProxy() {
		return this.proxy;
	}

	public int method_10253() {
		return this.field_18237;
	}

	public boolean active() {
		return this.proxy.getEnabled();
	}

	public void active(boolean bl) {
		this.proxy.setEnabled(bl);
	}

	public void setMessage(String string) {
		this.proxy.setText(string);
	}

	public int getWidth() {
		return this.proxy.getWidth();
	}

	public int getHeight() {
		return this.proxy.getHeight();
	}

	public int method_10254() {
		return this.proxy.getY();
	}

	public void render(int i, int j, float f) {
		this.proxy.draw(i, j, f);
	}

	public void blit(int i, int j, int k, int l, int m, int n) {
		this.proxy.drawTexturedRect(i, j, k, l, m, n);
	}

	public void renderBg(int i, int j) {
	}

	public int getYImage(boolean bl) {
		return this.proxy.getTexId(bl);
	}

	public abstract void onClick(double d, double e);

	public void onRelease(double d, double e) {
	}
}
