package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArrowEntityRenderer extends ProjectileEntityRenderer<ArrowEntity> {
	public static final Identifier SKIN = new Identifier("textures/entity/projectiles/arrow.png");
	public static final Identifier TIPPED_SKIN = new Identifier("textures/entity/projectiles/tipped_arrow.png");

	public ArrowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public Identifier getTexture(ArrowEntity arrowEntity) {
		return arrowEntity.getColor() > 0 ? TIPPED_SKIN : SKIN;
	}
}
