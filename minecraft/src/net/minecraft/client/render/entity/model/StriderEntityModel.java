package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.StriderEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StriderEntityModel extends EntityModel<StriderEntityRenderState> {
	/**
	 * The key of the right bottom bristle model part, whose value is {@value}.
	 */
	private static final String RIGHT_BOTTOM_BRISTLE = "right_bottom_bristle";
	/**
	 * The key of the right middle bristle model part, whose value is {@value}.
	 */
	private static final String RIGHT_MIDDLE_BRISTLE = "right_middle_bristle";
	/**
	 * The key of the right top bristle model part, whose value is {@value}.
	 */
	private static final String RIGHT_TOP_BRISTLE = "right_top_bristle";
	/**
	 * The key of the left top bristle model part, whose value is {@value}.
	 */
	private static final String LEFT_TOP_BRISTLE = "left_top_bristle";
	/**
	 * The key of the left middle bristle model part, whose value is {@value}.
	 */
	private static final String LEFT_MIDDLE_BRISTLE = "left_middle_bristle";
	/**
	 * The key of the left bottom bristle model part, whose value is {@value}.
	 */
	private static final String LEFT_BOTTOM_BRISTLE = "left_bottom_bristle";
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart body;
	private final ModelPart rightBottomBristle;
	private final ModelPart rightMiddleBristle;
	private final ModelPart rightTopBristle;
	private final ModelPart leftTopBristle;
	private final ModelPart leftMiddleBristle;
	private final ModelPart leftBottomBristle;

	public StriderEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
		this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
		this.body = modelPart.getChild(EntityModelPartNames.BODY);
		this.rightBottomBristle = this.body.getChild("right_bottom_bristle");
		this.rightMiddleBristle = this.body.getChild("right_middle_bristle");
		this.rightTopBristle = this.body.getChild("right_top_bristle");
		this.leftTopBristle = this.body.getChild("left_top_bristle");
		this.leftMiddleBristle = this.body.getChild("left_middle_bristle");
		this.leftBottomBristle = this.body.getChild("left_bottom_bristle");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), ModelTransform.pivot(-4.0F, 8.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 55).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), ModelTransform.pivot(4.0F, 8.0F, 0.0F)
		);
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F), ModelTransform.pivot(0.0F, 1.0F, 0.0F)
		);
		modelPartData2.addChild(
			"right_bottom_bristle",
			ModelPartBuilder.create().uv(16, 65).cuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			ModelTransform.of(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217305F)
		);
		modelPartData2.addChild(
			"right_middle_bristle",
			ModelPartBuilder.create().uv(16, 49).cuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			ModelTransform.of(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.134464F)
		);
		modelPartData2.addChild(
			"right_top_bristle",
			ModelPartBuilder.create().uv(16, 33).cuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			ModelTransform.of(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.87266463F)
		);
		modelPartData2.addChild(
			"left_top_bristle",
			ModelPartBuilder.create().uv(16, 33).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			ModelTransform.of(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.87266463F)
		);
		modelPartData2.addChild(
			"left_middle_bristle",
			ModelPartBuilder.create().uv(16, 49).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			ModelTransform.of(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.134464F)
		);
		modelPartData2.addChild(
			"left_bottom_bristle",
			ModelPartBuilder.create().uv(16, 65).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			ModelTransform.of(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217305F)
		);
		return TexturedModelData.of(modelData, 64, 128);
	}

	public void setAngles(StriderEntityRenderState striderEntityRenderState) {
		super.setAngles(striderEntityRenderState);
		float f = striderEntityRenderState.limbFrequency;
		float g = Math.min(striderEntityRenderState.limbAmplitudeMultiplier, 0.25F);
		if (!striderEntityRenderState.hasPassengers) {
			this.body.pitch = striderEntityRenderState.pitch * (float) (Math.PI / 180.0);
			this.body.yaw = striderEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		} else {
			this.body.pitch = 0.0F;
			this.body.yaw = 0.0F;
		}

		float h = 1.5F;
		this.body.roll = 0.1F * MathHelper.sin(f * 1.5F) * 4.0F * g;
		this.body.pivotY = 2.0F;
		this.body.pivotY = this.body.pivotY - 2.0F * MathHelper.cos(f * 1.5F) * 2.0F * g;
		this.leftLeg.pitch = MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.rightLeg.pitch = MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.leftLeg.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F) * g;
		this.rightLeg.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F + (float) Math.PI) * g;
		this.leftLeg.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.rightLeg.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.rightBottomBristle.roll = -1.2217305F;
		this.rightMiddleBristle.roll = -1.134464F;
		this.rightTopBristle.roll = -0.87266463F;
		this.leftTopBristle.roll = 0.87266463F;
		this.leftMiddleBristle.roll = 1.134464F;
		this.leftBottomBristle.roll = 1.2217305F;
		float i = MathHelper.cos(f * 1.5F + (float) Math.PI) * g;
		this.rightBottomBristle.roll += i * 1.3F;
		this.rightMiddleBristle.roll += i * 1.2F;
		this.rightTopBristle.roll += i * 0.6F;
		this.leftTopBristle.roll += i * 0.6F;
		this.leftMiddleBristle.roll += i * 1.2F;
		this.leftBottomBristle.roll += i * 1.3F;
		float j = 1.0F;
		float k = 1.0F;
		this.rightBottomBristle.roll = this.rightBottomBristle.roll + 0.05F * MathHelper.sin(striderEntityRenderState.age * 1.0F * -0.4F);
		this.rightMiddleBristle.roll = this.rightMiddleBristle.roll + 0.1F * MathHelper.sin(striderEntityRenderState.age * 1.0F * 0.2F);
		this.rightTopBristle.roll = this.rightTopBristle.roll + 0.1F * MathHelper.sin(striderEntityRenderState.age * 1.0F * 0.4F);
		this.leftTopBristle.roll = this.leftTopBristle.roll + 0.1F * MathHelper.sin(striderEntityRenderState.age * 1.0F * 0.4F);
		this.leftMiddleBristle.roll = this.leftMiddleBristle.roll + 0.1F * MathHelper.sin(striderEntityRenderState.age * 1.0F * 0.2F);
		this.leftBottomBristle.roll = this.leftBottomBristle.roll + 0.05F * MathHelper.sin(striderEntityRenderState.age * 1.0F * -0.4F);
	}
}
