package net.minecraft;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class class_4595<E extends Entity> extends EntityModel<E> {
	public class_4595() {
		this(RenderLayer::getEntitySolid);
	}

	public class_4595(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	@Override
	public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
		this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h));
	}

	public abstract Iterable<ModelPart> getParts();
}
