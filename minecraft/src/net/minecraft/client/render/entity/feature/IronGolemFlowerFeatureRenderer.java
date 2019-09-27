package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(EnvType.CLIENT)
public class IronGolemFlowerFeatureRenderer extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	public IronGolemFlowerFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4188(class_4587 arg, class_4597 arg2, int i, IronGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l, float m) {
		if (ironGolemEntity.method_6502() != 0) {
			arg.method_22903();
			arg.method_22905(-1.0F, -1.0F, 1.0F);
			arg.method_22907(Vector3f.field_20703.method_23214(5.0F + 180.0F * this.getModel().method_2809().pitch / (float) Math.PI, true));
			arg.method_22907(Vector3f.field_20703.method_23214(90.0F, true));
			arg.method_22904(0.6875, -0.3125, 1.0625);
			float n = 0.5F;
			arg.method_22905(0.5F, 0.5F, 0.5F);
			arg.method_22907(Vector3f.field_20703.method_23214(180.0F, true));
			arg.method_22904(-0.5, -0.5, 0.5);
			MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.POPPY.getDefaultState(), arg, arg2, i, 0, 10);
			arg.method_22909();
		}
	}
}
