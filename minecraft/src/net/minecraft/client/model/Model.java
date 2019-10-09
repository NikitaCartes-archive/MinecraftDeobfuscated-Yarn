package net.minecraft.client.model;

import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class Model implements Consumer<ModelPart> {
	protected final Function<Identifier, RenderLayer> field_21343;
	public int textureWidth = 64;
	public int textureHeight = 32;

	public Model(Function<Identifier, RenderLayer> function) {
		this.field_21343 = function;
	}

	public void method_22696(ModelPart modelPart) {
	}

	public final RenderLayer method_23500(Identifier identifier) {
		return (RenderLayer)this.field_21343.apply(identifier);
	}

	public abstract void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h);
}
