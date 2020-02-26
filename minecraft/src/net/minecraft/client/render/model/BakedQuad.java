package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BakedQuad {
	protected final int[] vertexData;
	protected final int colorIndex;
	protected final Direction face;
	protected final Sprite sprite;
	private final boolean field_22441;

	public BakedQuad(int[] vertexData, int colorIndex, Direction face, Sprite sprite, boolean bl) {
		this.vertexData = vertexData;
		this.colorIndex = colorIndex;
		this.face = face;
		this.sprite = sprite;
		this.field_22441 = bl;
	}

	public int[] getVertexData() {
		return this.vertexData;
	}

	public boolean hasColor() {
		return this.colorIndex != -1;
	}

	public int getColorIndex() {
		return this.colorIndex;
	}

	public Direction getFace() {
		return this.face;
	}

	public boolean method_24874() {
		return this.field_22441;
	}
}
