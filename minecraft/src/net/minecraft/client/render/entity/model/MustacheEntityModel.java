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
public class MustacheEntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithHead {
	public final ModelPart mustache;

	public MustacheEntityModel(ModelPart head) {
		this.mustache = head;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -2.5F, -4.75F, 10.0F, 3.0F, 0.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 32, 4);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.mustache.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return this.mustache;
	}
}
