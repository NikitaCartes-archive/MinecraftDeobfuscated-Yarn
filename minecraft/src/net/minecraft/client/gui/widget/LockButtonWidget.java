package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class LockButtonWidget extends ButtonWidget {
	private boolean locked;

	public LockButtonWidget(int i, int j, ButtonWidget.PressAction pressAction) {
		super(i, j, 20, 20, I18n.translate("narrator.button.difficulty_lock"), pressAction);
	}

	@Override
	protected String getNarrationMessage() {
		return super.getNarrationMessage()
			+ ". "
			+ (this.isLocked() ? I18n.translate("narrator.button.difficulty_lock.locked") : I18n.translate("narrator.button.difficulty_lock.unlocked"));
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean bl) {
		this.locked = bl;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		MinecraftClient.getInstance().method_1531().bindTexture(ButtonWidget.WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		LockButtonWidget.IconLocation iconLocation;
		if (!this.active) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.field_2139 : LockButtonWidget.IconLocation.field_2140;
		} else if (this.isHovered()) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.field_2138 : LockButtonWidget.IconLocation.field_2133;
		} else {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.field_2137 : LockButtonWidget.IconLocation.field_2132;
		}

		this.blit(this.x, this.y, iconLocation.getU(), iconLocation.getV(), this.width, this.height);
	}

	@Environment(EnvType.CLIENT)
	static enum IconLocation {
		field_2137(0, 146),
		field_2138(0, 166),
		field_2139(0, 186),
		field_2132(20, 146),
		field_2133(20, 166),
		field_2140(20, 186);

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
