package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTestUtil {
	private static final Logger LOGGER = LogUtils.getLogger();
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
		commandBlockBlockEntity.getCommandExecutor().setCommand("test runthis");
		BlockPos blockPos2 = StructureTemplate.transformAround(blockPos.add(0, 0, -1), BlockMirror.NONE, rotation, blockPos);
		world.setBlockState(blockPos2, Blocks.STONE_BUTTON.getDefaultState().rotate(rotation));
	}

	public static void createTestArea(String testName, BlockPos pos, Vec3i relativePos, BlockRotation rotation, ServerWorld world) {
		BlockBox blockBox = getStructureBlockBox(pos, relativePos, rotation);
		clearArea(blockBox, world);
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setTemplateName(new Identifier(testName));
		structureBlockBlockEntity.setSize(relativePos);
		structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
		structureBlockBlockEntity.setShowBoundingBox(true);
	}

	public static StructureBlockBlockEntity createStructureTemplate(String templateName, BlockPos pos, BlockRotation rotation, ServerWorld world, boolean initOnly) {
		Vec3i vec3i = createStructureTemplate(templateName, world).getSize();
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
		StructureBlockBlockEntity structureBlockBlockEntity = placeStructureTemplate(templateName, blockPos.down(), rotation, world, initOnly);
		world.getBlockTickScheduler().clearNextTicks(blockBox);
		world.clearUpdatesInArea(blockBox);
		return structureBlockBlockEntity;
	}

	private static void forceLoadNearbyChunks(BlockBox box, ServerWorld world) {
		streamChunkPos(box, 0).forEach(chunkPos -> world.setChunkForced(chunkPos.x, chunkPos.z, true));
		streamChunkPos(box, 3).forEach(chunkPos -> world.getChunk(chunkPos.x, chunkPos.z));
	}

	private static Stream<ChunkPos> streamChunkPos(BlockBox box, int margin) {
		int i = ChunkSectionPos.getSectionCoord(box.getMinX()) - margin;
		int j = ChunkSectionPos.getSectionCoord(box.getMinZ()) - margin;
		int k = ChunkSectionPos.getSectionCoord(box.getMaxX()) + margin;
		int l = ChunkSectionPos.getSectionCoord(box.getMaxZ()) + margin;
		return ChunkPos.stream(new ChunkPos(i, j), new ChunkPos(k, l));
	}

	public static void clearArea(BlockBox area, ServerWorld world) {
		int i = area.getMinY() - 1;
		BlockBox blockBox = new BlockBox(area.getMinX() - 2, area.getMinY() - 3, area.getMinZ() - 3, area.getMaxX() + 3, area.getMaxY() + 20, area.getMaxZ() + 3);
		BlockPos.stream(blockBox).forEach(pos -> resetBlock(i, pos, world));
		world.getBlockTickScheduler().clearNextTicks(blockBox);
		world.clearUpdatesInArea(blockBox);
		Box box = new Box(
			(double)blockBox.getMinX(),
			(double)blockBox.getMinY(),
			(double)blockBox.getMinZ(),
			(double)blockBox.getMaxX(),
			(double)blockBox.getMaxY(),
			(double)blockBox.getMaxZ()
		);
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
		return findStructureBlocks(pos, radius, world).stream().filter(structureBlockPos -> isInStructureBounds(structureBlockPos, pos, world)).findFirst();
	}

	@Nullable
	public static BlockPos findNearestStructureBlock(BlockPos pos, int radius, ServerWorld world) {
		Comparator<BlockPos> comparator = Comparator.comparingInt(posx -> posx.getManhattanDistance(pos));
		Collection<BlockPos> collection = findStructureBlocks(pos, radius, world);
		Optional<BlockPos> optional = collection.stream().min(comparator);
		return (BlockPos)optional.orElse(null);
	}

	public static Collection<BlockPos> findStructureBlocks(BlockPos pos, int radius, ServerWorld world) {
		Collection<BlockPos> collection = Lists.<BlockPos>newArrayList();
		BlockBox blockBox = new BlockBox(pos).expand(radius);
		BlockPos.stream(blockBox).forEach(blockPos -> {
			if (world.getBlockState(blockPos).isOf(Blocks.STRUCTURE_BLOCK)) {
				collection.add(blockPos.toImmutable());
			}
		});
		return collection;
	}

	private static StructureTemplate createStructureTemplate(String templateId, ServerWorld world) {
		StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
		Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(new Identifier(templateId));
		if (optional.isPresent()) {
			return (StructureTemplate)optional.get();
		} else {
			String string = templateId + ".snbt";
			Path path = Paths.get(testStructuresDirectoryName, string);
			NbtCompound nbtCompound = loadSnbt(path);
			if (nbtCompound == null) {
				throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
			} else {
				return structureTemplateManager.createTemplate(nbtCompound);
			}
		}
	}

	private static StructureBlockBlockEntity placeStructureTemplate(String name, BlockPos pos, BlockRotation rotation, ServerWorld world, boolean initOnly) {
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setMode(StructureBlockMode.LOAD);
		structureBlockBlockEntity.setRotation(rotation);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setTemplateName(new Identifier(name));
		structureBlockBlockEntity.loadStructure(world, initOnly);
		if (structureBlockBlockEntity.getSize() != Vec3i.ZERO) {
			return structureBlockBlockEntity;
		} else {
			StructureTemplate structureTemplate = createStructureTemplate(name, world);
			structureBlockBlockEntity.place(world, initOnly, structureTemplate);
			if (structureBlockBlockEntity.getSize() == Vec3i.ZERO) {
				throw new RuntimeException("Failed to load structure " + name);
			} else {
				return structureBlockBlockEntity;
			}
		}
	}

	@Nullable
	private static NbtCompound loadSnbt(Path path) {
		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			String string = IOUtils.toString(bufferedReader);
			return NbtHelper.fromNbtProviderString(string);
		} catch (IOException var3) {
			return null;
		} catch (CommandSyntaxException var4) {
			throw new RuntimeException("Error while trying to load structure " + path, var4);
		}
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
