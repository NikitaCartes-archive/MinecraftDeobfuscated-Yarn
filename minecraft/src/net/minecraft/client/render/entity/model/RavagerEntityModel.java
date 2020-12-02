package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RavagerEntityModel extends SinglePartEntityModel<RavagerEntity> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart neck;

	public RavagerEntityModel(ModelPart root) {
		this.root = root;
		this.neck = root.getChild("neck");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("mouth");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 16;
		ModelPartData modelPartData2 = modelPartData.addChild(
			"neck", ModelPartBuilder.create().uv(68, 73).cuboid(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), ModelTransform.pivot(0.0F, -7.0F, 5.5F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			"head",
			ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F).uv(0, 0).cuboid(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F),
			ModelTransform.pivot(0.0F, 16.0F, -17.0F)
		);
		modelPartData3.addChild(
			"right_horn",
			ModelPartBuilder.create().uv(74, 55).cuboid(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
			ModelTransform.of(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"left_horn",
			ModelPartBuilder.create().uv(74, 55).mirrored().cuboid(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
			ModelTransform.of(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
		);
		modelPartData3.addChild("mouth", ModelPartBuilder.create().uv(0, 36).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), ModelTransform.pivot(0.0F, -2.0F, 2.0F));
		modelPartData.addChild(
			"body",
			ModelPartBuilder.create().uv(0, 55).cuboid(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F).uv(0, 91).cuboid(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F),
			ModelTransform.of(0.0F, 1.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_hind_leg", ModelPartBuilder.create().uv(96, 0).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), ModelTransform.pivot(-8.0F, -13.0F, 18.0F)
		);
		modelPartData.addChild(
			"left_hind_leg", ModelPartBuilder.create().uv(96, 0).mirrored().cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), ModelTransform.pivot(8.0F, -13.0F, 18.0F)
		);
		modelPartData.addChild(
			"right_front_leg", ModelPartBuilder.create().uv(64, 0).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), ModelTransform.pivot(-8.0F, -13.0F, -5.0F)
		);
		modelPartData.addChild(
			"left_front_leg", ModelPartBuilder.create().uv(64, 0).mirrored().cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), ModelTransform.pivot(8.0F, -13.0F, -5.0F)
		);
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		float k = 0.4F * g;
		this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * k;
		this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * k;
		this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * k;
		this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * k;
	}

	public void animateModel(RavagerEntity ravagerEntity, float f, float g, float h) {
		super.animateModel(ravagerEntity, f, g, h);
		int i = ravagerEntity.getStunTick();
		int j = ravagerEntity.getRoarTick();
		int k = 20;
		int l = ravagerEntity.getAttackTick();
		int m = 10;
		if (l > 0) {
			float n = MathHelper.wrap((float)l - h, 10.0F);
			float o = (1.0F + n) * 0.5F;
			float p = o * o * o * 12.0F;
			float q = p * MathHelper.sin(this.neck.pitch);
			this.neck.pivotZ = -6.5F + p;
			this.neck.pivotY = -7.0F - q;
			float r = MathHelper.sin(((float)l - h) / 10.0F * (float) Math.PI * 0.25F);
			this.jaw.pitch = (float) (Math.PI / 2) * r;
			if (l > 5) {
				this.jaw.pitch = MathHelper.sin(((float)(-4 + l) - h) / 4.0F) * (float) Math.PI * 0.4F;
			} else {
				this.jaw.pitch = (float) (Math.PI / 20) * MathHelper.sin((float) Math.PI * ((float)l - h) / 10.0F);
			}
		} else {
			float n = -1.0F;
			float o = -1.0F * MathHelper.sin(this.neck.pitch);
			this.neck.pivotX = 0.0F;
			this.neck.pivotY = -7.0F - o;
			this.neck.pivotZ = 5.5F;
			boolean bl = i > 0;
			this.neck.pitch = bl ? 0.21991149F : 0.0F;
			this.jaw.pitch = (float) Math.PI * (bl ? 0.05F : 0.01F);
			if (bl) {
				double d = (double)i / 40.0;
				this.neck.pivotX = (float)Math.sin(d * 10.0) * 3.0F;
			} else if (j > 0) {
				float q = MathHelper.sin(((float)(20 - j) - h) / 20.0F * (float) Math.PI * 0.25F);
				this.jaw.pitch = (float) (Math.PI / 2) * q;
			}
		}
	}
}
