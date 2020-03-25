package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_4999 extends MobEntityRenderer<class_4985, class_4997<class_4985>> {
	private static final Identifier field_23372 = new Identifier("textures/entity/strider/strider.png");

	public class_4999(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4997<>(), 0.5F);
		this.addFeature(new PigSaddleFeatureRenderer<>(this, new class_4997<>(), new Identifier("textures/entity/strider/strider_saddle.png")));
	}

	public Identifier getTexture(class_4985 arg) {
		return field_23372;
	}

	protected void scale(class_4985 arg, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		if (arg.isBaby()) {
			g *= 0.5F;
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}

		matrixStack.scale(g, g, g);
	}

	protected boolean isShaking(class_4985 arg) {
		return arg.method_26348();
	}
}
