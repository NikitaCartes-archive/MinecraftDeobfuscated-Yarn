package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpectralArrowEntityRenderer extends ProjectileEntityRenderer<SpectralArrowEntity> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/spectral_arrow.png");

	public SpectralArrowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public Identifier method_4120(SpectralArrowEntity spectralArrowEntity) {
		return TEXTURE;
	}
}
