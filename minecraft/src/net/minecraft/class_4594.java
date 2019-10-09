package net.minecraft;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class class_4594<E extends Entity> extends class_4595<E> {
	private float field_20926 = 1.0F;
	private float field_20927 = 1.0F;
	private float field_20928 = 1.0F;

	public class_4594(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	public void method_22956(float f, float g, float h) {
		this.field_20926 = f;
		this.field_20927 = g;
		this.field_20928 = h;
	}

	@Override
	public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
		super.renderItem(matrixStack, vertexConsumer, i, j, this.field_20926 * f, this.field_20927 * g, this.field_20928 * h);
	}
}
