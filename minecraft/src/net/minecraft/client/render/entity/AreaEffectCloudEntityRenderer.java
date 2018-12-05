package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AreaEffectCloudEntityRenderer extends EntityRenderer<AreaEffectCloudEntity> {
	public AreaEffectCloudEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Nullable
	protected Identifier getTexture(AreaEffectCloudEntity areaEffectCloudEntity) {
		return null;
	}
}
