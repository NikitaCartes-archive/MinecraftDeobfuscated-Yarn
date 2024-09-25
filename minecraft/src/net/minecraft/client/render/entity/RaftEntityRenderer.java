package net.minecraft.client.render.entity;

import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.RaftEntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RaftEntityRenderer extends AbstractBoatEntityRenderer {
	private final EntityModel<BoatEntityRenderState> model;
	private final Identifier texture;

	public RaftEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer) {
		super(context);
		this.texture = layer.id().withPath((UnaryOperator<String>)(path -> "textures/entity/" + path + ".png"));
		this.model = new RaftEntityModel(context.getPart(layer));
	}

	@Override
	protected EntityModel<BoatEntityRenderState> getModel() {
		return this.model;
	}

	@Override
	protected RenderLayer getRenderLayer() {
		return this.model.getLayer(this.texture);
	}
}
