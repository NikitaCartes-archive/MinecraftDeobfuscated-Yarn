package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PhantomEntityModel<T extends PhantomEntity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart leftWingBase;
	private final ModelPart leftWingTip;
	private final ModelPart rightWingBase;
	private final ModelPart rightWingTip;
	private final ModelPart tailBase;
	private final ModelPart tailTip;

	public PhantomEntityModel(ModelPart root) {
		this.root = root;
		ModelPart modelPart = root.getChild("body");
		this.tailBase = modelPart.getChild("tail_base");
		this.tailTip = this.tailBase.getChild("tail_tip");
		this.leftWingBase = modelPart.getChild("left_wing_base");
		this.leftWingTip = this.leftWingBase.getChild("left_wing_tip");
		this.rightWingBase = modelPart.getChild("right_wing_base");
		this.rightWingTip = this.rightWingBase.getChild("right_wing_tip");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(0, 8).cuboid(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F), ModelTransform.rotation(-0.1F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			"tail_base", ModelPartBuilder.create().uv(3, 20).cuboid(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F), ModelTransform.pivot(0.0F, -2.0F, 1.0F)
		);
		modelPartData3.addChild("tail_tip", ModelPartBuilder.create().uv(4, 29).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F), ModelTransform.pivot(0.0F, 0.5F, 6.0F));
		ModelPartData modelPartData4 = modelPartData2.addChild(
			"left_wing_base", ModelPartBuilder.create().uv(23, 12).cuboid(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F), ModelTransform.of(2.0F, -2.0F, -8.0F, 0.0F, 0.0F, 0.1F)
		);
		modelPartData4.addChild(
			"left_wing_tip", ModelPartBuilder.create().uv(16, 24).cuboid(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F), ModelTransform.of(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F)
		);
		ModelPartData modelPartData5 = modelPartData2.addChild(
			"right_wing_base",
			ModelPartBuilder.create().uv(23, 12).mirrored().cuboid(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F),
			ModelTransform.of(-3.0F, -2.0F, -8.0F, 0.0F, 0.0F, -0.1F)
		);
		modelPartData5.addChild(
			"right_wing_tip",
			ModelPartBuilder.create().uv(16, 24).mirrored().cuboid(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F),
			ModelTransform.of(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F)
		);
		modelPartData2.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.of(0.0F, 1.0F, -7.0F, 0.2F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(T phantomEntity, float f, float g, float h, float i, float j) {
		float k = ((float)phantomEntity.method_33588() + h) * 7.448451F * (float) (Math.PI / 180.0);
		float l = 16.0F;
		this.leftWingBase.roll = MathHelper.cos(k) * 16.0F * (float) (Math.PI / 180.0);
		this.leftWingTip.roll = MathHelper.cos(k) * 16.0F * (float) (Math.PI / 180.0);
		this.rightWingBase.roll = -this.leftWingBase.roll;
		this.rightWingTip.roll = -this.leftWingTip.roll;
		this.tailBase.pitch = -(5.0F + MathHelper.cos(k * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
		this.tailTip.pitch = -(5.0F + MathHelper.cos(k * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
	}
}
