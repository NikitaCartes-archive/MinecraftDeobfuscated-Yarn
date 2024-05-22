package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class AllayEntityRenderer extends MobEntityRenderer<AllayEntity, AllayEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/allay/allay.png");

	public AllayEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new AllayEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4F);
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
	}

	public Identifier getTexture(AllayEntity allayEntity) {
		return TEXTURE;
	}

	protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
		return 15;
	}
}
