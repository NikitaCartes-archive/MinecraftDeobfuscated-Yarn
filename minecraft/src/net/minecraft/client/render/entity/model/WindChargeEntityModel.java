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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.projectile.WindChargeEntity;

@Environment(EnvType.CLIENT)
public class WindChargeEntityModel extends SinglePartEntityModel<WindChargeEntity> {
	private final ModelPart bone;

	public WindChargeEntityModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucent);
		this.bone = root.getChild(EntityModelPartNames.BONE);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BONE, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData modelPartData3 = modelPartData2.addChild("projectile", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData modelPartData4 = modelPartData3.addChild(
			"wind",
			ModelPartBuilder.create()
				.uv(20, 112)
				.cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
				.uv(0, 8)
				.cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		modelPartData4.addChild(
			"cube_r1",
			ModelPartBuilder.create().uv(32, 24).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(-0.6F)),
			ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 1.5708F)
		);
		modelPartData4.addChild(
			"cube_r2",
			ModelPartBuilder.create().uv(16, 40).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(-0.3F)),
			ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F)
		);
		modelPartData3.addChild(
			"wind_charge", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(WindChargeEntity windChargeEntity, float f, float g, float h, float i, float j) {
	}

	@Override
	public ModelPart getPart() {
		return this.bone;
	}
}
