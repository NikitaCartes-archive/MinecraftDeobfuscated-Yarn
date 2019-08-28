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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.io.IOUtils;

public class StructureTestUtil {
	public static String testStructuresDirectoryName = "gameteststructures";

	public static Box getStructureBoundingBox(StructureBlockBlockEntity structureBlockBlockEntity) {
		BlockPos blockPos = structureBlockBlockEntity.getPos().add(structureBlockBlockEntity.getOffset());
		return new Box(blockPos, blockPos.add(structureBlockBlockEntity.getSize()));
	}

	public static void placeStartButton(BlockPos blockPos, ServerWorld serverWorld) {
		serverWorld.setBlockState(blockPos, Blocks.COMMAND_BLOCK.getDefaultState());
		CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		commandBlockBlockEntity.getCommandExecutor().setCommand("test runthis");
		serverWorld.setBlockState(blockPos.add(0, 0, -1), Blocks.STONE_BUTTON.getDefaultState());
	}

	public static void createTestArea(String string, BlockPos blockPos, BlockPos blockPos2, int i, ServerWorld serverWorld) {
		clearArea(blockPos, blockPos2, i, serverWorld);
		serverWorld.setBlockState(blockPos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(string));
		structureBlockBlockEntity.setSize(blockPos2);
		structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
		structureBlockBlockEntity.setShowBoundingBox(true);
	}

	public static StructureBlockBlockEntity method_22250(String string, BlockPos blockPos, int i, ServerWorld serverWorld, boolean bl) {
		forceLoadNearbyChunks(blockPos, serverWorld);
		clearArea(blockPos, method_22369(string, serverWorld).getSize(), i, serverWorld);
		return placeStructure(string, blockPos, serverWorld, bl);
	}

	private static void forceLoadNearbyChunks(BlockPos blockPos, ServerWorld serverWorld) {
		ChunkPos chunkPos = new ChunkPos(blockPos);

		for (int i = -1; i < 4; i++) {
			for (int j = -1; j < 4; j++) {
				int k = chunkPos.x + i;
				int l = chunkPos.z + j;
				serverWorld.setChunkForced(k, l, true);
			}
		}
	}

	public static void clearArea(BlockPos blockPos, BlockPos blockPos2, int i, ServerWorld serverWorld) {
		BlockPos blockPos3 = blockPos.add(-i, -3, -i);
		BlockPos blockPos4 = blockPos.add(blockPos2).add(i - 1, 30, i - 1);
		BlockPos.stream(blockPos3, blockPos4).forEach(blockPos2x -> method_22368(blockPos.getY(), blockPos2x, serverWorld));
		Box box = new Box(blockPos3, blockPos4);
		List<Entity> list = serverWorld.getEntities(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
		list.forEach(Entity::kill);
	}

	public static Optional<BlockPos> findContainingStructureBlock(BlockPos blockPos, int i, ServerWorld serverWorld) {
		return findStructureBlocks(blockPos, i, serverWorld).stream().filter(blockPos2 -> isInStructureBounds(blockPos2, blockPos, serverWorld)).findFirst();
	}

	@Nullable
	public static BlockPos findNearestStructureBlock(BlockPos blockPos, int i, ServerWorld serverWorld) {
		Comparator<BlockPos> comparator = Comparator.comparingInt(blockPos2 -> blockPos2.getManhattanDistance(blockPos));
		Collection<BlockPos> collection = findStructureBlocks(blockPos, i, serverWorld);
		Optional<BlockPos> optional = collection.stream().min(comparator);
		return (BlockPos)optional.orElse(null);
	}

	public static Collection<BlockPos> findStructureBlocks(BlockPos blockPos, int i, ServerWorld serverWorld) {
		Collection<BlockPos> collection = Lists.<BlockPos>newArrayList();
		Box box = new Box(blockPos);
		box = box.expand((double)i);

		for (int j = (int)box.minX; j <= (int)box.maxX; j++) {
			for (int k = (int)box.minY; k <= (int)box.maxY; k++) {
				for (int l = (int)box.minZ; l <= (int)box.maxZ; l++) {
					BlockPos blockPos2 = new BlockPos(j, k, l);
					BlockState blockState = serverWorld.getBlockState(blockPos2);
					if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK) {
						collection.add(blockPos2);
					}
				}
			}
		}

		return collection;
	}

	private static Structure method_22369(String string, ServerWorld serverWorld) {
		StructureManager structureManager = serverWorld.getStructureManager();
		Structure structure = structureManager.getStructure(new Identifier(string));
		if (structure != null) {
			return structure;
		} else {
			String string2 = string + ".snbt";
			Path path = Paths.get(testStructuresDirectoryName, string2);
			CompoundTag compoundTag = loadSnbt(path);
			if (compoundTag == null) {
				throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
			} else {
				return structureManager.createStructure(compoundTag);
			}
		}
	}

	private static StructureBlockBlockEntity placeStructure(String string, BlockPos blockPos, ServerWorld serverWorld, boolean bl) {
		serverWorld.setBlockState(blockPos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		structureBlockBlockEntity.setMode(StructureBlockMode.LOAD);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(string));
		structureBlockBlockEntity.loadStructure(bl);
		if (structureBlockBlockEntity.getSize() != BlockPos.ORIGIN) {
			return structureBlockBlockEntity;
		} else {
			Structure structure = method_22369(string, serverWorld);
			structureBlockBlockEntity.place(bl, structure);
			if (structureBlockBlockEntity.getSize() == BlockPos.ORIGIN) {
				throw new RuntimeException("Failed to load structure " + string);
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

	private static void method_22368(int i, BlockPos blockPos, ServerWorld serverWorld) {
		ChunkGeneratorConfig chunkGeneratorConfig = serverWorld.method_14178().getChunkGenerator().getConfig();
		BlockState blockState;
		if (chunkGeneratorConfig instanceof FlatChunkGeneratorConfig) {
			BlockState[] blockStates = ((FlatChunkGeneratorConfig)chunkGeneratorConfig).getLayerBlocks();
			if (blockPos.getY() < i) {
				blockState = blockStates[blockPos.getY() - 1];
			} else {
				blockState = Blocks.AIR.getDefaultState();
			}
		} else if (blockPos.getY() == i - 1) {
			blockState = serverWorld.getBiome(blockPos).getSurfaceConfig().getTopMaterial();
		} else if (blockPos.getY() < i - 1) {
			blockState = serverWorld.getBiome(blockPos).getSurfaceConfig().getUnderMaterial();
		} else {
			blockState = Blocks.AIR.getDefaultState();
		}

		BlockStateArgument blockStateArgument = new BlockStateArgument(blockState, Collections.emptySet(), null);
		blockStateArgument.setBlockState(serverWorld, blockPos, 2);
		serverWorld.updateNeighbors(blockPos, blockState.getBlock());
	}

	private static boolean isInStructureBounds(BlockPos blockPos, BlockPos blockPos2, ServerWorld serverWorld) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		Box box = getStructureBoundingBox(structureBlockBlockEntity);
		return box.contains(new Vec3d(blockPos2));
	}
}
