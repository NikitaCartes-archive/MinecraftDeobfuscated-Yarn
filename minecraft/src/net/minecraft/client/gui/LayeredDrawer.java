package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LayeredDrawer {
	public static final float LAYER_Z_PADDING = 200.0F;
	private final List<LayeredDrawer.Layer> layers = new ArrayList();

	public LayeredDrawer addLayer(LayeredDrawer.Layer layer) {
		this.layers.add(layer);
		return this;
	}

	public LayeredDrawer addSubDrawer(LayeredDrawer drawer, BooleanSupplier shouldRender) {
		return this.addLayer((context, tickDelta) -> {
			if (shouldRender.getAsBoolean()) {
				drawer.renderInternal(context, tickDelta);
			}
		});
	}

	public void render(DrawContext context, float tickDelta) {
		context.getMatrices().push();
		this.renderInternal(context, tickDelta);
		context.getMatrices().pop();
	}

	private void renderInternal(DrawContext context, float tickDelta) {
		for (LayeredDrawer.Layer layer : this.layers) {
			layer.render(context, tickDelta);
			context.getMatrices().translate(0.0F, 0.0F, 200.0F);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Layer {
		void render(DrawContext context, float tickDelta);
	}
}
