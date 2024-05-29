package net.minecraft.test;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;

public class StructureTestUtil {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_51468 = 10;
	public static final String TEST_STRUCTURES_DIRECTORY_NAME = "gameteststructures";
	public static String testStructuresDirectoryName = "gameteststructures";

	public static BlockRotation getRotation(int steps) {
		switch (steps) {
			case 0:
				return BlockRotation.NONE;
			case 1:
				return BlockRotation.CLOCKWISE_90;
			case 2:
				return BlockRotation.CLOCKWISE_180;
			case 3:
				return BlockRotation.COUNTERCLOCKWISE_90;
			default:
				throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + steps);
		}
	}

	public static int getRotationSteps(BlockRotation rotation) {
		switch (rotation) {
			case NONE:
				return 0;
			case CLOCKWISE_90:
				return 1;
			case CLOCKWISE_180:
				return 2;
			case COUNTERCLOCKWISE_90:
				return 3;
			default:
				throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + rotation);
		}
	}

	public static Box getStructureBoundingBox(StructureBlockBlockEntity structureBlockEntity) {
		return Box.from(getStructureBlockBox(structureBlockEntity));
	}

	public static BlockBox getStructureBlockBox(StructureBlockBlockEntity structureBlockEntity) {
		BlockPos blockPos = getStructureBlockPos(structureBlockEntity);
		BlockPos blockPos2 = getStructureBlockBoxCornerPos(blockPos, structureBlockEntity.getSize(), structureBlockEntity.getRotation());
		return BlockBox.create(blockPos, blockPos2);
	}

	public static BlockPos getStructureBlockPos(StructureBlockBlockEntity structureBlockEntity) {
		return structureBlockEntity.getPos().add(structureBlockEntity.getOffset());
	}

	public static void placeStartButton(BlockPos pos, BlockPos relativePos, BlockRotation rotation, ServerWorld world) {
		BlockPos blockPos = StructureTemplate.transformAround(pos.add(relativePos), BlockMirror.NONE, rotation, pos);
		world.setBlockState(blockPos, Blocks.COMMAND_BLOCK.getDefaultState());
		CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)world.getBlockEntity(blockPos);
		commandBlockBlockEntity.getCommandExecutor().setCommand("test runclosest");
		BlockPos blockPos2 = StructureTemplate.transformAround(blockPos.add(0, 0, -1), BlockMirror.NONE, rotation, blockPos);
		world.setBlockState(blockPos2, Blocks.STONE_BUTTON.getDefaultState().rotate(rotation));
	}

	public static void createTestArea(String testName, BlockPos pos, Vec3i relativePos, BlockRotation rotation, ServerWorld world) {
		BlockBox blockBox = getStructureBlockBox(pos.up(), relativePos, rotation);
		clearArea(blockBox, world);
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setTemplateName(Identifier.of(testName));
		structureBlockBlockEntity.setSize(relativePos);
		structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
		structureBlockBlockEntity.setShowBoundingBox(true);
	}

	public static StructureBlockBlockEntity initStructure(GameTestState state, BlockPos pos, BlockRotation rotation, ServerWorld world) {
		Vec3i vec3i = ((StructureTemplate)world.getStructureTemplateManager()
				.getTemplate(Identifier.of(state.getTemplateName()))
				.orElseThrow(() -> new IllegalStateException("Missing test structure: " + state.getTemplateName())))
			.getSize();
		BlockBox blockBox = getStructureBlockBox(pos, vec3i, rotation);
		BlockPos blockPos;
		if (rotation == BlockRotation.NONE) {
			blockPos = pos;
		} else if (rotation == BlockRotation.CLOCKWISE_90) {
			blockPos = pos.add(vec3i.getZ() - 1, 0, 0);
		} else if (rotation == BlockRotation.CLOCKWISE_180) {
			blockPos = pos.add(vec3i.getX() - 1, 0, vec3i.getZ() - 1);
		} else {
			if (rotation != BlockRotation.COUNTERCLOCKWISE_90) {
				throw new IllegalArgumentException("Invalid rotation: " + rotation);
			}

			blockPos = pos.add(0, 0, vec3i.getX() - 1);
		}

		forceLoadNearbyChunks(blockBox, world);
		clearArea(blockBox, world);
		return placeStructureTemplate(state, blockPos.down(), rotation, world);
	}

	public static void placeBarrierBox(Box box, ServerWorld world, boolean noSkyAccess) {
		BlockPos blockPos = BlockPos.ofFloored(box.minX, box.minY, box.minZ).add(-1, 0, -1);
		BlockPos blockPos2 = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);
		BlockPos.stream(blockPos, blockPos2).forEach(pos -> {
			boolean bl2 = pos.getX() == blockPos.getX() || pos.getX() == blockPos2.getX() || pos.getZ() == blockPos.getZ() || pos.getZ() == blockPos2.getZ();
			boolean bl3 = pos.getY() == blockPos2.getY();
			if (bl2 || bl3 && noSkyAccess) {
				world.setBlockState(pos, Blocks.BARRIER.getDefaultState());
			}
		});
	}

	public static void clearBarrierBox(Box box, ServerWorld world) {
		BlockPos blockPos = BlockPos.ofFloored(box.minX, box.minY, box.minZ).add(-1, 0, -1);
		BlockPos blockPos2 = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);
		BlockPos.stream(blockPos, blockPos2).forEach(pos -> {
			boolean bl = pos.getX() == blockPos.getX() || pos.getX() == blockPos2.getX() || pos.getZ() == blockPos.getZ() || pos.getZ() == blockPos2.getZ();
			boolean bl2 = pos.getY() == blockPos2.getY();
			if (world.getBlockState(pos).isOf(Blocks.BARRIER) && (bl || bl2)) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		});
	}

	private static void forceLoadNearbyChunks(BlockBox box, ServerWorld world) {
		box.streamChunkPos().forEach(chunkPos -> world.setChunkForced(chunkPos.x, chunkPos.z, true));
	}

	public static void clearArea(BlockBox area, ServerWorld world) {
		int i = area.getMinY() - 1;
		BlockBox blockBox = new BlockBox(area.getMinX() - 2, area.getMinY() - 3, area.getMinZ() - 3, area.getMaxX() + 3, area.getMaxY() + 20, area.getMaxZ() + 3);
		BlockPos.stream(blockBox).forEach(pos -> resetBlock(i, pos, world));
		world.getBlockTickScheduler().clearNextTicks(blockBox);
		world.clearUpdatesInArea(blockBox);
		Box box = Box.from(blockBox);
		List<Entity> list = world.getEntitiesByClass(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
		list.forEach(Entity::discard);
	}

	public static BlockPos getStructureBlockBoxCornerPos(BlockPos pos, Vec3i size, BlockRotation rotation) {
		BlockPos blockPos = pos.add(size).add(-1, -1, -1);
		return StructureTemplate.transformAround(blockPos, BlockMirror.NONE, rotation, pos);
	}

	public static BlockBox getStructureBlockBox(BlockPos pos, Vec3i relativePos, BlockRotation rotation) {
		BlockPos blockPos = getStructureBlockBoxCornerPos(pos, relativePos, rotation);
		BlockBox blockBox = BlockBox.create(pos, blockPos);
		int i = Math.min(blockBox.getMinX(), blockBox.getMaxX());
		int j = Math.min(blockBox.getMinZ(), blockBox.getMaxZ());
		return blockBox.move(pos.getX() - i, 0, pos.getZ() - j);
	}

	public static Optional<BlockPos> findContainingStructureBlock(BlockPos pos, int radius, ServerWorld world) {
		return findStructureBlocks(pos, radius, world).filter(structureBlockPos -> isInStructureBounds(structureBlockPos, pos, world)).findFirst();
	}

	public static Optional<BlockPos> findNearestStructureBlock(BlockPos pos, int radius, ServerWorld world) {
		Comparator<BlockPos> comparator = Comparator.comparingInt(posx -> posx.getManhattanDistance(pos));
		return findStructureBlocks(pos, radius, world).min(comparator);
	}

	public static Stream<BlockPos> findStructureBlocks(BlockPos pos, int radius, ServerWorld world, String templateName) {
		return findStructureBlocks(pos, radius, world)
			.map(posx -> (StructureBlockBlockEntity)world.getBlockEntity(posx))
			.filter(Objects::nonNull)
			.filter(blockEntity -> Objects.equals(blockEntity.getTemplateName(), templateName))
			.map(BlockEntity::getPos)
			.map(BlockPos::toImmutable);
	}

	public static Stream<BlockPos> findStructureBlocks(BlockPos pos, int radius, ServerWorld world) {
		BlockBox blockBox = createBox(pos, radius, world);
		return BlockPos.stream(blockBox).filter(p -> world.getBlockState(p).isOf(Blocks.STRUCTURE_BLOCK)).map(BlockPos::toImmutable);
	}

	private static StructureBlockBlockEntity placeStructureTemplate(GameTestState state, BlockPos pos, BlockRotation rotation, ServerWorld world) {
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setMode(StructureBlockMode.LOAD);
		structureBlockBlockEntity.setRotation(rotation);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setTemplateName(Identifier.of(state.getTemplateName()));
		structureBlockBlockEntity.setMetadata(state.getTemplatePath());
		if (!structureBlockBlockEntity.loadStructure(world)) {
			throw new RuntimeException("Failed to load structure info for test: " + state.getTemplatePath() + ". Structure name: " + state.getTemplateName());
		} else {
			return structureBlockBlockEntity;
		}
	}

	private static BlockBox createBox(BlockPos pos, int radius, ServerWorld world) {
		BlockPos blockPos = BlockPos.ofFloored((double)pos.getX(), (double)world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY(), (double)pos.getZ());
		return new BlockBox(blockPos).expand(radius, 10, radius);
	}

	public static Stream<BlockPos> findTargetedStructureBlock(BlockPos pos, Entity entity, ServerWorld world) {
		int i = 200;
		Vec3d vec3d = entity.getEyePos();
		Vec3d vec3d2 = vec3d.add(entity.getRotationVector().multiply(200.0));
		return findStructureBlocks(pos, 200, world)
			.map(p -> world.getBlockEntity(p, BlockEntityType.STRUCTURE_BLOCK))
			.flatMap(Optional::stream)
			.filter(blockEntity -> getStructureBoundingBox(blockEntity).raycast(vec3d, vec3d2).isPresent())
			.map(BlockEntity::getPos)
			.sorted(Comparator.comparing(pos::getSquaredDistance))
			.limit(1L);
	}

	private static void resetBlock(int altitude, BlockPos pos, ServerWorld world) {
		BlockState blockState;
		if (pos.getY() < altitude) {
			blockState = Blocks.STONE.getDefaultState();
		} else {
			blockState = Blocks.AIR.getDefaultState();
		}

		BlockStateArgument blockStateArgument = new BlockStateArgument(blockState, Collections.emptySet(), null);
		blockStateArgument.setBlockState(world, pos, Block.NOTIFY_LISTENERS);
		world.updateNeighbors(pos, blockState.getBlock());
	}

	private static boolean isInStructureBounds(BlockPos structureBlockPos, BlockPos pos, ServerWorld world) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(structureBlockPos);
		return getStructureBlockBox(structureBlockBlockEntity).contains(pos);
	}
}
