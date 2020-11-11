package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27504;
	private final ModelPart head;
	private final ModelPart field_27505;
	private final ModelPart field_27506;
	private final ModelPart field_27507;
	private final ModelPart field_27508;
	private final ModelPart field_27509;
	private final ModelPart field_27510;
	private final ModelPart field_27511;
	private final ModelPart field_27512;

	public SpiderEntityModel(ModelPart modelPart) {
		this.field_27504 = modelPart;
		this.head = modelPart.getChild("head");
		this.field_27505 = modelPart.getChild("right_hind_leg");
		this.field_27506 = modelPart.getChild("left_hind_leg");
		this.field_27507 = modelPart.getChild("right_middle_hind_leg");
		this.field_27508 = modelPart.getChild("left_middle_hind_leg");
		this.field_27509 = modelPart.getChild("right_middle_front_leg");
		this.field_27510 = modelPart.getChild("left_middle_front_leg");
		this.field_27511 = modelPart.getChild("right_front_leg");
		this.field_27512 = modelPart.getChild("left_front_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 15;
		modelPartData.addChild("head", ModelPartBuilder.create().uv(32, 4).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), ModelTransform.pivot(0.0F, 15.0F, -3.0F));
		modelPartData.addChild("body0", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.pivot(0.0F, 15.0F, 0.0F));
		modelPartData.addChild("body1", ModelPartBuilder.create().uv(0, 12).cuboid(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), ModelTransform.pivot(0.0F, 15.0F, 9.0F));
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(18, 0).cuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(18, 0).cuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, 2.0F));
		modelPartData.addChild("left_hind_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, 2.0F));
		modelPartData.addChild("right_middle_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, 1.0F));
		modelPartData.addChild("left_middle_hind_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, 1.0F));
		modelPartData.addChild("right_middle_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, 0.0F));
		modelPartData.addChild("left_middle_front_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, 0.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, -1.0F));
		modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, -1.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27504;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		float f = (float) (Math.PI / 4);
		this.field_27505.roll = (float) (-Math.PI / 4);
		this.field_27506.roll = (float) (Math.PI / 4);
		this.field_27507.roll = -0.58119464F;
		this.field_27508.roll = 0.58119464F;
		this.field_27509.roll = -0.58119464F;
		this.field_27510.roll = 0.58119464F;
		this.field_27511.roll = (float) (-Math.PI / 4);
		this.field_27512.roll = (float) (Math.PI / 4);
		float g = -0.0F;
		float h = (float) (Math.PI / 8);
		this.field_27505.yaw = (float) (Math.PI / 4);
		this.field_27506.yaw = (float) (-Math.PI / 4);
		this.field_27507.yaw = (float) (Math.PI / 8);
		this.field_27508.yaw = (float) (-Math.PI / 8);
		this.field_27509.yaw = (float) (-Math.PI / 8);
		this.field_27510.yaw = (float) (Math.PI / 8);
		this.field_27511.yaw = (float) (-Math.PI / 4);
		this.field_27512.yaw = (float) (Math.PI / 4);
		float i = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbDistance;
		float j = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbDistance;
		float k = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float l = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		float m = Math.abs(MathHelper.sin(limbAngle * 0.6662F + 0.0F) * 0.4F) * limbDistance;
		float n = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * limbDistance;
		float o = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float p = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		this.field_27505.yaw += i;
		this.field_27506.yaw += -i;
		this.field_27507.yaw += j;
		this.field_27508.yaw += -j;
		this.field_27509.yaw += k;
		this.field_27510.yaw += -k;
		this.field_27511.yaw += l;
		this.field_27512.yaw += -l;
		this.field_27505.roll += m;
		this.field_27506.roll += -m;
		this.field_27507.roll += n;
		this.field_27508.roll += -n;
		this.field_27509.roll += o;
		this.field_27510.roll += -o;
		this.field_27511.roll += p;
		this.field_27512.roll += -p;
	}
}
