package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public final class HorseEntityRenderer extends HorseBaseEntityRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
	private static final Map<HorseColor, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseColor.class), enumMap -> {
		enumMap.put(HorseColor.field_23816, new Identifier("textures/entity/horse/horse_white.png"));
		enumMap.put(HorseColor.field_23817, new Identifier("textures/entity/horse/horse_creamy.png"));
		enumMap.put(HorseColor.field_23818, new Identifier("textures/entity/horse/horse_chestnut.png"));
		enumMap.put(HorseColor.field_23819, new Identifier("textures/entity/horse/horse_brown.png"));
		enumMap.put(HorseColor.field_23820, new Identifier("textures/entity/horse/horse_black.png"));
		enumMap.put(HorseColor.field_23821, new Identifier("textures/entity/horse/horse_gray.png"));
		enumMap.put(HorseColor.field_23822, new Identifier("textures/entity/horse/horse_darkbrown.png"));
	});

	public HorseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new HorseEntityModel<>(0.0F), 1.1F);
		this.addFeature(new HorseMarkingFeatureRenderer(this));
		this.addFeature(new HorseArmorFeatureRenderer(this));
	}

	public Identifier method_3983(HorseEntity horseEntity) {
		return (Identifier)TEXTURES.get(horseEntity.getColor());
	}
}
