package net.minecraft.util.context;

import com.google.common.collect.Sets;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.Contract;

public class ContextParameterMap {
	private final Map<ContextParameter<?>, Object> map;

	ContextParameterMap(Map<ContextParameter<?>, Object> map) {
		this.map = map;
	}

	public boolean contains(ContextParameter<?> parameter) {
		return this.map.containsKey(parameter);
	}

	public <T> T getOrThrow(ContextParameter<T> parameter) {
		T object = (T)this.map.get(parameter);
		if (object == null) {
			throw new NoSuchElementException(parameter.getId().toString());
		} else {
			return object;
		}
	}

	@Nullable
	public <T> T getNullable(ContextParameter<T> parameter) {
		return (T)this.map.get(parameter);
	}

	@Nullable
	@Contract("_,!null->!null; _,_->_")
	public <T> T getOrDefault(ContextParameter<T> parameter, @Nullable T defaultValue) {
		return (T)this.map.getOrDefault(parameter, defaultValue);
	}

	public static class Builder {
		private final Map<ContextParameter<?>, Object> map = new IdentityHashMap();

		public <T> ContextParameterMap.Builder add(ContextParameter<T> parameter, T value) {
			this.map.put(parameter, value);
			return this;
		}

		public <T> ContextParameterMap.Builder addNullable(ContextParameter<T> parameter, @Nullable T value) {
			if (value == null) {
				this.map.remove(parameter);
			} else {
				this.map.put(parameter, value);
			}

			return this;
		}

		public <T> T getOrThrow(ContextParameter<T> parameter) {
			T object = (T)this.map.get(parameter);
			if (object == null) {
				throw new NoSuchElementException(parameter.getId().toString());
			} else {
				return object;
			}
		}

		@Nullable
		public <T> T getNullable(ContextParameter<T> parameter) {
			return (T)this.map.get(parameter);
		}

		public ContextParameterMap build(ContextType type) {
			Set<ContextParameter<?>> set = Sets.<ContextParameter<?>>difference(this.map.keySet(), type.getAllowed());
			if (!set.isEmpty()) {
				throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
			} else {
				Set<ContextParameter<?>> set2 = Sets.<ContextParameter<?>>difference(type.getRequired(), this.map.keySet());
				if (!set2.isEmpty()) {
					throw new IllegalArgumentException("Missing required parameters: " + set2);
				} else {
					return new ContextParameterMap(this.map);
				}
			}
		}
	}
}
