package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PoisonousPotatoZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PoisonousPotatoZombieEntityRenderer extends ZombieBaseEntityRenderer<ZombieEntity, PoisonousPotatoZombieEntityModel<ZombieEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/poisonous_potato_zombie.png");

	public PoisonousPotatoZombieEntityRenderer(EntityRendererFactory.Context context) {
		this(context, EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_INNER_ARMOR, EntityModelLayers.ZOMBIE_OUTER_ARMOR);
	}

	@Override
	public Identifier getTexture(ZombieEntity zombieEntity) {
		return TEXTURE;
	}

	public PoisonousPotatoZombieEntityRenderer(
		EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer innerArmorLayer, EntityModelLayer outerArmorLayer
	) {
		super(
			context,
			new PoisonousPotatoZombieEntityModel<>(context.getPart(layer)),
			new PoisonousPotatoZombieEntityModel<>(context.getPart(innerArmorLayer)),
			new PoisonousPotatoZombieEntityModel<>(context.getPart(outerArmorLayer))
		);
	}
}
