package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieEntityRenderer extends ZombieBaseEntityRenderer<ZombieEntity, ZombieEntityRenderState, ZombieEntityModel<ZombieEntityRenderState>> {
	public ZombieEntityRenderer(EntityRendererFactory.Context context) {
		this(
			context,
			EntityModelLayers.ZOMBIE,
			EntityModelLayers.ZOMBIE_BABY,
			EntityModelLayers.ZOMBIE_INNER_ARMOR,
			EntityModelLayers.ZOMBIE_OUTER_ARMOR,
			EntityModelLayers.ZOMBIE_BABY_INNER_ARMOR,
			EntityModelLayers.ZOMBIE_BABY_OUTER_ARMOR
		);
	}

	public ZombieEntityRenderState createRenderState() {
		return new ZombieEntityRenderState();
	}

	public ZombieEntityRenderer(
		EntityRendererFactory.Context ctx,
		EntityModelLayer layer,
		EntityModelLayer legsArmorLayer,
		EntityModelLayer bodyArmorLayer,
		EntityModelLayer entityModelLayer,
		EntityModelLayer entityModelLayer2,
		EntityModelLayer entityModelLayer3
	) {
		super(
			ctx,
			new ZombieEntityModel<>(ctx.getPart(layer)),
			new ZombieEntityModel<>(ctx.getPart(legsArmorLayer)),
			new ZombieEntityModel<>(ctx.getPart(bodyArmorLayer)),
			new ZombieEntityModel<>(ctx.getPart(entityModelLayer)),
			new ZombieEntityModel<>(ctx.getPart(entityModelLayer2)),
			new ZombieEntityModel<>(ctx.getPart(entityModelLayer3))
		);
	}
}
