package net.minecraft.util.context;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Set;

public class ContextType {
	private final Set<ContextParameter<?>> required;
	private final Set<ContextParameter<?>> allowed;

	ContextType(Set<ContextParameter<?>> required, Set<ContextParameter<?>> allowed) {
		this.required = Set.copyOf(required);
		this.allowed = Set.copyOf(Sets.union(required, allowed));
	}

	public Set<ContextParameter<?>> getRequired() {
		return this.required;
	}

	public Set<ContextParameter<?>> getAllowed() {
		return this.allowed;
	}

	public String toString() {
		return "["
			+ Joiner.on(", ").join(this.allowed.stream().map(parameter -> (this.required.contains(parameter) ? "!" : "") + parameter.getId()).iterator())
			+ "]";
	}

	public static class Builder {
		private final Set<ContextParameter<?>> required = Sets.newIdentityHashSet();
		private final Set<ContextParameter<?>> allowed = Sets.newIdentityHashSet();

		public ContextType.Builder require(ContextParameter<?> parameter) {
			if (this.allowed.contains(parameter)) {
				throw new IllegalArgumentException("Parameter " + parameter.getId() + " is already optional");
			} else {
				this.required.add(parameter);
				return this;
			}
		}

		public ContextType.Builder allow(ContextParameter<?> parameter) {
			if (this.required.contains(parameter)) {
				throw new IllegalArgumentException("Parameter " + parameter.getId() + " is already required");
			} else {
				this.allowed.add(parameter);
				return this;
			}
		}

		public ContextType build() {
			return new ContextType(this.required, this.allowed);
		}
	}
}
