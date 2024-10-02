package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SnowGolemEntityModel extends EntityModel<LivingEntityRenderState> {
	/**
	 * The key of the upper body model part, whose value is {@value}.
	 */
	private static final String UPPER_BODY = "upper_body";
	private final ModelPart upperBody;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightArm;

	public SnowGolemEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
		this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
		this.upperBody = modelPart.getChild(EntityModelPartNames.UPPER_BODY);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 4.0F;
		Dilation dilation = new Dilation(-0.5F);
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation),
			ModelTransform.pivot(0.0F, 4.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.LEFT_ARM, modelPartBuilder, ModelTransform.of(5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 1.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, modelPartBuilder, ModelTransform.of(-5.0F, 6.0F, -1.0F, 0.0F, (float) Math.PI, -1.0F));
		modelPartData.addChild(
			EntityModelPartNames.UPPER_BODY,
			ModelPartBuilder.create().uv(0, 16).cuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, dilation),
			ModelTransform.pivot(0.0F, 13.0F, 0.0F)
		);
		modelPartData.addChild(
			"lower_body", ModelPartBuilder.create().uv(0, 36).cuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, dilation), ModelTransform.pivot(0.0F, 24.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(LivingEntityRenderState livingEntityRenderState) {
		super.setAngles(livingEntityRenderState);
		this.head.yaw = livingEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		this.head.pitch = livingEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.upperBody.yaw = livingEntityRenderState.yawDegrees * (float) (Math.PI / 180.0) * 0.25F;
		float f = MathHelper.sin(this.upperBody.yaw);
		float g = MathHelper.cos(this.upperBody.yaw);
		this.leftArm.yaw = this.upperBody.yaw;
		this.rightArm.yaw = this.upperBody.yaw + (float) Math.PI;
		this.leftArm.pivotX = g * 5.0F;
		this.leftArm.pivotZ = -f * 5.0F;
		this.rightArm.pivotX = -g * 5.0F;
		this.rightArm.pivotZ = f * 5.0F;
	}

	public ModelPart getHead() {
		return this.head;
	}
}
