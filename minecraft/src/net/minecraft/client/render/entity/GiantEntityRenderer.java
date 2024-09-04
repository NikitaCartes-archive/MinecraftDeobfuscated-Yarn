package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GiantEntityRenderer extends MobEntityRenderer<GiantEntity, ZombieEntityRenderState, BipedEntityModel<ZombieEntityRenderState>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

	public GiantEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
		super(ctx, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT)), 0.5F * scale);
		this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getItemRenderer()));
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this,
				new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_INNER_ARMOR)),
				new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_OUTER_ARMOR)),
				ctx.getEquipmentRenderer()
			)
		);
	}

	public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
		return TEXTURE;
	}

	public ZombieEntityRenderState getRenderState() {
		return new ZombieEntityRenderState();
	}

	public void updateRenderState(GiantEntity giantEntity, ZombieEntityRenderState zombieEntityRenderState, float f) {
		super.updateRenderState(giantEntity, zombieEntityRenderState, f);
		BipedEntityRenderer.updateBipedRenderState(giantEntity, zombieEntityRenderState, f);
	}
}
