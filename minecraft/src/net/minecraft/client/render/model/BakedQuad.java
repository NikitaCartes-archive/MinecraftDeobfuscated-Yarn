package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BakedQuad {
	protected final int[] vertexData;
	protected final int colorIndex;
	protected final Direction field_4173;
	protected final Sprite field_4176;

	public BakedQuad(int[] is, int i, Direction direction, Sprite sprite) {
		this.vertexData = is;
		this.colorIndex = i;
		this.field_4173 = direction;
		this.field_4176 = sprite;
	}

	public Sprite method_3356() {
		return this.field_4176;
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

	public Direction method_3358() {
		return this.field_4173;
	}
}
