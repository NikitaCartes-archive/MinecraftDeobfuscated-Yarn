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

	public BakedQuad(int[] vertexData, int colorIndex, Direction face, Sprite sprite) {
		this.vertexData = vertexData;
		this.colorIndex = colorIndex;
		this.face = face;
		this.sprite = sprite;
	}

	public Sprite getSprite() {
		return this.sprite;
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
}
