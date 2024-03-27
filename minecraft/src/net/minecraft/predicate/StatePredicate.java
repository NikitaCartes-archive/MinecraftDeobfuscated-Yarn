package net.minecraft.predicate;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public record StatePredicate(List<StatePredicate.Condition> conditions) {
	private static final Codec<List<StatePredicate.Condition>> CONDITION_LIST_CODEC = Codec.unboundedMap(Codec.STRING, StatePredicate.ValueMatcher.CODEC)
		.xmap(
			states -> states.entrySet()
					.stream()
					.map(state -> new StatePredicate.Condition((String)state.getKey(), (StatePredicate.ValueMatcher)state.getValue()))
					.toList(),
			conditions -> (Map)conditions.stream().collect(Collectors.toMap(StatePredicate.Condition::key, StatePredicate.Condition::valueMatcher))
		);
	public static final Codec<StatePredicate> CODEC = CONDITION_LIST_CODEC.xmap(StatePredicate::new, StatePredicate::conditions);
	public static final PacketCodec<ByteBuf, StatePredicate> PACKET_CODEC = StatePredicate.Condition.PACKET_CODEC
		.collect(PacketCodecs.toList())
		.xmap(StatePredicate::new, StatePredicate::conditions);

	public <S extends State<?, S>> boolean test(StateManager<?, S> stateManager, S container) {
		for (StatePredicate.Condition condition : this.conditions) {
			if (!condition.test(stateManager, container)) {
				return false;
			}
		}

		return true;
	}

	public boolean test(BlockState state) {
		return this.test(state.getBlock().getStateManager(), state);
	}

	public boolean test(FluidState state) {
		return this.test(state.getFluid().getStateManager(), state);
	}

	public Optional<String> findMissing(StateManager<?, ?> stateManager) {
		for (StatePredicate.Condition condition : this.conditions) {
			Optional<String> optional = condition.reportMissing(stateManager);
			if (optional.isPresent()) {
				return optional;
			}
		}

		return Optional.empty();
	}

	public static class Builder {
		private final ImmutableList.Builder<StatePredicate.Condition> conditions = ImmutableList.builder();

		private Builder() {
		}

		public static StatePredicate.Builder create() {
			return new StatePredicate.Builder();
		}

		public StatePredicate.Builder exactMatch(Property<?> property, String valueName) {
			this.conditions.add(new StatePredicate.Condition(property.getName(), new StatePredicate.ExactValueMatcher(valueName)));
			return this;
		}

		public StatePredicate.Builder exactMatch(Property<Integer> property, int value) {
			return this.exactMatch(property, Integer.toString(value));
		}

		public StatePredicate.Builder exactMatch(Property<Boolean> property, boolean value) {
			return this.exactMatch(property, Boolean.toString(value));
		}

		public <T extends Comparable<T> & StringIdentifiable> StatePredicate.Builder exactMatch(Property<T> property, T value) {
			return this.exactMatch(property, value.asString());
		}

		public Optional<StatePredicate> build() {
			return Optional.of(new StatePredicate(this.conditions.build()));
		}
	}

	static record Condition(String key, StatePredicate.ValueMatcher valueMatcher) {
		public static final PacketCodec<ByteBuf, StatePredicate.Condition> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.STRING,
			StatePredicate.Condition::key,
			StatePredicate.ValueMatcher.PACKET_CODEC,
			StatePredicate.Condition::valueMatcher,
			StatePredicate.Condition::new
		);

		public <S extends State<?, S>> boolean test(StateManager<?, S> stateManager, S state) {
			Property<?> property = stateManager.getProperty(this.key);
			return property != null && this.valueMatcher.test(state, property);
		}

		public Optional<String> reportMissing(StateManager<?, ?> factory) {
			Property<?> property = factory.getProperty(this.key);
			return property != null ? Optional.empty() : Optional.of(this.key);
		}
	}

	static record ExactValueMatcher(String value) implements StatePredicate.ValueMatcher {
		public static final Codec<StatePredicate.ExactValueMatcher> CODEC = Codec.STRING
			.xmap(StatePredicate.ExactValueMatcher::new, StatePredicate.ExactValueMatcher::value);
		public static final PacketCodec<ByteBuf, StatePredicate.ExactValueMatcher> PACKET_CODEC = PacketCodecs.STRING
			.xmap(StatePredicate.ExactValueMatcher::new, StatePredicate.ExactValueMatcher::value);

		@Override
		public <T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property) {
			T comparable = state.get(property);
			Optional<T> optional = property.parse(this.value);
			return optional.isPresent() && comparable.compareTo((Comparable)optional.get()) == 0;
		}
	}

	static record RangedValueMatcher(Optional<String> min, Optional<String> max) implements StatePredicate.ValueMatcher {
		public static final Codec<StatePredicate.RangedValueMatcher> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.optionalFieldOf("min").forGetter(StatePredicate.RangedValueMatcher::min),
						Codec.STRING.optionalFieldOf("max").forGetter(StatePredicate.RangedValueMatcher::max)
					)
					.apply(instance, StatePredicate.RangedValueMatcher::new)
		);
		public static final PacketCodec<ByteBuf, StatePredicate.RangedValueMatcher> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.optional(PacketCodecs.STRING),
			StatePredicate.RangedValueMatcher::min,
			PacketCodecs.optional(PacketCodecs.STRING),
			StatePredicate.RangedValueMatcher::max,
			StatePredicate.RangedValueMatcher::new
		);

		@Override
		public <T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property) {
			T comparable = state.get(property);
			if (this.min.isPresent()) {
				Optional<T> optional = property.parse((String)this.min.get());
				if (optional.isEmpty() || comparable.compareTo((Comparable)optional.get()) < 0) {
					return false;
				}
			}

			if (this.max.isPresent()) {
				Optional<T> optional = property.parse((String)this.max.get());
				if (optional.isEmpty() || comparable.compareTo((Comparable)optional.get()) > 0) {
					return false;
				}
			}

			return true;
		}
	}

	interface ValueMatcher {
		Codec<StatePredicate.ValueMatcher> CODEC = Codec.either(StatePredicate.ExactValueMatcher.CODEC, StatePredicate.RangedValueMatcher.CODEC)
			.xmap(Either::unwrap, valueMatcher -> {
				if (valueMatcher instanceof StatePredicate.ExactValueMatcher exactValueMatcher) {
					return Either.left(exactValueMatcher);
				} else if (valueMatcher instanceof StatePredicate.RangedValueMatcher rangedValueMatcher) {
					return Either.right(rangedValueMatcher);
				} else {
					throw new UnsupportedOperationException();
				}
			});
		PacketCodec<ByteBuf, StatePredicate.ValueMatcher> PACKET_CODEC = PacketCodecs.either(
				StatePredicate.ExactValueMatcher.PACKET_CODEC, StatePredicate.RangedValueMatcher.PACKET_CODEC
			)
			.xmap(Either::unwrap, valueMatcher -> {
				if (valueMatcher instanceof StatePredicate.ExactValueMatcher exactValueMatcher) {
					return Either.left(exactValueMatcher);
				} else if (valueMatcher instanceof StatePredicate.RangedValueMatcher rangedValueMatcher) {
					return Either.right(rangedValueMatcher);
				} else {
					throw new UnsupportedOperationException();
				}
			});

		<T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property);
	}
}
