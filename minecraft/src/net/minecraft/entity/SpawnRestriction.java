package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.Heightmap;

public class SpawnRestriction {
	private static final Map<EntityType<?>, SpawnRestriction.Entry> mapping = Maps.<EntityType<?>, SpawnRestriction.Entry>newHashMap();

	private static void register(EntityType<?> entityType, SpawnRestriction.Location location, Heightmap.Type type) {
		mapping.put(entityType, new SpawnRestriction.Entry(type, location));
	}

	@Nullable
	public static SpawnRestriction.Location getLocation(EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null ? null : entry.location;
	}

	public static Heightmap.Type getHeightMapType(@Nullable EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null ? Heightmap.Type.field_13203 : entry.heightMapType;
	}

	static {
		register(EntityType.COD, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.DOLPHIN, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.DROWNED, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.GUARDIAN, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.PUFFERFISH, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.SALMON, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.SQUID, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.TROPICAL_FISH, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.BAT, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.BLAZE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.CAVE_SPIDER, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.CHICKEN, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.COW, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.CREEPER, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.DONKEY, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.ENDERMAN, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.ENDERMITE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.ENDER_DRAGON, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.GHAST, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.GIANT, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.HORSE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.HUSK, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.IRON_GOLEM, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.LLAMA, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.MAGMA_CUBE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.MOOSHROOM, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.MULE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.OCELOT, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13197);
		register(EntityType.PARROT, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13197);
		register(EntityType.PIG, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.PILLAGER, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.POLAR_BEAR, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.RABBIT, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.SHEEP, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.SILVERFISH, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.SKELETON, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.SKELETON_HORSE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.SLIME, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.SNOW_GOLEM, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.SPIDER, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.STRAY, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.TURTLE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.VILLAGER, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.WITCH, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.WITHER, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.WITHER_SKELETON, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.WOLF, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.ZOMBIE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.ZOMBIE_HORSE, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.ZOMBIE_PIGMAN, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.ZOMBIE_VILLAGER, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
	}

	static class Entry {
		private final Heightmap.Type heightMapType;
		private final SpawnRestriction.Location location;

		public Entry(Heightmap.Type type, SpawnRestriction.Location location) {
			this.heightMapType = type;
			this.location = location;
		}
	}

	public static enum Location {
		field_6317,
		field_6318;
	}
}
