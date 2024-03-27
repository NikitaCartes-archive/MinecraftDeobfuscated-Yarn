package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

public class BlockRotStructureProcessor extends StructureProcessor {
	public static final MapCodec<BlockRotStructureProcessor> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.BLOCK).optionalFieldOf("rottable_blocks").forGetter(processor -> processor.rottableBlocks),
					Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter(processor -> processor.integrity)
				)
				.apply(instance, BlockRotStructureProcessor::new)
	);
	private final Optional<RegistryEntryList<Block>> rottableBlocks;
	private final float integrity;

	public BlockRotStructureProcessor(RegistryEntryList<Block> rottableBlocks, float integrity) {
		this(Optional.of(rottableBlocks), integrity);
	}

	public BlockRotStructureProcessor(float integrity) {
		this(Optional.empty(), integrity);
	}

	private BlockRotStructureProcessor(Optional<RegistryEntryList<Block>> rottableBlocks, float integrity) {
		this.integrity = integrity;
		this.rottableBlocks = rottableBlocks;
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
		Random random = data.getRandom(currentBlockInfo.pos());
		return (!this.rottableBlocks.isPresent() || originalBlockInfo.state().isIn((RegistryEntryList<Block>)this.rottableBlocks.get()))
				&& !(random.nextFloat() <= this.integrity)
			? null
			: currentBlockInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.BLOCK_ROT;
	}
}
