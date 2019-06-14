package net.minecraft.client.render.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public class RetexturedBakedQuad extends BakedQuad {
	private final Sprite field_4267;

	public RetexturedBakedQuad(BakedQuad bakedQuad, Sprite sprite) {
		super(
			Arrays.copyOf(bakedQuad.getVertexData(), bakedQuad.getVertexData().length),
			bakedQuad.colorIndex,
			BakedQuadFactory.method_3467(bakedQuad.getVertexData()),
			bakedQuad.method_3356()
		);
		this.field_4267 = sprite;
		this.recalculateUvs();
	}

	private void recalculateUvs() {
		for (int i = 0; i < 4; i++) {
			int j = 7 * i;
			this.vertexData[j + 4] = Float.floatToRawIntBits(this.field_4267.getU((double)this.field_4176.getXFromU(Float.intBitsToFloat(this.vertexData[j + 4]))));
			this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(
				this.field_4267.getV((double)this.field_4176.getYFromV(Float.intBitsToFloat(this.vertexData[j + 4 + 1])))
			);
		}
	}
}
