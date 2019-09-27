package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Deadmau5FeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public Deadmau5FeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4181(
		class_4587 arg, class_4597 arg2, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, float m
	) {
		if ("deadmau5".equals(abstractClientPlayerEntity.getName().getString())
			&& abstractClientPlayerEntity.hasSkinTexture()
			&& !abstractClientPlayerEntity.isInvisible()) {
			class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(abstractClientPlayerEntity.getSkinTexture()));
			LivingEntityRenderer.method_23184(abstractClientPlayerEntity, lv, 0.0F);

			for (int n = 0; n < 2; n++) {
				float o = MathHelper.lerp(h, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw)
					- MathHelper.lerp(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
				float p = MathHelper.lerp(h, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
				arg.method_22903();
				arg.method_22907(Vector3f.field_20705.method_23214(o, true));
				arg.method_22907(Vector3f.field_20703.method_23214(p, true));
				arg.method_22904((double)(0.375F * (float)(n * 2 - 1)), 0.0, 0.0);
				arg.method_22904(0.0, -0.375, 0.0);
				arg.method_22907(Vector3f.field_20703.method_23214(-p, true));
				arg.method_22907(Vector3f.field_20705.method_23214(-o, true));
				float q = 1.3333334F;
				arg.method_22905(1.3333334F, 1.3333334F, 1.3333334F);
				this.getModel().renderEars(arg, lv, 0.0625F, i);
				arg.method_22909();
			}

			lv.method_22923();
		}
	}
}
