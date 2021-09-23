package net.minecraft.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_6625;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleStructurePiece extends StructurePiece {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final String identifier;
	protected Structure structure;
	protected StructurePlacementData placementData;
	protected BlockPos pos;

	public SimpleStructurePiece(
		StructurePieceType type, int i, StructureManager structureManager, Identifier identifier, String string, StructurePlacementData placementData, BlockPos pos
	) {
		super(type, i, structureManager.getStructureOrBlank(identifier).calculateBoundingBox(placementData, pos));
		this.setOrientation(Direction.NORTH);
		this.identifier = string;
		this.pos = pos;
		this.structure = structureManager.getStructureOrBlank(identifier);
		this.placementData = placementData;
	}

	public SimpleStructurePiece(
		StructurePieceType type, NbtCompound nbtCompound, StructureManager structureManager, Function<Identifier, StructurePlacementData> function
	) {
		super(type, nbtCompound);
		this.setOrientation(Direction.NORTH);
		this.identifier = nbtCompound.getString("Template");
		this.pos = new BlockPos(nbtCompound.getInt("TPX"), nbtCompound.getInt("TPY"), nbtCompound.getInt("TPZ"));
		Identifier identifier = this.getId();
		this.structure = structureManager.getStructureOrBlank(identifier);
		this.placementData = (StructurePlacementData)function.apply(identifier);
		this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
	}

	protected Identifier getId() {
		return new Identifier(this.identifier);
	}

	@Override
	protected void writeNbt(class_6625 arg, NbtCompound nbt) {
		nbt.putInt("TPX", this.pos.getX());
		nbt.putInt("TPY", this.pos.getY());
		nbt.putInt("TPZ", this.pos.getZ());
		nbt.putString("Template", this.identifier);
	}

	@Override
	public void generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		ChunkPos chunkPos,
		BlockPos pos
	) {
		this.placementData.setBoundingBox(boundingBox);
		this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
		if (this.structure.place(world, this.pos, pos, this.placementData, random, Block.NOTIFY_LISTENERS)) {
			for (Structure.StructureBlockInfo structureBlockInfo : this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK)) {
				if (structureBlockInfo.nbt != null) {
					StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.nbt.getString("mode"));
					if (structureBlockMode == StructureBlockMode.DATA) {
						this.handleMetadata(structureBlockInfo.nbt.getString("metadata"), structureBlockInfo.pos, world, random, boundingBox);
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo2 : this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.JIGSAW)) {
				if (structureBlockInfo2.nbt != null) {
					String string = structureBlockInfo2.nbt.getString("final_state");
					BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);
					BlockState blockState = Blocks.AIR.getDefaultState();

					try {
						blockArgumentParser.parse(true);
						BlockState blockState2 = blockArgumentParser.getBlockState();
						if (blockState2 != null) {
							blockState = blockState2;
						} else {
							LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.pos);
						}
					} catch (CommandSyntaxException var16) {
						LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.pos);
					}

					world.setBlockState(structureBlockInfo2.pos, blockState, Block.NOTIFY_ALL);
				}
			}
		}
	}

	protected abstract void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox);

	@Deprecated
	@Override
	public void translate(int x, int y, int z) {
		super.translate(x, y, z);
		this.pos = this.pos.add(x, y, z);
	}

	@Override
	public BlockRotation getRotation() {
		return this.placementData.getRotation();
	}
}
