package net.minecraft.entity.decoration.painting;

public class PaintingVariant {
	private final int width;
	private final int height;

	public PaintingVariant(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
