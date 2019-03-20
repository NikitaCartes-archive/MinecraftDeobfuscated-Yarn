package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Environment(EnvType.CLIENT)
public abstract class RealmsAbstractButton<P extends AbstractButtonWidget & net.minecraft.client.gui.menu.RealmsButton<?>> {
	public abstract P getProxy();

	public boolean active() {
		return this.getProxy().isEnabled();
	}

	public void active(boolean bl) {
		this.getProxy().setEnabled(bl);
	}

	public boolean isVisible() {
		return this.getProxy().isVisible();
	}

	public void setVisible(boolean bl) {
		this.getProxy().setVisible(bl);
	}

	public void render(int i, int j, float f) {
		this.getProxy().render(i, j, f);
	}

	public void blit(int i, int j, int k, int l, int m, int n) {
		this.getProxy().drawTexturedRect(i, j, k, l, m, n);
	}
}
