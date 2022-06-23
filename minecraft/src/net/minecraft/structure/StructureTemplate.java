package net.minecraft.structure;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class StructureTemplate {
	public static final String PALETTE_KEY = "palette";
	public static final String PALETTES_KEY = "palettes";
	public static final String ENTITIES_KEY = "entities";
	public static final String BLOCKS_KEY = "blocks";
	public static final String BLOCKS_POS_KEY = "pos";
	public static final String BLOCKS_STATE_KEY = "state";
	public static final String BLOCKS_NBT_KEY = "nbt";
	public static final String ENTITIES_POS_KEY = "pos";
	public static final String ENTITIES_BLOCK_POS_KEY = "blockPos";
	public static final String ENTITIES_NBT_KEY = "nbt";
	public static final String SIZE_KEY = "size";
	static final int field_31698 = 16;
	private final List<StructureTemplate.PalettedBlockInfoList> blockInfoLists = Lists.<StructureTemplate.PalettedBlockInfoList>newArrayList();
	private final List<StructureTemplate.StructureEntityInfo> entities = Lists.<StructureTemplate.StructureEntityInfo>newArrayList();
	private Vec3i size = Vec3i.ZERO;
	private String author = "?";

	public Vec3i getSize() {
		return this.size;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return this.author;
	}

	public void saveFromWorld(World world, BlockPos start, Vec3i dimensions, boolean includeEntities, @Nullable Block ignoredBlock) {
		if (dimensions.getX() >= 1 && dimensions.getY() >= 1 && dimensions.getZ() >= 1) {
			BlockPos blockPos = start.add(dimensions).add(-1, -1, -1);
			List<StructureTemplate.StructureBlockInfo> list = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();
			List<StructureTemplate.StructureBlockInfo> list2 = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();
			List<StructureTemplate.StructureBlockInfo> list3 = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();
			BlockPos blockPos2 = new BlockPos(Math.min(start.getX(), blockPos.getX()), Math.min(start.getY(), blockPos.getY()), Math.min(start.getZ(), blockPos.getZ()));
			BlockPos blockPos3 = new BlockPos(Math.max(start.getX(), blockPos.getX()), Math.max(start.getY(), blockPos.getY()), Math.max(start.getZ(), blockPos.getZ()));
			this.size = dimensions;

			for (BlockPos blockPos4 : BlockPos.iterate(blockPos2, blockPos3)) {
				BlockPos blockPos5 = blockPos4.subtract(blockPos2);
				BlockState blockState = world.getBlockState(blockPos4);
				if (ignoredBlock == null || !blockState.isOf(ignoredBlock)) {
					BlockEntity blockEntity = world.getBlockEntity(blockPos4);
					StructureTemplate.StructureBlockInfo structureBlockInfo;
					if (blockEntity != null) {
						structureBlockInfo = new StructureTemplate.StructureBlockInfo(blockPos5, blockState, blockEntity.createNbtWithId());
					} else {
						structureBlockInfo = new StructureTemplate.StructureBlockInfo(blockPos5, blockState, null);
					}

					categorize(structureBlockInfo, list, list2, list3);
				}
			}

			List<StructureTemplate.StructureBlockInfo> list4 = combineSorted(list, list2, list3);
			this.blockInfoLists.clear();
			this.blockInfoLists.add(new StructureTemplate.PalettedBlockInfoList(list4));
			if (includeEntities) {
				this.addEntitiesFromWorld(world, blockPos2, blockPos3.add(1, 1, 1));
			} else {
				this.entities.clear();
			}
		}
	}

	/**
	 * Categorizes {@code blockInfo} based on its properties, modifying
	 * the passed lists in-place.
	 * 
	 * <p>If the block has an NBT associated with it, then it will be
	 * put in {@code blocksWithNbt}. If the block does not have an NBT
	 * associated with it, but is always a full cube, then it will be
	 * put in {@code fullBlocks}. Otherwise, it will be put in
	 * {@code otherBlocks}.
	 * 
	 * @apiNote After all blocks are categorized, {@link #combineSorted}
	 * should be called with the same parameters to get the final list.
	 */
	private static void categorize(
		StructureTemplate.StructureBlockInfo blockInfo,
		List<StructureTemplate.StructureBlockInfo> fullBlocks,
		List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
		List<StructureTemplate.StructureBlockInfo> otherBlocks
	) {
		if (blockInfo.nbt != null) {
			blocksWithNbt.add(blockInfo);
		} else if (!blockInfo.state.getBlock().hasDynamicBounds() && blockInfo.state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) {
			fullBlocks.add(blockInfo);
		} else {
			otherBlocks.add(blockInfo);
		}
	}

	/**
	 * {@return the list that sorts and combines the passed block lists}
	 * 
	 * @apiNote The parameters passed should be the same one that was passed
	 * to previous calls to {@link #categorize}. The returned value is meant to
	 * be passed to {@link PalettedBlockInfoList}.
	 * 
	 * @implNote Each list passed will be sorted in-place using the items'
	 * Y, X, and Z coordinates. The returned list contains all items of
	 * {@code fullBlocks}, {@code otherBlocks}, and {@code blocksWithNbt}
	 * in this order.
	 */
	private static List<StructureTemplate.StructureBlockInfo> combineSorted(
		List<StructureTemplate.StructureBlockInfo> fullBlocks,
		List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
		List<StructureTemplate.StructureBlockInfo> otherBlocks
	) {
		Comparator<StructureTemplate.StructureBlockInfo> comparator = Comparator.comparingInt(blockInfo -> blockInfo.pos.getY())
			.thenComparingInt(blockInfo -> blockInfo.pos.getX())
			.thenComparingInt(blockInfo -> blockInfo.pos.getZ());
		fullBlocks.sort(comparator);
		otherBlocks.sort(comparator);
		blocksWithNbt.sort(comparator);
		List<StructureTemplate.StructureBlockInfo> list = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();
		list.addAll(fullBlocks);
		list.addAll(otherBlocks);
		list.addAll(blocksWithNbt);
		return list;
	}

	private void addEntitiesFromWorld(World world, BlockPos firstCorner, BlockPos secondCorner) {
		List<Entity> list = world.getEntitiesByClass(Entity.class, new Box(firstCorner, secondCorner), entityx -> !(entityx instanceof PlayerEntity));
		this.entities.clear();

		for (Entity entity : list) {
			Vec3d vec3d = new Vec3d(entity.getX() - (double)firstCorner.getX(), entity.getY() - (double)firstCorner.getY(), entity.getZ() - (double)firstCorner.getZ());
			NbtCompound nbtCompound = new NbtCompound();
			entity.saveNbt(nbtCompound);
			BlockPos blockPos;
			if (entity instanceof PaintingEntity) {
				blockPos = ((PaintingEntity)entity).getDecorationBlockPos().subtract(firstCorner);
			} else {
				blockPos = new BlockPos(vec3d);
			}

			this.entities.add(new StructureTemplate.StructureEntityInfo(vec3d, blockPos, nbtCompound.copy()));
		}
	}

	public List<StructureTemplate.StructureBlockInfo> getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block) {
		return this.getInfosForBlock(pos, placementData, block, true);
	}

	public ObjectArrayList<StructureTemplate.StructureBlockInfo> getInfosForBlock(
		BlockPos pos, StructurePlacementData placementData, Block block, boolean transformed
	) {
		ObjectArrayList<StructureTemplate.StructureBlockInfo> objectArrayList = new ObjectArrayList<>();
		BlockBox blockBox = placementData.getBoundingBox();
		if (this.blockInfoLists.isEmpty()) {
			return objectArrayList;
		} else {
			for (StructureTemplate.StructureBlockInfo structureBlockInfo : placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAllOf(block)) {
				BlockPos blockPos = transformed ? transform(placementData, structureBlockInfo.pos).add(pos) : structureBlockInfo.pos;
				if (blockBox == null || blockBox.contains(blockPos)) {
					objectArrayList.add(
						new StructureTemplate.StructureBlockInfo(blockPos, structureBlockInfo.state.rotate(placementData.getRotation()), structureBlockInfo.nbt)
					);
				}
			}

			return objectArrayList;
		}
	}

	public BlockPos transformBox(StructurePlacementData placementData1, BlockPos pos1, StructurePlacementData placementData2, BlockPos pos2) {
		BlockPos blockPos = transform(placementData1, pos1);
		BlockPos blockPos2 = transform(placementData2, pos2);
		return blockPos.subtract(blockPos2);
	}

	public static BlockPos transform(StructurePlacementData placementData, BlockPos pos) {
		return transformAround(pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition());
	}

	public boolean place(ServerWorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, Random random, int flags) {
		if (this.blockInfoLists.isEmpty()) {
			return false;
		} else {
			List<StructureTemplate.StructureBlockInfo> list = placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAll();
			if ((!list.isEmpty() || !placementData.shouldIgnoreEntities() && !this.entities.isEmpty())
				&& this.size.getX() >= 1
				&& this.size.getY() >= 1
				&& this.size.getZ() >= 1) {
				BlockBox blockBox = placementData.getBoundingBox();
				List<BlockPos> list2 = Lists.<BlockPos>newArrayListWithCapacity(placementData.shouldPlaceFluids() ? list.size() : 0);
				List<BlockPos> list3 = Lists.<BlockPos>newArrayListWithCapacity(placementData.shouldPlaceFluids() ? list.size() : 0);
				List<Pair<BlockPos, NbtCompound>> list4 = Lists.<Pair<BlockPos, NbtCompound>>newArrayListWithCapacity(list.size());
				int i = Integer.MAX_VALUE;
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MIN_VALUE;
				int m = Integer.MIN_VALUE;
				int n = Integer.MIN_VALUE;

				for (StructureTemplate.StructureBlockInfo structureBlockInfo : process(world, pos, pivot, placementData, list)) {
					BlockPos blockPos = structureBlockInfo.pos;
					if (blockBox == null || blockBox.contains(blockPos)) {
						FluidState fluidState = placementData.shouldPlaceFluids() ? world.getFluidState(blockPos) : null;
						BlockState blockState = structureBlockInfo.state.mirror(placementData.getMirror()).rotate(placementData.getRotation());
						if (structureBlockInfo.nbt != null) {
							BlockEntity blockEntity = world.getBlockEntity(blockPos);
							Clearable.clear(blockEntity);
							world.setBlockState(blockPos, Blocks.BARRIER.getDefaultState(), Block.NO_REDRAW | Block.FORCE_STATE);
						}

						if (world.setBlockState(blockPos, blockState, flags)) {
							i = Math.min(i, blockPos.getX());
							j = Math.min(j, blockPos.getY());
							k = Math.min(k, blockPos.getZ());
							l = Math.max(l, blockPos.getX());
							m = Math.max(m, blockPos.getY());
							n = Math.max(n, blockPos.getZ());
							list4.add(Pair.of(blockPos, structureBlockInfo.nbt));
							if (structureBlockInfo.nbt != null) {
								BlockEntity blockEntity = world.getBlockEntity(blockPos);
								if (blockEntity != null) {
									if (blockEntity instanceof LootableContainerBlockEntity) {
										structureBlockInfo.nbt.putLong("LootTableSeed", random.nextLong());
									}

									blockEntity.readNbt(structureBlockInfo.nbt);
								}
							}

							if (fluidState != null) {
								if (blockState.getFluidState().isStill()) {
									list3.add(blockPos);
								} else if (blockState.getBlock() instanceof FluidFillable) {
									((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, fluidState);
									if (!fluidState.isStill()) {
										list2.add(blockPos);
									}
								}
							}
						}
					}
				}

				boolean bl = true;
				Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

				while (bl && !list2.isEmpty()) {
					bl = false;
					Iterator<BlockPos> iterator = list2.iterator();

					while (iterator.hasNext()) {
						BlockPos blockPos2 = (BlockPos)iterator.next();
						FluidState fluidState2 = world.getFluidState(blockPos2);

						for (int o = 0; o < directions.length && !fluidState2.isStill(); o++) {
							BlockPos blockPos3 = blockPos2.offset(directions[o]);
							FluidState fluidState3 = world.getFluidState(blockPos3);
							if (fluidState3.isStill() && !list3.contains(blockPos3)) {
								fluidState2 = fluidState3;
							}
						}

						if (fluidState2.isStill()) {
							BlockState blockState2 = world.getBlockState(blockPos2);
							Block block = blockState2.getBlock();
							if (block instanceof FluidFillable) {
								((FluidFillable)block).tryFillWithFluid(world, blockPos2, blockState2, fluidState2);
								bl = true;
								iterator.remove();
							}
						}
					}
				}

				if (i <= l) {
					if (!placementData.shouldUpdateNeighbors()) {
						VoxelSet voxelSet = new BitSetVoxelSet(l - i + 1, m - j + 1, n - k + 1);
						int p = i;
						int q = j;
						int ox = k;

						for (Pair<BlockPos, NbtCompound> pair : list4) {
							BlockPos blockPos4 = pair.getFirst();
							voxelSet.set(blockPos4.getX() - p, blockPos4.getY() - q, blockPos4.getZ() - ox);
						}

						updateCorner(world, flags, voxelSet, p, q, ox);
					}

					for (Pair<BlockPos, NbtCompound> pair2 : list4) {
						BlockPos blockPos5 = pair2.getFirst();
						if (!placementData.shouldUpdateNeighbors()) {
							BlockState blockState2 = world.getBlockState(blockPos5);
							BlockState blockState3 = Block.postProcessState(blockState2, world, blockPos5);
							if (blockState2 != blockState3) {
								world.setBlockState(blockPos5, blockState3, flags & ~Block.NOTIFY_NEIGHBORS | Block.FORCE_STATE);
							}

							world.updateNeighbors(blockPos5, blockState3.getBlock());
						}

						if (pair2.getSecond() != null) {
							BlockEntity blockEntity = world.getBlockEntity(blockPos5);
							if (blockEntity != null) {
								blockEntity.markDirty();
							}
						}
					}
				}

				if (!placementData.shouldIgnoreEntities()) {
					this.spawnEntities(
						world, pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition(), blockBox, placementData.shouldInitializeMobs()
					);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public static void updateCorner(WorldAccess world, int flags, VoxelSet set, int startX, int startY, int startZ) {
		set.forEachDirection((direction, x, y, z) -> {
			BlockPos blockPos = new BlockPos(startX + x, startY + y, startZ + z);
			BlockPos blockPos2 = blockPos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			BlockState blockState2 = world.getBlockState(blockPos2);
			BlockState blockState3 = blockState.getStateForNeighborUpdate(direction, blockState2, world, blockPos, blockPos2);
			if (blockState != blockState3) {
				world.setBlockState(blockPos, blockState3, flags & ~Block.NOTIFY_NEIGHBORS);
			}

			BlockState blockState4 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState3, world, blockPos2, blockPos);
			if (blockState2 != blockState4) {
				world.setBlockState(blockPos2, blockState4, flags & ~Block.NOTIFY_NEIGHBORS);
			}
		});
	}

	public static List<StructureTemplate.StructureBlockInfo> process(
		WorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, List<StructureTemplate.StructureBlockInfo> infos
	) {
		List<StructureTemplate.StructureBlockInfo> list = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();

		for (StructureTemplate.StructureBlockInfo structureBlockInfo : infos) {
			BlockPos blockPos = transform(placementData, structureBlockInfo.pos).add(pos);
			StructureTemplate.StructureBlockInfo structureBlockInfo2 = new StructureTemplate.StructureBlockInfo(
				blockPos, structureBlockInfo.state, structureBlockInfo.nbt != null ? structureBlockInfo.nbt.copy() : null
			);
			Iterator<StructureProcessor> iterator = placementData.getProcessors().iterator();

			while (structureBlockInfo2 != null && iterator.hasNext()) {
				structureBlockInfo2 = ((StructureProcessor)iterator.next()).process(world, pos, pivot, structureBlockInfo, structureBlockInfo2, placementData);
			}

			if (structureBlockInfo2 != null) {
				list.add(structureBlockInfo2);
			}
		}

		return list;
	}

	private void spawnEntities(
		ServerWorldAccess world, BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot, @Nullable BlockBox area, boolean initializeMobs
	) {
		for (StructureTemplate.StructureEntityInfo structureEntityInfo : this.entities) {
			BlockPos blockPos = transformAround(structureEntityInfo.blockPos, mirror, rotation, pivot).add(pos);
			if (area == null || area.contains(blockPos)) {
				NbtCompound nbtCompound = structureEntityInfo.nbt.copy();
				Vec3d vec3d = transformAround(structureEntityInfo.pos, mirror, rotation, pivot);
				Vec3d vec3d2 = vec3d.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
				NbtList nbtList = new NbtList();
				nbtList.add(NbtDouble.of(vec3d2.x));
				nbtList.add(NbtDouble.of(vec3d2.y));
				nbtList.add(NbtDouble.of(vec3d2.z));
				nbtCompound.put("Pos", nbtList);
				nbtCompound.remove("UUID");
				getEntity(world, nbtCompound).ifPresent(entity -> {
					float f = entity.applyRotation(rotation);
					f += entity.applyMirror(mirror) - entity.getYaw();
					entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.getPitch());
					if (initializeMobs && entity instanceof MobEntity) {
						((MobEntity)entity).initialize(world, world.getLocalDifficulty(new BlockPos(vec3d2)), SpawnReason.STRUCTURE, null, nbtCompound);
					}

					world.spawnEntityAndPassengers(entity);
				});
			}
		}
	}

	private static Optional<Entity> getEntity(ServerWorldAccess world, NbtCompound nbt) {
		try {
			return EntityType.getEntityFromNbt(nbt, world.toServerWorld());
		} catch (Exception var3) {
			return Optional.empty();
		}
	}

	public Vec3i getRotatedSize(BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				return new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
			default:
				return this.size;
		}
	}

	public static BlockPos transformAround(BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		boolean bl = true;
		switch (mirror) {
			case LEFT_RIGHT:
				k = -k;
				break;
			case FRONT_BACK:
				i = -i;
				break;
			default:
				bl = false;
		}

		int l = pivot.getX();
		int m = pivot.getZ();
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
				return new BlockPos(l - m + k, j, l + m - i);
			case CLOCKWISE_90:
				return new BlockPos(l + m - k, j, m - l + i);
			case CLOCKWISE_180:
				return new BlockPos(l + l - i, j, m + m - k);
			default:
				return bl ? new BlockPos(i, j, k) : pos;
		}
	}

	public static Vec3d transformAround(Vec3d point, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {
		double d = point.x;
		double e = point.y;
		double f = point.z;
		boolean bl = true;
		switch (mirror) {
			case LEFT_RIGHT:
				f = 1.0 - f;
				break;
			case FRONT_BACK:
				d = 1.0 - d;
				break;
			default:
				bl = false;
		}

		int i = pivot.getX();
		int j = pivot.getZ();
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
				return new Vec3d((double)(i - j) + f, e, (double)(i + j + 1) - d);
			case CLOCKWISE_90:
				return new Vec3d((double)(i + j + 1) - f, e, (double)(j - i) + d);
			case CLOCKWISE_180:
				return new Vec3d((double)(i + i + 1) - d, e, (double)(j + j + 1) - f);
			default:
				return bl ? new Vec3d(d, e, f) : point;
		}
	}

	public BlockPos offsetByTransformedSize(BlockPos pos, BlockMirror mirror, BlockRotation rotation) {
		return applyTransformedOffset(pos, mirror, rotation, this.getSize().getX(), this.getSize().getZ());
	}

	public static BlockPos applyTransformedOffset(BlockPos pos, BlockMirror mirror, BlockRotation rotation, int offsetX, int offsetZ) {
		offsetX--;
		offsetZ--;
		int i = mirror == BlockMirror.FRONT_BACK ? offsetX : 0;
		int j = mirror == BlockMirror.LEFT_RIGHT ? offsetZ : 0;
		BlockPos blockPos = pos;
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
				blockPos = pos.add(j, 0, offsetX - i);
				break;
			case CLOCKWISE_90:
				blockPos = pos.add(offsetZ - j, 0, i);
				break;
			case CLOCKWISE_180:
				blockPos = pos.add(offsetX - i, 0, offsetZ - j);
				break;
			case NONE:
				blockPos = pos.add(i, 0, j);
		}

		return blockPos;
	}

	public BlockBox calculateBoundingBox(StructurePlacementData placementData, BlockPos pos) {
		return this.calculateBoundingBox(pos, placementData.getRotation(), placementData.getPosition(), placementData.getMirror());
	}

	public BlockBox calculateBoundingBox(BlockPos pos, BlockRotation rotation, BlockPos pivot, BlockMirror mirror) {
		return createBox(pos, rotation, pivot, mirror, this.size);
	}

	@VisibleForTesting
	protected static BlockBox createBox(BlockPos pos, BlockRotation rotation, BlockPos pivot, BlockMirror mirror, Vec3i dimensions) {
		Vec3i vec3i = dimensions.add(-1, -1, -1);
		BlockPos blockPos = transformAround(BlockPos.ORIGIN, mirror, rotation, pivot);
		BlockPos blockPos2 = transformAround(BlockPos.ORIGIN.add(vec3i), mirror, rotation, pivot);
		return BlockBox.create(blockPos, blockPos2).move(pos);
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		if (this.blockInfoLists.isEmpty()) {
			nbt.put("blocks", new NbtList());
			nbt.put("palette", new NbtList());
		} else {
			List<StructureTemplate.Palette> list = Lists.<StructureTemplate.Palette>newArrayList();
			StructureTemplate.Palette palette = new StructureTemplate.Palette();
			list.add(palette);

			for (int i = 1; i < this.blockInfoLists.size(); i++) {
				list.add(new StructureTemplate.Palette());
			}

			NbtList nbtList = new NbtList();
			List<StructureTemplate.StructureBlockInfo> list2 = ((StructureTemplate.PalettedBlockInfoList)this.blockInfoLists.get(0)).getAll();

			for (int j = 0; j < list2.size(); j++) {
				StructureTemplate.StructureBlockInfo structureBlockInfo = (StructureTemplate.StructureBlockInfo)list2.get(j);
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.put("pos", this.createNbtIntList(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getY(), structureBlockInfo.pos.getZ()));
				int k = palette.getId(structureBlockInfo.state);
				nbtCompound.putInt("state", k);
				if (structureBlockInfo.nbt != null) {
					nbtCompound.put("nbt", structureBlockInfo.nbt);
				}

				nbtList.add(nbtCompound);

				for (int l = 1; l < this.blockInfoLists.size(); l++) {
					StructureTemplate.Palette palette2 = (StructureTemplate.Palette)list.get(l);
					palette2.set(((StructureTemplate.StructureBlockInfo)((StructureTemplate.PalettedBlockInfoList)this.blockInfoLists.get(l)).getAll().get(j)).state, k);
				}
			}

			nbt.put("blocks", nbtList);
			if (list.size() == 1) {
				NbtList nbtList2 = new NbtList();

				for (BlockState blockState : palette) {
					nbtList2.add(NbtHelper.fromBlockState(blockState));
				}

				nbt.put("palette", nbtList2);
			} else {
				NbtList nbtList2 = new NbtList();

				for (StructureTemplate.Palette palette3 : list) {
					NbtList nbtList3 = new NbtList();

					for (BlockState blockState2 : palette3) {
						nbtList3.add(NbtHelper.fromBlockState(blockState2));
					}

					nbtList2.add(nbtList3);
				}

				nbt.put("palettes", nbtList2);
			}
		}

		NbtList nbtList4 = new NbtList();

		for (StructureTemplate.StructureEntityInfo structureEntityInfo : this.entities) {
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("pos", this.createNbtDoubleList(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
			nbtCompound2.put(
				"blockPos", this.createNbtIntList(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ())
			);
			if (structureEntityInfo.nbt != null) {
				nbtCompound2.put("nbt", structureEntityInfo.nbt);
			}

			nbtList4.add(nbtCompound2);
		}

		nbt.put("entities", nbtList4);
		nbt.put("size", this.createNbtIntList(this.size.getX(), this.size.getY(), this.size.getZ()));
		nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		return nbt;
	}

	public void readNbt(NbtCompound nbt) {
		this.blockInfoLists.clear();
		this.entities.clear();
		NbtList nbtList = nbt.getList("size", NbtElement.INT_TYPE);
		this.size = new Vec3i(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
		NbtList nbtList2 = nbt.getList("blocks", NbtElement.COMPOUND_TYPE);
		if (nbt.contains("palettes", NbtElement.LIST_TYPE)) {
			NbtList nbtList3 = nbt.getList("palettes", NbtElement.LIST_TYPE);

			for (int i = 0; i < nbtList3.size(); i++) {
				this.loadPalettedBlockInfo(nbtList3.getList(i), nbtList2);
			}
		} else {
			this.loadPalettedBlockInfo(nbt.getList("palette", NbtElement.COMPOUND_TYPE), nbtList2);
		}

		NbtList nbtList3 = nbt.getList("entities", NbtElement.COMPOUND_TYPE);

		for (int i = 0; i < nbtList3.size(); i++) {
			NbtCompound nbtCompound = nbtList3.getCompound(i);
			NbtList nbtList4 = nbtCompound.getList("pos", NbtElement.DOUBLE_TYPE);
			Vec3d vec3d = new Vec3d(nbtList4.getDouble(0), nbtList4.getDouble(1), nbtList4.getDouble(2));
			NbtList nbtList5 = nbtCompound.getList("blockPos", NbtElement.INT_TYPE);
			BlockPos blockPos = new BlockPos(nbtList5.getInt(0), nbtList5.getInt(1), nbtList5.getInt(2));
			if (nbtCompound.contains("nbt")) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("nbt");
				this.entities.add(new StructureTemplate.StructureEntityInfo(vec3d, blockPos, nbtCompound2));
			}
		}
	}

	private void loadPalettedBlockInfo(NbtList paletteNbt, NbtList blocksNbt) {
		StructureTemplate.Palette palette = new StructureTemplate.Palette();

		for (int i = 0; i < paletteNbt.size(); i++) {
			palette.set(NbtHelper.toBlockState(paletteNbt.getCompound(i)), i);
		}

		List<StructureTemplate.StructureBlockInfo> list = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();
		List<StructureTemplate.StructureBlockInfo> list2 = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();
		List<StructureTemplate.StructureBlockInfo> list3 = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();

		for (int j = 0; j < blocksNbt.size(); j++) {
			NbtCompound nbtCompound = blocksNbt.getCompound(j);
			NbtList nbtList = nbtCompound.getList("pos", NbtElement.INT_TYPE);
			BlockPos blockPos = new BlockPos(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
			BlockState blockState = palette.getState(nbtCompound.getInt("state"));
			NbtCompound nbtCompound2;
			if (nbtCompound.contains("nbt")) {
				nbtCompound2 = nbtCompound.getCompound("nbt");
			} else {
				nbtCompound2 = null;
			}

			StructureTemplate.StructureBlockInfo structureBlockInfo = new StructureTemplate.StructureBlockInfo(blockPos, blockState, nbtCompound2);
			categorize(structureBlockInfo, list, list2, list3);
		}

		List<StructureTemplate.StructureBlockInfo> list4 = combineSorted(list, list2, list3);
		this.blockInfoLists.add(new StructureTemplate.PalettedBlockInfoList(list4));
	}

	private NbtList createNbtIntList(int... ints) {
		NbtList nbtList = new NbtList();

		for (int i : ints) {
			nbtList.add(NbtInt.of(i));
		}

		return nbtList;
	}

	private NbtList createNbtDoubleList(double... doubles) {
		NbtList nbtList = new NbtList();

		for (double d : doubles) {
			nbtList.add(NbtDouble.of(d));
		}

		return nbtList;
	}

	static class Palette implements Iterable<BlockState> {
		public static final BlockState AIR = Blocks.AIR.getDefaultState();
		private final IdList<BlockState> ids = new IdList<>(16);
		private int currentIndex;

		public int getId(BlockState state) {
			int i = this.ids.getRawId(state);
			if (i == -1) {
				i = this.currentIndex++;
				this.ids.set(state, i);
			}

			return i;
		}

		@Nullable
		public BlockState getState(int id) {
			BlockState blockState = this.ids.get(id);
			return blockState == null ? AIR : blockState;
		}

		public Iterator<BlockState> iterator() {
			return this.ids.iterator();
		}

		public void set(BlockState state, int id) {
			this.ids.set(state, id);
		}
	}

	public static final class PalettedBlockInfoList {
		private final List<StructureTemplate.StructureBlockInfo> infos;
		private final Map<Block, List<StructureTemplate.StructureBlockInfo>> blockToInfos = Maps.<Block, List<StructureTemplate.StructureBlockInfo>>newHashMap();

		PalettedBlockInfoList(List<StructureTemplate.StructureBlockInfo> infos) {
			this.infos = infos;
		}

		public List<StructureTemplate.StructureBlockInfo> getAll() {
			return this.infos;
		}

		public List<StructureTemplate.StructureBlockInfo> getAllOf(Block block) {
			return (List<StructureTemplate.StructureBlockInfo>)this.blockToInfos
				.computeIfAbsent(block, block2 -> (List)this.infos.stream().filter(info -> info.state.isOf(block2)).collect(Collectors.toList()));
		}
	}

	public static class StructureBlockInfo {
		public final BlockPos pos;
		public final BlockState state;
		public final NbtCompound nbt;

		public StructureBlockInfo(BlockPos pos, BlockState state, @Nullable NbtCompound nbt) {
			this.pos = pos;
			this.state = state;
			this.nbt = nbt;
		}

		public String toString() {
			return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
		}
	}

	public static class StructureEntityInfo {
		public final Vec3d pos;
		public final BlockPos blockPos;
		public final NbtCompound nbt;

		public StructureEntityInfo(Vec3d pos, BlockPos blockPos, NbtCompound nbt) {
			this.pos = pos;
			this.blockPos = blockPos;
			this.nbt = nbt;
		}
	}
}
