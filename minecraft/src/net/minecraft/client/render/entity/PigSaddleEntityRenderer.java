package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigSaddleEntityRenderer implements LayerEntityRenderer<PigEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/pig/pig_saddle.png");
	private final PigEntityRenderer field_4886;
	private final PigEntityModel field_4887 = new PigEntityModel(0.5F);

	public PigSaddleEntityRenderer(PigEntityRenderer pigEntityRenderer) {
		this.field_4886 = pigEntityRenderer;
	}

	public void render(PigEntity pigEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (pigEntity.isSaddled()) {
			this.field_4886.bindTexture(SKIN);
			this.field_4887.setAttributes(this.field_4886.method_4038());
			this.field_4887.render(pigEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
