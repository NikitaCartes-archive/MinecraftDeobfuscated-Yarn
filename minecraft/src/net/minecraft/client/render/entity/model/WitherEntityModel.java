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
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a {@linkplain WitherEntity}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@code shoulders}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #RIBCAGE}</td><td>{@linkplain #root Root part}</td><td>{@link #ribcage}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#TAIL}</td><td>{@linkplain #root Root part}</td><td>{@link #tail}</td>
 * </tr>
 * <tr>
 *   <td>{@value #CENTER_HEAD}</td><td>{@linkplain #root Root part}</td><td>{@link #centerHead}</td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT_HEAD}</td><td>{@linkplain #root Root part}</td><td>{@link #rightHead}</td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT_HEAD}</td><td>{@linkplain #root Root part}</td><td>{@link #leftHead}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity> extends SinglePartEntityModel<T> {
	/**
	 * The key of the ribcage model part, whose value is {@value}.
	 */
	private static final String RIBCAGE = "ribcage";
	/**
	 * The key of the center head model part, whose value is {@value}.
	 */
	private static final String CENTER_HEAD = "center_head";
	/**
	 * The key of the right head model part, whose value is {@value}.
	 */
	private static final String RIGHT_HEAD = "right_head";
	/**
	 * The key of the left head model part, whose value is {@value}.
	 */
	private static final String LEFT_HEAD = "left_head";
	private static final float RIBCAGE_PITCH_OFFSET = 0.065F;
	private static final float TAIL_PITCH_OFFSET = 0.265F;
	private final ModelPart root;
	private final ModelPart centerHead;
	private final ModelPart rightHead;
	private final ModelPart leftHead;
	private final ModelPart ribcage;
	private final ModelPart tail;

	public WitherEntityModel(ModelPart root) {
		this.root = root;
		this.ribcage = root.getChild("ribcage");
		this.tail = root.getChild(EntityModelPartNames.TAIL);
		this.centerHead = root.getChild("center_head");
		this.rightHead = root.getChild("right_head");
		this.leftHead = root.getChild("left_head");
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("shoulders", ModelPartBuilder.create().uv(0, 16).cuboid(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, dilation), ModelTransform.NONE);
		float f = 0.20420352F;
		modelPartData.addChild(
			"ribcage",
			ModelPartBuilder.create()
				.uv(0, 22)
				.cuboid(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, dilation)
				.uv(24, 22)
				.cuboid(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, dilation)
				.uv(24, 22)
				.cuboid(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, dilation)
				.uv(24, 22)
				.cuboid(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, dilation),
			ModelTransform.of(-2.0F, 6.9F, -0.5F, 0.20420352F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(12, 22).cuboid(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, dilation),
			ModelTransform.of(-2.0F, 6.9F + MathHelper.cos(0.20420352F) * 10.0F, -0.5F + MathHelper.sin(0.20420352F) * 10.0F, 0.83252203F, 0.0F, 0.0F)
		);
		modelPartData.addChild("center_head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.NONE);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, dilation);
		modelPartData.addChild("right_head", modelPartBuilder, ModelTransform.pivot(-8.0F, 4.0F, 0.0F));
		modelPartData.addChild("left_head", modelPartBuilder, ModelTransform.pivot(10.0F, 4.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(T witherEntity, float f, float g, float h, float i, float j) {
		float k = MathHelper.cos(h * 0.1F);
		this.ribcage.pitch = (0.065F + 0.05F * k) * (float) Math.PI;
		this.tail.setPivot(-2.0F, 6.9F + MathHelper.cos(this.ribcage.pitch) * 10.0F, -0.5F + MathHelper.sin(this.ribcage.pitch) * 10.0F);
		this.tail.pitch = (0.265F + 0.1F * k) * (float) Math.PI;
		this.centerHead.yaw = i * (float) (Math.PI / 180.0);
		this.centerHead.pitch = j * (float) (Math.PI / 180.0);
	}

	public void animateModel(T witherEntity, float f, float g, float h) {
		rotateHead(witherEntity, this.rightHead, 0);
		rotateHead(witherEntity, this.leftHead, 1);
	}

	private static <T extends WitherEntity> void rotateHead(T entity, ModelPart head, int sigma) {
		head.yaw = (entity.getHeadYaw(sigma) - entity.bodyYaw) * (float) (Math.PI / 180.0);
		head.pitch = entity.getHeadPitch(sigma) * (float) (Math.PI / 180.0);
	}
}
