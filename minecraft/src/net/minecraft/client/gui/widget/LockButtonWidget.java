package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class LockButtonWidget extends ButtonWidget {
	private boolean locked;

	public LockButtonWidget(int x, int y, ButtonWidget.PressAction action) {
		super(x, y, 20, 20, new TranslatableText("narrator.button.difficulty_lock"), action);
	}

	@Override
	protected MutableText getNarrationMessage() {
		return super.getNarrationMessage()
			.append(". ")
			.append(this.isLocked() ? new TranslatableText("narrator.button.difficulty_lock.locked") : new TranslatableText("narrator.button.difficulty_lock.unlocked"));
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		LockButtonWidget.IconLocation iconLocation;
		if (!this.active) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.field_2139 : LockButtonWidget.IconLocation.field_2140;
		} else if (this.isHovered()) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.field_2138 : LockButtonWidget.IconLocation.field_2133;
		} else {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.field_2137 : LockButtonWidget.IconLocation.field_2132;
		}

		this.drawTexture(matrices, this.x, this.y, iconLocation.getU(), iconLocation.getV(), this.width, this.height);
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
