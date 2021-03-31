package net.minecraft.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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
	protected final String field_31664;
	protected Structure structure;
	protected StructurePlacementData placementData;
	protected BlockPos pos;

	public SimpleStructurePiece(
		StructurePieceType structurePieceType,
		int i,
		StructureManager structureManager,
		Identifier identifier,
		String string,
		StructurePlacementData structurePlacementData,
		BlockPos blockPos
	) {
		super(structurePieceType, i, structureManager.getStructureOrBlank(identifier).calculateBoundingBox(structurePlacementData, blockPos));
		this.setOrientation(Direction.NORTH);
		this.field_31664 = string;
		this.pos = blockPos;
		this.structure = structureManager.getStructureOrBlank(identifier);
		this.placementData = structurePlacementData;
	}

	public SimpleStructurePiece(
		StructurePieceType structurePieceType, NbtCompound nbtCompound, ServerWorld serverWorld, Function<Identifier, StructurePlacementData> function
	) {
		super(structurePieceType, nbtCompound);
		this.setOrientation(Direction.NORTH);
		this.field_31664 = nbtCompound.getString("Template");
		this.pos = new BlockPos(nbtCompound.getInt("TPX"), nbtCompound.getInt("TPY"), nbtCompound.getInt("TPZ"));
		Identifier identifier = this.method_35470();
		this.structure = serverWorld.getStructureManager().getStructureOrBlank(identifier);
		this.placementData = (StructurePlacementData)function.apply(identifier);
		this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
	}

	protected Identifier method_35470() {
		return new Identifier(this.field_31664);
	}

	@Override
	protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		nbt.putInt("TPX", this.pos.getX());
		nbt.putInt("TPY", this.pos.getY());
		nbt.putInt("TPZ", this.pos.getZ());
		nbt.putString("Template", this.field_31664);
	}

	@Override
	public boolean generate(
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

		return true;
	}

	protected abstract void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox);

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
