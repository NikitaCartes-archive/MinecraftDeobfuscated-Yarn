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
	private static final float field_53834 = 2.25F;
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
		float g = polarBearEntityRenderState.ageScale;
		float h = polarBearEntityRenderState.baby ? 0.44444445F : 1.0F;
		this.body.pitch -= f * (float) Math.PI * 0.35F;
		this.body.pivotY += f * g * 2.0F;
		this.rightFrontLeg.pivotY -= f * g * 20.0F;
		this.rightFrontLeg.pivotZ += f * g * 4.0F;
		this.rightFrontLeg.pitch -= f * (float) Math.PI * 0.45F;
		this.leftFrontLeg.pivotY = this.rightFrontLeg.pivotY;
		this.leftFrontLeg.pivotZ = this.rightFrontLeg.pivotZ;
		this.leftFrontLeg.pitch -= f * (float) Math.PI * 0.45F;
		this.head.pivotY -= f * h * 24.0F;
		this.head.pivotZ += f * h * 13.0F;
		this.head.pitch += f * (float) Math.PI * 0.15F;
	}
}
