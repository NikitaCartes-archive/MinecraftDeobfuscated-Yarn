package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.entity.boss.dragon.EnderDragonFrameTracker;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DragonEntityModel extends EntityModel<EnderDragonEntityRenderState> {
	private static final int NUM_NECK_PARTS = 5;
	private static final int NUM_TAIL_PARTS = 12;
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart[] neckParts = new ModelPart[5];
	private final ModelPart[] tailParts = new ModelPart[12];
	private final ModelPart jaw;
	private final ModelPart body;
	private final ModelPart leftWing;
	private final ModelPart leftWingTip;
	private final ModelPart leftFrontLeg;
	private final ModelPart leftFrontLegTip;
	private final ModelPart leftFrontFoot;
	private final ModelPart leftHindLeg;
	private final ModelPart leftHindLegTip;
	private final ModelPart leftHindFoot;
	private final ModelPart rightWing;
	private final ModelPart rightWingTip;
	private final ModelPart rightFrontLeg;
	private final ModelPart rightFrontLegTip;
	private final ModelPart rightFrontFoot;
	private final ModelPart rightHindLeg;
	private final ModelPart rightHindLegTip;
	private final ModelPart rightHindFoot;

	private static String neck(int id) {
		return "neck" + id;
	}

	private static String tail(int id) {
		return "tail" + id;
	}

	public DragonEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.jaw = this.head.getChild(EntityModelPartNames.JAW);

		for (int i = 0; i < this.neckParts.length; i++) {
			this.neckParts[i] = root.getChild(neck(i));
		}

		for (int i = 0; i < this.tailParts.length; i++) {
			this.tailParts[i] = root.getChild(tail(i));
		}

		this.body = root.getChild(EntityModelPartNames.BODY);
		this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
		this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
		this.leftFrontLeg = this.body.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.leftFrontLegTip = this.leftFrontLeg.getChild(EntityModelPartNames.LEFT_FRONT_LEG_TIP);
		this.leftFrontFoot = this.leftFrontLegTip.getChild(EntityModelPartNames.LEFT_FRONT_FOOT);
		this.leftHindLeg = this.body.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.leftHindLegTip = this.leftHindLeg.getChild(EntityModelPartNames.LEFT_HIND_LEG_TIP);
		this.leftHindFoot = this.leftHindLegTip.getChild(EntityModelPartNames.LEFT_HIND_FOOT);
		this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
		this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
		this.rightFrontLeg = this.body.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.rightFrontLegTip = this.rightFrontLeg.getChild(EntityModelPartNames.RIGHT_FRONT_LEG_TIP);
		this.rightFrontFoot = this.rightFrontLegTip.getChild(EntityModelPartNames.RIGHT_FRONT_FOOT);
		this.rightHindLeg = this.body.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.rightHindLegTip = this.rightHindLeg.getChild(EntityModelPartNames.RIGHT_HIND_LEG_TIP);
		this.rightHindFoot = this.rightHindLegTip.getChild(EntityModelPartNames.RIGHT_HIND_FOOT);
	}

	public static TexturedModelData createTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = -16.0F;
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.cuboid("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44)
				.cuboid("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30)
				.mirrored()
				.cuboid("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.cuboid("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0)
				.mirrored()
				.cuboid("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.cuboid("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0),
			ModelTransform.pivot(0.0F, 20.0F, -62.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.JAW,
			ModelPartBuilder.create().cuboid(EntityModelPartNames.JAW, -6.0F, 0.0F, -16.0F, 12, 4, 16, 176, 65),
			ModelTransform.pivot(0.0F, 4.0F, -8.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create()
			.cuboid("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 192, 104)
			.cuboid("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 48, 0);

		for (int i = 0; i < 5; i++) {
			modelPartData.addChild(neck(i), modelPartBuilder, ModelTransform.pivot(0.0F, 20.0F, -12.0F - (float)i * 10.0F));
		}

		for (int i = 0; i < 12; i++) {
			modelPartData.addChild(tail(i), modelPartBuilder, ModelTransform.pivot(0.0F, 10.0F, 60.0F + (float)i * 10.0F));
		}

		ModelPartData modelPartData3 = modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.cuboid(EntityModelPartNames.BODY, -12.0F, 1.0F, -16.0F, 24, 24, 64, 0, 0)
				.cuboid("scale", -1.0F, -5.0F, -10.0F, 2, 6, 12, 220, 53)
				.cuboid("scale", -1.0F, -5.0F, 10.0F, 2, 6, 12, 220, 53)
				.cuboid("scale", -1.0F, -5.0F, 30.0F, 2, 6, 12, 220, 53),
			ModelTransform.pivot(0.0F, 3.0F, 8.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			EntityModelPartNames.LEFT_WING,
			ModelPartBuilder.create()
				.mirrored()
				.cuboid(EntityModelPartNames.BONE, 0.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88)
				.cuboid("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
			ModelTransform.pivot(12.0F, 2.0F, -6.0F)
		);
		modelPartData4.addChild(
			EntityModelPartNames.LEFT_WING_TIP,
			ModelPartBuilder.create()
				.mirrored()
				.cuboid(EntityModelPartNames.BONE, 0.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136)
				.cuboid("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
			ModelTransform.pivot(56.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData5 = modelPartData3.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().cuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104),
			ModelTransform.of(12.0F, 17.0F, -6.0F, 1.3F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData6 = modelPartData5.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138),
			ModelTransform.of(0.0F, 20.0F, -1.0F, -0.5F, 0.0F, 0.0F)
		);
		modelPartData6.addChild(
			EntityModelPartNames.LEFT_FRONT_FOOT,
			ModelPartBuilder.create().cuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104),
			ModelTransform.of(0.0F, 23.0F, 0.0F, 0.75F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData7 = modelPartData3.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().cuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0),
			ModelTransform.of(16.0F, 13.0F, 34.0F, 1.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData8 = modelPartData7.addChild(
			EntityModelPartNames.LEFT_HIND_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0),
			ModelTransform.of(0.0F, 32.0F, -4.0F, 0.5F, 0.0F, 0.0F)
		);
		modelPartData8.addChild(
			EntityModelPartNames.LEFT_HIND_FOOT,
			ModelPartBuilder.create().cuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0),
			ModelTransform.of(0.0F, 31.0F, 4.0F, 0.75F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData9 = modelPartData3.addChild(
			EntityModelPartNames.RIGHT_WING,
			ModelPartBuilder.create().cuboid(EntityModelPartNames.BONE, -56.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).cuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
			ModelTransform.pivot(-12.0F, 2.0F, -6.0F)
		);
		modelPartData9.addChild(
			EntityModelPartNames.RIGHT_WING_TIP,
			ModelPartBuilder.create()
				.cuboid(EntityModelPartNames.BONE, -56.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136)
				.cuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
			ModelTransform.pivot(-56.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData10 = modelPartData3.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().cuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104),
			ModelTransform.of(-12.0F, 17.0F, -6.0F, 1.3F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData11 = modelPartData10.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138),
			ModelTransform.of(0.0F, 20.0F, -1.0F, -0.5F, 0.0F, 0.0F)
		);
		modelPartData11.addChild(
			EntityModelPartNames.RIGHT_FRONT_FOOT,
			ModelPartBuilder.create().cuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104),
			ModelTransform.of(0.0F, 23.0F, 0.0F, 0.75F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData12 = modelPartData3.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().cuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0),
			ModelTransform.of(-16.0F, 13.0F, 34.0F, 1.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData13 = modelPartData12.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0),
			ModelTransform.of(0.0F, 32.0F, -4.0F, 0.5F, 0.0F, 0.0F)
		);
		modelPartData13.addChild(
			EntityModelPartNames.RIGHT_HIND_FOOT,
			ModelPartBuilder.create().cuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0),
			ModelTransform.of(0.0F, 31.0F, 4.0F, 0.75F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 256, 256);
	}

	public void setAngles(EnderDragonEntityRenderState enderDragonEntityRenderState) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		float f = enderDragonEntityRenderState.wingPosition * (float) (Math.PI * 2);
		this.jaw.pitch = (MathHelper.sin(f) + 1.0F) * 0.2F;
		float g = MathHelper.sin(f - 1.0F) + 1.0F;
		g = (g * g + g * 2.0F) * 0.05F;
		this.root.pivotY = (g - 2.0F) * 16.0F;
		this.root.pivotZ = -48.0F;
		this.root.pitch = g * 2.0F * (float) (Math.PI / 180.0);
		float h = this.neckParts[0].pivotX;
		float i = this.neckParts[0].pivotY;
		float j = this.neckParts[0].pivotZ;
		float k = 1.5F;
		EnderDragonFrameTracker.Frame frame = enderDragonEntityRenderState.getLerpedFrame(6);
		float l = MathHelper.wrapDegrees(enderDragonEntityRenderState.getLerpedFrame(5).yRot() - enderDragonEntityRenderState.getLerpedFrame(10).yRot());
		float m = MathHelper.wrapDegrees(enderDragonEntityRenderState.getLerpedFrame(5).yRot() + l / 2.0F);

		for (int n = 0; n < 5; n++) {
			ModelPart modelPart = this.neckParts[n];
			EnderDragonFrameTracker.Frame frame2 = enderDragonEntityRenderState.getLerpedFrame(5 - n);
			float o = MathHelper.cos((float)n * 0.45F + f) * 0.15F;
			modelPart.yaw = MathHelper.wrapDegrees(frame2.yRot() - frame.yRot()) * (float) (Math.PI / 180.0) * 1.5F;
			modelPart.pitch = o + enderDragonEntityRenderState.getNeckPartPitchOffset(n, frame, frame2) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			modelPart.roll = -MathHelper.wrapDegrees(frame2.yRot() - m) * (float) (Math.PI / 180.0) * 1.5F;
			modelPart.pivotY = i;
			modelPart.pivotZ = j;
			modelPart.pivotX = h;
			h -= MathHelper.sin(modelPart.yaw) * MathHelper.cos(modelPart.pitch) * 10.0F;
			i += MathHelper.sin(modelPart.pitch) * 10.0F;
			j -= MathHelper.cos(modelPart.yaw) * MathHelper.cos(modelPart.pitch) * 10.0F;
		}

		this.head.pivotY = i;
		this.head.pivotZ = j;
		this.head.pivotX = h;
		EnderDragonFrameTracker.Frame frame3 = enderDragonEntityRenderState.getLerpedFrame(0);
		this.head.yaw = MathHelper.wrapDegrees(frame3.yRot() - frame.yRot()) * (float) (Math.PI / 180.0);
		this.head.pitch = MathHelper.wrapDegrees(enderDragonEntityRenderState.getNeckPartPitchOffset(6, frame, frame3)) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
		this.head.roll = -MathHelper.wrapDegrees(frame3.yRot() - m) * (float) (Math.PI / 180.0);
		this.body.roll = -l * 1.5F * (float) (Math.PI / 180.0);
		this.leftWing.pitch = 0.125F - MathHelper.cos(f) * 0.2F;
		this.leftWing.yaw = -0.25F;
		this.leftWing.roll = -(MathHelper.sin(f) + 0.125F) * 0.8F;
		this.leftWingTip.roll = (MathHelper.sin(f + 2.0F) + 0.5F) * 0.75F;
		this.rightWing.pitch = this.leftWing.pitch;
		this.rightWing.yaw = -this.leftWing.yaw;
		this.rightWing.roll = -this.leftWing.roll;
		this.rightWingTip.roll = -this.leftWingTip.roll;
		this.setLegAngles(g, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftHindLeg, this.leftHindLegTip, this.leftHindFoot);
		this.setLegAngles(g, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightHindLeg, this.rightHindLegTip, this.rightHindFoot);
		float p = 0.0F;
		i = this.tailParts[0].pivotY;
		j = this.tailParts[0].pivotZ;
		h = this.tailParts[0].pivotX;
		frame = enderDragonEntityRenderState.getLerpedFrame(11);

		for (int q = 0; q < 12; q++) {
			EnderDragonFrameTracker.Frame frame4 = enderDragonEntityRenderState.getLerpedFrame(12 + q);
			p += MathHelper.sin((float)q * 0.45F + f) * 0.05F;
			ModelPart modelPart2 = this.tailParts[q];
			modelPart2.yaw = (MathHelper.wrapDegrees(frame4.yRot() - frame.yRot()) * 1.5F + 180.0F) * (float) (Math.PI / 180.0);
			modelPart2.pitch = p + (float)(frame4.y() - frame.y()) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			modelPart2.roll = MathHelper.wrapDegrees(frame4.yRot() - m) * (float) (Math.PI / 180.0) * 1.5F;
			modelPart2.pivotY = i;
			modelPart2.pivotZ = j;
			modelPart2.pivotX = h;
			i += MathHelper.sin(modelPart2.pitch) * 10.0F;
			j -= MathHelper.cos(modelPart2.yaw) * MathHelper.cos(modelPart2.pitch) * 10.0F;
			h -= MathHelper.sin(modelPart2.yaw) * MathHelper.cos(modelPart2.pitch) * 10.0F;
		}
	}

	private void setLegAngles(
		float offset, ModelPart frontLeg, ModelPart frontLegTip, ModelPart frontFoot, ModelPart hindLeg, ModelPart hindLegTip, ModelPart hindFoot
	) {
		hindLeg.pitch = 1.0F + offset * 0.1F;
		hindLegTip.pitch = 0.5F + offset * 0.1F;
		hindFoot.pitch = 0.75F + offset * 0.1F;
		frontLeg.pitch = 1.3F + offset * 0.1F;
		frontLegTip.pitch = -0.5F - offset * 0.1F;
		frontFoot.pitch = 0.75F + offset * 0.1F;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
