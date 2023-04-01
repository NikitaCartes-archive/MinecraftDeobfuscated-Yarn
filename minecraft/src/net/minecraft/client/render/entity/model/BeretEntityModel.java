package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
public class BeretEntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithHead {
	public final ModelPart beret;

	public BeretEntityModel(ModelPart beret) {
		this.beret = beret;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
				EntityModelPartNames.HAT,
				ModelPartBuilder.create().uv(0, 0).cuboid(-3.5F, -9.0F, -5.0F, 10.0F, 3.0F, 10.0F),
				ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float) (-Math.PI / 12))
			)
			.addChild(
				"innerhat", ModelPartBuilder.create().uv(0, 13).cuboid(-3.75F, -8.0F, -4.5F, 9.0F, 2.0F, 9.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1308997F)
			)
			.addChild(
				"thing",
				ModelPartBuilder.create().uv(0, 0).cuboid(-4.8F, -8.0F, -5.1F, 2.0F, 3.0F, 0.0F),
				ModelTransform.of(4.0F, -3.0F, 4.0F, (float) (-Math.PI / 18), (float) (Math.PI / 180.0), 0.0F)
			);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		matrices.scale(1.001F, 1.001F, 1.001F);
		this.beret.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return this.beret;
	}
}
