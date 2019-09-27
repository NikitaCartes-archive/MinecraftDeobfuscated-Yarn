package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(EnvType.CLIENT)
public class EndermanBlockFeatureRenderer extends FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
	public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntity, EndermanEntityModel<EndermanEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4179(class_4587 arg, class_4597 arg2, int i, EndermanEntity endermanEntity, float f, float g, float h, float j, float k, float l, float m) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		if (blockState != null) {
			arg.method_22903();
			arg.method_22904(0.0, 0.6875, -0.75);
			arg.method_22907(Vector3f.field_20703.method_23214(20.0F, true));
			arg.method_22907(Vector3f.field_20705.method_23214(45.0F, true));
			arg.method_22904(0.25, 0.1875, 0.25);
			float n = 0.5F;
			arg.method_22905(-0.5F, -0.5F, 0.5F);
			MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, arg, arg2, i, 0, 10);
			arg.method_22909();
		}
	}
}
