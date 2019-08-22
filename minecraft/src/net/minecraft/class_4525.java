package net.minecraft;

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
import net.minecraft.block.Block;
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
import org.apache.commons.io.IOUtils;

public class class_4525 {
	public static String field_20579 = "gameteststructures";

	public static Box method_22242(StructureBlockBlockEntity structureBlockBlockEntity) {
		BlockPos blockPos = structureBlockBlockEntity.getPos().add(structureBlockBlockEntity.getOffset());
		return new Box(blockPos, blockPos.add(structureBlockBlockEntity.getSize()));
	}

	public static void method_22248(BlockPos blockPos, ServerWorld serverWorld) {
		serverWorld.setBlockState(blockPos, Blocks.COMMAND_BLOCK.getDefaultState());
		CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		commandBlockBlockEntity.getCommandExecutor().setCommand("test runthis");
		serverWorld.setBlockState(blockPos.add(0, 0, -1), Blocks.STONE_BUTTON.getDefaultState());
	}

	public static void method_22251(String string, BlockPos blockPos, BlockPos blockPos2, int i, ServerWorld serverWorld) {
		method_22246(blockPos, blockPos2, i, serverWorld);
		serverWorld.setBlockState(blockPos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(string));
		structureBlockBlockEntity.setSize(blockPos2);
		structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
		structureBlockBlockEntity.setShowBoundingBox(true);
	}

	public static StructureBlockBlockEntity method_22250(String string, BlockPos blockPos, int i, ServerWorld serverWorld) {
		method_22256(blockPos, serverWorld);
		method_22260(string, blockPos, i, serverWorld);
		return method_22252(string, blockPos, serverWorld, false);
	}

	public static StructureBlockBlockEntity method_22257(String string, BlockPos blockPos, int i, ServerWorld serverWorld) {
		method_22256(blockPos, serverWorld);
		method_22260(string, blockPos, i, serverWorld);
		return method_22252(string, blockPos, serverWorld, true);
	}

	private static void method_22256(BlockPos blockPos, ServerWorld serverWorld) {
		ChunkPos chunkPos = new ChunkPos(blockPos);

		for (int i = -1; i < 4; i++) {
			for (int j = -1; j < 4; j++) {
				int k = chunkPos.x + i;
				int l = chunkPos.z + j;
				serverWorld.setChunkForced(k, l, true);
			}
		}
	}

	public static void method_22243(StructureBlockBlockEntity structureBlockBlockEntity, int i, ServerWorld serverWorld) {
		BlockPos blockPos = structureBlockBlockEntity.getSize();
		BlockPos blockPos2 = structureBlockBlockEntity.getPos().add(structureBlockBlockEntity.getOffset());
		method_22246(blockPos2, blockPos, i, serverWorld);
	}

	public static void method_22246(BlockPos blockPos, BlockPos blockPos2, int i, ServerWorld serverWorld) {
		BlockPos blockPos3 = blockPos.add(-i, -3, -i);
		BlockPos blockPos4 = blockPos.add(blockPos2).add(i - 1, 30, i - 1);
		BlockPos.stream(blockPos3, blockPos4).forEach(blockPosx -> method_22259(blockPosx, serverWorld));
		Box box = new Box(blockPos3, blockPos4);
		List<Entity> list = serverWorld.getEntities(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
		list.forEach(Entity::kill);
	}

	public static Optional<BlockPos> method_22244(BlockPos blockPos, int i, ServerWorld serverWorld) {
		return method_22258(blockPos, i, serverWorld).stream().filter(blockPos2 -> method_22247(blockPos2, blockPos, serverWorld)).findFirst();
	}

	@Nullable
	public static BlockPos method_22255(BlockPos blockPos, int i, ServerWorld serverWorld) {
		Comparator<BlockPos> comparator = Comparator.comparingInt(blockPos2 -> blockPos2.getManhattanDistance(blockPos));
		Collection<BlockPos> collection = method_22258(blockPos, i, serverWorld);
		Optional<BlockPos> optional = collection.stream().min(comparator);
		return (BlockPos)optional.orElse(null);
	}

	public static Collection<BlockPos> method_22258(BlockPos blockPos, int i, ServerWorld serverWorld) {
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

	private static void method_22260(String string, BlockPos blockPos, int i, ServerWorld serverWorld) {
		StructureBlockBlockEntity structureBlockBlockEntity = method_22252(string, blockPos, serverWorld, true);
		method_22243(structureBlockBlockEntity, i, serverWorld);
	}

	private static StructureBlockBlockEntity method_22252(String string, BlockPos blockPos, ServerWorld serverWorld, boolean bl) {
		serverWorld.setBlockState(blockPos, Blocks.STRUCTURE_BLOCK.getDefaultState());
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		structureBlockBlockEntity.setMode(StructureBlockMode.LOAD);
		structureBlockBlockEntity.setIgnoreEntities(false);
		structureBlockBlockEntity.setStructureName(new Identifier(string));
		structureBlockBlockEntity.loadStructure(bl);
		if (structureBlockBlockEntity.getSize() != BlockPos.ORIGIN) {
			return structureBlockBlockEntity;
		} else {
			StructureManager structureManager = serverWorld.getStructureManager();
			String string2 = string + ".snbt";
			Path path = Paths.get(field_20579, string2);
			CompoundTag compoundTag = method_22253(path);
			if (compoundTag == null) {
				throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
			} else {
				Structure structure = structureManager.method_21891(compoundTag);
				structureBlockBlockEntity.method_21864(bl, structure);
				if (structureBlockBlockEntity.getSize() == BlockPos.ORIGIN) {
					throw new RuntimeException("Failed to load structure " + path);
				} else {
					return structureBlockBlockEntity;
				}
			}
		}
	}

	@Nullable
	private static CompoundTag method_22253(Path path) {
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

	private static void method_22259(BlockPos blockPos, ServerWorld serverWorld) {
		Block block = blockPos.getY() <= 3 ? Blocks.GRASS_BLOCK : Blocks.AIR;
		BlockState blockState = block.getDefaultState();
		BlockStateArgument blockStateArgument = new BlockStateArgument(blockState, Collections.emptySet(), null);
		blockStateArgument.setBlockState(serverWorld, blockPos, 2);
		serverWorld.updateNeighbors(blockPos, block);
	}

	private static boolean method_22247(BlockPos blockPos, BlockPos blockPos2, ServerWorld serverWorld) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		Box box = method_22242(structureBlockBlockEntity);
		return box.contains(new Vec3d(blockPos2));
	}
}
