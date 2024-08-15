package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PillagerEntityRenderer extends IllagerEntityRenderer<PillagerEntity, IllagerEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/pillager.png");

	public PillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.PILLAGER)), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getItemRenderer()));
	}

	public Identifier getTexture(IllagerEntityRenderState illagerEntityRenderState) {
		return TEXTURE;
	}

	public IllagerEntityRenderState getRenderState() {
		return new IllagerEntityRenderState();
	}
}
