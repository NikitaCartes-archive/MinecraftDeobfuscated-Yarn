package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpiderEntityRenderer<T extends SpiderEntity> extends MobEntityRenderer<T, LivingEntityRenderState, SpiderEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/spider/spider.png");

	public SpiderEntityRenderer(EntityRendererFactory.Context context) {
		this(context, EntityModelLayers.SPIDER);
	}

	public SpiderEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
		super(ctx, new SpiderEntityModel(ctx.getPart(layer)), 0.8F);
		this.addFeature(new SpiderEyesFeatureRenderer<>(this));
	}

	@Override
	protected float method_3919() {
		return 180.0F;
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}

	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	public void updateRenderState(T spiderEntity, LivingEntityRenderState livingEntityRenderState, float f) {
		super.updateRenderState(spiderEntity, livingEntityRenderState, f);
	}
}
