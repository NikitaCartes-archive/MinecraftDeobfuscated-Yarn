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
	protected Structure field_15433;
	protected StructurePlacementData field_15434;
	protected BlockPos pos;

	public SimpleStructurePiece(StructurePieceType structurePieceType, int i) {
		super(structurePieceType, i);
	}

	public SimpleStructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
		super(structurePieceType, compoundTag);
		this.pos = new BlockPos(compoundTag.getInt("TPX"), compoundTag.getInt("TPY"), compoundTag.getInt("TPZ"));
	}

	protected void method_15027(Structure structure, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.field_15433 = structure;
		this.setOrientation(Direction.field_11043);
		this.pos = blockPos;
		this.field_15434 = structurePlacementData;
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
		this.field_15434.setBoundingBox(mutableIntBoundingBox);
		this.boundingBox = this.field_15433.calculateBoundingBox(this.field_15434, this.pos);
		if (this.field_15433.method_15172(iWorld, this.pos, this.field_15434, 2)) {
			for (Structure.StructureBlockInfo structureBlockInfo : this.field_15433.method_16445(this.pos, this.field_15434, Blocks.field_10465)) {
				if (structureBlockInfo.tag != null) {
					StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"));
					if (structureBlockMode == StructureBlockMode.field_12696) {
						this.handleMetadata(structureBlockInfo.tag.getString("metadata"), structureBlockInfo.pos, iWorld, random, mutableIntBoundingBox);
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo2 : this.field_15433.method_16445(this.pos, this.field_15434, Blocks.field_16540)) {
				if (structureBlockInfo2.tag != null) {
					String string = structureBlockInfo2.tag.getString("final_state");
					BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);
					BlockState blockState = Blocks.field_10124.method_9564();

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

					iWorld.method_8652(structureBlockInfo2.pos, blockState, 3);
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
		return this.field_15434.getRotation();
	}
}
