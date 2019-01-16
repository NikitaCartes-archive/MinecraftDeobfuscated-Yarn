package net.minecraft.sortme;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SpawnHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void method_8663(EntityCategory entityCategory, World world, WorldChunk worldChunk, BlockPos blockPos) {
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

				for (int m = 0; m < 3; m++) {
					int n = j;
					int o = l;
					int p = 6;
					Biome.SpawnEntry spawnEntry = null;
					EntityData entityData = null;
					int q = MathHelper.ceil(Math.random() * 4.0);
					int r = 0;

					for (int s = 0; s < q; s++) {
						n += world.random.nextInt(6) - world.random.nextInt(6);
						o += world.random.nextInt(6) - world.random.nextInt(6);
						mutable.set(n, k, o);
						float f = (float)n + 0.5F;
						float g = (float)o + 0.5F;
						PlayerEntity playerEntity = world.findClosestVisiblePlayer((double)f, (double)g, -1.0);
						if (playerEntity != null) {
							double d = playerEntity.squaredDistanceTo((double)f, (double)k, (double)g);
							if (!(d <= 576.0) && !(blockPos.squaredDistanceTo((double)f, (double)k, (double)g) < 576.0)) {
								ChunkPos chunkPos = new ChunkPos(mutable);
								if (Objects.equals(chunkPos, worldChunk.getPos())) {
									if (spawnEntry == null) {
										spawnEntry = method_8664(chunkGenerator, entityCategory, world.random, mutable);
										if (spawnEntry == null) {
											break;
										}

										q = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
									}

									if (MobEntity.class.isAssignableFrom(spawnEntry.type.getEntityClass())) {
										EntityType<? extends MobEntity> entityType = (EntityType<? extends MobEntity>)spawnEntry.type;
										if (entityType.isSummonable() && method_8659(chunkGenerator, entityCategory, spawnEntry, mutable)) {
											SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
											if (location != null
												&& canSpawn(location, world, mutable, entityType)
												&& world.method_8587(null, entityType.method_17683((double)f, (double)k, (double)g))) {
												MobEntity mobEntity;
												try {
													mobEntity = entityType.create(world);
													if (mobEntity == null) {
														throw new NullPointerException(Registry.ENTITY_TYPE.getId(entityType) + " is unable to create an instance");
													}
												} catch (Exception var31) {
													LOGGER.warn("Failed to create mob", (Throwable)var31);
													return;
												}

												mobEntity.setPositionAndAngles((double)f, (double)k, (double)g, world.random.nextFloat() * 360.0F, 0.0F);
												if ((!(d > 16384.0) || !mobEntity.canImmediatelyDespawn(d)) && mobEntity.canSpawn(world, SpawnType.field_16459) && mobEntity.method_5957(world)) {
													entityData = mobEntity.prepareEntityData(world, world.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.field_16459, entityData, null);
													i++;
													r++;
													world.spawnEntity(mobEntity);
													if (i >= mobEntity.getLimitPerChunk()) {
														return;
													}

													if (mobEntity.spawnsTooManyForEachTry(r)) {
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
		int i = chunkPos.getXStart() + world.random.nextInt(16);
		int j = chunkPos.getZStart() + world.random.nextInt(16);
		int k = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
		int l = world.random.nextInt(k + 1);
		return new BlockPos(i, l, j);
	}

	public static boolean isClearForSpawn(BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (blockState.method_11603(blockView, blockPos)) {
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
					if (!blockState2.hasSolidTopSurface(viewableWorld, blockPos3) && !SpawnRestriction.canSpawn(entityType, blockState2)) {
						return false;
					} else {
						Block block = blockState2.getBlock();
						boolean bl = block != Blocks.field_9987 && block != Blocks.field_10499;
						return bl
							&& isClearForSpawn(viewableWorld, blockPos, blockState, fluidState)
							&& isClearForSpawn(viewableWorld, blockPos2, viewableWorld.getBlockState(blockPos2), viewableWorld.getFluidState(blockPos2));
					}
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
							float f = spawnEntry.type.method_17685();
							double d = MathHelper.clamp((double)n, (double)k + (double)f, (double)k + 16.0 - (double)f);
							double e = MathHelper.clamp((double)o, (double)l + (double)f, (double)l + 16.0 - (double)f);
							if (!iWorld.method_8587(null, spawnEntry.type.method_17683(d, (double)blockPos.getY(), e))) {
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
								if (mobEntity.canSpawn(iWorld, SpawnType.field_16472) && mobEntity.method_5957(iWorld)) {
									entityData = mobEntity.prepareEntityData(iWorld, iWorld.getLocalDifficulty(new BlockPos(mobEntity)), SpawnType.field_16472, entityData, null);
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
