package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpectralArrowEntityRenderer extends ProjectileEntityRenderer<SpectralArrowEntity> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/spectral_arrow.png");

	public SpectralArrowEntityRenderer(class_5617.class_5618 arg) {
		super(arg);
	}

	public Identifier getTexture(SpectralArrowEntity spectralArrowEntity) {
		return TEXTURE;
	}
}
