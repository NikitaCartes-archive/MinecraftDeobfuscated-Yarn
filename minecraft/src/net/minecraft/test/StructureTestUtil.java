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
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureTestUtil {
	private static final Logger LOGGER = LogManager.getLogger();
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

	public static Box getStructureBoundingBox(StructureBlockBlockEntity structureBlockEntity) {
		BlockPos blockPos = structureBlockEntity.getPos();
		BlockPos blockPos2 = blockPos.add(structureBlockEntity.getSize().add(-1, -1, -1));
		BlockPos blockPos3 = Structure.transformAround(blockPos2, BlockMirror.NONE, structureBlockEntity.getRotation(), blockPos);
		return new Box(blockPos, blockPos3);
	}

	public static BlockBox getStructureBlockBox(StructureBlockBlockEntity structureBlockEntity) {
		BlockPos blockPos = structureBlockEntity.getPos();
		BlockPos blockPos2 = blockPos.add(structureBlockEntity.getSize().add(-1, -1, -1));
		BlockPos blockPos3 = Structure.transformAround(blockPos2, BlockMirror.NONE, structureBlockEntity.getRotation(), blockPos);
		return BlockBox.create(blockPos, blockPos3);
	}

	public static void placeStartButton(BlockPos blockPos, BlockPos blockPos2, BlockRotation rotation, ServerWorld world) {
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, rotation, blockPos);
		world.setBlockState(blockPos3, Blocks.COMMAND_BLOCK.getDefaultState());
		CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)world.getBlockEntity(blockPos3);
		commandBlockBlockEntity.getCommandExecutor().setCommand("test runthis");
		BlockPos blockPos4 = Structure.transformAround(blockPos3.add(0, 0, -1), BlockMirror.NONE, rotation, blockPos3);
		world.setBlockState(blockPos4, Blocks.STONE_BUTTON.getDefaultState().rotate(rotation));
	}

	public static void createTestArea(String structure, BlockPos pos, Vec3i vec3i, BlockRotation rotation, ServerWorld world) {
		BlockBox blockBox = getStructureBlockBox(pos, vec3i, rotation);
		clearArea(blockBox, pos.getY(), world);
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(structure));
		structureBlockBlockEntity.setSize(vec3i);
		structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
		structureBlockBlockEntity.setShowBoundingBox(true);
	}

	public static StructureBlockBlockEntity createStructure(String structureName, BlockPos pos, BlockRotation rotation, int i, ServerWorld world, boolean bl) {
		Vec3i vec3i = createStructure(structureName, world).getSize();
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

		forceLoadNearbyChunks(pos, world);
		clearArea(blockBox, pos.getY(), world);
		StructureBlockBlockEntity structureBlockBlockEntity = placeStructure(structureName, blockPos, rotation, world, bl);
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
		BlockBox blockBox = new BlockBox(area.minX - 2, area.minY - 3, area.minZ - 3, area.maxX + 3, area.maxY + 20, area.maxZ + 3);
		BlockPos.stream(blockBox).forEach(pos -> method_22368(i, pos, world));
		world.getBlockTickScheduler().getScheduledTicks(blockBox, true, false);
		world.clearUpdatesInArea(blockBox);
		Box box = new Box((double)blockBox.minX, (double)blockBox.minY, (double)blockBox.minZ, (double)blockBox.maxX, (double)blockBox.maxY, (double)blockBox.maxZ);
		List<Entity> list = world.getEntitiesByClass(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
		list.forEach(Entity::discard);
	}

	public static BlockBox getStructureBlockBox(BlockPos blockPos, Vec3i vec3i, BlockRotation rotation) {
		BlockPos blockPos2 = blockPos.add(vec3i).add(-1, -1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos2, BlockMirror.NONE, rotation, blockPos);
		BlockBox blockBox = BlockBox.create(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
		int i = Math.min(blockBox.minX, blockBox.maxX);
		int j = Math.min(blockBox.minZ, blockBox.maxZ);
		return blockBox.move(blockPos.getX() - i, 0, blockPos.getZ() - j);
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

	private static StructureBlockBlockEntity placeStructure(String name, BlockPos pos, BlockRotation rotation, ServerWorld world, boolean bl) {
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setMode(StructureBlockMode.LOAD);
		structureBlockBlockEntity.setRotation(rotation);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(name));
		structureBlockBlockEntity.loadStructure(world, bl);
		if (structureBlockBlockEntity.getSize() != Vec3i.ZERO) {
			return structureBlockBlockEntity;
		} else {
			Structure structure = createStructure(name, world);
			structureBlockBlockEntity.place(world, bl, structure);
			if (structureBlockBlockEntity.getSize() == Vec3i.ZERO) {
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
			return NbtHelper.method_32260(string);
		} catch (IOException var3) {
			return null;
		} catch (CommandSyntaxException var4) {
			throw new RuntimeException("Error while trying to load structure " + path, var4);
		}
	}

	private static void method_22368(int altitude, BlockPos pos, ServerWorld world) {
		BlockState blockState = null;
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.getDefaultConfig(world.getRegistryManager().get(Registry.BIOME_KEY));
		if (flatChunkGeneratorConfig instanceof FlatChunkGeneratorConfig) {
			BlockState[] blockStates = flatChunkGeneratorConfig.getLayerBlocks();
			int i = flatChunkGeneratorConfig.method_31926(pos.getY());
			if (pos.getY() < altitude && i > 0 && i <= blockStates.length) {
				blockState = blockStates[i - 1];
			}
		} else if (pos.getY() == altitude - 1) {
			blockState = world.getBiome(pos).getGenerationSettings().getSurfaceConfig().getTopMaterial();
		} else if (pos.getY() < altitude - 1) {
			blockState = world.getBiome(pos).getGenerationSettings().getSurfaceConfig().getUnderMaterial();
		}

		if (blockState == null) {
			blockState = Blocks.AIR.getDefaultState();
		}

		BlockStateArgument blockStateArgument = new BlockStateArgument(blockState, Collections.emptySet(), null);
		blockStateArgument.setBlockState(world, pos, 2);
		world.updateNeighbors(pos, blockState.getBlock());
	}

	private static boolean isInStructureBounds(BlockPos structureBlockPos, BlockPos pos, ServerWorld world) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(structureBlockPos);
		Box box = getStructureBoundingBox(structureBlockBlockEntity).expand(1.0);
		return box.contains(Vec3d.ofCenter(pos));
	}
}
