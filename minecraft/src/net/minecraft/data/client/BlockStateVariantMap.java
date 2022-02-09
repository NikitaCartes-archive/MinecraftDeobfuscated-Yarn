package net.minecraft.data.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.property.Property;

/**
 * An equivalence to the {@code Map<String, WeightedUnbakedModel>}
 * passed to the constructor of {@code ModelVariantMap}.
 */
public abstract class BlockStateVariantMap {
	private final Map<PropertiesMap, List<BlockStateVariant>> variants = Maps.<PropertiesMap, List<BlockStateVariant>>newHashMap();

	protected void register(PropertiesMap condition, List<BlockStateVariant> possibleVariants) {
		List<BlockStateVariant> list = (List<BlockStateVariant>)this.variants.put(condition, possibleVariants);
		if (list != null) {
			throw new IllegalStateException("Value " + condition + " is already defined");
		}
	}

	Map<PropertiesMap, List<BlockStateVariant>> getVariants() {
		this.checkAllPropertyDefinitions();
		return ImmutableMap.copyOf(this.variants);
	}

	private void checkAllPropertyDefinitions() {
		List<Property<?>> list = this.getProperties();
		Stream<PropertiesMap> stream = Stream.of(PropertiesMap.empty());

		for (Property<?> property : list) {
			stream = stream.flatMap(propertiesMap -> property.stream().map(propertiesMap::withValue));
		}

		List<PropertiesMap> list2 = (List<PropertiesMap>)stream.filter(propertiesMap -> !this.variants.containsKey(propertiesMap)).collect(Collectors.toList());
		if (!list2.isEmpty()) {
			throw new IllegalStateException("Missing definition for properties: " + list2);
		}
	}

	abstract List<Property<?>> getProperties();

	public static <T1 extends Comparable<T1>> BlockStateVariantMap.SingleProperty<T1> create(Property<T1> property) {
		return new BlockStateVariantMap.SingleProperty<>(property);
	}

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> BlockStateVariantMap.DoubleProperty<T1, T2> create(
		Property<T1> first, Property<T2> second
	) {
		return new BlockStateVariantMap.DoubleProperty<>(first, second);
	}

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> BlockStateVariantMap.TripleProperty<T1, T2, T3> create(
		Property<T1> first, Property<T2> second, Property<T3> third
	) {
		return new BlockStateVariantMap.TripleProperty<>(first, second, third);
	}

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> BlockStateVariantMap.QuadrupleProperty<T1, T2, T3, T4> create(
		Property<T1> first, Property<T2> second, Property<T3> third, Property<T4> fourth
	) {
		return new BlockStateVariantMap.QuadrupleProperty<>(first, second, third, fourth);
	}

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> BlockStateVariantMap.QuintupleProperty<T1, T2, T3, T4, T5> create(
		Property<T1> first, Property<T2> second, Property<T3> third, Property<T4> fourth, Property<T5> fifth
	) {
		return new BlockStateVariantMap.QuintupleProperty<>(first, second, third, fourth, fifth);
	}

	public static class DoubleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;

