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
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of an ocelot-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value #TAIL1}</td><td>Root part</td><td>{@link #upperTail}</td>
 * </tr>
 * <tr>
 *   <td>{@value #TAIL2}</td><td>Root part</td><td>{@link #lowerTail}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>Root part</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>Root part</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>Root part</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>Root part</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends AnimalModel<T> {
	private static final int SNEAKING_ANIMATION_STATE = 0;
	private static final int STANDING_ANIMATION_STATE = 1;
	private static final int SPRINTING_ANIMATION_STATE = 2;
	protected static final int SITTING_ANIMATION_STATE = 3;
	private static final float field_32527 = 0.0F;
	private static final float BODY_SIZE_Y = 16.0F;
	private static final float field_32529 = -9.0F;
	private static final float HEAD_PIVOT_Y = 15.0F;
	private static final float HEAD_PIVOT_Z = -9.0F;
	private static final float BODY_PIVOT_Y = 12.0F;
	private static final float BODY_PIVOT_Z = -10.0F;
	private static final float UPPER_TAIL_PIVOT_Y = 15.0F;
	private static final float UPPER_TAIL_PIVOT_Z = 8.0F;
	private static final float LOWER_TAIL_PIVOT_Y = 20.0F;
	private static final float LOWER_TAIL_PIVOT_Z = 14.0F;
	protected static final float HIND_LEG_PIVOT_Y = 18.0F;
	protected static final float HIND_LEG_PIVOT_Z = 5.0F;
	protected static final float FRONT_LEG_PIVOT_Y = 14.1F;
	private static final float FRONT_LEG_PIVOT_Z = -5.0F;
	/**
	 * The key of the upper tail model part, whose value is {@value}.
	 */
	private static final String TAIL1 = "tail1";
	/**
	 * The key of the lower tail model part, whose value is {@value}.
	 */
	private static final String TAIL2 = "tail2";
	protected final ModelPart leftHindLeg;
	protected final ModelPart rightHindLeg;
	protected final ModelPart leftFrontLeg;
	protected final ModelPart rightFrontLeg;
	protected final ModelPart upperTail;
	protected final ModelPart lowerTail;
	protected final ModelPart head;
	protected final ModelPart body;
	protected int animationState = 1;

	public OcelotEntityModel(ModelPart root) {
		super(true, 10.0F, 4.0F);
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.upperTail = root.getChild("tail1");
		this.lowerTail = root.getChild("tail2");
		this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
	}

	public static ModelData getModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		Dilation dilation2 = new Dilation(-0.02F);
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.cuboid("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, dilation)
				.cuboid(EntityModelPartNames.NOSE, -1.5F, -0.001F, -4.0F, 3, 2, 2, dilation, 0, 24)
				.cuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 0, 10)
				.cuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 6, 10),
			ModelTransform.pivot(0.0F, 15.0F, -9.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(20, 0).cuboid(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, dilation),
			ModelTransform.of(0.0F, 12.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"tail1", ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation), ModelTransform.of(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"tail2", ModelPartBuilder.create().uv(4, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation2), ModelTransform.pivot(0.0F, 20.0F, 14.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(8, 13).cuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(1.1F, 18.0F, 5.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-1.1F, 18.0F, 5.0F));
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(1.2F, 14.1F, -5.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(-1.2F, 14.1F, -5.0F));
		return modelData;
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body, this.leftHindLeg, this.rightHindLeg, this.leftFrontLeg, this.rightFrontLeg, this.upperTail, this.lowerTail);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		if (this.animationState != 3) {
			this.body.pitch = (float) (Math.PI / 2);
			if (this.animationState == 2) {
				this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 0.3F) * limbDistance;
				this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI + 0.3F) * limbDistance;
				this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(limbAngle) * limbDistance;
			} else {
				this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				if (this.animationState == 1) {
					this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(limbAngle) * limbDistance;
				} else {
					this.lowerTail.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(limbAngle) * limbDistance;
				}
			}
		}
	}

	@Override
	public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
		this.body.pivotY = 12.0F;
		this.body.pivotZ = -10.0F;
		this.head.pivotY = 15.0F;
		this.head.pivotZ = -9.0F;
		this.upperTail.pivotY = 15.0F;
		this.upperTail.pivotZ = 8.0F;
		this.lowerTail.pivotY = 20.0F;
		this.lowerTail.pivotZ = 14.0F;
		this.leftFrontLeg.pivotY = 14.1F;
		this.leftFrontLeg.pivotZ = -5.0F;
		this.rightFrontLeg.pivotY = 14.1F;
		this.rightFrontLeg.pivotZ = -5.0F;
		this.leftHindLeg.pivotY = 18.0F;
		this.leftHindLeg.pivotZ = 5.0F;
		this.rightHindLeg.pivotY = 18.0F;
		this.rightHindLeg.pivotZ = 5.0F;
		this.upperTail.pitch = 0.9F;
		if (entity.isInSneakingPose()) {
			this.body.pivotY++;
			this.head.pivotY += 2.0F;
			this.upperTail.pivotY++;
			this.lowerTail.pivotY += -4.0F;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
			this.animationState = 0;
		} else if (entity.isSprinting()) {
			this.lowerTail.pivotY = this.upperTail.pivotY;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
			this.animationState = 2;
		} else {
			this.animationState = 1;
		}
	}
}
