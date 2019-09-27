package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface FeatureRendererContext<T extends Entity, M extends EntityModel<T>> {
	M getModel();

	Identifier getTexture(T entity);
}
