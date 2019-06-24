package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;

public class SpawnRestriction {
	private static final Map<EntityType<?>, SpawnRestriction.Entry> mapping = Maps.<EntityType<?>, SpawnRestriction.Entry>newHashMap();

	private static <T extends MobEntity> void method_20637(
		EntityType<T> entityType, SpawnRestriction.Location location, Heightmap.Type type, SpawnRestriction.class_4306<T> arg
	) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.put(entityType, new SpawnRestriction.Entry(type, location, arg));
		if (entry != null) {
			throw new IllegalStateException("Duplicate registration for type " + Registry.ENTITY_TYPE.getId(entityType));
		}
	}

	public static SpawnRestriction.Location getLocation(EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null ? SpawnRestriction.Location.NO_RESTRICTIONS : entry.location;
	}

	public static Heightmap.Type getHeightMapType(@Nullable EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entry.heightMapType;
	}

	public static <T extends Entity> boolean method_20638(EntityType<T> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)mapping.get(entityType);
		return entry == null || entry.field_19349.test(entityType, iWorld, spawnType, blockPos, random);
	}

	static {
		method_20637(EntityType.COD, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::method_20662);
		method_20637(EntityType.DOLPHIN, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DolphinEntity::method_20664);
		method_20637(EntityType.DROWNED, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DrownedEntity::method_20673);
		method_20637(EntityType.GUARDIAN, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::method_20676);
		method_20637(EntityType.PUFFERFISH, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::method_20662);
		method_20637(EntityType.SALMON, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::method_20662);
		method_20637(EntityType.SQUID, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SquidEntity::method_20670);
		method_20637(EntityType.TROPICAL_FISH, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::method_20662);
		method_20637(EntityType.BAT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BatEntity::method_20661);
		method_20637(EntityType.BLAZE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20681);
		method_20637(EntityType.CAVE_SPIDER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.CHICKEN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.COW, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.CREEPER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.DONKEY, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.ENDERMAN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.ENDERMITE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndermiteEntity::method_20674);
		method_20637(EntityType.ENDER_DRAGON, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
		method_20637(EntityType.GHAST, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhastEntity::method_20675);
		method_20637(EntityType.GIANT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.HORSE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.HUSK, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HuskEntity::method_20677);
		method_20637(EntityType.IRON_GOLEM, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
		method_20637(EntityType.LLAMA, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.MAGMA_CUBE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MagmaCubeEntity::method_20678);
		method_20637(EntityType.MOOSHROOM, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MooshroomEntity::method_20665);
		method_20637(EntityType.MULE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.OCELOT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, OcelotEntity::method_20666);
		method_20637(EntityType.PARROT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, ParrotEntity::method_20667);
		method_20637(EntityType.PIG, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.PILLAGER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PatrolEntity::method_20739);
		method_20637(EntityType.POLAR_BEAR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PolarBearEntity::method_20668);
		method_20637(EntityType.RABBIT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RabbitEntity::method_20669);
		method_20637(EntityType.SHEEP, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.SILVERFISH, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SilverfishEntity::method_20684);
		method_20637(EntityType.SKELETON, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.SKELETON_HORSE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.SLIME, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlimeEntity::method_20685);
		method_20637(EntityType.SNOW_GOLEM, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
		method_20637(EntityType.SPIDER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.STRAY, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StrayEntity::method_20686);
		method_20637(EntityType.TURTLE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TurtleEntity::method_20671);
		method_20637(EntityType.VILLAGER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
		method_20637(EntityType.WITCH, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.WITHER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.WITHER_SKELETON, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.WOLF, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.ZOMBIE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.ZOMBIE_HORSE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.ZOMBIE_PIGMAN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ZombiePigmanEntity::method_20682);
		method_20637(EntityType.ZOMBIE_VILLAGER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.CAT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.ELDER_GUARDIAN, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::method_20676);
		method_20637(EntityType.EVOKER, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.FOX, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.ILLUSIONER, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.PANDA, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.PHANTOM, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
		method_20637(EntityType.RAVAGER, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.SHULKER, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
		method_20637(EntityType.TRADER_LLAMA, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::method_20663);
		method_20637(EntityType.VEX, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.VINDICATOR, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::method_20680);
		method_20637(EntityType.WANDERING_TRADER, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::method_20636);
	}

	static class Entry {
		private final Heightmap.Type heightMapType;
		private final SpawnRestriction.Location location;
		private final SpawnRestriction.class_4306<?> field_19349;

		public Entry(Heightmap.Type type, SpawnRestriction.Location location, SpawnRestriction.class_4306<?> arg) {
			this.heightMapType = type;
			this.location = location;
			this.field_19349 = arg;
		}
	}

	public static enum Location {
		ON_GROUND,
		IN_WATER,
		NO_RESTRICTIONS;
	}

	@FunctionalInterface
	public interface class_4306<T extends Entity> {
		boolean test(EntityType<T> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random);
	}
}
