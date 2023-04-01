package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class CrownEntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithHead {
	public final ModelPart crown;

	public CrownEntityModel(ModelPart crown) {
		this.crown = crown;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HAT,
			ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -9.0F, -2.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.1F, 0.1F, 0.1F)),
			ModelTransform.NONE
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.crown.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return this.crown;
	}
}
