package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.world.gen.Heightmap;

public class class_1317 {
	private static final Map<EntityType<?>, class_1317.class_1318> field_6313 = Maps.<EntityType<?>, class_1317.class_1318>newHashMap();

	private static void method_6161(EntityType<?> entityType, class_1317.class_1319 arg, Heightmap.Type type) {
		method_6157(entityType, arg, type, null);
	}

	private static void method_6157(EntityType<?> entityType, class_1317.class_1319 arg, Heightmap.Type type, @Nullable Tag<Block> tag) {
		field_6313.put(entityType, new class_1317.class_1318(type, arg, tag));
	}

	@Nullable
	public static class_1317.class_1319 method_6159(EntityType<? extends MobEntity> entityType) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(entityType);
		return lv == null ? null : lv.field_6315;
	}

	public static Heightmap.Type method_6160(@Nullable EntityType<? extends MobEntity> entityType) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(entityType);
		return lv == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : lv.field_6314;
	}

	public static boolean method_6158(EntityType<? extends MobEntity> entityType, BlockState blockState) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(entityType);
		return lv == null ? false : lv.field_6316 != null && blockState.matches(lv.field_6316);
	}

	static {
		method_6161(EntityType.COD, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.DOLPHIN, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.DROWNED, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.GUARDIAN, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.PUFFERFISH, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SALMON, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SQUID, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.TROPICAL_FISH, class_1317.class_1319.field_6318, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6157(EntityType.OCELOT, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING, BlockTags.field_15503);
		method_6157(EntityType.PARROT, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING, BlockTags.field_15503);
		method_6157(EntityType.POLAR_BEAR, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlockTags.field_15467);
		method_6161(EntityType.BAT, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.BLAZE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.CAVE_SPIDER, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.CHICKEN, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.COW, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.CREEPER, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.DONKEY, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ENDERMAN, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ENDERMITE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ENDER_DRAGON, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.GHAST, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.GIANT, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.HORSE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.HUSK, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.PILLAGER, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.LLAMA, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.MAGMA_CUBE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.MOOSHROOM, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.MULE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.PIG, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.RABBIT, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SHEEP, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SILVERFISH, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SKELETON, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SKELETON_HORSE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SLIME, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SNOW_GOLEM, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.SPIDER, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.STRAY, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.TURTLE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.VILLAGER, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.IRON_GOLEM, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WITCH, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WITHER, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WITHER_SKELETON, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.WOLF, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE_HORSE, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE_PIGMAN, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		method_6161(EntityType.ZOMBIE_VILLAGER, class_1317.class_1319.field_6317, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
	}

	static class class_1318 {
		private final Heightmap.Type field_6314;
		private final class_1317.class_1319 field_6315;
		@Nullable
		private final Tag<Block> field_6316;

		public class_1318(Heightmap.Type type, class_1317.class_1319 arg, @Nullable Tag<Block> tag) {
			this.field_6314 = type;
			this.field_6315 = arg;
			this.field_6316 = tag;
		}
	}

	public static enum class_1319 {
		field_6317,
		field_6318;
	}
}
