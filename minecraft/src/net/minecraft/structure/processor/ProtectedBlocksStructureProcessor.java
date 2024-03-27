package net.minecraft.structure.processor;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;

public class ProtectedBlocksStructureProcessor extends StructureProcessor {
	public final TagKey<Block> protectedBlocksTag;
	public static final MapCodec<ProtectedBlocksStructureProcessor> CODEC = TagKey.codec(RegistryKeys.BLOCK)
		.<ProtectedBlocksStructureProcessor>xmap(ProtectedBlocksStructureProcessor::new, processor -> processor.protectedBlocksTag)
		.fieldOf("value");

	public ProtectedBlocksStructureProcessor(TagKey<Block> protectedBlocksTag) {
		this.protectedBlocksTag = protectedBlocksTag;
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
		return Feature.notInBlockTagPredicate(this.protectedBlocksTag).test(world.getBlockState(currentBlockInfo.pos())) ? currentBlockInfo : null;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.PROTECTED_BLOCKS;
	}
}
