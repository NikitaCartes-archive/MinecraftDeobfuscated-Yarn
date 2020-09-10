package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SilverfishEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart[] body;
	private final ModelPart[] scales;
	private final ImmutableList<ModelPart> parts;
	private final float[] scaleSizes = new float[7];
	private static final int[][] segmentLocations = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] segmentSizes = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public SilverfishEntityModel() {
		this.body = new ModelPart[7];
		float f = -3.5F;

		for (int i = 0; i < this.body.length; i++) {
			this.body[i] = new ModelPart(this, segmentSizes[i][0], segmentSizes[i][1]);
			this.body[i]
				.addCuboid(
					(float)segmentLocations[i][0] * -0.5F,
					0.0F,
					(float)segmentLocations[i][2] * -0.5F,
					(float)segmentLocations[i][0],
					(float)segmentLocations[i][1],
					(float)segmentLocations[i][2]
				);
			this.body[i].setPivot(0.0F, (float)(24 - segmentLocations[i][1]), f);
			this.scaleSizes[i] = f;
			if (i < this.body.length - 1) {
				f += (float)(segmentLocations[i][2] + segmentLocations[i + 1][2]) * 0.5F;
			}
		}

		this.scales = new ModelPart[3];
		this.scales[0] = new ModelPart(this, 20, 0);
		this.scales[0].addCuboid(-5.0F, 0.0F, (float)segmentLocations[2][2] * -0.5F, 10.0F, 8.0F, (float)segmentLocations[2][2]);
		this.scales[0].setPivot(0.0F, 16.0F, this.scaleSizes[2]);
		this.scales[1] = new ModelPart(this, 20, 11);
		this.scales[1].addCuboid(-3.0F, 0.0F, (float)segmentLocations[4][2] * -0.5F, 6.0F, 4.0F, (float)segmentLocations[4][2]);
		this.scales[1].setPivot(0.0F, 20.0F, this.scaleSizes[4]);
		this.scales[2] = new ModelPart(this, 20, 18);
		this.scales[2].addCuboid(-3.0F, 0.0F, (float)segmentLocations[4][2] * -0.5F, 6.0F, 5.0F, (float)segmentLocations[1][2]);
		this.scales[2].setPivot(0.0F, 19.0F, this.scaleSizes[1]);
		Builder<ModelPart> builder = ImmutableList.builder();
		builder.addAll(Arrays.asList(this.body));
		builder.addAll(Arrays.asList(this.scales));
		this.parts = builder.build();
	}

	public ImmutableList<ModelPart> getParts() {
		return this.parts;
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
