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
import net.minecraft.util.math.random.Random;

/**
 * Represents the model of a ghast-like entity.
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
 *   <td>{@code tentacle0}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[0]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle1}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[1]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle2}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[2]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle3}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[3]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle4}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[4]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle5}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[5]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle6}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[6]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle7}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[7]}</td>
 * </tr>
 * <tr>
 *   <td>{@code tentacle8}</td><td>{@linkplain #root Root part}</td><td>{@link #tentacles tentacles[8]}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class GhastEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart[] tentacles = new ModelPart[9];

	public GhastEntityModel(ModelPart root) {
		this.root = root;

		for (int i = 0; i < this.tentacles.length; i++) {
			this.tentacles[i] = root.getChild(getTentacleName(i));
		}
	}

	private static String getTentacleName(int index) {
		return "tentacle" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), ModelTransform.pivot(0.0F, 17.6F, 0.0F)
		);
		Random random = Random.create(1660L);

		for (int i = 0; i < 9; i++) {
			float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
			float g = ((float)(i / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
			int j = random.nextInt(7) + 8;
			modelPartData.addChild(
				getTentacleName(i), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, (float)j, 2.0F), ModelTransform.pivot(f, 24.6F, g)
			);
		}

		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (int i = 0; i < this.tentacles.length; i++) {
			this.tentacles[i].pitch = 0.2F * MathHelper.sin(animationProgress * 0.3F + (float)i) + 0.4F;
		}
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
