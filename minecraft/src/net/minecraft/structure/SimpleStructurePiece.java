package net.minecraft.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleStructurePiece extends StructurePiece {
	private static final Logger LOGGER = LogManager.getLogger();
	public Structure field_15433;
	public StructurePlacementData field_15434;
	public BlockPos field_15432;

	public SimpleStructurePiece(StructurePieceType structurePieceType, int i) {
		super(structurePieceType, i);
	}

	public SimpleStructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
		super(structurePieceType, compoundTag);
		this.field_15432 = new BlockPos(compoundTag.getInt("TPX"), compoundTag.getInt("TPY"), compoundTag.getInt("TPZ"));
	}

	protected void method_15027(Structure structure, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.field_15433 = structure;
		this.method_14926(Direction.NORTH);
		this.field_15432 = blockPos;
		this.field_15434 = structurePlacementData;
		this.boundingBox = structure.method_16187(structurePlacementData, blockPos);
	}

	@Override
	protected void method_14943(CompoundTag compoundTag) {
		compoundTag.putInt("TPX", this.field_15432.getX());
		compoundTag.putInt("TPY", this.field_15432.getY());
		compoundTag.putInt("TPZ", this.field_15432.getZ());
	}

	@Override
	public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		this.field_15434.setBoundingBox(mutableIntBoundingBox);
		if (this.field_15433.method_15172(iWorld, this.field_15432, this.field_15434, 2)) {
			for (Structure.StructureBlockInfo structureBlockInfo : this.field_15433.method_16445(this.field_15432, this.field_15434, Blocks.field_10465)) {
				if (structureBlockInfo.field_15595 != null) {
					StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.field_15595.getString("mode"));
					if (structureBlockMode == StructureBlockMode.field_12696) {
						this.method_15026(structureBlockInfo.field_15595.getString("metadata"), structureBlockInfo.field_15597, iWorld, random, mutableIntBoundingBox);
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo2 : this.field_15433.method_16445(this.field_15432, this.field_15434, Blocks.field_16540)) {
				if (structureBlockInfo2.field_15595 != null) {
					String string = structureBlockInfo2.field_15595.getString("final_state");
					BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);
					BlockState blockState = Blocks.field_10124.method_9564();

					try {
						blockArgumentParser.parse(true);
						BlockState blockState2 = blockArgumentParser.getBlockState();
						if (blockState2 != null) {
							blockState = blockState2;
						} else {
							LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.field_15597);
						}
					} catch (CommandSyntaxException var13) {
						LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.field_15597);
					}

					iWorld.method_8652(structureBlockInfo2.field_15597, blockState, 3);
				}
			}
		}

		return true;
	}

	protected abstract void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox);

	@Override
	public void translate(int i, int j, int k) {
		super.translate(i, j, k);
		this.field_15432 = this.field_15432.add(i, j, k);
	}

	@Override
	public Rotation getRotation() {
		return this.field_15434.getRotation();
	}
}
