package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SpawnHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void spawnEntitiesInChunk(EntityCategory entityCategory, World world, WorldChunk worldChunk, BlockPos blockPos) {
		ChunkGenerator<?> chunkGenerator = world.getChunkManager().getChunkGenerator();
		int i = 0;
		BlockPos blockPos2 = method_8657(world, worldChunk);
		int j = blockPos2.getX();
		int k = blockPos2.getY();
		int l = blockPos2.getZ();
		if (k >= 1) {
			BlockState blockState = worldChunk.getBlockState(blockPos2);
			if (!blockState.isSimpleFullBlock(worldChunk, blockPos2)) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				int m = 0;

				while (m < 3) {
					int n = j;
					int o = l;
					int p = 6;
					Biome.SpawnEntry spawnEntry = null;
					EntityData entityData = null;
					int q = MathHelper.ceil(Math.random() * 4.0);
					int r = 0;
					int s = 0;

					while (true) {
						label119: {
							label94:
							if (s < q) {
								n += world.random.nextInt(6) - world.random.nextInt(6);
								o += world.random.nextInt(6) - world.random.nextInt(6);
								mutable.set(n, k, o);
								float f = (float)n + 0.5F;
								float g = (float)o + 0.5F;
								PlayerEntity playerEntity = world.getClosestPlayer((double)f, (double)g, -1.0);
								if (playerEntity == null
									|| playerEntity.squaredDistanceTo((double)f, (double)k, (double)g) <= 576.0
									|| blockPos.isWithinDistance(new Vec3d((double)f, (double)k, (double)g), 24.0)) {
									break label119;
								}

								ChunkPos chunkPos = new ChunkPos(mutable);
								if (!Objects.equals(chunkPos, worldChunk.getPos())) {
									break label119;
								}

								if (spawnEntry == null) {
									spawnEntry = method_8664(chunkGenerator, entityCategory, world.random, mutable);
									if (spawnEntry == null) {
										break label94;
									}

									q = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
								}

								if (spawnEntry.type.getCategory() == EntityCategory.field_17715) {
									break label119;
								}

								EntityType<?> entityType = spawnEntry.type;
								if (!entityType.isSummonable() || !method_8659(chunkGenerator, entityCategory, spawnEntry, mutable)) {
									break label119;
								}

								SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
								if (location == null
									|| !canSpawn(location, world, mutable, entityType)
									|| !world.doesNotCollide(entityType.createSimpleBoundingBox((double)f, (double)k, (double)g))) {
									break label119;
								}

								MobEntity mobEntity;
								try {
									Entity entity = entityType.create(world);
									if (!(entity instanceof MobEntity)) {
										throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(entityType));
									}

									mobEntity = (MobEntity)entity;
								} catch (Exception var29) {
									LOGGER.warn("Failed to create mob", (Throwable)var29);
									return;
								}

								mobEntity.setPositionAndAngles((double)f, (double)k, (double)g, world.random.nextFloat() * 360.0F, 0.0F);
								if (playerEntity.squaredDistanceTo((double)f, (double)k, (double)g) > 16384.0
										&& mobEntity.canImmediatelyDespawn(playerEntity.squaredDistanceTo((double)f, (double)k, (double)g))
									|| !mobEntity.canSpawn(world, SpawnType.field_16459)
									|| !mobEntity.canSpawn(world)) {
									break label119;
								}

								entityData = mobEntity.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.field_16459, entityData, null);
								i++;
								r++;
								world.spawnEntity(mobEntity);
								if (i >= mobEntity.getLimitPerChunk()) {
									return;
								}

								if (!mobEntity.spawnsTooManyForEachTry(r)) {
									break label119;
								}
							}

							m++;
							break;
						}

						s++;
					}
				}
			}
		}
	}

	@Nullable
	private static Biome.SpawnEntry method_8664(ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Random random, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(entityCategory, blockPos);
		return list.isEmpty() ? null : WeightedPicker.getRandom(random, list);
	}

	private static boolean method_8659(ChunkGenerator<?> chunkGenerator, EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = chunkGenerator.getEntitySpawnList(entityCategory, blockPos);
		return list.isEmpty() ? false : list.contains(spawnEntry);
	}

	private static BlockPos method_8657(World world, WorldChunk worldChunk) {
		ChunkPos chunkPos = worldChunk.getPos();
		int i = chunkPos.getStartX() + world.random.nextInt(16);
		int j = chunkPos.getStartZ() + world.random.nextInt(16);
		int k = worldChunk.sampleHeightmap(Heightmap.Type.field_13202, i, j) + 1;
		int l = world.random.nextInt(k + 1);
		return new BlockPos(i, l, j);
	}

	public static boolean isClearForSpawn(BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (Block.isShapeFullCube(blockState.getCollisionShape(blockView, blockPos))) {
			return false;
		} else if (blockState.emitsRedstonePower()) {
			return false;
		} else {
			return !fluidState.isEmpty() ? false : !blockState.matches(BlockTags.field_15463);
		}
	}

	public static boolean canSpawn(SpawnRestriction.Location location, ViewableWorld viewableWorld, BlockPos blockPos, @Nullable EntityType<?> entityType) {
		if (entityType != null && viewableWorld.getWorldBorder().contains(blockPos)) {
			BlockState blockState = viewableWorld.getBlockState(blockPos);
			FluidState fluidState = viewableWorld.getFluidState(blockPos);
			BlockPos blockPos2 = blockPos.up();
			BlockPos blockPos3 = blockPos.down();
			switch (location) {
				case field_6318:
					return fluidState.matches(FluidTags.field_15517)
						&& viewableWorld.getFluidState(blockPos3).matches(FluidTags.field_15517)
						&& !viewableWorld.getBlockState(blockPos2).isSimpleFullBlock(viewableWorld, blockPos2);
				case field_6317:
				default:
					BlockState blockState2 = viewableWorld.getBlockState(blockPos3);
					return !blockState2.allowsSpawning(viewableWorld, blockPos3, entityType)
						? false
						: isClearForSpawn(viewableWorld, blockPos, blockState, fluidState)
							&& isClearForSpawn(viewableWorld, blockPos2, viewableWorld.getBlockState(blockPos2), viewableWorld.getFluidState(blockPos2));
			}
		} else {
			return false;
		}
	}

	public static void populateEntities(IWorld iWorld, Biome biome, int i, int j, Random random) {
		List<Biome.SpawnEntry> list = biome.getEntitySpawnList(EntityCategory.field_6294);
		if (!list.isEmpty()) {
			int k = i << 4;
			int l = j << 4;

			while (random.nextFloat() < biome.getMaxSpawnLimit()) {
				Biome.SpawnEntry spawnEntry = WeightedPicker.getRandom(random, list);
				int m = spawnEntry.minGroupSize + random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
				EntityData entityData = null;
				int n = k + random.nextInt(16);
				int o = l + random.nextInt(16);
				int p = n;
				int q = o;

				for (int r = 0; r < m; r++) {
					boolean bl = false;

					for (int s = 0; !bl && s < 4; s++) {
						BlockPos blockPos = method_8658(iWorld, spawnEntry.type, n, o);
						if (spawnEntry.type.isSummonable() && canSpawn(SpawnRestriction.Location.field_6317, iWorld, blockPos, spawnEntry.type)) {
							float f = spawnEntry.type.getWidth();
							double d = MathHelper.clamp((double)n, (double)k + (double)f, (double)k + 16.0 - (double)f);
							double e = MathHelper.clamp((double)o, (double)l + (double)f, (double)l + 16.0 - (double)f);
							if (!iWorld.doesNotCollide(spawnEntry.type.createSimpleBoundingBox(d, (double)blockPos.getY(), e))) {
								continue;
							}

							Entity entity;
							try {
								entity = spawnEntry.type.create(iWorld.getWorld());
							} catch (Exception var26) {
								LOGGER.warn("Failed to create mob", (Throwable)var26);
								continue;
							}

							entity.setPositionAndAngles(d, (double)blockPos.getY(), e, random.nextFloat() * 360.0F, 0.0F);
							if (entity instanceof MobEntity) {
								MobEntity mobEntity = (MobEntity)entity;
								if (mobEntity.canSpawn(iWorld, SpawnType.field_16472) && mobEntity.canSpawn(iWorld)) {
									entityData = mobEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.field_16472, entityData, null);
									iWorld.spawnEntity(mobEntity);
									bl = true;
								}
							}
						}

						n += random.nextInt(5) - random.nextInt(5);

						for (o += random.nextInt(5) - random.nextInt(5); n < k || n >= k + 16 || o < l || o >= l + 16; o = q + random.nextInt(5) - random.nextInt(5)) {
							n = p + random.nextInt(5) - random.nextInt(5);
						}
					}
				}
			}
		}
	}

	private static BlockPos method_8658(ViewableWorld viewableWorld, @Nullable EntityType<?> entityType, int i, int j) {
		BlockPos blockPos = new BlockPos(i, viewableWorld.getTop(SpawnRestriction.getHeightMapType(entityType), i, j), j);
		BlockPos blockPos2 = blockPos.down();
		return viewableWorld.getBlockState(blockPos2).canPlaceAtSide(viewableWorld, blockPos2, BlockPlacementEnvironment.field_50) ? blockPos2 : blockPos;
	}
}
