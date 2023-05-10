package net.minecraft.loot.context;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LootContextParameterSet {
	private final ServerWorld world;
	private final Map<LootContextParameter<?>, Object> parameters;
	private final Map<Identifier, LootContextParameterSet.DynamicDrop> dynamicDrops;
	private final float luck;

	public LootContextParameterSet(
		ServerWorld world, Map<LootContextParameter<?>, Object> parameters, Map<Identifier, LootContextParameterSet.DynamicDrop> dynamicDrops, float luck
	) {
		this.world = world;
		this.parameters = parameters;
		this.dynamicDrops = dynamicDrops;
		this.luck = luck;
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public boolean contains(LootContextParameter<?> parameter) {
		return this.parameters.containsKey(parameter);
	}

	public <T> T get(LootContextParameter<T> parameter) {
		T object = (T)this.parameters.get(parameter);
		if (object == null) {
			throw new NoSuchElementException(parameter.getId().toString());
		} else {
			return object;
		}
	}

	@Nullable
	public <T> T method_51868(LootContextParameter<T> parameter) {
		return (T)this.parameters.get(parameter);
	}

	@Nullable
	public <T> T getOptional(LootContextParameter<T> parameter) {
		return (T)this.parameters.get(parameter);
	}

	public void addDynamicDrops(Identifier id, Consumer<ItemStack> lootConsumer) {
		LootContextParameterSet.DynamicDrop dynamicDrop = (LootContextParameterSet.DynamicDrop)this.dynamicDrops.get(id);
		if (dynamicDrop != null) {
			dynamicDrop.add(lootConsumer);
		}
	}

	public float getLuck() {
		return this.luck;
	}

	public static class Builder {
		private final ServerWorld world;
		private final Map<LootContextParameter<?>, Object> parameters = Maps.<LootContextParameter<?>, Object>newIdentityHashMap();
		private final Map<Identifier, LootContextParameterSet.DynamicDrop> dynamicDrops = Maps.<Identifier, LootContextParameterSet.DynamicDrop>newHashMap();
		private float luck;

		public Builder(ServerWorld world) {
			this.world = world;
		}

		public ServerWorld getWorld() {
			return this.world;
		}

		public <T> LootContextParameterSet.Builder add(LootContextParameter<T> parameter, T value) {
			this.parameters.put(parameter, value);
			return this;
		}

		public <T> LootContextParameterSet.Builder addOptional(LootContextParameter<T> parameter, @Nullable T value) {
			if (value == null) {
				this.parameters.remove(parameter);
			} else {
				this.parameters.put(parameter, value);
			}

			return this;
		}

		public <T> T get(LootContextParameter<T> parameter) {
			T object = (T)this.parameters.get(parameter);
			if (object == null) {
				throw new NoSuchElementException(parameter.getId().toString());
			} else {
				return object;
			}
		}

		@Nullable
		public <T> T getOptional(LootContextParameter<T> parameter) {
			return (T)this.parameters.get(parameter);
		}

		public LootContextParameterSet.Builder addDynamicDrop(Identifier id, LootContextParameterSet.DynamicDrop dynamicDrop) {
			LootContextParameterSet.DynamicDrop dynamicDrop2 = (LootContextParameterSet.DynamicDrop)this.dynamicDrops.put(id, dynamicDrop);
			if (dynamicDrop2 != null) {
				throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
			} else {
				return this;
			}
		}

		public LootContextParameterSet.Builder luck(float luck) {
			this.luck = luck;
			return this;
		}

		public LootContextParameterSet build(LootContextType contextType) {
			Set<LootContextParameter<?>> set = Sets.<LootContextParameter<?>>difference(this.parameters.keySet(), contextType.getAllowed());
			if (!set.isEmpty()) {
				throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
			} else {
				Set<LootContextParameter<?>> set2 = Sets.<LootContextParameter<?>>difference(contextType.getRequired(), this.parameters.keySet());
				if (!set2.isEmpty()) {
					throw new IllegalArgumentException("Missing required parameters: " + set2);
				} else {
					return new LootContextParameterSet(this.world, this.parameters, this.dynamicDrops, this.luck);
				}
			}
		}
	}

	@FunctionalInterface
	public interface DynamicDrop {
		void add(Consumer<ItemStack> lootConsumer);
	}
}
