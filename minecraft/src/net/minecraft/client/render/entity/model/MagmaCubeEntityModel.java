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
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityModel<T extends SlimeEntity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27441;
	private final ModelPart[] field_3427 = new ModelPart[8];

	public MagmaCubeEntityModel(ModelPart modelPart) {
		this.field_27441 = modelPart;
		Arrays.setAll(this.field_3427, i -> modelPart.getChild(method_32015(i)));
	}

	private static String method_32015(int i) {
		return "cube" + i;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		for (int i = 0; i < 8; i++) {
			int j = 0;
			int k = i;
			if (i == 2) {
				j = 24;
				k = 10;
			} else if (i == 3) {
				j = 24;
				k = 19;
			}

			modelPartData.addChild(method_32015(i), ModelPartBuilder.create().uv(j, k).cuboid(-4.0F, (float)(16 + i), -4.0F, 8.0F, 1.0F, 8.0F), ModelTransform.NONE);
		}

		modelPartData.addChild("inside_cube", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(T slimeEntity, float f, float g, float h, float i, float j) {
	}

	public void animateModel(T slimeEntity, float f, float g, float h) {
		float i = MathHelper.lerp(h, slimeEntity.lastStretch, slimeEntity.stretch);
		if (i < 0.0F) {
			i = 0.0F;
		}

		for (int j = 0; j < this.field_3427.length; j++) {
			this.field_3427[j].pivotY = (float)(-(4 - j)) * i * 1.7F;
		}
	}

	@Override
	public ModelPart getPart() {
		return this.field_27441;
	}
}
