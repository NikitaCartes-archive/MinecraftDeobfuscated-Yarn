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
import net.minecraft.entity.projectile.AbstractWindChargeEntity;

@Environment(EnvType.CLIENT)
public class WindChargeEntityModel extends SinglePartEntityModel<AbstractWindChargeEntity> {
	private static final int field_48704 = 16;
	private final ModelPart bone;
	private final ModelPart windCharge;
	private final ModelPart wind;

	public WindChargeEntityModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucent);
		this.bone = root.getChild(EntityModelPartNames.BONE);
		this.wind = this.bone.getChild("wind");
		this.windCharge = this.bone.getChild("wind_charge");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BONE, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		modelPartData2.addChild(
			"wind",
			ModelPartBuilder.create()
				.uv(15, 20)
				.cuboid(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.0F))
				.uv(0, 9)
				.cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)),
			ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
		);
		modelPartData2.addChild(
			"wind_charge", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(AbstractWindChargeEntity abstractWindChargeEntity, float f, float g, float h, float i, float j) {
		this.windCharge.yaw = -h * 16.0F * (float) (Math.PI / 180.0);
		this.wind.yaw = h * 16.0F * (float) (Math.PI / 180.0);
	}

	@Override
	public ModelPart getPart() {
		return this.bone;
	}
}
