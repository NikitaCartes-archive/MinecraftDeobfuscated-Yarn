package net.minecraft.client.render.entity.model;

import java.util.Arrays;
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

@Environment(EnvType.CLIENT)
public class SilverfishEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private static final int BODY_PARTS_COUNT = 7;
	private final ModelPart root;
	private final ModelPart[] body = new ModelPart[7];
	private final ModelPart[] scales = new ModelPart[3];
	private static final int[][] SEGMENT_LOCATIONS = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] SEGMENT_SIZES = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public SilverfishEntityModel(ModelPart root) {
		this.root = root;
		Arrays.setAll(this.body, index -> root.getChild(getSegmentName(index)));
		Arrays.setAll(this.scales, index -> root.getChild(getLayerName(index)));
	}

	private static String getLayerName(int index) {
		return "layer" + index;
	}

	private static String getSegmentName(int index) {
		return "segment" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float[] fs = new float[7];
		float f = -3.5F;

		for (int i = 0; i < 7; i++) {
			modelPartData.addChild(
				getSegmentName(i),
				ModelPartBuilder.create()
					.uv(SEGMENT_SIZES[i][0], SEGMENT_SIZES[i][1])
					.cuboid(
						(float)SEGMENT_LOCATIONS[i][0] * -0.5F,
						0.0F,
						(float)SEGMENT_LOCATIONS[i][2] * -0.5F,
						(float)SEGMENT_LOCATIONS[i][0],
						(float)SEGMENT_LOCATIONS[i][1],
						(float)SEGMENT_LOCATIONS[i][2]
					),
				ModelTransform.pivot(0.0F, (float)(24 - SEGMENT_LOCATIONS[i][1]), f)
			);
			fs[i] = f;
			if (i < 6) {
				f += (float)(SEGMENT_LOCATIONS[i][2] + SEGMENT_LOCATIONS[i + 1][2]) * 0.5F;
			}
		}

		modelPartData.addChild(
			getLayerName(0),
			ModelPartBuilder.create().uv(20, 0).cuboid(-5.0F, 0.0F, (float)SEGMENT_LOCATIONS[2][2] * -0.5F, 10.0F, 8.0F, (float)SEGMENT_LOCATIONS[2][2]),
			ModelTransform.pivot(0.0F, 16.0F, fs[2])
		);
		modelPartData.addChild(
			getLayerName(1),
			ModelPartBuilder.create().uv(20, 11).cuboid(-3.0F, 0.0F, (float)SEGMENT_LOCATIONS[4][2] * -0.5F, 6.0F, 4.0F, (float)SEGMENT_LOCATIONS[4][2]),
			ModelTransform.pivot(0.0F, 20.0F, fs[4])
		);
		modelPartData.addChild(
			getLayerName(2),
			ModelPartBuilder.create().uv(20, 18).cuboid(-3.0F, 0.0F, (float)SEGMENT_LOCATIONS[4][2] * -0.5F, 6.0F, 5.0F, (float)SEGMENT_LOCATIONS[1][2]),
			ModelTransform.pivot(0.0F, 19.0F, fs[1])
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (int i = 0; i < this.body.length; i++) {
			this.body[i].yaw = MathHelper.cos(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
			this.body[i].pivotX = MathHelper.sin(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(i - 2);
		}

		this.scales[0].yaw = this.body[2].yaw;
		this.scales[1].yaw = this.body[4].yaw;
		this.scales[1].pivotX = this.body[4].pivotX;
		this.scales[2].yaw = this.body[1].yaw;
		this.scales[2].pivotX = this.body[1].pivotX;
	}
}
