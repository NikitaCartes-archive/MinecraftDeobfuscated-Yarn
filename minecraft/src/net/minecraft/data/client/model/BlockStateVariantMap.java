package net.minecraft.data.client.model;

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
			stream = stream.flatMap(propertiesMap -> property.stream().map(propertiesMap::method_25819));
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
		Property<T1> property, Property<T2> property2
	) {
		return new BlockStateVariantMap.DoubleProperty<>(property, property2);
	}

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> BlockStateVariantMap.TripleProperty<T1, T2, T3> create(
		Property<T1> property, Property<T2> property2, Property<T3> property3
	) {
		return new BlockStateVariantMap.TripleProperty<>(property, property2, property3);
	}

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> BlockStateVariantMap.QuadrupleProperty<T1, T2, T3, T4> create(
		Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4
	) {
		return new BlockStateVariantMap.QuadrupleProperty<>(property, property2, property3, property4);
	}

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> BlockStateVariantMap.QuintupleProperty<T1, T2, T3, T4, T5> create(
		Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4, Property<T5> property5
	) {
		return new BlockStateVariantMap.QuintupleProperty<>(property, property2, property3, property4, property5);
	}

	public static class DoubleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;

		private DoubleProperty(Property<T1> property, Property<T2> property2) {
			this.first = property;
			this.second = property2;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second);
		}

		public BlockStateVariantMap.DoubleProperty<T1, T2> register(T1 comparable, T2 comparable2, List<BlockStateVariant> list) {
			PropertiesMap propertiesMap = PropertiesMap.method_25821(this.first.createValue(comparable), this.second.createValue(comparable2));
			this.register(propertiesMap, list);
			return this;
		}

		public BlockStateVariantMap.DoubleProperty<T1, T2> register(T1 comparable, T2 comparable2, BlockStateVariant blockStateVariant) {
			return this.register(comparable, comparable2, Collections.singletonList(blockStateVariant));
		}

		public BlockStateVariantMap register(BiFunction<T1, T2, BlockStateVariant> variantFactory) {
			this.first
				.getValues()
				.forEach(
					comparable -> this.second
							.getValues()
							.forEach(comparable2 -> this.register((T1)comparable, (T2)comparable2, (BlockStateVariant)variantFactory.apply(comparable, comparable2)))
				);
			return this;
		}

		public BlockStateVariantMap registerVariants(BiFunction<T1, T2, List<BlockStateVariant>> variantsFactory) {
			this.first
				.getValues()
				.forEach(
					comparable -> this.second
							.getValues()
							.forEach(comparable2 -> this.register((T1)comparable, (T2)comparable2, (List<BlockStateVariant>)variantsFactory.apply(comparable, comparable2)))
				);
			return this;
		}
	}

	public static class QuadrupleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
		extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;
		private final Property<T3> third;
		private final Property<T4> fourth;

		private QuadrupleProperty(Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4) {
			this.first = property;
			this.second = property2;
			this.third = property3;
			this.fourth = property4;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second, this.third, this.fourth);
		}

		public BlockStateVariantMap.QuadrupleProperty<T1, T2, T3, T4> register(
			T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, List<BlockStateVariant> list
		) {
			PropertiesMap propertiesMap = PropertiesMap.method_25821(
				this.first.createValue(comparable), this.second.createValue(comparable2), this.third.createValue(comparable3), this.fourth.createValue(comparable4)
			);
			this.register(propertiesMap, list);
			return this;
		}

		public BlockStateVariantMap.QuadrupleProperty<T1, T2, T3, T4> register(
			T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, BlockStateVariant blockStateVariant
		) {
			return this.register(comparable, comparable2, comparable3, comparable4, Collections.singletonList(blockStateVariant));
		}
	}

	public static class QuintupleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
		extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;
		private final Property<T3> third;
		private final Property<T4> fourth;
		private final Property<T5> fifth;

		private QuintupleProperty(Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4, Property<T5> property5) {
			this.first = property;
			this.second = property2;
			this.third = property3;
			this.fourth = property4;
			this.fifth = property5;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second, this.third, this.fourth, this.fifth);
		}

		public BlockStateVariantMap.QuintupleProperty<T1, T2, T3, T4, T5> register(
			T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, T5 comparable5, List<BlockStateVariant> list
		) {
			PropertiesMap propertiesMap = PropertiesMap.method_25821(
				this.first.createValue(comparable),
				this.second.createValue(comparable2),
				this.third.createValue(comparable3),
				this.fourth.createValue(comparable4),
				this.fifth.createValue(comparable5)
			);
			this.register(propertiesMap, list);
			return this;
		}

		public BlockStateVariantMap.QuintupleProperty<T1, T2, T3, T4, T5> register(
			T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, T5 comparable5, BlockStateVariant blockStateVariant
		) {
			return this.register(comparable, comparable2, comparable3, comparable4, comparable5, Collections.singletonList(blockStateVariant));
		}
	}

	public static class SingleProperty<T1 extends Comparable<T1>> extends BlockStateVariantMap {
		private final Property<T1> property;

		private SingleProperty(Property<T1> property) {
			this.property = property;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.property);
		}

		public BlockStateVariantMap.SingleProperty<T1> register(T1 value, List<BlockStateVariant> variants) {
			PropertiesMap propertiesMap = PropertiesMap.method_25821(this.property.createValue(value));
			this.register(propertiesMap, variants);
			return this;
		}

		public BlockStateVariantMap.SingleProperty<T1> register(T1 value, BlockStateVariant variant) {
			return this.register(value, Collections.singletonList(variant));
		}

		public BlockStateVariantMap register(Function<T1, BlockStateVariant> variantFactory) {
			this.property.getValues().forEach(comparable -> this.register((T1)comparable, (BlockStateVariant)variantFactory.apply(comparable)));
			return this;
		}
	}

	@FunctionalInterface
	public interface TriFunction<P1, P2, P3, R> {
		R apply(P1 object, P2 object2, P3 object3);
	}

	public static class TripleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> extends BlockStateVariantMap {
		private final Property<T1> first;
		private final Property<T2> second;
		private final Property<T3> third;

		private TripleProperty(Property<T1> property, Property<T2> property2, Property<T3> property3) {
			this.first = property;
			this.second = property2;
			this.third = property3;
		}

		@Override
		public List<Property<?>> getProperties() {
			return ImmutableList.of(this.first, this.second, this.third);
		}

		public BlockStateVariantMap.TripleProperty<T1, T2, T3> register(T1 comparable, T2 comparable2, T3 comparable3, List<BlockStateVariant> list) {
			PropertiesMap propertiesMap = PropertiesMap.method_25821(
				this.first.createValue(comparable), this.second.createValue(comparable2), this.third.createValue(comparable3)
			);
			this.register(propertiesMap, list);
			return this;
		}

		public BlockStateVariantMap.TripleProperty<T1, T2, T3> register(T1 comparable, T2 comparable2, T3 comparable3, BlockStateVariant blockStateVariant) {
			return this.register(comparable, comparable2, comparable3, Collections.singletonList(blockStateVariant));
		}

		public BlockStateVariantMap register(BlockStateVariantMap.TriFunction<T1, T2, T3, BlockStateVariant> triFunction) {
			this.first
				.getValues()
				.forEach(
					comparable -> this.second
							.getValues()
							.forEach(
								comparable2 -> this.third
										.getValues()
										.forEach(
											comparable3 -> this.register((T1)comparable, (T2)comparable2, (T3)comparable3, triFunction.apply((T1)comparable, (T2)comparable2, (T3)comparable3))
										)
							)
				);
			return this;
		}
	}
}
