package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TurtleEntityRenderer extends MobEntityRenderer<TurtleEntity, TurtleEntityModel<TurtleEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/turtle/big_sea_turtle.png");

	public TurtleEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new TurtleEntityModel<>(arg.method_32167(EntityModelLayers.TURTLE)), 0.7F);
	}

	public void render(TurtleEntity turtleEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (turtleEntity.isBaby()) {
			this.shadowRadius *= 0.5F;
		}

		super.render(turtleEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(TurtleEntity turtleEntity) {
		return TEXTURE;
	}
}
