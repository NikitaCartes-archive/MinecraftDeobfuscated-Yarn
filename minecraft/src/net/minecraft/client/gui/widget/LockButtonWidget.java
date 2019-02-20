package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class LockButtonWidget extends ButtonWidget {
	private boolean locked;

	public LockButtonWidget(int i, int j) {
		super(i, j, 20, 20, "");
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean bl) {
		this.locked = bl;
	}

	@Override
	public void draw(int i, int j, float f) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGET_TEX);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		LockButtonWidget.IconLocation iconLocation;
		if (!this.enabled) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.LOCKED_DISABLED : LockButtonWidget.IconLocation.UNLOCKED_DISABLED;
		} else if (this.isHovered()) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.LOCKED_HOVERED : LockButtonWidget.IconLocation.UNLOCKED_HOVERED;
		} else {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.LOCKED : LockButtonWidget.IconLocation.UNLOCKED;
		}

		this.drawTexturedRect(this.x, this.y, iconLocation.getU(), iconLocation.getV(), this.width, this.height);
	}

	@Environment(EnvType.CLIENT)
	static enum IconLocation {
		LOCKED(0, 146),
		LOCKED_HOVERED(0, 166),
		LOCKED_DISABLED(0, 186),
		UNLOCKED(20, 146),
		UNLOCKED_HOVERED(20, 166),
		UNLOCKED_DISABLED(20, 186);

		private final int u;
		private final int v;

		private IconLocation(int j, int k) {
			this.u = j;
			this.v = k;
		}

		public int getU() {
			return this.u;
		}

		public int getV() {
			return this.v;
		}
	}
}
