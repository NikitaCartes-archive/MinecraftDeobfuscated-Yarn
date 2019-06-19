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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
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

	protected void setStructureData(Structure structure, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.structure = structure;
		this.setOrientation(Direction.field_11043);
		this.pos = blockPos;
		this.placementData = structurePlacementData;
		this.boundingBox = structure.calculateBoundingBox(structurePlacementData, blockPos);
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		compoundTag.putInt("TPX", this.pos.getX());
		compoundTag.putInt("TPY", this.pos.getY());
		compoundTag.putInt("TPZ", this.pos.getZ());
	}

	@Override
	public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		this.placementData.setBoundingBox(mutableIntBoundingBox);
		this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
		if (this.structure.method_15172(iWorld, this.pos, this.placementData, 2)) {
			for (Structure.StructureBlockInfo structureBlockInfo : this.structure.method_16445(this.pos, this.placementData, Blocks.field_10465)) {
				if (structureBlockInfo.tag != null) {
					StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"));
					if (structureBlockMode == StructureBlockMode.field_12696) {
						this.handleMetadata(structureBlockInfo.tag.getString("metadata"), structureBlockInfo.pos, iWorld, random, mutableIntBoundingBox);
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo2 : this.structure.method_16445(this.pos, this.placementData, Blocks.field_16540)) {
				if (structureBlockInfo2.tag != null) {
					String string = structureBlockInfo2.tag.getString("final_state");
					BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);
					BlockState blockState = Blocks.field_10124.getDefaultState();

					try {
						blockArgumentParser.parse(true);
						BlockState blockState2 = blockArgumentParser.getBlockState();
						if (blockState2 != null) {
							blockState = blockState2;
						} else {
							LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.pos);
						}
					} catch (CommandSyntaxException var13) {
						LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.pos);
					}

					iWorld.setBlockState(structureBlockInfo2.pos, blockState, 3);
				}
			}
		}

		return true;
	}

	protected abstract void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox);

	@Override
	public void translate(int i, int j, int k) {
		super.translate(i, j, k);
		this.pos = this.pos.add(i, j, k);
	}

	@Override
	public BlockRotation getRotation() {
		return this.placementData.getRotation();
	}
}
