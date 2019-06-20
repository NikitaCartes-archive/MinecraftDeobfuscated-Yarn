package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_339;

@Environment(EnvType.CLIENT)
public abstract class AbstractRealmsButton<P extends class_339 & RealmsAbstractButtonProxy<?>> {
	public abstract P getProxy();

	public boolean active() {
		return this.getProxy().active();
	}

	public void active(boolean bl) {
		this.getProxy().active(bl);
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
		this.getProxy().blit(i, j, k, l, m, n);
	}

	public void tick() {
	}
}
