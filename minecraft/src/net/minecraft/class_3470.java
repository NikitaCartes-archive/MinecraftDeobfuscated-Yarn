package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.Structure;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.StructurePlacementData;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3470 extends class_3443 {
	private static final Logger field_16586 = LogManager.getLogger();
	public Structure field_15433;
	public StructurePlacementData field_15434;
	public BlockPos field_15432;

	public class_3470(StructurePiece structurePiece, int i) {
		super(structurePiece, i);
	}

	public class_3470(StructurePiece structurePiece, CompoundTag compoundTag) {
		super(structurePiece, compoundTag);
		this.field_15432 = new BlockPos(compoundTag.getInt("TPX"), compoundTag.getInt("TPY"), compoundTag.getInt("TPZ"));
	}

	protected void method_15027(Structure structure, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.field_15433 = structure;
		this.method_14926(Direction.NORTH);
		this.field_15432 = blockPos;
		this.field_15434 = structurePlacementData;
		this.structureBounds = structure.method_16187(structurePlacementData, blockPos);
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		compoundTag.putInt("TPX", this.field_15432.getX());
		compoundTag.putInt("TPY", this.field_15432.getY());
		compoundTag.putInt("TPZ", this.field_15432.getZ());
	}

	@Override
	public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		this.field_15434.setBoundingBox(mutableIntBoundingBox);
		if (this.field_15433.method_15172(iWorld, this.field_15432, this.field_15434, 2)) {
			for (Structure.StructureBlockInfo structureBlockInfo : this.field_15433.method_16445(this.field_15432, this.field_15434, Blocks.field_10465)) {
				if (structureBlockInfo.tag != null) {
					StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"));
					if (structureBlockMode == StructureBlockMode.field_12696) {
						this.method_15026(structureBlockInfo.tag.getString("metadata"), structureBlockInfo.pos, iWorld, random, mutableIntBoundingBox);
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo2 : this.field_15433.method_16445(this.field_15432, this.field_15434, Blocks.field_16540)) {
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
							field_16586.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.pos);
						}
					} catch (CommandSyntaxException var13) {
						field_16586.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo2.pos);
					}

					iWorld.setBlockState(structureBlockInfo2.pos, blockState, 3);
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
	public Rotation method_16888() {
		return this.field_15434.getRotation();
	}
}
