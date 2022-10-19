package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a {@linkplain BoatEntity}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value #BOTTOM}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #BACK}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #FRONT}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT_PADDLE}</td><td>Root part</td><td>{@link #leftPaddle}</td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT_PADDLE}</td><td>Root part</td><td>{@link #rightPaddle}</td>
 * </tr>
 * <tr>
 *   <td>{@value #WATER_PATCH}</td><td>Root part</td><td>{@link #waterPatch}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class BoatEntityModel extends CompositeEntityModel<BoatEntity> implements ModelWithWaterPatch {
	/**
	 * The key of the left paddle model part, whose value is {@value}.
	 */
	private static final String LEFT_PADDLE = "left_paddle";
	/**
	 * The key of the right paddle model part, whose value is {@value}.
	 */
	private static final String RIGHT_PADDLE = "right_paddle";
	/**
	 * The key of the water patch model part, whose value is {@value}.
	 */
	private static final String WATER_PATCH = "water_patch";
	/**
	 * The key of the bottom model part, whose value is {@value}.
	 */
	private static final String BOTTOM = "bottom";
	/**
	 * The key of the back model part, whose value is {@value}.
	 */
	private static final String BACK = "back";
	/**
	 * The key of the front model part, whose value is {@value}.
	 */
	private static final String FRONT = "front";
	/**
	 * The key of the right model part, whose value is {@value}.
	 */
	private static final String RIGHT = "right";
	/**
	 * The key of the left model part, whose value is {@value}.
	 */
	private static final String LEFT = "left";
	private final ModelPart leftPaddle;
	private final ModelPart rightPaddle;
	private final ModelPart waterPatch;
	private final ImmutableList<ModelPart> parts;

	public BoatEntityModel(ModelPart root) {
		this.leftPaddle = root.getChild("left_paddle");
		this.rightPaddle = root.getChild("right_paddle");
		this.waterPatch = root.getChild("water_patch");
		this.parts = this.getParts(root).build();
	}

	protected Builder<ModelPart> getParts(ModelPart root) {
		Builder<ModelPart> builder = new Builder<>();
		builder.add(
			root.getChild("bottom"), root.getChild("back"), root.getChild("front"), root.getChild("right"), root.getChild("left"), this.leftPaddle, this.rightPaddle
		);
		return builder;
	}

	public static void addParts(ModelPartData modelPartData) {
		int i = 32;
		int j = 6;
		int k = 20;
		int l = 4;
		int m = 28;
		modelPartData.addChild(
			"bottom",
			ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
			ModelTransform.of(0.0F, 3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"back",
			ModelPartBuilder.create().uv(0, 19).cuboid(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F),
			ModelTransform.of(-15.0F, 4.0F, 4.0F, 0.0F, (float) (Math.PI * 3.0 / 2.0), 0.0F)
		);
		modelPartData.addChild(
			"front",
			ModelPartBuilder.create().uv(0, 27).cuboid(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F),
			ModelTransform.of(15.0F, 4.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		modelPartData.addChild(
			"right",
			ModelPartBuilder.create().uv(0, 35).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
			ModelTransform.of(0.0F, 4.0F, -9.0F, 0.0F, (float) Math.PI, 0.0F)
		);
		modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 43).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), ModelTransform.pivot(0.0F, 4.0F, 9.0F));
		int n = 20;
		int o = 7;
		int p = 6;
		float f = -5.0F;
		modelPartData.addChild(
			"left_paddle",
			ModelPartBuilder.create().uv(62, 0).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			ModelTransform.of(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, (float) (Math.PI / 16))
		);
		modelPartData.addChild(
			"right_paddle",
			ModelPartBuilder.create().uv(62, 20).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
			ModelTransform.of(3.0F, -5.0F, -9.0F, 0.0F, (float) Math.PI, (float) (Math.PI / 16))
		);
		modelPartData.addChild(
			"water_patch",
			ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
			ModelTransform.of(0.0F, -3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		addParts(modelPartData);
		return TexturedModelData.of(modelData, 128, 64);
	}

	public void setAngles(BoatEntity boatEntity, float f, float g, float h, float i, float j) {
		setPaddleAngle(boatEntity, 0, this.leftPaddle, f);
		setPaddleAngle(boatEntity, 1, this.rightPaddle, f);
	}

	public ImmutableList<ModelPart> getParts() {
		return this.parts;
	}

	@Override
	public ModelPart getWaterPatch() {
		return this.waterPatch;
	}

	private static void setPaddleAngle(BoatEntity entity, int sigma, ModelPart part, float angle) {
		float f = entity.interpolatePaddlePhase(sigma, angle);
		part.pitch = MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (MathHelper.sin(-f) + 1.0F) / 2.0F);
		part.yaw = MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F);
		if (sigma == 1) {
			part.yaw = (float) Math.PI - part.yaw;
		}
	}
}
