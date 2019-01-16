package net.minecraft.sortme.structures.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class BlockIgnoreStructureProcessor extends AbstractStructureProcessor {
	public static final BlockIgnoreStructureProcessor field_16718 = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.field_10465));
	public static final BlockIgnoreStructureProcessor field_16719 = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.field_10124));
	public static final BlockIgnoreStructureProcessor field_16721 = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.field_10124, Blocks.field_10465));
	private final ImmutableList<Block> blocks;

	public BlockIgnoreStructureProcessor(List<Block> list) {
		this.blocks = ImmutableList.copyOf(list);
	}

	public BlockIgnoreStructureProcessor(Dynamic<?> dynamic) {
		this(dynamic.get("blocks").asList(dynamicx -> BlockState.deserialize(dynamicx).getBlock()));
	}

	@Nullable
	@Override
	public class_3499.class_3501 process(ViewableWorld viewableWorld, BlockPos blockPos, class_3499.class_3501 arg, class_3499.class_3501 arg2, class_3492 arg3) {
		return this.blocks.contains(arg2.field_15596.getBlock()) ? null : arg2;
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16986;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("blocks"),
					dynamicOps.createList(this.blocks.stream().map(block -> BlockState.serialize(dynamicOps, block.getDefaultState()).getValue()))
				)
			)
		);
	}
}
