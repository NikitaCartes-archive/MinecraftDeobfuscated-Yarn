package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SilverfishEntityModel<T extends Entity> extends class_4595<T> {
	private final ModelPart[] field_3560;
	private final ModelPart[] field_3557;
	private final ImmutableList<ModelPart> field_20941;
	private final float[] field_3561 = new float[7];
	private static final int[][] field_3558 = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] field_3559 = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public SilverfishEntityModel() {
		this.field_3560 = new ModelPart[7];
		float f = -3.5F;

		for (int i = 0; i < this.field_3560.length; i++) {
			this.field_3560[i] = new ModelPart(this, field_3559[i][0], field_3559[i][1]);
			this.field_3560[i]
				.addCuboid(
					(float)field_3558[i][0] * -0.5F, 0.0F, (float)field_3558[i][2] * -0.5F, (float)field_3558[i][0], (float)field_3558[i][1], (float)field_3558[i][2]
				);
			this.field_3560[i].setPivot(0.0F, (float)(24 - field_3558[i][1]), f);
			this.field_3561[i] = f;
			if (i < this.field_3560.length - 1) {
				f += (float)(field_3558[i][2] + field_3558[i + 1][2]) * 0.5F;
			}
		}

		this.field_3557 = new ModelPart[3];
		this.field_3557[0] = new ModelPart(this, 20, 0);
		this.field_3557[0].addCuboid(-5.0F, 0.0F, (float)field_3558[2][2] * -0.5F, 10.0F, 8.0F, (float)field_3558[2][2]);
		this.field_3557[0].setPivot(0.0F, 16.0F, this.field_3561[2]);
		this.field_3557[1] = new ModelPart(this, 20, 11);
		this.field_3557[1].addCuboid(-3.0F, 0.0F, (float)field_3558[4][2] * -0.5F, 6.0F, 4.0F, (float)field_3558[4][2]);
		this.field_3557[1].setPivot(0.0F, 20.0F, this.field_3561[4]);
		this.field_3557[2] = new ModelPart(this, 20, 18);
		this.field_3557[2].addCuboid(-3.0F, 0.0F, (float)field_3558[4][2] * -0.5F, 6.0F, 5.0F, (float)field_3558[1][2]);
		this.field_3557[2].setPivot(0.0F, 19.0F, this.field_3561[1]);
		Builder<ModelPart> builder = ImmutableList.builder();
		builder.addAll(Arrays.asList(this.field_3560));
		builder.addAll(Arrays.asList(this.field_3557));
		this.field_20941 = builder.build();
	}

	public ImmutableList<ModelPart> method_22969() {
		return this.field_20941;
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < this.field_3560.length; l++) {
			this.field_3560[l].yaw = MathHelper.cos(h * 0.9F + (float)l * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (float)(1 + Math.abs(l - 2));
			this.field_3560[l].pivotX = MathHelper.sin(h * 0.9F + (float)l * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(l - 2);
		}

		this.field_3557[0].yaw = this.field_3560[2].yaw;
		this.field_3557[1].yaw = this.field_3560[4].yaw;
		this.field_3557[1].pivotX = this.field_3560[4].pivotX;
		this.field_3557[2].yaw = this.field_3560[1].yaw;
		this.field_3557[2].pivotX = this.field_3560[1].pivotX;
	}
}
