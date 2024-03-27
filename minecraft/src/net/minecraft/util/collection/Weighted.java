package net.minecraft.util.collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface Weighted {
	Weight getWeight();

	static <T> Weighted.Present<T> of(T data, int weight) {
		return new Weighted.Present<>(data, Weight.of(weight));
	}

	public static class Absent implements Weighted {
		private final Weight weight;

		public Absent(int weight) {
			this.weight = Weight.of(weight);
		}

		public Absent(Weight weight) {
			this.weight = weight;
		}

		@Override
		public Weight getWeight() {
			return this.weight;
		}
	}

	public static record Present<T>(T data, Weight weight) implements Weighted {
		@Override
		public Weight getWeight() {
			return this.weight;
		}

		public static <E> Codec<Weighted.Present<E>> createCodec(Codec<E> dataCodec) {
			return RecordCodecBuilder.create(
				instance -> instance.group(dataCodec.fieldOf("data").forGetter(Weighted.Present::data), Weight.CODEC.fieldOf("weight").forGetter(Weighted.Present::weight))
						.apply(instance, Weighted.Present::new)
			);
		}
	}
}
