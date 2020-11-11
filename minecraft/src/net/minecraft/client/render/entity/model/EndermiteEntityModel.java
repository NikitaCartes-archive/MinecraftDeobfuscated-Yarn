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

@Environment(EnvType.CLIENT)
public class EndermiteEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private static final int[][] field_3366 = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
	private static final int[][] field_3369 = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
	private final ModelPart field_27413;
	private final ModelPart[] field_3368;

	public EndermiteEntityModel(ModelPart modelPart) {
		this.field_27413 = modelPart;
		this.field_3368 = new ModelPart[4];

		for (int i = 0; i < 4; i++) {
			this.field_3368[i] = modelPart.getChild(method_31997(i));
		}
	}

	private static String method_31997(int i) {
		return "segment" + i;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = -3.5F;

		for (int i = 0; i < 4; i++) {
			modelPartData.addChild(
				method_31997(i),
				ModelPartBuilder.create()
					.uv(field_3369[i][0], field_3369[i][1])
					.cuboid((float)field_3366[i][0] * -0.5F, 0.0F, (float)field_3366[i][2] * -0.5F, (float)field_3366[i][0], (float)field_3366[i][1], (float)field_3366[i][2]),
				ModelTransform.pivot(0.0F, (float)(24 - field_3366[i][1]), f)
			);
			if (i < 3) {
				f += (float)(field_3366[i][2] + field_3366[i + 1][2]) * 0.5F;
			}
		}

		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27413;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		for (int i = 0; i < this.field_3368.length; i++) {
			this.field_3368[i].yaw = MathHelper.cos(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI)
				* (float) Math.PI
				* 0.01F
				* (float)(1 + Math.abs(i - 2));
			this.field_3368[i].pivotX = MathHelper.sin(animationProgress * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.1F * (float)Math.abs(i - 2);
		}
	}
}
