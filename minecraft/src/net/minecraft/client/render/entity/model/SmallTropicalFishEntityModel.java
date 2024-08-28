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
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallTropicalFishEntityModel extends EntityModel<TropicalFishEntityRenderState> {
	private final ModelPart tail;

	public SmallTropicalFishEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.tail = modelPart.getChild(EntityModelPartNames.TAIL);
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 22;
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, dilation),
			ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(22, -6).cuboid(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, dilation),
			ModelTransform.pivot(0.0F, 22.0F, 3.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FIN,
			ModelPartBuilder.create().uv(2, 16).cuboid(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, dilation),
			ModelTransform.of(-1.0F, 22.5F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FIN,
			ModelPartBuilder.create().uv(2, 12).cuboid(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, dilation),
			ModelTransform.of(1.0F, 22.5F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TOP_FIN,
			ModelPartBuilder.create().uv(10, -5).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, dilation),
			ModelTransform.pivot(0.0F, 20.5F, -3.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
		super.setAngles(tropicalFishEntityRenderState);
		float f = tropicalFishEntityRenderState.touchingWater ? 1.0F : 1.5F;
		this.tail.yaw = -f * 0.45F * MathHelper.sin(0.6F * tropicalFishEntityRenderState.age);
	}
}
