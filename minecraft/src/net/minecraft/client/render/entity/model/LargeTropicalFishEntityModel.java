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
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LargeTropicalFishEntityModel<T extends Entity> extends TintableCompositeModel<T> {
	private final ModelPart root;
	private final ModelPart tail;

	public LargeTropicalFishEntityModel(ModelPart root) {
		this.root = root;
		this.tail = root.getChild(EntityModelPartNames.TAIL);
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 19;
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 20).cuboid(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, dilation),
			ModelTransform.pivot(0.0F, 19.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(21, 16).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 5.0F, dilation),
			ModelTransform.pivot(0.0F, 19.0F, 3.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FIN,
			ModelPartBuilder.create().uv(2, 16).cuboid(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, dilation),
			ModelTransform.of(-1.0F, 20.0F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FIN,
			ModelPartBuilder.create().uv(2, 12).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, dilation),
			ModelTransform.of(1.0F, 20.0F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TOP_FIN,
			ModelPartBuilder.create().uv(20, 11).cuboid(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F, dilation),
			ModelTransform.pivot(0.0F, 16.0F, -3.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BOTTOM_FIN,
			ModelPartBuilder.create().uv(20, 21).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 6.0F, dilation),
			ModelTransform.pivot(0.0F, 22.0F, -3.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.tail.yaw = -f * 0.45F * MathHelper.sin(0.6F * animationProgress);
	}
}
