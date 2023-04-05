package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;

/**
 * Represents the model of a squid-like entity.
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
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class SquidEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart[] tentacles = new ModelPart[8];
	private final ModelPart root;

	public SquidEntityModel(ModelPart root) {
		this.root = root;
		Arrays.setAll(this.tentacles, index -> root.getChild(getTentacleName(index)));
	}

	private static String getTentacleName(int index) {
		return "tentacle" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		Dilation dilation = new Dilation(0.02F);
		int i = -16;
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, dilation),
			ModelTransform.pivot(0.0F, 8.0F, 0.0F)
		);
		int j = 8;
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);

		for (int k = 0; k < 8; k++) {
			double d = (double)k * Math.PI * 2.0 / 8.0;
			float f = (float)Math.cos(d) * 5.0F;
			float g = 15.0F;
			float h = (float)Math.sin(d) * 5.0F;
			d = (double)k * Math.PI * -2.0 / 8.0 + (Math.PI / 2);
			float l = (float)d;
			modelPartData.addChild(getTentacleName(k), modelPartBuilder, ModelTransform.of(f, 15.0F, h, 0.0F, l, 0.0F));
		}

		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (ModelPart modelPart : this.tentacles) {
			modelPart.pitch = animationProgress;
		}
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
