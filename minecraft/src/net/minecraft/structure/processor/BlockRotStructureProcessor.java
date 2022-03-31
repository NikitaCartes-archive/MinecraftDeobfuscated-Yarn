package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;

public class BlockRotStructureProcessor extends StructureProcessor {
	public static final Codec<BlockRotStructureProcessor> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					TagKey.identifierCodec(Registry.BLOCK_KEY).optionalFieldOf("rottable_blocks").forGetter(processor -> processor.rottableBlocks),
					Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter(processor -> processor.integrity)
				)
				.apply(instance, BlockRotStructureProcessor::new)
	);
	private Optional<TagKey<Block>> rottableBlocks;
	private final float integrity;

	public BlockRotStructureProcessor(TagKey<Block> rottableBlocks, float integrity) {
		this(Optional.of(rottableBlocks), integrity);
	}

	public BlockRotStructureProcessor(float integrity) {
		this(Optional.empty(), integrity);
	}

	private BlockRotStructureProcessor(Optional<TagKey<Block>> rottableBlocks, float integrity) {
		this.integrity = integrity;
		this.rottableBlocks = rottableBlocks;
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
		Random random = data.getRandom(currentBlockInfo.pos);
		return (!this.rottableBlocks.isPresent() || originalBlockInfo.state.isIn((TagKey<Block>)this.rottableBlocks.get()))
				&& !(random.nextFloat() <= this.integrity)
			? null
			: currentBlockInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.BLOCK_ROT;
	}
}
