package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigSaddleFeatureRenderer extends FeatureRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/pig/pig_saddle.png");
	private final PigEntityModel<PigEntity> model = new PigEntityModel<>(0.5F);

	public PigSaddleFeatureRenderer(FeatureRendererContext<PigEntity, PigEntityModel<PigEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4196(class_4587 arg, class_4597 arg2, int i, PigEntity pigEntity, float f, float g, float h, float j, float k, float l, float m) {
		if (pigEntity.isSaddled()) {
			this.getModel().copyStateTo(this.model);
			this.model.animateModel(pigEntity, f, g, h);
			this.model.setAngles(pigEntity, f, g, j, k, l, m);
			method_23197(this.model, SKIN, arg, arg2, i, 1.0F, 1.0F, 1.0F);
		}
	}
}
