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
public class DolphinEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart tailFin;

	public DolphinEntityModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.tail = this.body.getChild("tail");
		this.tailFin = this.tail.getChild("tail_fin");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 18.0F;
		float g = -8.0F;
		ModelPartData modelPartData2 = modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(22, 0).cuboid(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F), ModelTransform.pivot(0.0F, 22.0F, -5.0F)
		);
		modelPartData2.addChild(
			"back_fin", ModelPartBuilder.create().uv(51, 0).cuboid(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F), ModelTransform.rotation((float) (Math.PI / 3), 0.0F, 0.0F)
		);
		modelPartData2.addChild(
			"left_fin",
			ModelPartBuilder.create().uv(48, 20).mirrored().cuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F),
			ModelTransform.of(2.0F, -2.0F, 4.0F, (float) (Math.PI / 3), 0.0F, (float) (Math.PI * 2.0 / 3.0))
		);
		modelPartData2.addChild(
			"right_fin",
			ModelPartBuilder.create().uv(48, 20).cuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F),
			ModelTransform.of(-2.0F, -2.0F, 4.0F, (float) (Math.PI / 3), 0.0F, (float) (-Math.PI * 2.0 / 3.0))
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			"tail", ModelPartBuilder.create().uv(0, 19).cuboid(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 11.0F), ModelTransform.of(0.0F, -2.5F, 11.0F, -0.10471976F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"tail_fin", ModelPartBuilder.create().uv(19, 20).cuboid(-5.0F, -0.5F, 0.0F, 10.0F, 1.0F, 6.0F), ModelTransform.pivot(0.0F, 0.0F, 9.0F)
		);
		ModelPartData modelPartData4 = modelPartData2.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F), ModelTransform.pivot(0.0F, -4.0F, -3.0F)
		);
		modelPartData4.addChild("nose", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, 2.0F, -7.0F, 2.0F, 2.0F, 4.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.body.pitch = headPitch * (float) (Math.PI / 180.0);
		this.body.yaw = headYaw * (float) (Math.PI / 180.0);
		if (Entity.squaredHorizontalLength(entity.getVelocity()) > 1.0E-7) {
			this.body.pitch = this.body.pitch + (-0.05F - 0.05F * MathHelper.cos(animationProgress * 0.3F));
			this.tail.pitch = -0.1F * MathHelper.cos(animationProgress * 0.3F);
			this.tailFin.pitch = -0.2F * MathHelper.cos(animationProgress * 0.3F);
		}
	}
}
