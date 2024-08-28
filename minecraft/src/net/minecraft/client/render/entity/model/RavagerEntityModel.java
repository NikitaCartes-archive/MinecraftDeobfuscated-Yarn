package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.RavagerEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RavagerEntityModel extends EntityModel<RavagerEntityRenderState> {
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart neck;

	public RavagerEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.neck = modelPart.getChild(EntityModelPartNames.NECK);
		this.head = this.neck.getChild(EntityModelPartNames.HEAD);
		this.jaw = this.head.getChild(EntityModelPartNames.MOUTH);
		this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 16;
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.NECK, ModelPartBuilder.create().uv(68, 73).cuboid(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), ModelTransform.pivot(0.0F, -7.0F, 5.5F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F).uv(0, 0).cuboid(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F),
			ModelTransform.pivot(0.0F, 16.0F, -17.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_HORN,
			ModelPartBuilder.create().uv(74, 55).cuboid(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
			ModelTransform.of(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_HORN,
			ModelPartBuilder.create().uv(74, 55).mirrored().cuboid(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
			ModelTransform.of(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.MOUTH, ModelPartBuilder.create().uv(0, 36).cuboid(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), ModelTransform.pivot(0.0F, -2.0F, 2.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 55).cuboid(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F).uv(0, 91).cuboid(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F),
			ModelTransform.of(0.0F, 1.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(96, 0).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			ModelTransform.pivot(-8.0F, -13.0F, 18.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(96, 0).mirrored().cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			ModelTransform.pivot(8.0F, -13.0F, 18.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(64, 0).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			ModelTransform.pivot(-8.0F, -13.0F, -5.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(64, 0).mirrored().cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			ModelTransform.pivot(8.0F, -13.0F, -5.0F)
		);
		return TexturedModelData.of(modelData, 128, 128);
	}

	public void setAngles(RavagerEntityRenderState ravagerEntityRenderState) {
		super.setAngles(ravagerEntityRenderState);
		float f = ravagerEntityRenderState.stunTick;
		float g = ravagerEntityRenderState.attackTick;
		int i = 10;
		if (g > 0.0F) {
			float h = MathHelper.wrap(g, 10.0F);
			float j = (1.0F + h) * 0.5F;
			float k = j * j * j * 12.0F;
			float l = k * MathHelper.sin(this.neck.pitch);
			this.neck.pivotZ = -6.5F + k;
			this.neck.pivotY = -7.0F - l;
			if (g > 5.0F) {
				this.jaw.pitch = MathHelper.sin((-4.0F + g) / 4.0F) * (float) Math.PI * 0.4F;
			} else {
				this.jaw.pitch = (float) (Math.PI / 20) * MathHelper.sin((float) Math.PI * g / 10.0F);
			}
		} else {
			float h = -1.0F;
			float j = -1.0F * MathHelper.sin(this.neck.pitch);
			this.neck.pivotX = 0.0F;
			this.neck.pivotY = -7.0F - j;
			this.neck.pivotZ = 5.5F;
			boolean bl = f > 0.0F;
			this.neck.pitch = bl ? 0.21991149F : 0.0F;
			this.jaw.pitch = (float) Math.PI * (bl ? 0.05F : 0.01F);
			if (bl) {
				double d = (double)f / 40.0;
				this.neck.pivotX = (float)Math.sin(d * 10.0) * 3.0F;
			} else if ((double)ravagerEntityRenderState.roarTick > 0.0) {
				float l = MathHelper.sin(ravagerEntityRenderState.roarTick * (float) Math.PI * 0.25F);
				this.jaw.pitch = (float) (Math.PI / 2) * l;
			}
		}

		this.head.pitch = ravagerEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.head.yaw = ravagerEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		float hx = ravagerEntityRenderState.limbFrequency;
		float jx = 0.4F * ravagerEntityRenderState.limbAmplitudeMultiplier;
		this.rightHindLeg.pitch = MathHelper.cos(hx * 0.6662F) * jx;
		this.leftHindLeg.pitch = MathHelper.cos(hx * 0.6662F + (float) Math.PI) * jx;
		this.rightFrontLeg.pitch = MathHelper.cos(hx * 0.6662F + (float) Math.PI) * jx;
		this.leftFrontLeg.pitch = MathHelper.cos(hx * 0.6662F) * jx;
	}
}
