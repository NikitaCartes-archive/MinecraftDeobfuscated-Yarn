package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class PigEntityModel<T extends Entity> extends QuadrupedEntityModel<T> {
	public PigEntityModel(ModelPart modelPart) {
		super(modelPart, false, 4.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = QuadrupedEntityModel.method_32033(6, dilation);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"head",
			ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, dilation).uv(16, 16).cuboid(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, dilation),
			ModelTransform.pivot(0.0F, 12.0F, -6.0F)
		);
		return TexturedModelData.of(modelData, 64, 32);
	}
}
