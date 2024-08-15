package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.PolarBearEntityRenderState;

@Environment(EnvType.CLIENT)
public class PolarBearEntityModel extends QuadrupedEntityModel<PolarBearEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.0F, 4.0F, 2.25F, 2.0F, 24.0F, Set.of("head"));

	public PolarBearEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F)
				.uv(0, 44)
				.cuboid(EntityModelPartNames.MOUTH, -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F)
				.uv(26, 0)
				.cuboid(EntityModelPartNames.RIGHT_EAR, -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F)
				.uv(26, 0)
				.mirrored()
				.cuboid(EntityModelPartNames.LEFT_EAR, 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F),
			ModelTransform.pivot(0.0F, 10.0F, -16.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 19).cuboid(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F).uv(39, 0).cuboid(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F),
			ModelTransform.of(-2.0F, 9.0F, 12.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		int i = 10;
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(50, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.5F, 14.0F, 6.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(4.5F, 14.0F, 6.0F));
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(50, 40).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(-3.5F, 14.0F, -8.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(3.5F, 14.0F, -8.0F));
		return TexturedModelData.of(modelData, 128, 64).transform(ModelTransformer.scaling(1.2F));
	}

	public void setAngles(PolarBearEntityRenderState polarBearEntityRenderState) {
		super.setAngles(polarBearEntityRenderState);
		float f = polarBearEntityRenderState.warningAnimationProgress * polarBearEntityRenderState.warningAnimationProgress;
		float g = 1.0F - f;
		this.body.pitch = (float) (Math.PI / 2) - f * (float) Math.PI * 0.35F;
		this.body.pivotY = 9.0F * g + 11.0F * f;
		this.rightFrontLeg.pivotY = 14.0F * g - 6.0F * f;
		this.rightFrontLeg.pivotZ = -8.0F * g - 4.0F * f;
		this.rightFrontLeg.pitch -= f * (float) Math.PI * 0.45F;
		this.leftFrontLeg.pivotY = this.rightFrontLeg.pivotY;
		this.leftFrontLeg.pivotZ = this.rightFrontLeg.pivotZ;
		this.leftFrontLeg.pitch -= f * (float) Math.PI * 0.45F;
		if (polarBearEntityRenderState.baby) {
			this.head.pivotY = 10.0F * g - 9.0F * f;
			this.head.pivotZ = -16.0F * g - 7.0F * f;
		} else {
			this.head.pivotY = 10.0F * g - 14.0F * f;
			this.head.pivotZ = -16.0F * g - 3.0F * f;
		}

		this.head.pitch += f * (float) Math.PI * 0.15F;
	}
}
