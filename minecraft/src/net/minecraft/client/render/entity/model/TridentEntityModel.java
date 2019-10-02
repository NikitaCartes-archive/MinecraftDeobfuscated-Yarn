package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class TridentEntityModel extends Model {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
	private final ModelPart field_3593 = new ModelPart(32, 32, 0, 6);

	public TridentEntityModel() {
		this.field_3593.addCuboid(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F, 0.0F);
		ModelPart modelPart = new ModelPart(32, 32, 4, 0);
		modelPart.addCuboid(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F);
		this.field_3593.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(32, 32, 4, 3);
		modelPart2.addCuboid(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F);
		this.field_3593.addChild(modelPart2);
		ModelPart modelPart3 = new ModelPart(32, 32, 0, 0);
		modelPart3.addCuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.field_3593.addChild(modelPart3);
		ModelPart modelPart4 = new ModelPart(32, 32, 4, 3);
		modelPart4.mirror = true;
		modelPart4.addCuboid(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F);
		this.field_3593.addChild(modelPart4);
	}

	public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i) {
		this.field_3593.render(matrixStack, vertexConsumer, 0.0625F, i, null);
	}
}
