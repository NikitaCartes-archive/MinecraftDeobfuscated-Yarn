/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Random;
import net.fabricmc.yarn.constants.SetBlockStateFlags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockRotation;
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

public abstract class SimpleStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogManager.getLogger();
    protected Structure structure;
    protected StructurePlacementData placementData;
    protected BlockPos pos;

    public SimpleStructurePiece(StructurePieceType structurePieceType, int i) {
        super(structurePieceType, i);
    }

    public SimpleStructurePiece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
        super(structurePieceType, nbtCompound);
        this.pos = new BlockPos(nbtCompound.getInt("TPX"), nbtCompound.getInt("TPY"), nbtCompound.getInt("TPZ"));
    }

    protected void setStructureData(Structure structure, BlockPos pos, StructurePlacementData placementData) {
        this.structure = structure;
        this.setOrientation(Direction.NORTH);
        this.pos = pos;
        this.placementData = placementData;
        this.boundingBox = structure.calculateBoundingBox(placementData, pos);
    }

    @Override
    protected void writeNbt(ServerWorld world, NbtCompound nbt) {
        nbt.putInt("TPX", this.pos.getX());
        nbt.putInt("TPY", this.pos.getY());
        nbt.putInt("TPZ", this.pos.getZ());
    }

    @Override
    public boolean generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        this.placementData.setBoundingBox(boundingBox);
        this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
        if (this.structure.place(world, this.pos, pos, this.placementData, random, SetBlockStateFlags.NOTIFY_LISTENERS)) {
            List<Structure.StructureBlockInfo> list = this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK);
            for (Structure.StructureBlockInfo structureBlockInfo : list) {
                StructureBlockMode structureBlockMode;
                if (structureBlockInfo.tag == null || (structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"))) != StructureBlockMode.DATA) continue;
                this.handleMetadata(structureBlockInfo.tag.getString("metadata"), structureBlockInfo.pos, world, random, boundingBox);
            }
            List<Structure.StructureBlockInfo> list2 = this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.JIGSAW);
            for (Structure.StructureBlockInfo structureBlockInfo2 : list2) {
                if (structureBlockInfo2.tag == null) continue;
                String string = structureBlockInfo2.tag.getString("final_state");
                BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);
                BlockState blockState = Blocks.AIR.getDefaultState();
                try {
                    blockArgumentParser.parse(true);
                    BlockState blockState2 = blockArgumentParser.getBlockState();
                    if (blockState2 != null) {
                        blockState = blockState2;
                    } else {
                        LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)structureBlockInfo2.pos);
                    }
                } catch (CommandSyntaxException commandSyntaxException) {
                    LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)structureBlockInfo2.pos);
                }
                world.setBlockState(structureBlockInfo2.pos, blockState, SetBlockStateFlags.DEFAULT);
            }
        }
        return true;
    }

    protected abstract void handleMetadata(String var1, BlockPos var2, ServerWorldAccess var3, Random var4, BlockBox var5);

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

