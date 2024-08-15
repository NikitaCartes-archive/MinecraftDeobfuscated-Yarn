package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldView;

public class SpawnRestriction {
	private static final Map<EntityType<?>, SpawnRestriction.Entry> RESTRICTIONS = Maps.<EntityType<?>, SpawnRestriction.Entry>newHashMap();

	private static <T extends MobEntity> void register(
		EntityType<T> type, SpawnLocation location, Heightmap.Type heightmapType, SpawnRestriction.SpawnPredicate<T> predicate
	) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.put(type, new SpawnRestriction.Entry(heightmapType, location, predicate));
		if (entry != null) {
			throw new IllegalStateException("Duplicate registration for type " + Registries.ENTITY_TYPE.getId(type));
		}
	}

	public static SpawnLocation getLocation(EntityType<?> type) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.get(type);
		return entry == null ? SpawnLocationTypes.UNRESTRICTED : entry.location;
	}

	public static boolean isSpawnPosAllowed(EntityType<?> type, WorldView world, BlockPos pos) {
		return getLocation(type).isSpawnPositionOk(world, pos, type);
	}

	public static Heightmap.Type getHeightmapType(@Nullable EntityType<?> type) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.get(type);
		return entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entry.heightmapType;
	}

	public static <T extends Entity> boolean canSpawn(EntityType<T> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.get(type);
		return entry == null || entry.predicate.test(type, world, spawnReason, pos, random);
	}

	static {
		register(EntityType.AXOLOTL, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AxolotlEntity::canSpawn);
		register(EntityType.COD, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);
		register(EntityType.DOLPHIN, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterAnimalEntity::canSpawn);
		register(EntityType.DROWNED, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DrownedEntity::canSpawn);
		register(EntityType.GUARDIAN, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::canSpawn);
		register(EntityType.PUFFERFISH, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);
		register(EntityType.SALMON, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);
		register(EntityType.SQUID, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterAnimalEntity::canSpawn);
		register(EntityType.TROPICAL_FISH, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TropicalFishEntity::canTropicalFishSpawn);
		register(EntityType.ARMADILLO, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ArmadilloEntity::canSpawn);
		register(EntityType.BAT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BatEntity::canSpawn);
		register(EntityType.BLAZE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
		register(EntityType.BOGGED, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.BREEZE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
		register(EntityType.CAVE_SPIDER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.CHICKEN, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.COW, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.CREEPER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.DONKEY, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.ENDERMAN, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.ENDERMITE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndermiteEntity::canSpawn);
		register(EntityType.ENDER_DRAGON, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		register(EntityType.FROG, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FrogEntity::canSpawn);
		register(EntityType.GHAST, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhastEntity::canSpawn);
		register(EntityType.GIANT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.GLOW_SQUID, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GlowSquidEntity::canSpawn);
		register(EntityType.GOAT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GoatEntity::canSpawn);
		register(EntityType.HORSE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.HUSK, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HuskEntity::canSpawn);
		register(EntityType.IRON_GOLEM, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		register(EntityType.LLAMA, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.MAGMA_CUBE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MagmaCubeEntity::canMagmaCubeSpawn);
		register(EntityType.MOOSHROOM, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MooshroomEntity::canSpawn);
		register(EntityType.MULE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.OCELOT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, OcelotEntity::canSpawn);
		register(EntityType.PARROT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, ParrotEntity::canSpawn);
		register(EntityType.PIG, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.HOGLIN, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HoglinEntity::canSpawn);
		register(EntityType.PIGLIN, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PiglinEntity::canSpawn);
		register(EntityType.PILLAGER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PatrolEntity::canSpawn);
		register(EntityType.POLAR_BEAR, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PolarBearEntity::canSpawn);
		register(EntityType.RABBIT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RabbitEntity::canSpawn);
		register(EntityType.SHEEP, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.SILVERFISH, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SilverfishEntity::canSpawn);
		register(EntityType.SKELETON, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.SKELETON_HORSE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SkeletonHorseEntity::canSpawn);
		register(EntityType.SLIME, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlimeEntity::canSpawn);
		register(EntityType.SNOW_GOLEM, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		register(EntityType.SPIDER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.STRAY, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StrayEntity::canSpawn);
		register(EntityType.STRIDER, SpawnLocationTypes.IN_LAVA, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StriderEntity::canSpawn);
		register(EntityType.TURTLE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TurtleEntity::canSpawn);
		register(EntityType.VILLAGER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		register(EntityType.WITCH, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.WITHER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.WITHER_SKELETON, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.WOLF, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WolfEntity::canSpawn);
		register(EntityType.ZOGLIN, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
		register(EntityType.ZOMBIE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.ZOMBIE_HORSE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ZombieHorseEntity::canSpawn);
		register(EntityType.ZOMBIFIED_PIGLIN, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ZombifiedPiglinEntity::canSpawn);
		register(EntityType.ZOMBIE_VILLAGER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.CAT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.ELDER_GUARDIAN, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::canSpawn);
		register(EntityType.EVOKER, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.FOX, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FoxEntity::canSpawn);
		register(EntityType.ILLUSIONER, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.PANDA, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.PHANTOM, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		register(EntityType.RAVAGER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.SHULKER, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		register(EntityType.TRADER_LLAMA, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.VEX, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.VINDICATOR, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		register(EntityType.WANDERING_TRADER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		register(EntityType.WARDEN, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
	}

	static record Entry(Heightmap.Type heightmapType, SpawnLocation location, SpawnRestriction.SpawnPredicate<?> predicate) {
	}

	@FunctionalInterface
	public interface SpawnPredicate<T extends Entity> {
		boolean test(EntityType<T> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random);
	}
}
