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
		register(EntityType.field_6070, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6087, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6123, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6118, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6062, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6073, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6114, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6111, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203);
		register(EntityType.field_6108, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6099, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6084, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6132, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6085, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6046, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6067, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6091, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6128, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6116, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6107, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6095, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6139, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6071, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6147, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6074, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6102, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6143, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6057, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6081, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13197);
		register(EntityType.field_6104, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13197);
		register(EntityType.field_6093, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6105, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6042, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6140, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6115, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6125, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6137, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6075, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6069, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6047, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6079, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6098, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6113, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6077, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6145, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6119, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6076, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6055, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6051, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6048, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6050, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
		register(EntityType.field_6054, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203);
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
