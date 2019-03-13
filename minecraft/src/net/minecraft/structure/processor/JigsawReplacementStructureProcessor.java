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
import net.minecraft.world.ViewableWorld;

public class JigsawReplacementStructureProcessor extends StructureProcessor {
	public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

	private JigsawReplacementStructureProcessor() {
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo method_15110(
		ViewableWorld viewableWorld,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Block block = structureBlockInfo2.state.getBlock();
		if (block != Blocks.field_16540) {
			return structureBlockInfo2;
		} else {
			String string = structureBlockInfo2.field_15595.getString("final_state");
			BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);

			try {
				blockArgumentParser.parse(true);
			} catch (CommandSyntaxException var10) {
				throw new RuntimeException(var10);
			}

			return new Structure.StructureBlockInfo(structureBlockInfo2.field_15597, blockArgumentParser.getBlockState(), null);
		}
	}

	@Override
	protected StructureProcessorType method_16772() {
		return StructureProcessorType.field_16991;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
