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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TadpoleEntityModel extends EntityModel<LivingEntityRenderState> {
	private final ModelPart tail;

	public TadpoleEntityModel(ModelPart modelPart) {
		super(modelPart, RenderLayer::getEntityCutoutNoCull);
		this.tail = modelPart.getChild(EntityModelPartNames.TAIL);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 0.0F;
		float g = 22.0F;
		float h = -3.0F;
		modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F, 22.0F, -3.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 7.0F), ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 16, 16);
	}

	public void setAngles(LivingEntityRenderState livingEntityRenderState) {
		super.setAngles(livingEntityRenderState);
		float f = livingEntityRenderState.touchingWater ? 1.0F : 1.5F;
		this.tail.yaw = -f * 0.25F * MathHelper.sin(0.3F * livingEntityRenderState.age);
	}
}
