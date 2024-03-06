package net.minecraft.component;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public final class ComponentMapImpl implements ComponentMap {
	private final ComponentMap baseComponents;
	private Reference2ObjectMap<DataComponentType<?>, Optional<?>> changedComponents;
	private boolean copyOnWrite;

	public ComponentMapImpl(ComponentMap baseComponents) {
		this(baseComponents, Reference2ObjectMaps.emptyMap(), true);
	}

	private ComponentMapImpl(ComponentMap baseComponents, Reference2ObjectMap<DataComponentType<?>, Optional<?>> changedComponents, boolean copyOnWrite) {
		this.baseComponents = baseComponents;
		this.changedComponents = changedComponents;
		this.copyOnWrite = copyOnWrite;
	}

	public static ComponentMapImpl create(ComponentMap baseComponents, ComponentChanges changes) {
		if (shouldReuseChangesMap(baseComponents, changes.changedComponents)) {
			return new ComponentMapImpl(baseComponents, changes.changedComponents, true);
		} else {
			ComponentMapImpl componentMapImpl = new ComponentMapImpl(baseComponents);
			componentMapImpl.applyChanges(changes);
			return componentMapImpl;
		}
	}

	private static boolean shouldReuseChangesMap(ComponentMap baseComponents, Reference2ObjectMap<DataComponentType<?>, Optional<?>> changedComponents) {
		for (Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changedComponents)) {
			Object object = baseComponents.get((DataComponentType)entry.getKey());
			Optional<?> optional = (Optional<?>)entry.getValue();
			if (optional.isPresent() && optional.get().equals(object)) {
				return false;
			}

			if (optional.isEmpty() && object == null) {
				return false;
			}
		}

		return true;
	}

	@Nullable
	@Override
	public <T> T get(DataComponentType<? extends T> type) {
		Optional<? extends T> optional = (Optional<? extends T>)this.changedComponents.get(type);
		return (T)(optional != null ? optional.orElse(null) : this.baseComponents.get(type));
	}

	@Nullable
	public <T> T set(DataComponentType<? super T> type, @Nullable T value) {
		this.onWrite();
		T object = this.baseComponents.get((DataComponentType<? extends T>)type);
		Optional<T> optional;
		if (Objects.equals(value, object)) {
			optional = (Optional<T>)this.changedComponents.remove(type);
		} else {
			optional = (Optional<T>)this.changedComponents.put(type, Optional.ofNullable(value));
		}

		return (T)(optional != null ? optional.orElse(object) : object);
	}

	@Nullable
	public <T> T remove(DataComponentType<? extends T> type) {
		this.onWrite();
		T object = this.baseComponents.get(type);
		Optional<? extends T> optional;
		if (object != null) {
			optional = (Optional<? extends T>)this.changedComponents.put(type, Optional.empty());
		} else {
			optional = (Optional<? extends T>)this.changedComponents.remove(type);
		}

		return (T)(optional != null ? optional.orElse(null) : object);
	}

	public void applyChanges(ComponentChanges changes) {
		this.onWrite();

		for (Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changes.changedComponents)) {
			this.applyChange((DataComponentType<?>)entry.getKey(), (Optional<?>)entry.getValue());
		}
	}

	private void applyChange(DataComponentType<?> type, Optional<?> optional) {
		Object object = this.baseComponents.get(type);
		if (optional.isPresent()) {
			if (optional.get().equals(object)) {
				this.changedComponents.remove(type);
			} else {
				this.changedComponents.put(type, optional);
			}
		} else if (object != null) {
			this.changedComponents.put(type, Optional.empty());
		} else {
			this.changedComponents.remove(type);
		}
	}

	public void setAll(ComponentMap components) {
		for (Component<?> component : components) {
			component.apply(this);
		}
	}

	private void onWrite() {
		if (this.copyOnWrite) {
			this.changedComponents = new Reference2ObjectArrayMap<>(this.changedComponents);
			this.copyOnWrite = false;
		}
	}

	@Override
	public Set<DataComponentType<?>> getTypes() {
		if (this.changedComponents.isEmpty()) {
			return this.baseComponents.getTypes();
		} else {
			Set<DataComponentType<?>> set = new ReferenceArraySet<>(this.baseComponents.getTypes());

			for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
				this.changedComponents
			)) {
				Optional<?> optional = (Optional<?>)entry.getValue();
				if (optional.isPresent()) {
					set.add((DataComponentType)entry.getKey());
				} else {
					set.remove(entry.getKey());
				}
			}

			return set;
		}
	}

	@Override
	public Iterator<Component<?>> iterator() {
		if (this.changedComponents.isEmpty()) {
			return this.baseComponents.iterator();
		} else {
			List<Component<?>> list = new ArrayList(this.changedComponents.size() + this.baseComponents.size());

			for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
				this.changedComponents
			)) {
				if (((Optional)entry.getValue()).isPresent()) {
					list.add(Component.of((DataComponentType)entry.getKey(), ((Optional)entry.getValue()).get()));
				}
			}

			for (Component<?> component : this.baseComponents) {
				if (!this.changedComponents.containsKey(component.type())) {
					list.add(component);
				}
			}

			return list.iterator();
		}
	}

	@Override
	public int size() {
		int i = this.baseComponents.size();

		for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
			this.changedComponents
		)) {
			boolean bl = ((Optional)entry.getValue()).isPresent();
			boolean bl2 = this.baseComponents.contains((DataComponentType<?>)entry.getKey());
			if (bl != bl2) {
				i += bl ? 1 : -1;
			}
		}

		return i;
	}

	public ComponentChanges getChanges() {
		if (this.changedComponents.isEmpty()) {
			return ComponentChanges.EMPTY;
		} else {
			this.copyOnWrite = true;
			return new ComponentChanges(this.changedComponents);
		}
	}

	public ComponentMapImpl copy() {
		this.copyOnWrite = true;
		return new ComponentMapImpl(this.baseComponents, this.changedComponents, true);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof ComponentMapImpl componentMapImpl
				&& this.baseComponents.equals(componentMapImpl.baseComponents)
				&& this.changedComponents.equals(componentMapImpl.changedComponents)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.baseComponents.hashCode() + this.changedComponents.hashCode() * 31;
	}

	public String toString() {
		return "{" + (String)this.stream().map(Component::toString).collect(Collectors.joining(", ")) + "}";
	}
}
