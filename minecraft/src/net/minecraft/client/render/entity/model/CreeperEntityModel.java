package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CreeperEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27406;
	private final ModelPart head;
	private final ModelPart field_27407;
	private final ModelPart field_27408;
	private final ModelPart field_27409;
	private final ModelPart field_27410;

	public CreeperEntityModel(ModelPart modelPart) {
		this.field_27406 = modelPart;
		this.head = modelPart.getChild("head");
		this.field_27408 = modelPart.getChild("right_hind_leg");
		this.field_27407 = modelPart.getChild("left_hind_leg");
		this.field_27410 = modelPart.getChild("right_front_leg");
		this.field_27409 = modelPart.getChild("left_front_leg");
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.pivot(0.0F, 6.0F, 0.0F)
		);
		modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(0.0F, 6.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation);
		modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-2.0F, 18.0F, 4.0F));
		modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(2.0F, 18.0F, 4.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-2.0F, 18.0F, -4.0F));
		modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(2.0F, 18.0F, -4.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27406;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.field_27407.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.field_27408.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.field_27409.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.field_27410.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}
