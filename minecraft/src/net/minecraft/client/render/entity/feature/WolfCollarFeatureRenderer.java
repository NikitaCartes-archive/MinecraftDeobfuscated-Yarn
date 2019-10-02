package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class WolfCollarFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/wolf/wolf_collar.png");

	public WolfCollarFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4209(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		WolfEntity wolfEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if (wolfEntity.isTamed() && !wolfEntity.isInvisible()) {
			float[] fs = wolfEntity.getCollarColor().getColorComponents();
			method_23199(this.getModel(), SKIN, matrixStack, layeredVertexConsumerStorage, i, wolfEntity, fs[0], fs[1], fs[2]);
		}
	}
}
