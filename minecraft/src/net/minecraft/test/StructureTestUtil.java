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
import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
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
import org.slf4j.Logger;

public class StructureTestUtil {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String TEST_STRUCTURES_DIRECTORY_NAME = "gameteststructures";
	public static String testStructuresDirectoryName = "gameteststructures";
	private static final int field_33174 = 4;

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

	public static void main(String[] args) throws IOException {
		Bootstrap.initialize();
		Files.walk(Paths.get(testStructuresDirectoryName)).filter(path -> path.toString().endsWith(".snbt")).forEach(path -> {
			try {
				String string = Files.readString(path);
				NbtCompound nbtCompound = NbtHelper.fromNbtProviderString(string);
				NbtCompound nbtCompound2 = StructureValidatorProvider.update(path.toString(), nbtCompound);
				NbtProvider.writeTo(DataWriter.field_39439, path, NbtHelper.toNbtProviderString(nbtCompound2));
			} catch (IOException | CommandSyntaxException var4) {
				LOGGER.error("Something went wrong upgrading: {}", path, var4);
			}
		});
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

	public static void placeStartButton(BlockPos pos, BlockPos relativePos, BlockRotation rotation, ServerWorld world) {
		BlockPos blockPos = Structure.transformAround(pos.add(relativePos), BlockMirror.NONE, rotation, pos);
		world.setBlockState(blockPos, Blocks.COMMAND_BLOCK.getDefaultState());
		CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)world.getBlockEntity(blockPos);
		commandBlockBlockEntity.getCommandExecutor().setCommand("test runthis");
		BlockPos blockPos2 = Structure.transformAround(blockPos.add(0, 0, -1), BlockMirror.NONE, rotation, blockPos);
		world.setBlockState(blockPos2, Blocks.STONE_BUTTON.getDefaultState().rotate(rotation));
	}

	public static void createTestArea(String structure, BlockPos pos, Vec3i relativePos, BlockRotation rotation, ServerWorld world) {
		BlockBox blockBox = getStructureBlockBox(pos, relativePos, rotation);
		clearArea(blockBox, pos.getY(), world);
		world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(structure));
		structureBlockBlockEntity.setSize(relativePos);
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
		world.getBlockTickScheduler().clearNextTicks(blockBox);
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

	public static void clearArea(BlockBox area, int altitude, ServerWorld world) {
		BlockBox blockBox = new BlockBox(area.getMinX() - 2, area.getMinY() - 3, area.getMinZ() - 3, area.getMaxX() + 3, area.getMaxY() + 20, area.getMaxZ() + 3);
		BlockPos.stream(blockBox).forEach(pos -> resetBlock(altitude, pos, world));
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

	public static BlockBox getStructureBlockBox(BlockPos pos, Vec3i relativePos, BlockRotation rotation) {
		BlockPos blockPos = pos.add(relativePos).add(-1, -1, -1);
		BlockPos blockPos2 = Structure.transformAround(blockPos, BlockMirror.NONE, rotation, pos);
		BlockBox blockBox = BlockBox.create(pos, blockPos2);
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
		Optional<Structure> optional = structureManager.getStructure(new Identifier(structureId));
		if (optional.isPresent()) {
			return (Structure)optional.get();
		} else {
			String string = structureId + ".snbt";
			Path path = Paths.get(testStructuresDirectoryName, string);
			NbtCompound nbtCompound = loadSnbt(path);
			if (nbtCompound == null) {
				throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
			} else {
				return structureManager.createStructure(nbtCompound);
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
		BlockState blockState = null;
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.getDefaultConfig(
			world.getRegistryManager().get(Registry.BIOME_KEY), world.getRegistryManager().get(Registry.STRUCTURE_SET_KEY)
		);
		List<BlockState> list = flatChunkGeneratorConfig.getLayerBlocks();
		int i = pos.getY() - world.getBottomY();
		if (pos.getY() < altitude && i > 0 && i <= list.size()) {
			blockState = (BlockState)list.get(i - 1);
		}

		if (blockState == null) {
			blockState = Blocks.AIR.getDefaultState();
		}

		BlockStateArgument blockStateArgument = new BlockStateArgument(blockState, Collections.emptySet(), null);
		blockStateArgument.setBlockState(world, pos, Block.NOTIFY_LISTENERS);
		world.updateNeighbors(pos, blockState.getBlock());
	}

	private static boolean isInStructureBounds(BlockPos structureBlockPos, BlockPos pos, ServerWorld world) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(structureBlockPos);
		Box box = getStructureBoundingBox(structureBlockBlockEntity).expand(1.0);
		return box.contains(Vec3d.ofCenter(pos));
	}
}
