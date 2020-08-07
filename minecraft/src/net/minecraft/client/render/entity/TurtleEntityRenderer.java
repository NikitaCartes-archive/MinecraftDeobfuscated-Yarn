package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TurtleEntityRenderer extends MobEntityRenderer<TurtleEntity, TurtleEntityModel<TurtleEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/turtle/big_sea_turtle.png");

	public TurtleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new TurtleEntityModel<>(0.0F), 0.7F);
	}

	public void method_4138(TurtleEntity turtleEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (turtleEntity.isBaby()) {
			this.shadowRadius *= 0.5F;
		}

		super.method_4072(turtleEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier method_4139(TurtleEntity turtleEntity) {
		return TEXTURE;
	}
}
