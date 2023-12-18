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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.BreezeAnimations;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BreezeEntityModel<T extends BreezeEntity> extends SinglePartEntityModel<T> {
	private static final float field_47431 = 0.6F;
	private static final float field_47432 = 0.8F;
	private static final float field_47433 = 1.0F;
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart eyes;
	private final ModelPart windBody;
	private final ModelPart windTop;
	private final ModelPart windMid;
	private final ModelPart windBottom;
	private final ModelPart rods;

	public BreezeEntityModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucent);
		this.root = root;
		this.windBody = root.getChild(EntityModelPartNames.WIND_BODY);
		this.windBottom = this.windBody.getChild(EntityModelPartNames.WIND_BOTTOM);
		this.windMid = this.windBottom.getChild(EntityModelPartNames.WIND_MID);
		this.windTop = this.windMid.getChild(EntityModelPartNames.WIND_TOP);
		this.head = root.getChild(EntityModelPartNames.BODY).getChild(EntityModelPartNames.HEAD);
		this.eyes = this.head.getChild(EntityModelPartNames.EYES);
		this.rods = root.getChild(EntityModelPartNames.BODY).getChild(EntityModelPartNames.RODS);
	}

	public static TexturedModelData getTexturedModelData(int textureWidth, int textureHeight) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.RODS, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		modelPartData3.addChild(
			"rod_1",
			ModelPartBuilder.create().uv(0, 17).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.of(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F)
		);
		modelPartData3.addChild(
			"rod_2",
			ModelPartBuilder.create().uv(0, 17).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.of(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F)
		);
		modelPartData3.addChild(
			"rod_3",
			ModelPartBuilder.create().uv(0, 17).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.of(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData2.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(4, 24)
				.cuboid(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 0)
				.cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 4.0F, 0.0F)
		);
		modelPartData4.addChild(
			EntityModelPartNames.EYES,
			ModelPartBuilder.create()
				.uv(4, 24)
				.cuboid(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 0)
				.cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData5 = modelPartData.addChild(EntityModelPartNames.WIND_BODY, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData modelPartData6 = modelPartData5.addChild(
			EntityModelPartNames.WIND_BOTTOM,
			ModelPartBuilder.create().uv(1, 83).cuboid(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 24.0F, 0.0F)
		);
		ModelPartData modelPartData7 = modelPartData6.addChild(
			EntityModelPartNames.WIND_MID,
			ModelPartBuilder.create()
				.uv(74, 28)
				.cuboid(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new Dilation(0.0F))
				.uv(78, 32)
				.cuboid(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new Dilation(0.0F))
				.uv(49, 71)
				.cuboid(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, -7.0F, 0.0F)
		);
		modelPartData7.addChild(
			EntityModelPartNames.WIND_TOP,
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new Dilation(0.0F))
				.uv(6, 6)
				.cuboid(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new Dilation(0.0F))
				.uv(105, 57)
				.cuboid(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, -6.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, textureWidth, textureHeight);
	}

	public void setAngles(T breezeEntity, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		float k = h * (float) Math.PI * -0.1F;
		this.windTop.pivotX = MathHelper.cos(k) * 1.0F * 0.6F;
		this.windTop.pivotZ = MathHelper.sin(k) * 1.0F * 0.6F;
		this.windMid.pivotX = MathHelper.sin(k) * 0.5F * 0.8F;
		this.windMid.pivotZ = MathHelper.cos(k) * 0.8F;
		this.windBottom.pivotX = MathHelper.cos(k) * -0.25F * 1.0F;
		this.windBottom.pivotZ = MathHelper.sin(k) * -0.25F * 1.0F;
		this.head.pivotY = 4.0F + MathHelper.cos(k) / 4.0F;
		this.rods.yaw = h * (float) Math.PI * 0.1F;
		this.updateAnimation(breezeEntity.shootingAnimationState, BreezeAnimations.SHOOTING, h);
		this.updateAnimation(breezeEntity.slidingAnimationState, BreezeAnimations.SLIDING, h);
		this.updateAnimation(breezeEntity.field_47816, BreezeAnimations.field_47846, h);
		this.updateAnimation(breezeEntity.inhalingAnimationState, BreezeAnimations.INHALING, h);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public ModelPart getHead() {
		return this.head;
	}

	public ModelPart getEyes() {
		return this.eyes;
	}

	public ModelPart getRods() {
		return this.rods;
	}

	public ModelPart getWindBody() {
		return this.windBody;
	}
}
