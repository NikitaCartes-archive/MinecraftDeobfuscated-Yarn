package net.minecraft.client.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class Model {
	protected final Function<Identifier, RenderLayer> layerFactory;

	public Model(Function<Identifier, RenderLayer> layerFactory) {
		this.layerFactory = layerFactory;
	}

	public final RenderLayer getLayer(Identifier texture) {
		return (RenderLayer)this.layerFactory.apply(texture);
	}

	public abstract void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);
}
