package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GridRenderer;
import net.minecraft.entity.GridCarrierEntity;
import net.minecraft.world.GridCarrierView;

@Environment(EnvType.CLIENT)
public class ClientGridCarrierView extends GridCarrierView implements AutoCloseable {
	private final GridRenderer renderer = new GridRenderer(this);

	public ClientGridCarrierView(ClientWorld world, GridCarrierEntity gridCarrier) {
		super(world, gridCarrier);
	}

	public GridRenderer getRenderer() {
		return this.renderer;
	}

	public void close() {
		this.renderer.close();
	}
}
