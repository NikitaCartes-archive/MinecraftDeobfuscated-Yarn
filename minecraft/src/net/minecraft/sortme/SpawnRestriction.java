package net.minecraft.sortme;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.world.gen.Heightmap;

public class SpawnRestriction {
	private static final Map<EntityType<?>, SpawnRestriction.Entry> mapping = Maps.<EntityType<?>, SpawnRestriction.Entry>newHashMap();

	private static void method_6161(EntityType<?> entityType, SpawnRestriction.Location location, Heightmap.Type type) {
		method_6157(entityType, location, type, null);
	}

	private static void method_6157(EntityType<?> entityType, SpawnRestriction.Location location, Heightmap.Type type, @Nullable Tag<Block> tag) {
		mapping.put(entityType, new SpawnRestriction.Entry(type, location, tag));
	}

	@Nullable
	public static SpawnRestriction.Location method_6159(EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null ? null : entry.field_6315;
	}

	public static Heightmap.Type method_6160(@Nullable EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entry.field_6314;
	}

	public static boolean method_6158(EntityType<?> entityType, BlockState blockState) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null ? false : entry.field_6316 != null && blockState.method_11602(entry.field_6316);
	}

	static {
		method_6161(EntityType.COD, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.DOLPHIN, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.DROWNED, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.GUARDIAN, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.PUFFERFISH, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SALMON, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SQUID, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.TROPICAL_FISH, SpawnRestriction.Location.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6157(EntityType.OCELOT, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING, BlockTags.field_15503);
		method_6157(EntityType.PARROT, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING, BlockTags.field_15503);
		method_6157(EntityType.POLAR_BEAR, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlockTags.field_15467);
		method_6161(EntityType.BAT, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.BLAZE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.CAVE_SPIDER, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.CHICKEN, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.COW, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.CREEPER, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.DONKEY, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ENDERMAN, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ENDERMITE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ENDER_DRAGON, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.GHAST, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.GIANT, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.HORSE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.HUSK, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.PILLAGER, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.LLAMA, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.MAGMA_CUBE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.MOOSHROOM, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.MULE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.PIG, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.RABBIT, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SHEEP, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SILVERFISH, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SKELETON, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SKELETON_HORSE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SLIME, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SNOW_GOLEM, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SPIDER, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.STRAY, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.TURTLE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.VILLAGER, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.IRON_GOLEM, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WITCH, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WITHER, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WITHER_SKELETON, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WOLF, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE_HORSE, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE_PIGMAN, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE_VILLAGER, SpawnRestriction.Location.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
	}

	static class Entry {
		private final Heightmap.Type field_6314;
		private final SpawnRestriction.Location field_6315;
		@Nullable
		private final Tag<Block> field_6316;

		public Entry(Heightmap.Type type, SpawnRestriction.Location location, @Nullable Tag<Block> tag) {
			this.field_6314 = type;
			this.field_6315 = location;
			this.field_6316 = tag;
		}
	}

	public static enum Location {
		field_6317,
		field_6318;
	}
}
