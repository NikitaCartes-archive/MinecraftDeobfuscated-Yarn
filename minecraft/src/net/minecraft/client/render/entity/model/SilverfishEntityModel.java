package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SilverfishEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart[] field_3560;
	private final ModelPart[] field_3557;
	private final float[] field_3561 = new float[7];
	private static final int[][] field_3558 = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] field_3559 = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public SilverfishEntityModel() {
		this.field_3560 = new ModelPart[7];
		float f = -3.5F;

		for (int i = 0; i < this.field_3560.length; i++) {
			this.field_3560[i] = new ModelPart(this, field_3559[i][0], field_3559[i][1]);
			this.field_3560[i].addCuboid((float)field_3558[i][0] * -0.5F, 0.0F, (float)field_3558[i][2] * -0.5F, field_3558[i][0], field_3558[i][1], field_3558[i][2]);
			this.field_3560[i].setRotationPoint(0.0F, (float)(24 - field_3558[i][1]), f);
			this.field_3561[i] = f;
			if (i < this.field_3560.length - 1) {
				f += (float)(field_3558[i][2] + field_3558[i + 1][2]) * 0.5F;
			}
		}

		this.field_3557 = new ModelPart[3];
		this.field_3557[0] = new ModelPart(this, 20, 0);
		this.field_3557[0].addCuboid(-5.0F, 0.0F, (float)field_3558[2][2] * -0.5F, 10, 8, field_3558[2][2]);
		this.field_3557[0].setRotationPoint(0.0F, 16.0F, this.field_3561[2]);
		this.field_3557[1] = new ModelPart(this, 20, 11);
		this.field_3557[1].addCuboid(-3.0F, 0.0F, (float)field_3558[4][2] * -0.5F, 6, 4, field_3558[4][2]);
		this.field_3557[1].setRotationPoint(0.0F, 20.0F, this.field_3561[4]);
		this.field_3557[2] = new ModelPart(this, 20, 18);
		this.field_3557[2].addCuboid(-3.0F, 0.0F, (float)field_3558[4][2] * -0.5F, 6, 5, field_3558[1][2]);
		this.field_3557[2].setRotationPoint(0.0F, 19.0F, this.field_3561[1]);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);

		for (ModelPart modelPart : this.field_3560) {
			modelPart.render(k);
		}

		for (ModelPart modelPart : this.field_3557) {
			modelPart.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < this.field_3560.length; l++) {
			this.field_3560[l].yaw = MathHelper.cos(h * 0.9F + (float)l * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (float)(1 + Math.abs(l - 2));
			this.field_3560[l].rotationPointX = MathHelper.sin(h * 0.9F + (float)l * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(l - 2);
		}

		this.field_3557[0].yaw = this.field_3560[2].yaw;
		this.field_3557[1].yaw = this.field_3560[4].yaw;
		this.field_3557[1].rotationPointX = this.field_3560[4].rotationPointX;
		this.field_3557[2].yaw = this.field_3560[1].yaw;
		this.field_3557[2].rotationPointX = this.field_3560[1].rotationPointX;
	}
}
