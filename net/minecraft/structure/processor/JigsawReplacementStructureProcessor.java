/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class JigsawReplacementStructureProcessor
extends StructureProcessor {
    public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

    private JigsawReplacementStructureProcessor() {
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView worldView, BlockPos pos, BlockPos blockPos, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        Block block = structureBlockInfo2.state.getBlock();
        if (block != Blocks.JIGSAW) {
            return structureBlockInfo2;
        }
        String string = structureBlockInfo2.tag.getString("final_state");
        BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);
        try {
            blockArgumentParser.parse(true);
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new RuntimeException(commandSyntaxException);
        }
        if (blockArgumentParser.getBlockState().getBlock() == Blocks.STRUCTURE_VOID) {
            return null;
        }
        return new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockArgumentParser.getBlockState(), null);
    }

    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.JIGSAW_REPLACEMENT;
    }

    @Override
    protected <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.emptyMap());
    }
}

