package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;

public class CappedStructureProcessor extends StructureProcessor {
	public static final Codec<CappedStructureProcessor> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructureProcessorType.CODEC.fieldOf("delegate").forGetter(processor -> processor.delegate),
					IntProvider.POSITIVE_CODEC.fieldOf("limit").forGetter(processor -> processor.limit)
				)
				.apply(instance, CappedStructureProcessor::new)
	);
	private final StructureProcessor delegate;
	private final IntProvider limit;

	public CappedStructureProcessor(StructureProcessor delegate, IntProvider limit) {
		this.delegate = delegate;
		this.limit = limit;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.CAPPED;
	}

	@Override
	public final List<StructureTemplate.StructureBlockInfo> reprocess(
		ServerWorldAccess world,
		BlockPos pos,
		BlockPos pivot,
		List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
		List<StructureTemplate.StructureBlockInfo> currentBlockInfos,
		StructurePlacementData data
	) {
		if (this.limit.getMax() != 0 && !currentBlockInfos.isEmpty()) {
			if (originalBlockInfos.size() != currentBlockInfos.size()) {
				Util.error(
					"Original block info list not in sync with processed list, skipping processing. Original size: "
						+ originalBlockInfos.size()
						+ ", Processed size: "
						+ currentBlockInfos.size()
				);
				return currentBlockInfos;
			} else {
				Random random = Random.create(world.toServerWorld().getSeed()).nextSplitter().split(pos);
				int i = Math.min(this.limit.get(random), currentBlockInfos.size());
				if (i < 1) {
					return currentBlockInfos;
				} else {
					IntArrayList intArrayList = Util.shuffle(IntStream.range(0, currentBlockInfos.size()), random);
					IntIterator intIterator = intArrayList.intIterator();
					int j = 0;

					while (intIterator.hasNext() && j < i) {
						int k = intIterator.nextInt();
						StructureTemplate.StructureBlockInfo structureBlockInfo = (StructureTemplate.StructureBlockInfo)originalBlockInfos.get(k);
						StructureTemplate.StructureBlockInfo structureBlockInfo2 = (StructureTemplate.StructureBlockInfo)currentBlockInfos.get(k);
						StructureTemplate.StructureBlockInfo structureBlockInfo3 = this.delegate.process(world, pos, pivot, structureBlockInfo, structureBlockInfo2, data);
						if (structureBlockInfo3 != null && !structureBlockInfo2.equals(structureBlockInfo3)) {
							j++;
							currentBlockInfos.set(k, structureBlockInfo3);
						}
					}

					return currentBlockInfos;
				}
			}
		} else {
			return currentBlockInfos;
		}
	}
}
