package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a villager resembling entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>{@linkplain #root Root part}</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HAT}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #hat}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HAT_RIM}</td><td>{@value EntityModelPartNames#HAT}</td><td>{@link #hatRim}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#NOSE}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #nose}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#JACKET}</td><td>{@value EntityModelPartNames#BODY}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#ARMS}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #rightLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #leftLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity> extends SinglePartEntityModel<T> implements ModelWithHead, ModelWithHat {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart hatRim;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	protected final ModelPart nose;

	public VillagerResemblingModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.hat = this.head.getChild(EntityModelPartNames.HAT);
		this.hatRim = this.hat.getChild(EntityModelPartNames.HAT_RIM);
		this.nose = this.head.getChild(EntityModelPartNames.NOSE);
		this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
		this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
	}

	public static ModelData getModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 0.5F;
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.NONE
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.51F)), ModelTransform.NONE
		);
		modelPartData3.addChild(
			EntityModelPartNames.HAT_RIM,
			ModelPartBuilder.create().uv(30, 47).cuboid(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F),
			ModelTransform.rotation((float) (-Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 20).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), ModelTransform.NONE
		);
		modelPartData4.addChild(
			EntityModelPartNames.JACKET, ModelPartBuilder.create().uv(0, 38).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new Dilation(0.5F)), ModelTransform.NONE
		);
		modelPartData.addChild(
			EntityModelPartNames.ARMS,
			ModelPartBuilder.create()
				.uv(44, 22)
				.cuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
				.uv(44, 22)
				.cuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, true)
				.uv(40, 38)
				.cuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
			ModelTransform.of(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-2.0F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			ModelTransform.pivot(2.0F, 12.0F, 0.0F)
		);
		return modelData;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		boolean bl = false;
		if (entity instanceof MerchantEntity) {
			bl = ((MerchantEntity)entity).getHeadRollingTimeLeft() > 0;
		}

		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		if (bl) {
			this.head.roll = 0.3F * MathHelper.sin(0.45F * animationProgress);
			this.head.pitch = 0.4F;
		} else {
			this.head.roll = 0.0F;
		}

		this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance * 0.5F;
		this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance * 0.5F;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setHatVisible(boolean visible) {
		this.head.visible = visible;
		this.hat.visible = visible;
		this.hatRim.visible = visible;
	}
}
