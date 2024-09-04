package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public final class HorseEntityRenderer extends AbstractHorseEntityRenderer<HorseEntity, HorseEntityRenderState, HorseEntityModel> {
	private static final Map<HorseColor, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseColor.class), map -> {
		map.put(HorseColor.WHITE, Identifier.ofVanilla("textures/entity/horse/horse_white.png"));
		map.put(HorseColor.CREAMY, Identifier.ofVanilla("textures/entity/horse/horse_creamy.png"));
		map.put(HorseColor.CHESTNUT, Identifier.ofVanilla("textures/entity/horse/horse_chestnut.png"));
		map.put(HorseColor.BROWN, Identifier.ofVanilla("textures/entity/horse/horse_brown.png"));
		map.put(HorseColor.BLACK, Identifier.ofVanilla("textures/entity/horse/horse_black.png"));
		map.put(HorseColor.GRAY, Identifier.ofVanilla("textures/entity/horse/horse_gray.png"));
		map.put(HorseColor.DARK_BROWN, Identifier.ofVanilla("textures/entity/horse/horse_darkbrown.png"));
	});

	public HorseEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new HorseEntityModel(context.getPart(EntityModelLayers.HORSE)), new HorseEntityModel(context.getPart(EntityModelLayers.HORSE_BABY)), 1.1F);
		this.addFeature(new HorseMarkingFeatureRenderer(this));
		this.addFeature(new HorseArmorFeatureRenderer(this, context.getModelLoader(), context.getEquipmentRenderer()));
	}

	public Identifier getTexture(HorseEntityRenderState horseEntityRenderState) {
		return (Identifier)TEXTURES.get(horseEntityRenderState.color);
	}

	public HorseEntityRenderState getRenderState() {
		return new HorseEntityRenderState();
	}

	public void updateRenderState(HorseEntity horseEntity, HorseEntityRenderState horseEntityRenderState, float f) {
		super.updateRenderState(horseEntity, horseEntityRenderState, f);
		horseEntityRenderState.color = horseEntity.getVariant();
		horseEntityRenderState.marking = horseEntity.getMarking();
		horseEntityRenderState.armor = horseEntity.getBodyArmor().copy();
	}
}
