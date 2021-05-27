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
 * Represents the model of llama-spit-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value #MAIN}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	/**
	 * The key of the main model part, whose value is {@value}.
	 */
	private static final String MAIN = "main";
	private final ModelPart root;

	public LlamaSpitEntityModel(ModelPart root) {
		this.root = root;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 2;
		modelPartData.addChild(
			"main",
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-4.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F)
				.cuboid(0.0F, -4.0F, 0.0F, 2.0F, 2.0F, 2.0F)
				.cuboid(0.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F)
				.cuboid(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F)
				.cuboid(2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F)
				.cuboid(0.0F, 2.0F, 0.0F, 2.0F, 2.0F, 2.0F)
				.cuboid(0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 2.0F),
			ModelTransform.NONE
		);
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
