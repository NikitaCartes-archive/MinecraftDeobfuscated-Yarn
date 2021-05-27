package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a cod-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#NOSE}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FIN}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FIN}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#TAIL_FIN}</td><td>{@linkplain #root Root part}</td><td>{@link #tailFin}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#TOP_FIN}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class CodEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart tailFin;

	public CodEntityModel(ModelPart root) {
		this.root = root;
		this.tailFin = root.getChild(EntityModelPartNames.TAIL_FIN);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 22;
		modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 7.0F), ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(11, 0).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F), ModelTransform.pivot(0.0F, 22.0F, -3.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FIN,
			ModelPartBuilder.create().uv(22, 1).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F),
			ModelTransform.of(-1.0F, 23.0F, 0.0F, 0.0F, 0.0F, (float) (-Math.PI / 4))
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FIN,
			ModelPartBuilder.create().uv(22, 4).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F),
			ModelTransform.of(1.0F, 23.0F, 0.0F, 0.0F, 0.0F, (float) (Math.PI / 4))
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL_FIN, ModelPartBuilder.create().uv(22, 3).cuboid(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F), ModelTransform.pivot(0.0F, 22.0F, 7.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TOP_FIN, ModelPartBuilder.create().uv(20, -6).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 6.0F), ModelTransform.pivot(0.0F, 20.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.tailFin.yaw = -f * 0.45F * MathHelper.sin(0.6F * animationProgress);
	}
}
