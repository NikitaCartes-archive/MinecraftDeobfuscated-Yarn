package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class AxolotlEntityRenderer extends AgeableMobEntityRenderer<AxolotlEntity, AxolotlEntityRenderState, AxolotlEntityModel> {
	private static final Map<AxolotlEntity.Variant, Identifier> TEXTURES = Util.make(Maps.<AxolotlEntity.Variant, Identifier>newHashMap(), variants -> {
		for (AxolotlEntity.Variant variant : AxolotlEntity.Variant.values()) {
			variants.put(variant, Identifier.ofVanilla(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", variant.getName())));
		}
	});

	public AxolotlEntityRenderer(EntityRendererFactory.Context context) {
		super(
			context, new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL)), new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL_BABY)), 0.5F
		);
	}

	public Identifier getTexture(AxolotlEntityRenderState axolotlEntityRenderState) {
		return (Identifier)TEXTURES.get(axolotlEntityRenderState.variant);
	}

	public AxolotlEntityRenderState createRenderState() {
		return new AxolotlEntityRenderState();
	}

	public void updateRenderState(AxolotlEntity axolotlEntity, AxolotlEntityRenderState axolotlEntityRenderState, float f) {
		super.updateRenderState(axolotlEntity, axolotlEntityRenderState, f);
		axolotlEntityRenderState.variant = axolotlEntity.getVariant();
		axolotlEntityRenderState.playingDeadValue = axolotlEntity.playingDeadFf.getValue(f);
		axolotlEntityRenderState.inWaterValue = axolotlEntity.inWaterFf.getValue(f);
		axolotlEntityRenderState.onGroundValue = axolotlEntity.onGroundFf.getValue(f);
		axolotlEntityRenderState.isMovingValue = axolotlEntity.isMovingFf.getValue(f);
	}
}
