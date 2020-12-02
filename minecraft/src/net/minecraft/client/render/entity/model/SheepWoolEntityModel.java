package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepWoolEntityModel<T extends SheepEntity> extends QuadrupedEntityModel<T> {
	private float headAngle;

	public SheepWoolEntityModel(ModelPart root) {
		super(root, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.6F)), ModelTransform.pivot(0.0F, 6.0F, -8.0F)
		);
		modelPartData.addChild(
			"body",
			ModelPartBuilder.create().uv(28, 8).cuboid(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, new Dilation(1.75F)),
			ModelTransform.of(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.5F));
		modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-3.0F, 12.0F, 7.0F));
		modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(3.0F, 12.0F, 7.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-3.0F, 12.0F, -5.0F));
		modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(3.0F, 12.0F, -5.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void animateModel(T sheepEntity, float f, float g, float h) {
		super.animateModel(sheepEntity, f, g, h);
		this.head.pivotY = 6.0F + sheepEntity.getNeckAngle(h) * 9.0F;
		this.headAngle = sheepEntity.getHeadAngle(h);
	}

	public void setAngles(T sheepEntity, float f, float g, float h, float i, float j) {
		super.setAngles(sheepEntity, f, g, h, i, j);
		this.head.pitch = this.headAngle;
	}
}
