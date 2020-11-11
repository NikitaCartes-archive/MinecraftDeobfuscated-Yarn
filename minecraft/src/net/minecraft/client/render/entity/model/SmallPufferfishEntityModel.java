package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallPufferfishEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27473;
	private final ModelPart field_27474;
	private final ModelPart field_27475;

	public SmallPufferfishEntityModel(ModelPart modelPart) {
		this.field_27473 = modelPart;
		this.field_27474 = modelPart.getChild("left_fin");
		this.field_27475 = modelPart.getChild("right_fin");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 23;
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 27).cuboid(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F, 23.0F, 0.0F));
		modelPartData.addChild("right_eye", ModelPartBuilder.create().uv(24, 6).cuboid(-1.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F), ModelTransform.pivot(0.0F, 20.0F, 0.0F));
		modelPartData.addChild("left_eye", ModelPartBuilder.create().uv(28, 6).cuboid(0.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F), ModelTransform.pivot(0.0F, 20.0F, 0.0F));
		modelPartData.addChild("back_fin", ModelPartBuilder.create().uv(-3, 0).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F), ModelTransform.pivot(0.0F, 22.0F, 1.5F));
		modelPartData.addChild(
			"right_fin", ModelPartBuilder.create().uv(25, 0).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F), ModelTransform.pivot(-1.5F, 22.0F, -1.5F)
		);
		modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(25, 0).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F), ModelTransform.pivot(1.5F, 22.0F, -1.5F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27473;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.field_27475.roll = -0.2F + 0.4F * MathHelper.sin(animationProgress * 0.2F);
		this.field_27474.roll = 0.2F - 0.4F * MathHelper.sin(animationProgress * 0.2F);
	}
}
