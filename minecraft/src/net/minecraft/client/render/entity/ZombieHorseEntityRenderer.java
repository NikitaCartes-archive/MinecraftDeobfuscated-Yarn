package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayer;
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

	public ZombieHorseEntityRenderer(class_5617.class_5618 arg, EntityModelLayer entityModelLayer) {
		super(arg, new HorseEntityModel<>(arg.method_32167(entityModelLayer)), 1.0F);
	}

	public Identifier getTexture(HorseBaseEntity horseBaseEntity) {
		return (Identifier)TEXTURES.get(horseBaseEntity.getType());
	}
}
