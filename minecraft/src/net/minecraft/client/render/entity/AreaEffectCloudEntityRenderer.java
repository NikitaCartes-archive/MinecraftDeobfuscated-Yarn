package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AreaEffectCloudEntityRenderer extends EntityRenderer<AreaEffectCloudEntity> {
	public AreaEffectCloudEntityRenderer(class_5617.class_5618 arg) {
		super(arg);
	}

	public Identifier getTexture(AreaEffectCloudEntity areaEffectCloudEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
