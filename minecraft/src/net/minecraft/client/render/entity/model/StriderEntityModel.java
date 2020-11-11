package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StriderEntityModel<T extends StriderEntity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27514;
	private final ModelPart field_23353;
	private final ModelPart field_23354;
	private final ModelPart field_23355;
	private final ModelPart field_27515;
	private final ModelPart field_27516;
	private final ModelPart field_27517;
	private final ModelPart field_27518;
	private final ModelPart field_27519;
	private final ModelPart field_27520;

	public StriderEntityModel(ModelPart modelPart) {
		this.field_27514 = modelPart;
		this.field_23353 = modelPart.getChild("right_leg");
		this.field_23354 = modelPart.getChild("left_leg");
		this.field_23355 = modelPart.getChild("body");
		this.field_27515 = this.field_23355.getChild("right_bottom_bristle");
		this.field_27516 = this.field_23355.getChild("right_middle_bristle");
		this.field_27517 = this.field_23355.getChild("right_top_bristle");
		this.field_27518 = this.field_23355.getChild("left_top_bristle");
		this.field_27519 = this.field_23355.getChild("left_middle_bristle");
		this.field_27520 = this.field_23355.getChild("left_bottom_bristle");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"right_leg", ModelPartBuilder.create().uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), ModelTransform.pivot(-4.0F, 8.0F, 0.0F)
		);
		modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 55).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), ModelTransform.pivot(4.0F, 8.0F, 0.0F));
		ModelPartData modelPartData2 = modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F), ModelTransform.pivot(0.0F, 1.0F, 0.0F)
		);
		modelPartData2.addChild(
			"right_bottom_bristle",
			ModelPartBuilder.create().uv(16, 65).cuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			ModelTransform.of(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217305F)
		);
		modelPartData2.addChild(
			"right_middle_bristle",
			ModelPartBuilder.create().uv(16, 49).cuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			ModelTransform.of(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.134464F)
		);
		modelPartData2.addChild(
			"right_top_bristle",
			ModelPartBuilder.create().uv(16, 33).cuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			ModelTransform.of(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.87266463F)
		);
		modelPartData2.addChild(
			"left_top_bristle",
			ModelPartBuilder.create().uv(16, 33).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			ModelTransform.of(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.87266463F)
		);
		modelPartData2.addChild(
			"left_middle_bristle",
			ModelPartBuilder.create().uv(16, 49).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			ModelTransform.of(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.134464F)
		);
		modelPartData2.addChild(
			"left_bottom_bristle",
			ModelPartBuilder.create().uv(16, 65).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			ModelTransform.of(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217305F)
		);
		return TexturedModelData.of(modelData, 64, 128);
	}

	public void setAngles(StriderEntity striderEntity, float f, float g, float h, float i, float j) {
		g = Math.min(0.25F, g);
		if (!striderEntity.hasPassengers()) {
			this.field_23355.pitch = j * (float) (Math.PI / 180.0);
			this.field_23355.yaw = i * (float) (Math.PI / 180.0);
		} else {
			this.field_23355.pitch = 0.0F;
			this.field_23355.yaw = 0.0F;
		}

		float k = 1.5F;
		this.field_23355.roll = 0.1F * MathHelper.sin(f * 1.5F) * 4.0F * g;
		this.field_23355.pivotY = 2.0F;
		this.field_23355.pivotY = this.field_23355.pivotY - 2.0F * MathHelper.cos(f * 1.5F) * 2.0F * g;
		this.field_23354.pitch = MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.field_23353.pitch = MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.field_23354.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F) * g;
		this.field_23353.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F + (float) Math.PI) * g;
		this.field_23354.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.field_23353.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.field_27515.roll = -1.2217305F;
		this.field_27516.roll = -1.134464F;
		this.field_27517.roll = -0.87266463F;
		this.field_27518.roll = 0.87266463F;
		this.field_27519.roll = 1.134464F;
		this.field_27520.roll = 1.2217305F;
		float l = MathHelper.cos(f * 1.5F + (float) Math.PI) * g;
		this.field_27515.roll += l * 1.3F;
		this.field_27516.roll += l * 1.2F;
		this.field_27517.roll += l * 0.6F;
		this.field_27518.roll += l * 0.6F;
		this.field_27519.roll += l * 1.2F;
		this.field_27520.roll += l * 1.3F;
		float m = 1.0F;
		float n = 1.0F;
		this.field_27515.roll = this.field_27515.roll + 0.05F * MathHelper.sin(h * 1.0F * -0.4F);
		this.field_27516.roll = this.field_27516.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.2F);
		this.field_27517.roll = this.field_27517.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.4F);
		this.field_27518.roll = this.field_27518.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.4F);
		this.field_27519.roll = this.field_27519.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.2F);
		this.field_27520.roll = this.field_27520.roll + 0.05F * MathHelper.sin(h * 1.0F * -0.4F);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27514;
	}
}
