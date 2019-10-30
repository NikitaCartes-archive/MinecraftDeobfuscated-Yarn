package net.minecraft.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleStructurePiece extends StructurePiece {
	private static final Logger LOGGER = LogManager.getLogger();
	protected Structure structure;
	protected StructurePlacementData placementData;
	protected BlockPos pos;

	public SimpleStructurePiece(StructurePieceType structurePieceType, int i) {
		super(structurePieceType, i);
	}

	public SimpleStructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
		super(structurePieceType, compoundTag);
		this.pos = new BlockPos(compoundTag.getInt("TPX"), compoundTag.getInt("TPY"), compoundTag.getInt("TPZ"));
	}

	protected void setStructureData(Structure structure, BlockPos pos, StructurePlacementData placementData) {
		this.structure = structure;
		this.setOrientation(Direction.NORTH);
		this.pos = pos;
		this.placementData = placementData;
		this.boundingBox = structure.calculateBoundingBox(placementData, pos);
	}

	@Override
	protected void toNbt(CompoundTag tag) {
		tag.putInt("TPX", this.pos.getX());
		tag.putInt("TPY", this.pos.getY());
		tag.putInt("TPZ", this.pos.getZ());
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		this.placementData.setBoundingBox(blockBox);
		this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
		if (this.structure.method_15172(world, this.pos, this.placementData, 2)) {
			for (Structure.StructureBlockInfo structureBlockInfo : this.structure.method_16445(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK)) {
				if (structureBlockInfo.tag != null) {
					StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"));
					if (structureBlockMode == StructureBlockMode.DATA) {
						this.handleMetadata(structureBlockInfo.tag.getString("metadata"), structureBlockInfo.pos, world, random, blockBox);
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo2 : this.structure.method_16445(this.pos, this.placementData, Blocks.JIGSAW)) {
				if (structureBlockInfo2.tag != null) {
					String string = structureBlockInfo2.tag.getString("final_state");
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
					} catch (CommandSyntaxException var14) {
						LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.pos);
					}

					world.setBlockState(structureBlockInfo2.pos, blockState, 3);
				}
			}
		}

		return true;
	}

	protected abstract void handleMetadata(String metadata, BlockPos pos, IWorld world, Random random, BlockBox boundingBox);

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
