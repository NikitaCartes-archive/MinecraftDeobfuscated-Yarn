package net.minecraft.structure.processor;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class JigsawReplacementStructureProcessor extends StructureProcessor {
	public static final Codec<JigsawReplacementStructureProcessor> CODEC = Codec.unit(
		(Supplier<JigsawReplacementStructureProcessor>)(() -> JigsawReplacementStructureProcessor.INSTANCE)
	);
	public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

	private JigsawReplacementStructureProcessor() {
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		BlockState blockState = structureBlockInfo2.state;
		if (blockState.isOf(Blocks.JIGSAW)) {
			String string = structureBlockInfo2.tag.getString("final_state");
			BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);

			try {
				blockArgumentParser.parse(true);
			} catch (CommandSyntaxException var11) {
				throw new RuntimeException(var11);
			}

			return blockArgumentParser.getBlockState().isOf(Blocks.STRUCTURE_VOID)
				? null
				: new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockArgumentParser.getBlockState(), null);
		} else {
			return structureBlockInfo2;
		}
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.JIGSAW_REPLACEMENT;
	}
}
