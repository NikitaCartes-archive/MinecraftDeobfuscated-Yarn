package net.minecraft.structure.processor;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class JigsawReplacementStructureProcessor extends StructureProcessor {
	public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

	private JigsawReplacementStructureProcessor() {
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos pos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData placementData
	) {
		Block block = structureBlockInfo2.state.getBlock();
		if (block != Blocks.JIGSAW) {
			return structureBlockInfo2;
		} else {
			String string = structureBlockInfo2.tag.getString("final_state");
			BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);

			try {
				blockArgumentParser.parse(true);
			} catch (CommandSyntaxException var10) {
				throw new RuntimeException(var10);
			}

			return blockArgumentParser.getBlockState().getBlock() == Blocks.STRUCTURE_VOID
				? null
				: new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockArgumentParser.getBlockState(), null);
		}
	}

	@Override
	protected StructureProcessorType getType() {
		return StructureProcessorType.JIGSAW_REPLACEMENT;
	}

	@Override
	protected <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
