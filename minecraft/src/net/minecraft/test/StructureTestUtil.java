package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.io.IOUtils;

public class StructureTestUtil {
	public static String testStructuresDirectoryName = "gameteststructures";

	public static Box getStructureBoundingBox(StructureBlockBlockEntity structureBlockEntity) {
		BlockPos blockPos = structureBlockEntity.getPos().add(structureBlockEntity.getOffset());
		return new Box(blockPos, blockPos.add(structureBlockEntity.getSize()));
	}

	public static void placeStartButton(BlockPos pos, ServerWorld world) {
		world.setBlockState(pos, Blocks.COMMAND_BLOCK.getDefaultState());
		CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)world.getBlockEntity(pos);
		commandBlockBlockEntity.getCommandExecutor().setCommand("test runthis");
		world.setBlockState(pos.add(0, 0, -1), Blocks.STONE_BUTTON.getDefaultState());
	}

	public static void createTestArea(String structure, BlockPos pos, BlockPos size, int margin, ServerWorld world) {
		BlockBox blockBox = createArea(pos, size, margin);
		clearArea(blockBox, pos.getY(), world);
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(structure));
		structureBlockBlockEntity.setSize(size);
		structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
		structureBlockBlockEntity.setShowBoundingBox(true);
	}

	public static StructureBlockBlockEntity method_22250(String structureId, BlockPos pos, int i, ServerWorld world, boolean bl) {
		BlockBox blockBox = createArea(pos, createStructure(structureId, world).getSize(), i);
		forceLoadNearbyChunks(pos, world);
		clearArea(blockBox, pos.getY(), world);
		StructureBlockBlockEntity structureBlockBlockEntity = placeStructure(structureId, pos, world, bl);
		world.getBlockTickScheduler().getScheduledTicks(blockBox, true, false);
		world.clearUpdatesInArea(blockBox);
		return structureBlockBlockEntity;
	}

	private static void forceLoadNearbyChunks(BlockPos pos, ServerWorld world) {
		ChunkPos chunkPos = new ChunkPos(pos);

		for (int i = -1; i < 4; i++) {
			for (int j = -1; j < 4; j++) {
				int k = chunkPos.x + i;
				int l = chunkPos.z + j;
				world.setChunkForced(k, l, true);
			}
		}
	}

	public static void clearArea(BlockBox area, int i, ServerWorld world) {
		BlockPos.stream(area).forEach(blockPos -> method_22368(i, blockPos, world));
		world.getBlockTickScheduler().getScheduledTicks(area, true, false);
		world.clearUpdatesInArea(area);
		Box box = new Box((double)area.minX, (double)area.minY, (double)area.minZ, (double)area.maxX, (double)area.maxY, (double)area.maxZ);
		List<Entity> list = world.getEntities(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
		list.forEach(Entity::remove);
	}

	public static BlockBox createArea(BlockPos pos, BlockPos size, int margin) {
		BlockPos blockPos = pos.add(-margin, -3, -margin);
		BlockPos blockPos2 = pos.add(size).add(margin - 1, 30, margin - 1);
		return BlockBox.create(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
	}

	public static Optional<BlockPos> findContainingStructureBlock(BlockPos pos, int radius, ServerWorld world) {
		return findStructureBlocks(pos, radius, world).stream().filter(blockPos2 -> isInStructureBounds(blockPos2, pos, world)).findFirst();
	}

	@Nullable
	public static BlockPos findNearestStructureBlock(BlockPos pos, int radius, ServerWorld world) {
		Comparator<BlockPos> comparator = Comparator.comparingInt(blockPos2 -> blockPos2.getManhattanDistance(pos));
		Collection<BlockPos> collection = findStructureBlocks(pos, radius, world);
		Optional<BlockPos> optional = collection.stream().min(comparator);
		return (BlockPos)optional.orElse(null);
	}

	public static Collection<BlockPos> findStructureBlocks(BlockPos pos, int radius, ServerWorld world) {
		Collection<BlockPos> collection = Lists.<BlockPos>newArrayList();
		Box box = new Box(pos);
		box = box.expand((double)radius);

		for (int i = (int)box.minX; i <= (int)box.maxX; i++) {
			for (int j = (int)box.minY; j <= (int)box.maxY; j++) {
				for (int k = (int)box.minZ; k <= (int)box.maxZ; k++) {
					BlockPos blockPos = new BlockPos(i, j, k);
					BlockState blockState = world.getBlockState(blockPos);
					if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
						collection.add(blockPos);
					}
				}
			}
		}

		return collection;
	}

	private static Structure createStructure(String structureId, ServerWorld world) {
		StructureManager structureManager = world.getStructureManager();
		Structure structure = structureManager.getStructure(new Identifier(structureId));
		if (structure != null) {
			return structure;
		} else {
			String string = structureId + ".snbt";
			Path path = Paths.get(testStructuresDirectoryName, string);
			CompoundTag compoundTag = loadSnbt(path);
			if (compoundTag == null) {
				throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
			} else {
				return structureManager.createStructure(compoundTag);
			}
		}
	}

	private static StructureBlockBlockEntity placeStructure(String name, BlockPos pos, ServerWorld world, boolean resizeDisabled) {
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setMode(StructureBlockMode.LOAD);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(name));
		structureBlockBlockEntity.loadStructure(resizeDisabled);
		if (structureBlockBlockEntity.getSize() != BlockPos.ORIGIN) {
			return structureBlockBlockEntity;
		} else {
			Structure structure = createStructure(name, world);
			structureBlockBlockEntity.place(resizeDisabled, structure);
			if (structureBlockBlockEntity.getSize() == BlockPos.ORIGIN) {
				throw new RuntimeException("Failed to load structure " + name);
			} else {
				return structureBlockBlockEntity;
			}
		}
	}

	@Nullable
	private static CompoundTag loadSnbt(Path path) {
		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			String string = IOUtils.toString(bufferedReader);
			return StringNbtReader.parse(string);
		} catch (IOException var3) {
			return null;
		} catch (CommandSyntaxException var4) {
			throw new RuntimeException("Error while trying to load structure " + path, var4);
		}
	}

	private static void method_22368(int altitude, BlockPos pos, ServerWorld world) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.getDefaultConfig();
		BlockState[] blockStates = flatChunkGeneratorConfig.getLayerBlocks();
		BlockState blockState;
		if (pos.getY() < altitude) {
			blockState = blockStates[pos.getY() - 1];
		} else {
			blockState = Blocks.AIR.getDefaultState();
		}

		BlockStateArgument blockStateArgument = new BlockStateArgument(blockState, Collections.emptySet(), null);
		blockStateArgument.setBlockState(world, pos, 2);
		world.updateNeighbors(pos, blockState.getBlock());
	}

	private static boolean isInStructureBounds(BlockPos structureBlockPos, BlockPos pos, ServerWorld world) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(structureBlockPos);
		Box box = getStructureBoundingBox(structureBlockBlockEntity);
		return box.contains(Vec3d.of(pos));
	}
}
