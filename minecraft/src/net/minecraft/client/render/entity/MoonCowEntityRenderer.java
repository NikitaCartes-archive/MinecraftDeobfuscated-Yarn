package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.MoonCowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MoonCowEntityRenderer extends MobEntityRenderer<MoonCowEntity, CowEntityModel<MoonCowEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/cow/moon_cow.png");

	public MoonCowEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CowEntityModel<>(context.getPart(EntityModelLayers.COW)), 0.7F);
		this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getHeldItemRenderer()));
	}

	public Identifier getTexture(MoonCowEntity moonCowEntity) {
		return TEXTURE;
	}

	protected void scale(MoonCowEntity moonCowEntity, MatrixStack matrixStack, float f) {
		float g = moonCowEntity.getBloatScale();
		matrixStack.scale(g, g, g);
	}
}
