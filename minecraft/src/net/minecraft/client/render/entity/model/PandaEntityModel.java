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
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaEntityModel extends QuadrupedEntityModel<PandaEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 23.0F, 4.8F, 2.7F, 3.0F, 49.0F, Set.of("head"));

	public PandaEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(0, 6)
				.cuboid(-6.5F, -5.0F, -4.0F, 13.0F, 10.0F, 9.0F)
				.uv(45, 16)
				.cuboid(EntityModelPartNames.NOSE, -3.5F, 0.0F, -6.0F, 7.0F, 5.0F, 2.0F)
				.uv(52, 25)
				.cuboid(EntityModelPartNames.LEFT_EAR, 3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F)
				.uv(52, 25)
				.cuboid(EntityModelPartNames.RIGHT_EAR, -8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F),
			ModelTransform.pivot(0.0F, 11.5F, -17.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 25).cuboid(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F),
			ModelTransform.of(0.0F, 10.0F, 0.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		int i = 9;
		int j = 6;
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(40, 0).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-5.5F, 15.0F, 9.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(5.5F, 15.0F, 9.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-5.5F, 15.0F, -9.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(5.5F, 15.0F, -9.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(PandaEntityRenderState pandaEntityRenderState) {
		super.setAngles(pandaEntityRenderState);
		if (pandaEntityRenderState.askingForBamboo) {
			this.head.yaw = 0.35F * MathHelper.sin(0.6F * pandaEntityRenderState.age);
			this.head.roll = 0.35F * MathHelper.sin(0.6F * pandaEntityRenderState.age);
			this.rightFrontLeg.pitch = -0.75F * MathHelper.sin(0.3F * pandaEntityRenderState.age);
			this.leftFrontLeg.pitch = 0.75F * MathHelper.sin(0.3F * pandaEntityRenderState.age);
		} else {
			this.head.roll = 0.0F;
		}

		if (pandaEntityRenderState.sneezing) {
			if (pandaEntityRenderState.sneezeProgress < 15) {
				this.head.pitch = (float) (-Math.PI / 4) * (float)pandaEntityRenderState.sneezeProgress / 14.0F;
			} else if (pandaEntityRenderState.sneezeProgress < 20) {
				float f = (float)((pandaEntityRenderState.sneezeProgress - 15) / 5);
				this.head.pitch = (float) (-Math.PI / 4) + (float) (Math.PI / 4) * f;
			}
		}

		if (pandaEntityRenderState.sittingAnimationProgress > 0.0F) {
			this.body.pitch = MathHelper.lerpAngleRadians(pandaEntityRenderState.sittingAnimationProgress, this.body.pitch, 1.7407963F);
			this.head.pitch = MathHelper.lerpAngleRadians(pandaEntityRenderState.sittingAnimationProgress, this.head.pitch, (float) (Math.PI / 2));
			this.rightFrontLeg.roll = -0.27079642F;
			this.leftFrontLeg.roll = 0.27079642F;
			this.rightHindLeg.roll = 0.5707964F;
			this.leftHindLeg.roll = -0.5707964F;
			if (pandaEntityRenderState.eating) {
				this.head.pitch = (float) (Math.PI / 2) + 0.2F * MathHelper.sin(pandaEntityRenderState.age * 0.6F);
				this.rightFrontLeg.pitch = -0.4F - 0.2F * MathHelper.sin(pandaEntityRenderState.age * 0.6F);
				this.leftFrontLeg.pitch = -0.4F - 0.2F * MathHelper.sin(pandaEntityRenderState.age * 0.6F);
			}

			if (pandaEntityRenderState.scaredByThunderstorm) {
				this.head.pitch = 2.1707964F;
				this.rightFrontLeg.pitch = -0.9F;
				this.leftFrontLeg.pitch = -0.9F;
			}
		} else {
			this.rightHindLeg.roll = 0.0F;
			this.leftHindLeg.roll = 0.0F;
			this.rightFrontLeg.roll = 0.0F;
			this.leftFrontLeg.roll = 0.0F;
		}

		if (pandaEntityRenderState.lieOnBackAnimationProgress > 0.0F) {
			this.rightHindLeg.pitch = -0.6F * MathHelper.sin(pandaEntityRenderState.age * 0.15F);
			this.leftHindLeg.pitch = 0.6F * MathHelper.sin(pandaEntityRenderState.age * 0.15F);
			this.rightFrontLeg.pitch = 0.3F * MathHelper.sin(pandaEntityRenderState.age * 0.25F);
			this.leftFrontLeg.pitch = -0.3F * MathHelper.sin(pandaEntityRenderState.age * 0.25F);
			this.head.pitch = MathHelper.lerpAngleRadians(pandaEntityRenderState.lieOnBackAnimationProgress, this.head.pitch, (float) (Math.PI / 2));
		}

		if (pandaEntityRenderState.rollOverAnimationProgress > 0.0F) {
			this.head.pitch = MathHelper.lerpAngleRadians(pandaEntityRenderState.rollOverAnimationProgress, this.head.pitch, 2.0561945F);
			this.rightHindLeg.pitch = -0.5F * MathHelper.sin(pandaEntityRenderState.age * 0.5F);
			this.leftHindLeg.pitch = 0.5F * MathHelper.sin(pandaEntityRenderState.age * 0.5F);
			this.rightFrontLeg.pitch = 0.5F * MathHelper.sin(pandaEntityRenderState.age * 0.5F);
			this.leftFrontLeg.pitch = -0.5F * MathHelper.sin(pandaEntityRenderState.age * 0.5F);
		}
	}
}
