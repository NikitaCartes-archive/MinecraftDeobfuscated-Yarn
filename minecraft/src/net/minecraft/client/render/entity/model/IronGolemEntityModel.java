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
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class IronGolemEntityModel<T extends IronGolemEntity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27436;
	private final ModelPart head;
	private final ModelPart field_27437;
	private final ModelPart field_27438;
	private final ModelPart field_27439;
	private final ModelPart field_27440;

	public IronGolemEntityModel(ModelPart modelPart) {
		this.field_27436 = modelPart;
		this.head = modelPart.getChild("head");
		this.field_27437 = modelPart.getChild("right_arm");
		this.field_27438 = modelPart.getChild("left_arm");
		this.field_27439 = modelPart.getChild("right_leg");
		this.field_27440 = modelPart.getChild("left_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"head",
			ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F).uv(24, 0).cuboid(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F),
			ModelTransform.pivot(0.0F, -7.0F, -2.0F)
		);
		modelPartData.addChild(
			"body",
			ModelPartBuilder.create()
				.uv(0, 40)
				.cuboid(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F)
				.uv(0, 70)
				.cuboid(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new Dilation(0.5F)),
			ModelTransform.pivot(0.0F, -7.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_arm", ModelPartBuilder.create().uv(60, 21).cuboid(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), ModelTransform.pivot(0.0F, -7.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_arm", ModelPartBuilder.create().uv(60, 58).cuboid(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), ModelTransform.pivot(0.0F, -7.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_leg", ModelPartBuilder.create().uv(37, 0).cuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), ModelTransform.pivot(-4.0F, 11.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_leg", ModelPartBuilder.create().uv(60, 0).mirrored().cuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), ModelTransform.pivot(5.0F, 11.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27436;
	}

	public void setAngles(T ironGolemEntity, float f, float g, float h, float i, float j) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.field_27439.pitch = -1.5F * MathHelper.method_24504(f, 13.0F) * g;
		this.field_27440.pitch = 1.5F * MathHelper.method_24504(f, 13.0F) * g;
		this.field_27439.yaw = 0.0F;
		this.field_27440.yaw = 0.0F;
	}

	public void animateModel(T ironGolemEntity, float f, float g, float h) {
		int i = ironGolemEntity.getAttackTicksLeft();
		if (i > 0) {
			this.field_27437.pitch = -2.0F + 1.5F * MathHelper.method_24504((float)i - h, 10.0F);
			this.field_27438.pitch = -2.0F + 1.5F * MathHelper.method_24504((float)i - h, 10.0F);
		} else {
			int j = ironGolemEntity.getLookingAtVillagerTicks();
			if (j > 0) {
				this.field_27437.pitch = -0.8F + 0.025F * MathHelper.method_24504((float)j, 70.0F);
				this.field_27438.pitch = 0.0F;
			} else {
				this.field_27437.pitch = (-0.2F + 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
				this.field_27438.pitch = (-0.2F - 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
			}
		}
	}

	public ModelPart getRightArm() {
		return this.field_27437;
	}
}
