package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.animation.SnifferAnimations;
import net.minecraft.entity.passive.SnifferEntity;

@Environment(EnvType.CLIENT)
public class SnifferEntityModel<T extends SnifferEntity> extends SinglePartEntityModelWithChildTransform<T> {
	private static final float LIMB_ANGLE_SCALE = 9.0F;
	private static final float LIMB_DISTANCE_SCALE = 100.0F;
	private final ModelPart root;
	private final ModelPart head;

	public SnifferEntityModel(ModelPart root) {
		super(0.5F, 24.0F);
		this.root = root.getChild(EntityModelPartNames.ROOT);
		this.head = this.root.getChild(EntityModelPartNames.BONE).getChild(EntityModelPartNames.BODY).getChild(EntityModelPartNames.HEAD);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot().addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 5.0F, 0.0F));
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BONE, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(62, 68)
				.cuboid(-12.5F, -14.0F, -20.0F, 25.0F, 29.0F, 40.0F, new Dilation(0.0F))
				.uv(62, 0)
				.cuboid(-12.5F, -14.0F, -20.0F, 25.0F, 24.0F, 40.0F, new Dilation(0.5F))
				.uv(87, 68)
				.cuboid(-12.5F, 12.0F, -20.0F, 25.0F, 0.0F, 40.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(32, 87).cuboid(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-7.5F, 10.0F, -15.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.RIGHT_MID_LEG,
			ModelPartBuilder.create().uv(32, 105).cuboid(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-7.5F, 10.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(32, 123).cuboid(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-7.5F, 10.0F, 15.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(0, 87).cuboid(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(7.5F, 10.0F, -15.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.LEFT_MID_LEG,
			ModelPartBuilder.create().uv(0, 105).cuboid(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(7.5F, 10.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(0, 123).cuboid(-3.5F, -1.0F, -4.0F, 7.0F, 10.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(7.5F, 10.0F, 15.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(8, 15)
				.cuboid(-6.5F, -7.5F, -11.5F, 13.0F, 18.0F, 11.0F, new Dilation(0.0F))
				.uv(8, 4)
				.cuboid(-6.5F, 7.5F, -11.5F, 13.0F, 0.0F, 11.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 6.5F, -19.48F)
		);
		modelPartData4.addChild(
			EntityModelPartNames.LEFT_EAR,
			ModelPartBuilder.create().uv(2, 0).cuboid(0.0F, 0.0F, -3.0F, 1.0F, 19.0F, 7.0F, new Dilation(0.0F)),
			ModelTransform.pivot(6.51F, -7.5F, -4.51F)
		);
		modelPartData4.addChild(
			EntityModelPartNames.RIGHT_EAR,
			ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 19.0F, 7.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-6.51F, -7.5F, -4.51F)
		);
		modelPartData4.addChild(
			EntityModelPartNames.NOSE,
			ModelPartBuilder.create().uv(10, 45).cuboid(-6.5F, -2.0F, -9.0F, 13.0F, 2.0F, 9.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, -4.5F, -11.5F)
		);
		modelPartData4.addChild(
			"lower_beak",
			ModelPartBuilder.create().uv(10, 57).cuboid(-6.5F, -7.0F, -8.0F, 13.0F, 12.0F, 9.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 2.5F, -12.5F)
		);
		return TexturedModelData.of(modelData, 192, 192);
	}

	public void setAngles(T snifferEntity, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		if (snifferEntity.isSearching()) {
			this.animateMovement(SnifferAnimations.SEARCHING, f, g, 9.0F, 100.0F);
		} else {
			this.animateMovement(SnifferAnimations.WALKING, f, g, 9.0F, 100.0F);
		}

		this.updateAnimation(snifferEntity.diggingAnimationState, SnifferAnimations.DIGGING, h);
		this.updateAnimation(snifferEntity.sniffingAnimationState, SnifferAnimations.SNIFFING, h);
		this.updateAnimation(snifferEntity.risingAnimationState, SnifferAnimations.RISING, h);
		this.updateAnimation(snifferEntity.feelingHappyAnimationState, SnifferAnimations.FEELING_HAPPY, h);
		this.updateAnimation(snifferEntity.scentingAnimationState, SnifferAnimations.SCENTING, h);
		if (this.child) {
			this.animate(SnifferAnimations.BABY_GROWTH);
		}
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
