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
 * Represents the model of a minecart-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@code bottom}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@code front}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@code back}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@code left}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@code right}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class MinecartEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart root;

	public MinecartEntityModel(ModelPart root) {
		this.root = root;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		modelPartData.addChild(
			"bottom",
			ModelPartBuilder.create().uv(0, 10).cuboid(-10.0F, -8.0F, -1.0F, 20.0F, 16.0F, 2.0F),
			ModelTransform.of(0.0F, 4.0F, 0.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"front",
			ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
			ModelTransform.of(-9.0F, 4.0F, 0.0F, 0.0F, (float) (Math.PI * 3.0 / 2.0), 0.0F)
		);
		modelPartData.addChild(
			"back",
			ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
			ModelTransform.of(9.0F, 4.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		modelPartData.addChild(
			"left", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F), ModelTransform.of(0.0F, 4.0F, -7.0F, 0.0F, (float) Math.PI, 0.0F)
		);
		modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F), ModelTransform.pivot(0.0F, 4.0F, 7.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
