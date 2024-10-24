package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.SalmonEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SalmonEntityModel extends EntityModel<SalmonEntityRenderState> {
	public static final ModelTransformer SMALL_TRANSFORMER = ModelTransformer.scaling(0.5F);
	public static final ModelTransformer LARGE_TRANSFORMER = ModelTransformer.scaling(1.5F);
	/**
	 * The key of the body front model part, whose value is {@value}.
	 */
	private static final String BODY_FRONT = "body_front";
	/**
	 * The key of the body back model part, whose value is {@value}.
	 */
	private static final String BODY_BACK = "body_back";
	private static final float field_54015 = -7.2F;
	private final ModelPart tail;

	public SalmonEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.tail = modelPart.getChild("body_back");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 20;
		ModelPartData modelPartData2 = modelPartData.addChild(
			"body_front", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), ModelTransform.pivot(0.0F, 20.0F, -7.2F)
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
			"body_back", ModelPartBuilder.create().uv(0, 13).cuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), ModelTransform.pivot(0.0F, 20.0F, 0.8000002F)
		);
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(22, 0).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), ModelTransform.pivot(0.0F, 20.0F, -7.2F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.BACK_FIN, ModelPartBuilder.create().uv(20, 10).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F), ModelTransform.pivot(0.0F, 0.0F, 8.0F)
		);
		modelPartData2.addChild(
			"top_front_fin", ModelPartBuilder.create().uv(2, 1).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F, -4.5F, 5.0F)
		);
		modelPartData3.addChild(
			"top_back_fin", ModelPartBuilder.create().uv(0, 2).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F), ModelTransform.pivot(0.0F, -4.5F, -1.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FIN,
			ModelPartBuilder.create().uv(-4, 0).cuboid(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F),
			ModelTransform.of(-1.5F, 21.5F, -7.2F, 0.0F, 0.0F, (float) (-Math.PI / 4))
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FIN,
			ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F),
			ModelTransform.of(1.5F, 21.5F, -7.2F, 0.0F, 0.0F, (float) (Math.PI / 4))
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(SalmonEntityRenderState salmonEntityRenderState) {
		super.setAngles(salmonEntityRenderState);
		float f = 1.0F;
		float g = 1.0F;
		if (!salmonEntityRenderState.touchingWater) {
			f = 1.3F;
			g = 1.7F;
		}

		this.tail.yaw = -f * 0.25F * MathHelper.sin(g * 0.6F * salmonEntityRenderState.age);
	}
}
