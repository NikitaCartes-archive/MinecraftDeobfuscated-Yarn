package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;

@Environment(EnvType.CLIENT)
public abstract class class_4186<P extends ButtonWidget & class_4187<?>> {
	public abstract P getProxy();

	public boolean active() {
		return this.getProxy().method_2067();
	}

	public void active(boolean bl) {
		this.getProxy().method_2062(bl);
	}

	public boolean isVisible() {
		return this.getProxy().method_19358();
	}

	public void setVisible(boolean bl) {
		this.getProxy().method_19360(bl);
	}

	public void render(int i, int j, float f) {
		this.getProxy().draw(i, j, f);
	}

	public void blit(int i, int j, int k, int l, int m, int n) {
		this.getProxy().drawTexturedRect(i, j, k, l, m, n);
	}
}
