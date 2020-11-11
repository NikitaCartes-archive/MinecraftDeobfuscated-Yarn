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
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SnowGolemEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27500;
	private final ModelPart field_27501;
	private final ModelPart topSnowball;
	private final ModelPart field_27502;
	private final ModelPart field_27503;

	public SnowGolemEntityModel(ModelPart modelPart) {
		this.field_27500 = modelPart;
		this.topSnowball = modelPart.getChild("head");
		this.field_27502 = modelPart.getChild("left_arm");
		this.field_27503 = modelPart.getChild("right_arm");
		this.field_27501 = modelPart.getChild("upper_body");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 4.0F;
		Dilation dilation = new Dilation(-0.5F);
		modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.pivot(0.0F, 4.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, dilation);
		modelPartData.addChild("left_arm", modelPartBuilder, ModelTransform.of(5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 1.0F));
		modelPartData.addChild("right_arm", modelPartBuilder, ModelTransform.of(-5.0F, 6.0F, -1.0F, 0.0F, (float) Math.PI, -1.0F));
		modelPartData.addChild(
			"upper_body", ModelPartBuilder.create().uv(0, 16).cuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, dilation), ModelTransform.pivot(0.0F, 13.0F, 0.0F)
		);
		modelPartData.addChild(
			"lower_body", ModelPartBuilder.create().uv(0, 36).cuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, dilation), ModelTransform.pivot(0.0F, 24.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.topSnowball.yaw = headYaw * (float) (Math.PI / 180.0);
		this.topSnowball.pitch = headPitch * (float) (Math.PI / 180.0);
		this.field_27501.yaw = headYaw * (float) (Math.PI / 180.0) * 0.25F;
		float f = MathHelper.sin(this.field_27501.yaw);
		float g = MathHelper.cos(this.field_27501.yaw);
		this.field_27502.yaw = this.field_27501.yaw;
		this.field_27503.yaw = this.field_27501.yaw + (float) Math.PI;
		this.field_27502.pivotX = g * 5.0F;
		this.field_27502.pivotZ = -f * 5.0F;
		this.field_27503.pivotX = -g * 5.0F;
		this.field_27503.pivotZ = f * 5.0F;
	}

	@Override
	public ModelPart getPart() {
		return this.field_27500;
	}

	public ModelPart getTopSnowball() {
		return this.topSnowball;
	}
}
