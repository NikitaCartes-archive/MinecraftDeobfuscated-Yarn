package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GiantEntityRenderer extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
	private final float scale;

	public GiantEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
		super(ctx, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT)), 0.5F * scale);
		this.scale = scale;
		this.addFeature(new HeldItemFeatureRenderer<>(this));
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_INNER_ARMOR)), new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_OUTER_ARMOR))
			)
		);
		this.addFeature(new EndermanBlockFeatureRenderer<>(this, 0.125F, 0.25F, 0.5F));
	}

	protected void scale(GiantEntity giantEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(this.scale, this.scale, this.scale);
	}

	public Identifier getTexture(GiantEntity giantEntity) {
		return TEXTURE;
	}
}
