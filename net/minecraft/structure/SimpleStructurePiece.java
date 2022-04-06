/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.slf4j.Logger;

public abstract class SimpleStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final String template;
    protected Structure structure;
    protected StructurePlacementData placementData;
    protected BlockPos pos;

    public SimpleStructurePiece(StructurePieceType type, int length, StructureManager structureManager, Identifier id, String template, StructurePlacementData placementData, BlockPos pos) {
        super(type, length, structureManager.getStructureOrBlank(id).calculateBoundingBox(placementData, pos));
        this.setOrientation(Direction.NORTH);
        this.template = template;
        this.pos = pos;
        this.structure = structureManager.getStructureOrBlank(id);
        this.placementData = placementData;
    }

    public SimpleStructurePiece(StructurePieceType type, NbtCompound nbt, StructureManager structureManager, Function<Identifier, StructurePlacementData> placementDataGetter) {
        super(type, nbt);
        this.setOrientation(Direction.NORTH);
        this.template = nbt.getString("Template");
        this.pos = new BlockPos(nbt.getInt("TPX"), nbt.getInt("TPY"), nbt.getInt("TPZ"));
        Identifier identifier = this.getId();
        this.structure = structureManager.getStructureOrBlank(identifier);
        this.placementData = placementDataGetter.apply(identifier);
        this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
    }

    protected Identifier getId() {
        return new Identifier(this.template);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putInt("TPX", this.pos.getX());
        nbt.putInt("TPY", this.pos.getY());
        nbt.putInt("TPZ", this.pos.getZ());
        nbt.putString("Template", this.template);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, AbstractRandom random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
        this.placementData.setBoundingBox(chunkBox);
        this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
        if (this.structure.place(world, this.pos, pos, this.placementData, random, 2)) {
            List<Structure.StructureBlockInfo> list = this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK);
            for (Structure.StructureBlockInfo structureBlockInfo : list) {
                StructureBlockMode structureBlockMode;
                if (structureBlockInfo.nbt == null || (structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.nbt.getString("mode"))) != StructureBlockMode.DATA) continue;
                this.handleMetadata(structureBlockInfo.nbt.getString("metadata"), structureBlockInfo.pos, world, random, chunkBox);
            }
            List<Structure.StructureBlockInfo> list2 = this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.JIGSAW);
            for (Structure.StructureBlockInfo structureBlockInfo2 : list2) {
                if (structureBlockInfo2.nbt == null) continue;
                String string = structureBlockInfo2.nbt.getString("final_state");
                BlockState blockState = Blocks.AIR.getDefaultState();
                try {
                    blockState = BlockArgumentParser.block(Registry.BLOCK, string, true).blockState();
                } catch (CommandSyntaxException commandSyntaxException) {
                    LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)structureBlockInfo2.pos);
                }
                world.setBlockState(structureBlockInfo2.pos, blockState, Block.NOTIFY_ALL);
            }
        }
    }

    protected abstract void handleMetadata(String var1, BlockPos var2, ServerWorldAccess var3, AbstractRandom var4, BlockBox var5);

    @Override
    @Deprecated
    public void translate(int x, int y, int z) {
        super.translate(x, y, z);
        this.pos = this.pos.add(x, y, z);
    }

    @Override
    public BlockRotation getRotation() {
        return this.placementData.getRotation();
    }

    public Structure method_41624() {
        return this.structure;
    }

    public BlockPos method_41625() {
        return this.pos;
    }

    public StructurePlacementData method_41626() {
        return this.placementData;
    }
}

