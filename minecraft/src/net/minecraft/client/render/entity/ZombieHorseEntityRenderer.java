package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieHorseEntityRenderer extends HorseBaseEntityRenderer<HorseBaseEntity, HorseEntityModel<HorseBaseEntity>> {
	private static final Map<EntityType<?>, Identifier> TEXTURES = Maps.<EntityType<?>, Identifier>newHashMap(
		ImmutableMap.of(
			EntityType.ZOMBIE_HORSE,
			new Identifier("textures/entity/horse/horse_zombie.png"),
			EntityType.SKELETON_HORSE,
			new Identifier("textures/entity/horse/horse_skeleton.png")
		)
	);

	public ZombieHorseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new HorseEntityModel<>(RenderLayer::getEntityCutoutNoCull, 0.0F), 1.0F);
	}

	public Identifier method_4145(HorseBaseEntity horseBaseEntity) {
		return (Identifier)TEXTURES.get(horseBaseEntity.getType());
	}
}
