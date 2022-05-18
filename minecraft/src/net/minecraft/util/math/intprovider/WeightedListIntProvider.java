package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.random.Random;

public class WeightedListIntProvider extends IntProvider {
	public static final Codec<WeightedListIntProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(DataPool.createCodec(IntProvider.VALUE_CODEC).fieldOf("distribution").forGetter(provider -> provider.weightedList))
				.apply(instance, WeightedListIntProvider::new)
	);
	private final DataPool<IntProvider> weightedList;
	private final int min;
	private final int max;

	public WeightedListIntProvider(DataPool<IntProvider> weightedList) {
		this.weightedList = weightedList;
		List<Weighted.Present<IntProvider>> list = weightedList.getEntries();
		int i = Integer.MAX_VALUE;
		int j = Integer.MIN_VALUE;

		for (Weighted.Present<IntProvider> present : list) {
			int k = present.getData().getMin();
			int l = present.getData().getMax();
			i = Math.min(i, k);
			j = Math.max(j, l);
		}

		this.min = i;
		this.max = j;
	}

	@Override
	public int get(Random random) {
		return ((IntProvider)this.weightedList.getDataOrEmpty(random).orElseThrow(IllegalStateException::new)).get(random);
	}

	@Override
	public int getMin() {
		return this.min;
	}

	@Override
	public int getMax() {
		return this.max;
	}

	@Override
	public IntProviderType<?> getType() {
		return IntProviderType.WEIGHTED_LIST;
	}
}
