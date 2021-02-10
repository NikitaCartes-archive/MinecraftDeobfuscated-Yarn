package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AxolotlEntityModel<T extends AxolotlEntity> extends AnimalModel<T> {
	private final ModelPart tail;
	private final ModelPart leftHindLeg;
	private final ModelPart rightHindLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart topGills;
	private final ModelPart leftGills;
	private final ModelPart rightGills;

	public AxolotlEntityModel(ModelPart root) {
		super(true, 8.0F, 3.35F);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.rightHindLeg = this.body.getChild("right_hind_leg");
		this.leftHindLeg = this.body.getChild("left_hind_leg");
		this.rightFrontLeg = this.body.getChild("right_front_leg");
		this.leftFrontLeg = this.body.getChild("left_front_leg");
		this.tail = this.body.getChild("tail");
		this.topGills = this.head.getChild("top_gills");
		this.leftGills = this.head.getChild("left_gills");
		this.rightGills = this.head.getChild("right_gills");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"body",
			ModelPartBuilder.create().uv(0, 11).cuboid(-4.0F, -2.0F, -9.0F, 8.0F, 4.0F, 10.0F).uv(2, 17).cuboid(0.0F, -3.0F, -8.0F, 0.0F, 5.0F, 9.0F),
			ModelTransform.pivot(0.0F, 20.0F, 5.0F)
		);
		Dilation dilation = new Dilation(0.001F);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			"head", ModelPartBuilder.create().uv(0, 1).cuboid(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F, dilation), ModelTransform.pivot(0.0F, 0.0F, -9.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(3, 37).cuboid(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 0.0F, dilation);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 40).cuboid(-3.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, dilation);
		ModelPartBuilder modelPartBuilder3 = ModelPartBuilder.create().uv(11, 40).cuboid(0.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, dilation);
		modelPartData3.addChild("top_gills", modelPartBuilder, ModelTransform.pivot(0.0F, -3.0F, -1.0F));
		modelPartData3.addChild("left_gills", modelPartBuilder2, ModelTransform.pivot(-4.0F, 0.0F, -1.0F));
		modelPartData3.addChild("right_gills", modelPartBuilder3, ModelTransform.pivot(4.0F, 0.0F, -1.0F));
		ModelPartBuilder modelPartBuilder4 = ModelPartBuilder.create().uv(2, 13).cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, dilation);
		ModelPartBuilder modelPartBuilder5 = ModelPartBuilder.create().uv(2, 13).cuboid(-2.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, dilation);
		modelPartData2.addChild("right_hind_leg", modelPartBuilder5, ModelTransform.pivot(-3.5F, 1.0F, -1.0F));
		modelPartData2.addChild("left_hind_leg", modelPartBuilder4, ModelTransform.pivot(3.5F, 1.0F, -1.0F));
		modelPartData2.addChild("right_front_leg", modelPartBuilder5, ModelTransform.pivot(-3.5F, 1.0F, -8.0F));
		modelPartData2.addChild("left_front_leg", modelPartBuilder4, ModelTransform.pivot(3.5F, 1.0F, -8.0F));
		modelPartData2.addChild("tail", ModelPartBuilder.create().uv(2, 19).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 12.0F), ModelTransform.pivot(0.0F, 0.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body);
	}

	public void setAngles(T axolotlEntity, float f, float g, float h, float i, float j) {
		this.method_33292(i, j);
		if (axolotlEntity.isPlayingDead()) {
			this.method_33298();
		} else {
			boolean bl = Entity.squaredHorizontalLength(axolotlEntity.getVelocity()) > 1.0E-7;
			if (axolotlEntity.isInsideWaterOrBubbleColumn()) {
				if (bl) {
					this.method_33295(h, j);
				} else {
					this.method_33297(h);
				}
			} else {
				if (axolotlEntity.isOnGround()) {
					if (bl) {
						this.method_33294(h);
					} else {
						this.method_33291(h);
					}
				}
			}
		}
	}

	private void method_33292(float f, float g) {
		this.body.pivotX = 0.0F;
		this.head.pivotY = 0.0F;
		this.body.pivotY = 20.0F;
		this.body.method_33425(g * (float) (Math.PI / 180.0), f * (float) (Math.PI / 180.0), 0.0F);
		this.head.method_33425(0.0F, 0.0F, 0.0F);
		this.leftHindLeg.method_33425(0.0F, 0.0F, 0.0F);
		this.rightHindLeg.method_33425(0.0F, 0.0F, 0.0F);
		this.leftFrontLeg.method_33425(0.0F, 0.0F, 0.0F);
		this.rightFrontLeg.method_33425(0.0F, 0.0F, 0.0F);
		this.leftGills.method_33425(0.0F, 0.0F, 0.0F);
		this.rightGills.method_33425(0.0F, 0.0F, 0.0F);
		this.topGills.method_33425(0.0F, 0.0F, 0.0F);
		this.tail.method_33425(0.0F, 0.0F, 0.0F);
	}

	private void method_33291(float f) {
		float g = f * 0.09F;
		float h = MathHelper.sin(g);
		float i = MathHelper.cos(g);
		float j = h * h - 2.0F * h;
		float k = i * i - 3.0F * h;
		this.head.pitch = -0.09F * j;
		this.head.roll = -0.2F;
		this.tail.yaw = -0.1F + 0.1F * j;
		this.topGills.pitch = 0.6F + 0.05F * k;
		this.leftGills.yaw = -this.topGills.pitch;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.leftHindLeg.method_33425(1.1F, 1.0F, 0.0F);
		this.leftFrontLeg.method_33425(0.8F, 2.3F, -0.5F);
		this.method_33299();
	}

	private void method_33294(float f) {
		float g = f * 0.11F;
		float h = MathHelper.cos(g);
		float i = (h * h - 2.0F * h) / 5.0F;
		float j = 0.7F * h;
		this.head.yaw = 0.09F * h;
		this.tail.yaw = this.head.yaw;
		this.topGills.pitch = 0.6F - 0.08F * (h * h + 2.0F * MathHelper.sin(g));
		this.leftGills.yaw = -this.topGills.pitch;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.leftHindLeg.method_33425(0.9424779F, 1.5F - i, -0.1F);
		this.leftFrontLeg.method_33425(1.0995574F, (float) (Math.PI / 2) - j, 0.0F);
		this.rightHindLeg.method_33425(this.leftHindLeg.pitch, -1.0F - i, 0.0F);
		this.rightFrontLeg.method_33425(this.leftFrontLeg.pitch, (float) (-Math.PI / 2) - j, 0.0F);
	}

	private void method_33297(float f) {
		float g = f * 0.075F;
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g) * 0.15F;
		this.body.pitch = -0.15F + 0.075F * h;
		this.body.pivotY -= i;
		this.head.pitch = -this.body.pitch;
		this.topGills.pitch = 0.2F * h;
		this.leftGills.yaw = -0.3F * h - 0.19F;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.leftHindLeg.method_33425((float) (Math.PI * 3.0 / 4.0) - h * 0.11F, 0.47123894F, 1.7278761F);
		this.leftFrontLeg.method_33425((float) (Math.PI / 4) - h * 0.2F, 2.042035F, 0.0F);
		this.method_33299();
		this.tail.yaw = 0.5F * h;
	}

	private void method_33295(float f, float g) {
		float h = f * 0.33F;
		float i = MathHelper.sin(h);
		float j = MathHelper.cos(h);
		float k = 0.13F * i;
		this.body.pitch = g * (float) (Math.PI / 180.0) + k;
		this.head.pitch = -k * 1.8F;
		this.body.pivotY -= 0.45F * j;
		this.topGills.pitch = -0.5F * i - 0.8F;
		this.leftGills.yaw = 0.3F * i + 0.9F;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.tail.yaw = 0.3F * MathHelper.cos(h * 0.9F);
		this.leftHindLeg.method_33425(1.8849558F, -0.4F * i, (float) (Math.PI / 2));
		this.leftFrontLeg.method_33425(1.8849558F, -0.2F * j - 0.1F, (float) (Math.PI / 2));
		this.method_33299();
	}

	private void method_33298() {
		this.leftHindLeg.method_33425(1.4137167F, 1.0995574F, (float) (Math.PI / 4));
		this.leftFrontLeg.method_33425((float) (Math.PI / 4), 2.042035F, 0.0F);
		this.body.pitch = -0.15F;
		this.body.roll = 0.35F;
		this.method_33299();
	}

	private void method_33299() {
		this.rightHindLeg.method_33425(this.leftHindLeg.pitch, -this.leftHindLeg.yaw, -this.leftHindLeg.roll);
		this.rightFrontLeg.method_33425(this.leftFrontLeg.pitch, -this.leftFrontLeg.yaw, -this.leftFrontLeg.roll);
	}
}
