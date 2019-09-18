package net.minecraft.client.render.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public class RetexturedBakedQuad extends BakedQuad {
	private final Sprite spriteRetextured;

	public RetexturedBakedQuad(BakedQuad bakedQuad, Sprite sprite) {
		super(
			Arrays.copyOf(bakedQuad.getVertexData(), bakedQuad.getVertexData().length),
			bakedQuad.colorIndex,
			BakedQuadFactory.method_3467(bakedQuad.getVertexData()),
			bakedQuad.getSprite()
		);
		this.spriteRetextured = sprite;
		this.recalculateUvs();
	}

	private void recalculateUvs() {
		for (int i = 0; i < 4; i++) {
			int j = 8 * i;
			this.vertexData[j + 4] = Float.floatToRawIntBits(this.spriteRetextured.getU((double)this.sprite.getXFromU(Float.intBitsToFloat(this.vertexData[j + 4]))));
			this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(
				this.spriteRetextured.getV((double)this.sprite.getYFromV(Float.intBitsToFloat(this.vertexData[j + 4 + 1])))
			);
		}
	}
}
