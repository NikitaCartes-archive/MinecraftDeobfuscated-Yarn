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
public class LargePufferfishEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart leftBlueFin;
	private final ModelPart rightBlueFin;

	public LargePufferfishEntityModel(ModelPart root) {
		this.root = root;
		this.leftBlueFin = root.getChild(EntityModelPartNames.LEFT_BLUE_FIN);
		this.rightBlueFin = root.getChild(EntityModelPartNames.RIGHT_BLUE_FIN);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 22;
		modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_BLUE_FIN,
			ModelPartBuilder.create().uv(24, 0).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F),
			ModelTransform.pivot(-4.0F, 15.0F, -2.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_BLUE_FIN,
			ModelPartBuilder.create().uv(24, 3).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F),
			ModelTransform.pivot(4.0F, 15.0F, -2.0F)
		);
		modelPartData.addChild(
			"top_front_fin",
			ModelPartBuilder.create().uv(15, 17).cuboid(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F),
			ModelTransform.of(0.0F, 14.0F, -4.0F, (float) (Math.PI / 4), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"top_middle_fin", ModelPartBuilder.create().uv(14, 16).cuboid(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F), ModelTransform.pivot(0.0F, 14.0F, 0.0F)
		);
		modelPartData.addChild(
			"top_back_fin",
			ModelPartBuilder.create().uv(23, 18).cuboid(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F),
			ModelTransform.of(0.0F, 14.0F, 4.0F, (float) (-Math.PI / 4), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_front_fin",
			ModelPartBuilder.create().uv(5, 17).cuboid(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F),
			ModelTransform.of(-4.0F, 22.0F, -4.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			"left_front_fin",
			ModelPartBuilder.create().uv(1, 17).cuboid(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F),
			ModelTransform.of(4.0F, 22.0F, -4.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			"bottom_front_fin",
			ModelPartBuilder.create().uv(15, 20).cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F),
			ModelTransform.of(0.0F, 22.0F, -4.0F, (float) (-Math.PI / 4), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"bottom_middle_fin", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F), ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		modelPartData.addChild(
			"bottom_back_fin",
			ModelPartBuilder.create().uv(15, 20).cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F),
			ModelTransform.of(0.0F, 22.0F, 4.0F, (float) (Math.PI / 4), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_back_fin",
			ModelPartBuilder.create().uv(9, 17).cuboid(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F),
			ModelTransform.of(-4.0F, 22.0F, 4.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			"left_back_fin",
			ModelPartBuilder.create().uv(9, 17).cuboid(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F),
			ModelTransform.of(4.0F, 22.0F, 4.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.rightBlueFin.roll = -0.2F + 0.4F * MathHelper.sin(animationProgress * 0.2F);
		this.leftBlueFin.roll = 0.2F - 0.4F * MathHelper.sin(animationProgress * 0.2F);
	}
}
