package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EndermiteEntityModel<T extends Entity> extends EntityModel<T> {
	private static final int[][] field_3366 = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
	private static final int[][] field_3369 = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
	private static final int field_3367 = field_3366.length;
	private final ModelPart[] field_3368 = new ModelPart[field_3367];

	public EndermiteEntityModel() {
		float f = -3.5F;

		for (int i = 0; i < this.field_3368.length; i++) {
			this.field_3368[i] = new ModelPart(this, field_3369[i][0], field_3369[i][1]);
			this.field_3368[i].addCuboid((float)field_3366[i][0] * -0.5F, 0.0F, (float)field_3366[i][2] * -0.5F, field_3366[i][0], field_3366[i][1], field_3366[i][2]);
			this.field_3368[i].setPivot(0.0F, (float)(24 - field_3366[i][1]), f);
			if (i < this.field_3368.length - 1) {
				f += (float)(field_3366[i][2] + field_3366[i + 1][2]) * 0.5F;
			}
		}
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);

		for (ModelPart modelPart : this.field_3368) {
			modelPart.render(scale);
		}
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		for (int i = 0; i < this.field_3368.length; i++) {
			this.field_3368[i].yaw = MathHelper.cos(age * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.01F * (float)(1 + Math.abs(i - 2));
			this.field_3368[i].pivotX = MathHelper.sin(age * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.1F * (float)Math.abs(i - 2);
		}
	}
}
