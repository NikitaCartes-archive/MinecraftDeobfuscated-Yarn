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

/**
 * Represents the model of a leash-knot-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value KNOT}</td><td>{@linkplain #root Root part}</td><td>{@link #knot}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class LeashKnotEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	/**
	 * The key of the knot model part, whose value is {@value}.
	 */
	private static final String KNOT = "knot";
	private final ModelPart root;
	private final ModelPart knot;

	public LeashKnotEntityModel(ModelPart root) {
		this.root = root;
		this.knot = root.getChild("knot");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("knot", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.knot.yaw = headYaw * (float) (Math.PI / 180.0);
		this.knot.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
