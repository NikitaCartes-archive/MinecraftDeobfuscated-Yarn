package net.minecraft.client.model;

import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class Model implements Consumer<ModelPart> {
	protected final Function<Identifier, RenderLayer> layerFactory;
	public int textureWidth = 64;
	public int textureHeight = 32;

	public Model(Function<Identifier, RenderLayer> layerFactory) {
		this.layerFactory = layerFactory;
	}

	public void accept(ModelPart modelPart) {
	}

	public final RenderLayer getLayer(Identifier texture) {
		return (RenderLayer)this.layerFactory.apply(texture);
	}

	public abstract void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float r, float g, float b, float f);
}
