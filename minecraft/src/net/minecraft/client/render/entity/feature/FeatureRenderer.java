package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class FeatureRenderer<T extends Entity, M extends EntityModel<T>> {
	private final FeatureRendererContext<T, M> context;

	public FeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		this.context = featureRendererContext;
	}

	public M getModel() {
		return this.context.getModel();
	}

	public void bindTexture(Identifier identifier) {
		this.context.bindTexture(identifier);
	}

	public void applyLightmapCoordinates(T entity) {
		this.context.applyLightmapCoordinates(entity);
	}

	public abstract void render(T entity, float f, float g, float h, float i, float j, float k, float l);

	public abstract boolean method_4200();
}
