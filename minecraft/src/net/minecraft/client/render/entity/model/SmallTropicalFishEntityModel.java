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
public class SmallTropicalFishEntityModel<T extends Entity> extends TintableCompositeModel<T> {
	private final ModelPart field_27522;
	private final ModelPart field_27523;

	public SmallTropicalFishEntityModel(ModelPart modelPart) {
		this.field_27522 = modelPart;
		this.field_27523 = modelPart.getChild("tail");
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 22;
		modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, dilation), ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		modelPartData.addChild(
			"tail", ModelPartBuilder.create().uv(22, -6).cuboid(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, dilation), ModelTransform.pivot(0.0F, 22.0F, 3.0F)
		);
		modelPartData.addChild(
			"right_fin",
			ModelPartBuilder.create().uv(2, 16).cuboid(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, dilation),
			ModelTransform.of(-1.0F, 22.5F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			"left_fin",
			ModelPartBuilder.create().uv(2, 12).cuboid(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, dilation),
			ModelTransform.of(1.0F, 22.5F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			"top_fin", ModelPartBuilder.create().uv(10, -5).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, dilation), ModelTransform.pivot(0.0F, 20.5F, -3.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27522;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.field_27523.yaw = -f * 0.45F * MathHelper.sin(0.6F * animationProgress);
	}
}
