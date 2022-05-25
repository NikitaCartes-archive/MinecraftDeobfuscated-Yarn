package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;

public class ProtectedBlocksStructureProcessor extends StructureProcessor {
	public final TagKey<Block> protectedBlocksTag;
	public static final Codec<ProtectedBlocksStructureProcessor> CODEC = TagKey.codec(Registry.BLOCK_KEY)
		.xmap(ProtectedBlocksStructureProcessor::new, processor -> processor.protectedBlocksTag);

	public ProtectedBlocksStructureProcessor(TagKey<Block> protectedBlocksTag) {
		this.protectedBlocksTag = protectedBlocksTag;
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		Structure.StructureBlockInfo originalBlockInfo,
		Structure.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	) {
		return Feature.notInBlockTagPredicate(this.protectedBlocksTag).test(world.getBlockState(currentBlockInfo.pos)) ? currentBlockInfo : null;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.PROTECTED_BLOCKS;
	}
}
