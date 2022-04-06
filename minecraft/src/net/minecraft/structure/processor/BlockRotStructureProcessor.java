package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.WorldView;

public class BlockRotStructureProcessor extends StructureProcessor {
	public static final Codec<BlockRotStructureProcessor> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryCodecs.entryList(Registry.BLOCK_KEY).optionalFieldOf("rottable_blocks").forGetter(processor -> processor.rottableBlocks),
					Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter(processor -> processor.integrity)
				)
				.apply(instance, BlockRotStructureProcessor::new)
	);
	private Optional<RegistryEntryList<Block>> rottableBlocks;
	private final float integrity;

	public BlockRotStructureProcessor(TagKey<Block> rottableBlocks, float integrity) {
		this(Optional.of(Registry.BLOCK.getOrCreateEntryList(rottableBlocks)), integrity);
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
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		Structure.StructureBlockInfo originalBlockInfo,
		Structure.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	) {
		AbstractRandom abstractRandom = data.getRandom(currentBlockInfo.pos);
		return (!this.rottableBlocks.isPresent() || originalBlockInfo.state.isIn((RegistryEntryList<Block>)this.rottableBlocks.get()))
				&& !(abstractRandom.nextFloat() <= this.integrity)
			? null
			: currentBlockInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.BLOCK_ROT;
	}
}
