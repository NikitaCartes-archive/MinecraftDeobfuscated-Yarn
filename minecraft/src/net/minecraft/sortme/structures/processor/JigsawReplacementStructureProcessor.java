package net.minecraft.sortme.structures.processor;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class JigsawReplacementStructureProcessor extends AbstractStructureProcessor {
	public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

	private JigsawReplacementStructureProcessor() {
	}

	@Nullable
	@Override
	public class_3499.class_3501 process(ViewableWorld viewableWorld, BlockPos blockPos, class_3499.class_3501 arg, class_3499.class_3501 arg2, class_3492 arg3) {
		Block block = arg2.field_15596.getBlock();
		if (block != Blocks.field_16540) {
			return arg2;
		} else {
			String string = arg2.field_15595.getString("final_state");
			BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), false);

			try {
				blockArgumentParser.parse(true);
			} catch (CommandSyntaxException var10) {
				throw new RuntimeException(var10);
			}

			return new class_3499.class_3501(arg2.field_15597, blockArgumentParser.getBlockState(), null);
		}
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16991;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
