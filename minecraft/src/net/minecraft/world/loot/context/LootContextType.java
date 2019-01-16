package net.minecraft.world.loot.context;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.world.loot.LootTableReporter;

public class LootContextType {
	private final Set<Parameter<?>> required;
	private final Set<Parameter<?>> allowed;

	private LootContextType(Set<Parameter<?>> set, Set<Parameter<?>> set2) {
		this.required = ImmutableSet.copyOf(set);
		this.allowed = ImmutableSet.copyOf(Sets.union(set, set2));
	}

	public Set<Parameter<?>> getRequired() {
		return this.required;
	}

	public Set<Parameter<?>> getAllowed() {
		return this.allowed;
	}

	public String toString() {
		return "["
			+ Joiner.on(", ").join(this.allowed.stream().map(parameter -> (this.required.contains(parameter) ? "!" : "") + parameter.getIdentifier()).iterator())
			+ "]";
	}

	public void check(LootTableReporter lootTableReporter, ParameterConsumer parameterConsumer) {
		Set<Parameter<?>> set = parameterConsumer.getRequiredParameters();
		Set<Parameter<?>> set2 = Sets.<Parameter<?>>difference(set, this.allowed);
		if (!set2.isEmpty()) {
			lootTableReporter.report("Parameters " + set2 + " are not provided in this context");
		}
	}

	public static class Builder {
		private final Set<Parameter<?>> required = Sets.newIdentityHashSet();
		private final Set<Parameter<?>> allowed = Sets.newIdentityHashSet();

		public LootContextType.Builder require(Parameter<?> parameter) {
			if (this.allowed.contains(parameter)) {
				throw new IllegalArgumentException("Parameter " + parameter.getIdentifier() + " is already optional");
			} else {
				this.required.add(parameter);
				return this;
			}
		}

		public LootContextType.Builder allow(Parameter<?> parameter) {
			if (this.required.contains(parameter)) {
				throw new IllegalArgumentException("Parameter " + parameter.getIdentifier() + " is already required");
			} else {
				this.allowed.add(parameter);
				return this;
			}
		}

		public LootContextType build() {
			return new LootContextType(this.required, this.allowed);
		}
	}
}