		DoubleProperty(Property<T1> first, Property<T2> second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second);
		}

		public BlockStateVariantMap.DoubleProperty<T1, T2> register(T1 firstValue, T2 secondValue, List<BlockStateVariant> variants) {
			PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstValue), this.second.createValue(secondValue));
			this.register(propertiesMap, variants);
			return this;
		}

		public BlockStateVariantMap.DoubleProperty<T1, T2> register(T1 firstValue, T2 secondValue, BlockStateVariant variant) {
			return this.register(firstValue, secondValue, Collections.singletonList(variant));
		}

		public BlockStateVariantMap register(BiFunction<T1, T2, BlockStateVariant> variantFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(secondValue -> this.register((T1)firstValue, (T2)secondValue, (BlockStateVariant)variantFactory.apply(firstValue, secondValue)))
				);
			return this;
		}

		public BlockStateVariantMap registerVariants(BiFunction<T1, T2, List<BlockStateVariant>> variantsFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(secondValue -> this.register((T1)firstValue, (T2)secondValue, (List<BlockStateVariant>)variantsFactory.apply(firstValue, secondValue)))
				);
			return this;
		}
	}

	@FunctionalInterface
	public interface QuadFunction<P1, P2, P3, P4, R> {
		R apply(P1 one, P2 two, P3 three, P4 four);
	}

	public static class QuadrupleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
		extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;
		private final Property<T3> third;
		private final Property<T4> fourth;

		QuadrupleProperty(Property<T1> first, Property<T2> second, Property<T3> third, Property<T4> fourth) {
			this.first = first;
			this.second = second;
			this.third = third;
			this.fourth = fourth;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second, this.third, this.fourth);
		}

		public BlockStateVariantMap.QuadrupleProperty<T1, T2, T3, T4> register(
			T1 firstValue, T2 secondValue, T3 thirdValue, T4 fourthValue, List<BlockStateVariant> variants
		) {
			PropertiesMap propertiesMap = PropertiesMap.withValues(
				this.first.createValue(firstValue), this.second.createValue(secondValue), this.third.createValue(thirdValue), this.fourth.createValue(fourthValue)
			);
			this.register(propertiesMap, variants);
			return this;
		}

		public BlockStateVariantMap.QuadrupleProperty<T1, T2, T3, T4> register(
			T1 firstValue, T2 secondValue, T3 thirdValue, T4 fourthValue, BlockStateVariant variant
		) {
			return this.register(firstValue, secondValue, thirdValue, fourthValue, Collections.singletonList(variant));
		}

		public BlockStateVariantMap register(BlockStateVariantMap.QuadFunction<T1, T2, T3, T4, BlockStateVariant> variantFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(
								secondValue -> this.third
										.getValues()
										.forEach(
											thirdValue -> this.fourth
													.getValues()
													.forEach(
														fourthValue -> this.register(
																(T1)firstValue,
																(T2)secondValue,
																(T3)thirdValue,
																(T4)fourthValue,
																variantFactory.apply((T1)firstValue, (T2)secondValue, (T3)thirdValue, (T4)fourthValue)
															)
													)
										)
							)
				);
			return this;
		}

		public BlockStateVariantMap registerVariants(BlockStateVariantMap.QuadFunction<T1, T2, T3, T4, List<BlockStateVariant>> variantFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(
								secondValue -> this.third
										.getValues()
										.forEach(
											thirdValue -> this.fourth
													.getValues()
													.forEach(
														fourthValue -> this.register(
																(T1)firstValue,
																(T2)secondValue,
																(T3)thirdValue,
																(T4)fourthValue,
																variantFactory.apply((T1)firstValue, (T2)secondValue, (T3)thirdValue, (T4)fourthValue)
															)
													)
										)
							)
				);
			return this;
		}
	}

	@FunctionalInterface
	public interface QuintFunction<P1, P2, P3, P4, P5, R> {
		R apply(P1 one, P2 two, P3 three, P4 four, P5 five);
	}

	public static class QuintupleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
		extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;
		private final Property<T3> third;
		private final Property<T4> fourth;
		private final Property<T5> fifth;

		QuintupleProperty(Property<T1> first, Property<T2> second, Property<T3> third, Property<T4> fourth, Property<T5> fifth) {
			this.first = first;
			this.second = second;
			this.third = third;
			this.fourth = fourth;
			this.fifth = fifth;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second, this.third, this.fourth, this.fifth);
		}

		public BlockStateVariantMap.QuintupleProperty<T1, T2, T3, T4, T5> register(
			T1 firstValue, T2 secondValue, T3 thirdValue, T4 fourthValue, T5 fifthValue, List<BlockStateVariant> variants
		) {
			PropertiesMap propertiesMap = PropertiesMap.withValues(
				this.first.createValue(firstValue),
				this.second.createValue(secondValue),
				this.third.createValue(thirdValue),
				this.fourth.createValue(fourthValue),
				this.fifth.createValue(fifthValue)
			);
			this.register(propertiesMap, variants);
			return this;
		}

		public BlockStateVariantMap.QuintupleProperty<T1, T2, T3, T4, T5> register(
			T1 firstValue, T2 secondValue, T3 thirdValue, T4 fourthValue, T5 fifthValue, BlockStateVariant variant
		) {
			return this.register(firstValue, secondValue, thirdValue, fourthValue, fifthValue, Collections.singletonList(variant));
		}

		public BlockStateVariantMap register(BlockStateVariantMap.QuintFunction<T1, T2, T3, T4, T5, BlockStateVariant> variantFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(
								secondValue -> this.third
										.getValues()
										.forEach(
											thirdValue -> this.fourth
													.getValues()
													.forEach(
														fourthValue -> this.fifth
																.getValues()
																.forEach(
																	fifthValue -> this.register(
																			(T1)firstValue,
																			(T2)secondValue,
																			(T3)thirdValue,
																			(T4)fourthValue,
																			(T5)fifthValue,
																			variantFactory.apply((T1)firstValue, (T2)secondValue, (T3)thirdValue, (T4)fourthValue, (T5)fifthValue)
																		)
																)
													)
										)
							)
				);
			return this;
		}

		public BlockStateVariantMap registerVariants(BlockStateVariantMap.QuintFunction<T1, T2, T3, T4, T5, List<BlockStateVariant>> variantFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(
								secondValue -> this.third
										.getValues()
										.forEach(
											thirdValue -> this.fourth
													.getValues()
													.forEach(
														fourthValue -> this.fifth
																.getValues()
																.forEach(
																	fifthValue -> this.register(
																			(T1)firstValue,
																			(T2)secondValue,
																			(T3)thirdValue,
																			(T4)fourthValue,
																			(T5)fifthValue,
																			variantFactory.apply((T1)firstValue, (T2)secondValue, (T3)thirdValue, (T4)fourthValue, (T5)fifthValue)
																		)
																)
													)
										)
							)
				);
			return this;
		}
	}

	public static class SingleProperty<T1 extends Comparable<T1>> extends BlockStateVariantMap {
		private final Property<T1> property;

		SingleProperty(Property<T1> property) {
			this.property = property;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.property);
		}

		public BlockStateVariantMap.SingleProperty<T1> register(T1 value, List<BlockStateVariant> variants) {
			PropertiesMap propertiesMap = PropertiesMap.withValues(this.property.createValue(value));
			this.register(propertiesMap, variants);
			return this;
		}

		public BlockStateVariantMap.SingleProperty<T1> register(T1 value, BlockStateVariant variant) {
			return this.register(value, Collections.singletonList(variant));
		}

		public BlockStateVariantMap register(Function<T1, BlockStateVariant> variantFactory) {
			this.property.getValues().forEach(value -> this.register((T1)value, (BlockStateVariant)variantFactory.apply(value)));
			return this;
		}

		public BlockStateVariantMap registerVariants(Function<T1, List<BlockStateVariant>> variantFactory) {
			this.property.getValues().forEach(value -> this.register((T1)value, (List<BlockStateVariant>)variantFactory.apply(value)));
			return this;
		}
	}

	@FunctionalInterface
	public interface TriFunction<P1, P2, P3, R> {
		R apply(P1 one, P2 two, P3 three);
	}

	public static class TripleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;
		private final Property<T3> third;

		TripleProperty(Property<T1> first, Property<T2> second, Property<T3> third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second, this.third);
		}

		public BlockStateVariantMap.TripleProperty<T1, T2, T3> register(T1 firstValue, T2 secondValue, T3 thirdValue, List<BlockStateVariant> variants) {
			PropertiesMap propertiesMap = PropertiesMap.withValues(
				this.first.createValue(firstValue), this.second.createValue(secondValue), this.third.createValue(thirdValue)
			);
			this.register(propertiesMap, variants);
			return this;
		}

		public BlockStateVariantMap.TripleProperty<T1, T2, T3> register(T1 firstValue, T2 secondValue, T3 thirdValue, BlockStateVariant variant) {
			return this.register(firstValue, secondValue, thirdValue, Collections.singletonList(variant));
		}

		public BlockStateVariantMap register(BlockStateVariantMap.TriFunction<T1, T2, T3, BlockStateVariant> variantFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(
								secondValue -> this.third
										.getValues()
										.forEach(
											thirdValue -> this.register((T1)firstValue, (T2)secondValue, (T3)thirdValue, variantFactory.apply((T1)firstValue, (T2)secondValue, (T3)thirdValue))
										)
							)
				);
			return this;
		}

		public BlockStateVariantMap registerVariants(BlockStateVariantMap.TriFunction<T1, T2, T3, List<BlockStateVariant>> variantFactory) {
			this.first
				.getValues()
				.forEach(
					firstValue -> this.second
							.getValues()
							.forEach(
								secondValue -> this.third
										.getValues()
										.forEach(
											thirdValue -> this.register((T1)firstValue, (T2)secondValue, (T3)thirdValue, variantFactory.apply((T1)firstValue, (T2)secondValue, (T3)thirdValue))
										)
							)
				);
			return this;
		}
	}
}
