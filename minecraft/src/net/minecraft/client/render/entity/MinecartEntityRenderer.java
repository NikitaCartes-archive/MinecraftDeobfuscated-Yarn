package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

@Environment(EnvType.CLIENT)
public class MinecartEntityRenderer extends AbstractMinecartEntityRenderer<AbstractMinecartEntity, MinecartEntityRenderState> {
	public MinecartEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer entityModelLayer) {
		super(context, entityModelLayer);
	}

	public MinecartEntityRenderState getRenderState() {
		return new MinecartEntityRenderState();
	}
}
