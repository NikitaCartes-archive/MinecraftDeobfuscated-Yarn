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
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
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
		BlockBox blockBox = method_23646(pos, size, margin);
		clearArea(blockBox, pos.getY(), world);
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(structure));
		structureBlockBlockEntity.setSize(size);
		structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
		structureBlockBlockEntity.setShowBoundingBox(true);
	}

	public static StructureBlockBlockEntity method_22250(String string, BlockPos blockPos, int i, ServerWorld serverWorld, boolean bl) {
		BlockBox blockBox = method_23646(blockPos, method_22369(string, serverWorld).getSize(), i);
		forceLoadNearbyChunks(blockPos, serverWorld);
		clearArea(blockBox, blockPos.getY(), serverWorld);
		StructureBlockBlockEntity structureBlockBlockEntity = placeStructure(string, blockPos, serverWorld, bl);
		serverWorld.method_14196().getScheduledTicks(blockBox, true, false);
		serverWorld.method_23658(blockBox);
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

	public static void clearArea(BlockBox blockBox, int i, ServerWorld serverWorld) {
		BlockPos.method_23627(blockBox).forEach(blockPos -> method_22368(i, blockPos, serverWorld));
		serverWorld.method_14196().getScheduledTicks(blockBox, true, false);
		serverWorld.method_23658(blockBox);
		Box box = new Box((double)blockBox.minX, (double)blockBox.minY, (double)blockBox.minZ, (double)blockBox.maxX, (double)blockBox.maxY, (double)blockBox.maxZ);
		List<Entity> list = serverWorld.getEntities(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
		list.forEach(Entity::remove);
	}

	public static BlockBox method_23646(BlockPos blockPos, BlockPos blockPos2, int i) {
		BlockPos blockPos3 = blockPos.add(-i, -3, -i);
		BlockPos blockPos4 = blockPos.add(blockPos2).add(i - 1, 30, i - 1);
		return BlockBox.create(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), blockPos4.getX(), blockPos4.getY(), blockPos4.getZ());
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

		for (int i = (int)box.x1; i <= (int)box.x2; i++) {
			for (int j = (int)box.y1; j <= (int)box.y2; j++) {
				for (int k = (int)box.z1; k <= (int)box.z2; k++) {
					BlockPos blockPos = new BlockPos(i, j, k);
					BlockState blockState = world.getBlockState(blockPos);
					if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK) {
						collection.add(blockPos);
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
			Structure structure = method_22369(name, world);
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

	private static boolean isInStructureBounds(BlockPos structureBlockPos, BlockPos pos, ServerWorld world) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(structureBlockPos);
		Box box = getStructureBoundingBox(structureBlockBlockEntity);
		return box.contains(new Vec3d(pos));
	}
}
