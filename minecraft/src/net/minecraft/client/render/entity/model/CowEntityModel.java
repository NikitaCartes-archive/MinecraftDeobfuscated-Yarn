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

@Environment(EnvType.CLIENT)
public class CowEntityModel<T extends Entity> extends QuadrupedEntityModel<T> {
	public CowEntityModel(ModelPart root) {
		super(root, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 12;
		modelPartData.addChild(
			"head",
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
				.uv(22, 0)
				.cuboid("right_horn", -5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F)
				.uv(22, 0)
				.cuboid("left_horn", 4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F),
			ModelTransform.pivot(0.0F, 4.0F, -8.0F)
		);
		modelPartData.addChild(
			"body",
			ModelPartBuilder.create().uv(18, 4).cuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F).uv(52, 0).cuboid(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F),
			ModelTransform.of(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
		modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 12.0F, 7.0F));
		modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(4.0F, 12.0F, 7.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 12.0F, -6.0F));
		modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(4.0F, 12.0F, -6.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	public ModelPart getHead() {
		return this.head;
	}
}
