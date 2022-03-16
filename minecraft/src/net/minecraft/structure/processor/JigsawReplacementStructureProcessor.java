package net.minecraft.structure.processor;

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
import net.minecraft.util.registry.Registry;
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
		BlockPos pivot,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData data
	) {
		BlockState blockState = structureBlockInfo2.state;
		if (blockState.isOf(Blocks.JIGSAW)) {
			String string = structureBlockInfo2.nbt.getString("final_state");

			BlockState blockState2;
			try {
				BlockArgumentParser.BlockResult blockResult = BlockArgumentParser.block(Registry.BLOCK, string, true);
				blockState2 = blockResult.blockState();
			} catch (CommandSyntaxException var11) {
				throw new RuntimeException(var11);
			}

			return blockState2.isOf(Blocks.STRUCTURE_VOID) ? null : new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockState2, null);
		} else {
			return structureBlockInfo2;
		}
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.JIGSAW_REPLACEMENT;
	}
}
