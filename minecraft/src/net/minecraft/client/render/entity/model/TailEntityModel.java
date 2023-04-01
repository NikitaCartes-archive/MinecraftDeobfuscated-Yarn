package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TailEntityModel<T extends LivingEntity> extends EntityModel<T> {
	public final ModelPart root;
	public final ModelPart tail;

	public TailEntityModel(ModelPart root) {
		super(RenderLayer::getEntityCutoutNoCull);
		this.root = root;
		this.tail = root.getChild(EntityModelPartNames.TAIL);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 4.0F, 9.0F, 5.0F),
			ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5184364F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 18, 14);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		float k = MathHelper.cos(h) * 0.01F + MathHelper.cos(f) * 0.1F * g;
		this.tail.yaw = k;
		if (livingEntity.isInSneakingPose()) {
			this.tail.pivotY = 15.0F;
			this.tail.pivotX = -2.0F;
			this.tail.pivotZ = 4.0F;
		} else {
			this.tail.pivotY = 14.0F;
			this.tail.pivotX = -2.0F;
			this.tail.pivotZ = 2.0F;
		}
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
