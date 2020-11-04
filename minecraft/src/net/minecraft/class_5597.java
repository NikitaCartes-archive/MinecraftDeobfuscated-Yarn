package net.minecraft;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class class_5597<E extends Entity> extends EntityModel<E> {
	public class_5597() {
		this(RenderLayer::getEntityCutoutNoCull);
	}

	public class_5597(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.method_32008().render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	public abstract ModelPart method_32008();
}
