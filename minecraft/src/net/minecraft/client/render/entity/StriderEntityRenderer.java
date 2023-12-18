package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StriderEntityRenderer extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/strider/strider.png");
	private static final Identifier COLD_TEXTURE = new Identifier("textures/entity/strider/strider_cold.png");
	private static final float field_47888 = 0.5F;

	public StriderEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new StriderEntityModel<>(context.getPart(EntityModelLayers.STRIDER)), 0.5F);
		this.addFeature(
			new SaddleFeatureRenderer<>(
				this, new StriderEntityModel<>(context.getPart(EntityModelLayers.STRIDER_SADDLE)), new Identifier("textures/entity/strider/strider_saddle.png")
			)
		);
	}

	public Identifier getTexture(StriderEntity striderEntity) {
		return striderEntity.isCold() ? COLD_TEXTURE : TEXTURE;
	}

	protected float method_55832(StriderEntity striderEntity) {
		float f = super.method_55831(striderEntity);
		return striderEntity.isBaby() ? f * 0.5F : f;
	}

	protected boolean isShaking(StriderEntity striderEntity) {
		return super.isShaking(striderEntity) || striderEntity.isCold();
	}
}
