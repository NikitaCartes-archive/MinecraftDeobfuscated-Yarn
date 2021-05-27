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
 * Represents the model of an endermite-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@code segment0}</td><td>{@linkplain #root Root part}</td><td>{@link #bodySegments bodySegments[0]}</td>
 * </tr>
 * <tr>
 *   <td>{@code segment1}</td><td>{@linkplain #root Root part}</td><td>{@link #bodySegments bodySegments[1]}</td>
 * </tr>
 * <tr>
 *   <td>{@code segment2}</td><td>{@linkplain #root Root part}</td><td>{@link #bodySegments bodySegments[2]}</td>
 * </tr>
 * <tr>
 *   <td>{@code segment3}</td><td>{@linkplain #root Root part}</td><td>{@link #bodySegments bodySegments[3]}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class EndermiteEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private static final int BODY_SEGMENTS_COUNT = 4;
	private static final int[][] SEGMENT_DIMENSIONS = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
	private static final int[][] SEGMENT_UVS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
	private final ModelPart root;
	private final ModelPart[] bodySegments;

	public EndermiteEntityModel(ModelPart root) {
		this.root = root;
		this.bodySegments = new ModelPart[4];

		for (int i = 0; i < 4; i++) {
			this.bodySegments[i] = root.getChild(getSegmentName(i));
		}
	}

	private static String getSegmentName(int index) {
		return "segment" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = -3.5F;

		for (int i = 0; i < 4; i++) {
			modelPartData.addChild(
				getSegmentName(i),
				ModelPartBuilder.create()
					.uv(SEGMENT_UVS[i][0], SEGMENT_UVS[i][1])
					.cuboid(
						(float)SEGMENT_DIMENSIONS[i][0] * -0.5F,
						0.0F,
						(float)SEGMENT_DIMENSIONS[i][2] * -0.5F,
						(float)SEGMENT_DIMENSIONS[i][0],
						(float)SEGMENT_DIMENSIONS[i][1],
						(float)SEGMENT_DIMENSIONS[i][2]
					),
				ModelTransform.pivot(0.0F, (float)(24 - SEGMENT_DIMENSIONS[i][1]), f)
			);
			if (i < 3) {
				f += (float)(SEGMENT_DIMENSIONS[i][2] + SEGMENT_DIMENSIONS[i + 1][2]) * 0.5F;
			}
		}

		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (int i = 0; i < this.bodySegments.length; i++) {
			this.bodySegments[i].yaw = MathHelper.cos(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI)
				* (float) Math.PI
				* 0.01F
				* (float)(1 + Math.abs(i - 2));
			this.bodySegments[i].pivotX = MathHelper.sin(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI)
				* (float) Math.PI
				* 0.1F
				* (float)Math.abs(i - 2);
		}
	}
}
