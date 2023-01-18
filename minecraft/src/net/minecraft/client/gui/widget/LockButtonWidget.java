package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class LockButtonWidget extends ButtonWidget {
	private boolean locked;

	public LockButtonWidget(int x, int y, ButtonWidget.PressAction action) {
		super(x, y, 20, 20, Text.translatable("narrator.button.difficulty_lock"), action, DEFAULT_NARRATION_SUPPLIER);
	}

	@Override
	protected MutableText getNarrationMessage() {
		return ScreenTexts.joinSentences(
			super.getNarrationMessage(),
			this.isLocked() ? Text.translatable("narrator.button.difficulty_lock.locked") : Text.translatable("narrator.button.difficulty_lock.unlocked")
		);
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, ButtonWidget.WIDGETS_TEXTURE);
		LockButtonWidget.IconLocation iconLocation;
		if (!this.active) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.LOCKED_DISABLED : LockButtonWidget.IconLocation.UNLOCKED_DISABLED;
		} else if (this.isHovered()) {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.LOCKED_HOVER : LockButtonWidget.IconLocation.UNLOCKED_HOVER;
		} else {
			iconLocation = this.locked ? LockButtonWidget.IconLocation.LOCKED : LockButtonWidget.IconLocation.UNLOCKED;
		}

		this.drawTexture(matrices, this.getX(), this.getY(), iconLocation.getU(), iconLocation.getV(), this.width, this.height);
	}

	@Environment(EnvType.CLIENT)
	static enum IconLocation {
		LOCKED(0, 146),
		LOCKED_HOVER(0, 166),
		LOCKED_DISABLED(0, 186),
		UNLOCKED(20, 146),
		UNLOCKED_HOVER(20, 166),
		UNLOCKED_DISABLED(20, 186);

		private final int u;
		private final int v;

		private IconLocation(int u, int v) {
			this.u = u;
			this.v = v;
		}

		public int getU() {
			return this.u;
		}

		public int getV() {
			return this.v;
		}
	}
}
