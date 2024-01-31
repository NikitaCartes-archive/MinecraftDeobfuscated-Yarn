package net.minecraft.block.vault;

import net.minecraft.util.math.MathHelper;

public class VaultClientData {
	public static final float DISPLAY_ROTATION_SPEED = 10.0F;
	private float displayRotation;
	private float prevDisplayRotation;

	public float getDisplayRotation() {
		return this.displayRotation;
	}

	public float getPreviousDisplayRotation() {
		return this.prevDisplayRotation;
	}

	public void rotateDisplay() {
		this.prevDisplayRotation = this.displayRotation;
		this.displayRotation = MathHelper.wrapDegrees(this.displayRotation + 10.0F);
	}
}
