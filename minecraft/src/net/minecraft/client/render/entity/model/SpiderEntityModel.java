package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel extends EntityModel<LivingEntityRenderState> {
	/**
	 * The key of the first model part of the body, whose value is {@value}.
	 */
	private static final String BODY0 = "body0";
	/**
	 * The key of the second model part of the body, whose value is {@value}.
	 */
	private static final String BODY1 = "body1";
	/**
	 * The key of the right middle front leg model part, whose value is {@value}.
	 */
	private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
	/**
	 * The key of the left middle front leg model part, whose value is {@value}.
	 */
	private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
	/**
	 * The key of the right middle hind leg model part, whose value is {@value}.
	 */
	private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
	/**
	 * The key of the left middle hind leg model part, whose value is {@value}.
	 */
	private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
	private final ModelPart head;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightMiddleLeg;
	private final ModelPart leftMiddleLeg;
	private final ModelPart rightMiddleFrontLeg;
	private final ModelPart leftMiddleFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public SpiderEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightMiddleLeg = modelPart.getChild("right_middle_hind_leg");
		this.leftMiddleLeg = modelPart.getChild("left_middle_hind_leg");
		this.rightMiddleFrontLeg = modelPart.getChild("right_middle_front_leg");
		this.leftMiddleFrontLeg = modelPart.getChild("left_middle_front_leg");
		this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 15;
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(32, 4).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), ModelTransform.pivot(0.0F, 15.0F, -3.0F)
		);
		modelPartData.addChild("body0", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.pivot(0.0F, 15.0F, 0.0F));
		modelPartData.addChild("body1", ModelPartBuilder.create().uv(0, 12).cuboid(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), ModelTransform.pivot(0.0F, 15.0F, 9.0F));
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(18, 0).cuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(18, 0).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		float f = (float) (Math.PI / 4);
		float g = (float) (Math.PI / 8);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, 2.0F, 0.0F, (float) (Math.PI / 4), (float) (-Math.PI / 4))
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, 2.0F, 0.0F, (float) (-Math.PI / 4), (float) (Math.PI / 4))
		);
		modelPartData.addChild("right_middle_hind_leg", modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, 1.0F, 0.0F, (float) (Math.PI / 8), -0.58119464F));
		modelPartData.addChild("left_middle_hind_leg", modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, 1.0F, 0.0F, (float) (-Math.PI / 8), 0.58119464F));
		modelPartData.addChild("right_middle_front_leg", modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, 0.0F, 0.0F, (float) (-Math.PI / 8), -0.58119464F));
		modelPartData.addChild("left_middle_front_leg", modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, 0.0F, 0.0F, (float) (Math.PI / 8), 0.58119464F));
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.of(-4.0F, 15.0F, -1.0F, 0.0F, (float) (-Math.PI / 4), (float) (-Math.PI / 4))
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.of(4.0F, 15.0F, -1.0F, 0.0F, (float) (Math.PI / 4), (float) (Math.PI / 4))
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(LivingEntityRenderState livingEntityRenderState) {
		super.setAngles(livingEntityRenderState);
		this.head.yaw = livingEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		this.head.pitch = livingEntityRenderState.pitch * (float) (Math.PI / 180.0);
		float f = livingEntityRenderState.limbFrequency * 0.6662F;
		float g = livingEntityRenderState.limbAmplitudeMultiplier;
		float h = -(MathHelper.cos(f * 2.0F + 0.0F) * 0.4F) * g;
		float i = -(MathHelper.cos(f * 2.0F + (float) Math.PI) * 0.4F) * g;
		float j = -(MathHelper.cos(f * 2.0F + (float) (Math.PI / 2)) * 0.4F) * g;
		float k = -(MathHelper.cos(f * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * g;
		float l = Math.abs(MathHelper.sin(f + 0.0F) * 0.4F) * g;
		float m = Math.abs(MathHelper.sin(f + (float) Math.PI) * 0.4F) * g;
		float n = Math.abs(MathHelper.sin(f + (float) (Math.PI / 2)) * 0.4F) * g;
		float o = Math.abs(MathHelper.sin(f + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * g;
		this.rightHindLeg.yaw += h;
		this.leftHindLeg.yaw -= h;
		this.rightMiddleLeg.yaw += i;
		this.leftMiddleLeg.yaw -= i;
		this.rightMiddleFrontLeg.yaw += j;
		this.leftMiddleFrontLeg.yaw -= j;
		this.rightFrontLeg.yaw += k;
		this.leftFrontLeg.yaw -= k;
		this.rightHindLeg.roll += l;
		this.leftHindLeg.roll -= l;
		this.rightMiddleLeg.roll += m;
		this.leftMiddleLeg.roll -= m;
		this.rightMiddleFrontLeg.roll += n;
		this.leftMiddleFrontLeg.roll -= n;
		this.rightFrontLeg.roll += o;
		this.leftFrontLeg.roll -= o;
	}
}
