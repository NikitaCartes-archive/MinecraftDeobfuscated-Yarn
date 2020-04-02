package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SpawnHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void spawnEntitiesInChunk(EntityCategory category, ServerWorld world, WorldChunk chunk) {
		BlockPos blockPos = getSpawnPos(world, chunk);
		if (blockPos.getY() >= 1) {
			spawnEntitiesInChunk(category, world, chunk, blockPos);
		}
	}

	public static void spawnEntitiesInChunk(EntityCategory category, ServerWorld world, Chunk chunk, BlockPos pos) {
		StructureAccessor structureAccessor = world.getStructureAccessor();
		ChunkGenerator<?> chunkGenerator = world.getChunkManager().getChunkGenerator();
		int i = pos.getY();
		BlockState blockState = chunk.getBlockState(pos);
		if (!blockState.isSolidBlock(chunk, pos)) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			int j = 0;

			for (int k = 0; k < 3; k++) {
				int l = pos.getX();
				int m = pos.getZ();
				int n = 6;
				Biome.SpawnEntry spawnEntry = null;
				EntityData entityData = null;
				int o = MathHelper.ceil(Math.random() * 4.0);
				int p = 0;

				for (int q = 0; q < o; q++) {
					l += world.random.nextInt(6) - world.random.nextInt(6);
					m += world.random.nextInt(6) - world.random.nextInt(6);
					mutable.set(l, i, m);
					float f = (float)l + 0.5F;
					float g = (float)m + 0.5F;
					PlayerEntity playerEntity = world.getClosestPlayer((double)f, (double)i, (double)g, -1.0, false);
					if (playerEntity != null) {
						double d = playerEntity.squaredDistanceTo((double)f, (double)i, (double)g);
						if (method_24933(world, chunk, mutable, d)) {
							if (spawnEntry == null) {
								spawnEntry = pickRandomSpawnEntry(structureAccessor, chunkGenerator, category, world.random, mutable);
								if (spawnEntry == null) {
									break;
								}

								o = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
							}

							if (method_24934(world, category, structureAccessor, chunkGenerator, spawnEntry, mutable, d)) {
								MobEntity mobEntity = method_24931(world, spawnEntry.type);
								if (mobEntity == null) {
									return;
								}

								mobEntity.refreshPositionAndAngles((double)f, (double)i, (double)g, world.random.nextFloat() * 360.0F, 0.0F);
								if (method_24932(world, mobEntity, d)) {
									entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnType.NATURAL, entityData, null);
									j++;
									p++;
									world.spawnEntity(mobEntity);
									if (j >= mobEntity.getLimitPerChunk()) {
										return;
									}

									if (mobEntity.spawnsTooManyForEachTry(p)) {
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static boolean method_24933(ServerWorld serverWorld, Chunk chunk, BlockPos.Mutable mutable, double d) {
		if (d <= 576.0) {
			return false;
		} else if (serverWorld.getSpawnPos()
			.isWithinDistance(new Vec3d((double)((float)mutable.getX() + 0.5F), (double)mutable.getY(), (double)((float)mutable.getZ() + 0.5F)), 24.0)) {
			return false;
		} else {
			ChunkPos chunkPos = new ChunkPos(mutable);
			return Objects.equals(chunkPos, chunk.getPos()) || serverWorld.getChunkManager().shouldTickChunk(chunkPos);
		}
	}

	private static boolean method_24934(
		ServerWorld serverWorld,
		EntityCategory entityCategory,
		StructureAccessor structureAccessor,
		ChunkGenerator<?> chunkGenerator,
		Biome.SpawnEntry spawnEntry,
		BlockPos.Mutable mutable,
		double d
	) {
		EntityType<?> entityType = spawnEntry.type;
		if (entityType.getCategory() == EntityCategory.MISC) {
			return false;
		} else if (!entityType.isSpawnableFarFromPlayer() && d > (double)(entityType.method_24908() * entityType.method_24908())) {
			return false;
		} else if (entityType.isSummonable() && containsSpawnEntry(structureAccessor, chunkGenerator, entityCategory, spawnEntry, mutable)) {
			SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
			if (!canSpawn(location, serverWorld, mutable, entityType)) {
				return false;
			} else {
				return !SpawnRestriction.canSpawn(entityType, serverWorld, SpawnType.NATURAL, mutable, serverWorld.random)
					? false
					: serverWorld.doesNotCollide(
						entityType.createSimpleBoundingBox((double)((float)mutable.getX() + 0.5F), (double)mutable.getY(), (double)((float)mutable.getZ() + 0.5F))
					);
			}
		} else {
			return false;
		}
	}

	@Nullable
	private static MobEntity method_24931(ServerWorld serverWorld, EntityType<?> entityType) {
		try {
			Entity entity = entityType.create(serverWorld);
			if (!(entity instanceof MobEntity)) {
				throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(entityType));
			} else {
				return (MobEntity)entity;
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed to create mob", (Throwable)var4);
			return null;
		}
	}

	private static boolean method_24932(ServerWorld serverWorld, MobEntity mobEntity, double d) {
		return d > (double)(mobEntity.getType().method_24908() * mobEntity.getType().method_24908()) && mobEntity.canImmediatelyDespawn(d)
			? false
			: mobEntity.canSpawn(serverWorld, SpawnType.NATURAL) && mobEntity.canSpawn(serverWorld);
	}

	@Nullable
	private static Biome.SpawnEntry pickRandomSpawnEntry(
		StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Random random, BlockPos blockPos
	) {
		List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(structureAccessor, entityCategory, blockPos);
		return list.isEmpty() ? null : WeightedPicker.getRandom(random, list);
	}

	private static boolean containsSpawnEntry(
		StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos blockPos
	) {
		return chunkGenerator.getEntitySpawnList(structureAccessor, entityCategory, blockPos).contains(spawnEntry);
	}

	private static BlockPos getSpawnPos(World world, WorldChunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX() + world.random.nextInt(16);
		int j = chunkPos.getStartZ() + world.random.nextInt(16);
		int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
		int l = world.random.nextInt(k + 1);
		return new BlockPos(i, l, j);
	}

	public static boolean isClearForSpawn(BlockView blockView, BlockPos pos, BlockState state, FluidState fluidState) {
		if (state.isFullCube(blockView, pos)) {
			return false;
		} else if (state.emitsRedstonePower()) {
			return false;
		} else {
			return !fluidState.isEmpty() ? false : !state.isIn(BlockTags.RAILS);
		}
	}

	public static boolean canSpawn(SpawnRestriction.Location location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType) {
		if (location == SpawnRestriction.Location.NO_RESTRICTIONS) {
			return true;
		} else if (entityType != null && world.getWorldBorder().contains(pos)) {
			BlockState blockState = world.getBlockState(pos);
			FluidState fluidState = world.getFluidState(pos);
			BlockPos blockPos = pos.up();
			BlockPos blockPos2 = pos.down();
			switch (location) {
				case IN_WATER:
					return fluidState.matches(FluidTags.WATER)
						&& world.getFluidState(blockPos2).matches(FluidTags.WATER)
						&& !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
				case IN_LAVA:
					return fluidState.matches(FluidTags.LAVA)
						&& world.getFluidState(blockPos2).matches(FluidTags.LAVA)
						&& !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
				case ON_GROUND:
				default:
					BlockState blockState2 = world.getBlockState(blockPos2);
					return !blockState2.allowsSpawning(world, blockPos2, entityType)
						? false
						: isClearForSpawn(world, pos, blockState, fluidState) && isClearForSpawn(world, blockPos, world.getBlockState(blockPos), world.getFluidState(blockPos));
			}
		} else {
			return false;
		}
	}

	public static void populateEntities(IWorld world, Biome biome, int chunkX, int chunkZ, Random random) {
		List<Biome.SpawnEntry> list = biome.getEntitySpawnList(EntityCategory.CREATURE);
		if (!list.isEmpty()) {
			int i = chunkX << 4;
			int j = chunkZ << 4;

			while (random.nextFloat() < biome.getMaxSpawnLimit()) {
				Biome.SpawnEntry spawnEntry = WeightedPicker.getRandom(random, list);
				int k = spawnEntry.minGroupSize + random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
				EntityData entityData = null;
				int l = i + random.nextInt(16);
				int m = j + random.nextInt(16);
				int n = l;
				int o = m;

				for (int p = 0; p < k; p++) {
					boolean bl = false;

					for (int q = 0; !bl && q < 4; q++) {
						BlockPos blockPos = getEntitySpawnPos(world, spawnEntry.type, l, m);
						if (spawnEntry.type.isSummonable() && canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos, spawnEntry.type)) {
							float f = spawnEntry.type.getWidth();
							double d = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0 - (double)f);
							double e = MathHelper.clamp((double)m, (double)j + (double)f, (double)j + 16.0 - (double)f);
							if (!world.doesNotCollide(spawnEntry.type.createSimpleBoundingBox(d, (double)blockPos.getY(), e))
								|| !SpawnRestriction.canSpawn(spawnEntry.type, world, SpawnType.CHUNK_GENERATION, new BlockPos(d, (double)blockPos.getY(), e), world.getRandom())) {
								continue;
							}

							Entity entity;
							try {
								entity = spawnEntry.type.create(world.getWorld());
							} catch (Exception var26) {
								LOGGER.warn("Failed to create mob", (Throwable)var26);
								continue;
							}

							entity.refreshPositionAndAngles(d, (double)blockPos.getY(), e, random.nextFloat() * 360.0F, 0.0F);
							if (entity instanceof MobEntity) {
								MobEntity mobEntity = (MobEntity)entity;
								if (mobEntity.canSpawn(world, SpawnType.CHUNK_GENERATION) && mobEntity.canSpawn(world)) {
									entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnType.CHUNK_GENERATION, entityData, null);
									world.spawnEntity(mobEntity);
									bl = true;
								}
							}
						}

						l += random.nextInt(5) - random.nextInt(5);

						for (m += random.nextInt(5) - random.nextInt(5); l < i || l >= i + 16 || m < j || m >= j + 16; m = o + random.nextInt(5) - random.nextInt(5)) {
							l = n + random.nextInt(5) - random.nextInt(5);
						}
					}
				}
			}
		}
	}

	private static BlockPos getEntitySpawnPos(WorldView world, @Nullable EntityType<?> entityType, int x, int z) {
		BlockPos blockPos = new BlockPos(x, world.getTopY(SpawnRestriction.getHeightmapType(entityType), x, z), z);
		BlockPos blockPos2 = blockPos.down();
		return world.getBlockState(blockPos2).canPathfindThrough(world, blockPos2, NavigationType.LAND) ? blockPos2 : blockPos;
	}
}
