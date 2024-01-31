package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.passive.GoatEntity;

/**
 * Represents the model of a {@linkplain GoatEntity}.
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
 *   <td>{@value EntityModelPartNames#LEFT_HORN}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HORN}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#NOSE}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
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
public class GoatEntityModel<T extends GoatEntity> extends QuadrupedEntityModel<T> {
	public GoatEntityModel(ModelPart root) {
		super(root, true, 19.0F, 1.0F, 2.5F, 2.0F, 24);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(2, 61)
				.cuboid("right ear", -6.0F, -11.0F, -10.0F, 3.0F, 2.0F, 1.0F)
				.uv(2, 61)
				.mirrored()
				.cuboid("left ear", 2.0F, -11.0F, -10.0F, 3.0F, 2.0F, 1.0F)
				.uv(23, 52)
				.cuboid("goatee", -0.5F, -3.0F, -14.0F, 0.0F, 7.0F, 5.0F),
			ModelTransform.pivot(1.0F, 14.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.LEFT_HORN,
			ModelPartBuilder.create().uv(12, 55).cuboid(-0.01F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.RIGHT_HORN,
			ModelPartBuilder.create().uv(12, 55).cuboid(-2.99F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.NOSE,
			ModelPartBuilder.create().uv(34, 46).cuboid(-3.0F, -4.0F, -8.0F, 5.0F, 7.0F, 10.0F),
			ModelTransform.of(0.0F, -8.0F, -8.0F, 0.9599F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(1, 1).cuboid(-4.0F, -17.0F, -7.0F, 9.0F, 11.0F, 16.0F).uv(0, 28).cuboid(-5.0F, -18.0F, -8.0F, 11.0F, 14.0F, 11.0F),
			ModelTransform.pivot(0.0F, 24.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().uv(36, 29).cuboid(0.0F, 4.0F, 0.0F, 3.0F, 6.0F, 3.0F), ModelTransform.pivot(1.0F, 14.0F, 4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(49, 29).cuboid(0.0F, 4.0F, 0.0F, 3.0F, 6.0F, 3.0F),
			ModelTransform.pivot(-3.0F, 14.0F, 4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(49, 2).cuboid(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F),
			ModelTransform.pivot(1.0F, 14.0F, -6.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(35, 2).cuboid(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F),
			ModelTransform.pivot(-3.0F, 14.0F, -6.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(T goatEntity, float f, float g, float h, float i, float j) {
		this.head.getChild(EntityModelPartNames.LEFT_HORN).visible = goatEntity.hasLeftHorn();
		this.head.getChild(EntityModelPartNames.RIGHT_HORN).visible = goatEntity.hasRightHorn();
		super.setAngles(goatEntity, f, g, h, i, j);
		float k = goatEntity.getHeadPitch();
		if (k != 0.0F) {
			this.head.pitch = k;
		}
	}
}
