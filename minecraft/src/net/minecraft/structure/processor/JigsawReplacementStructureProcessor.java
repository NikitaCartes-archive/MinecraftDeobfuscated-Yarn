package net.minecraft.structure.processor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.slf4j.Logger;

public class JigsawReplacementStructureProcessor extends StructureProcessor {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<JigsawReplacementStructureProcessor> CODEC = MapCodec.unit(
		(Supplier<JigsawReplacementStructureProcessor>)(() -> JigsawReplacementStructureProcessor.INSTANCE)
	);
	public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

	private JigsawReplacementStructureProcessor() {
	}

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		StructureTemplate.StructureBlockInfo originalBlockInfo,
		StructureTemplate.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	) {
		BlockState blockState = currentBlockInfo.state();
		if (blockState.isOf(Blocks.JIGSAW)) {
			if (currentBlockInfo.nbt() == null) {
				LOGGER.warn("Jigsaw block at {} is missing nbt, will not replace", pos);
				return currentBlockInfo;
			} else {
				String string = currentBlockInfo.nbt().getString("final_state");

				BlockState blockState2;
				try {
					BlockArgumentParser.BlockResult blockResult = BlockArgumentParser.block(world.createCommandRegistryWrapper(RegistryKeys.BLOCK), string, true);
					blockState2 = blockResult.blockState();
				} catch (CommandSyntaxException var11) {
					LOGGER.error("Failed to parse jigsaw replacement state '{}' at {}: {}", string, pos, var11.getMessage());
					return null;
				}

				return blockState2.isOf(Blocks.STRUCTURE_VOID) ? null : new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), blockState2, null);
			}
		} else {
			return currentBlockInfo;
		}
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.JIGSAW_REPLACEMENT;
	}
}
