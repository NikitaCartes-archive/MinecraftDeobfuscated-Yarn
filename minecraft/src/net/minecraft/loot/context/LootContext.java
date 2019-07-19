package net.minecraft.loot.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LootContext {
	private final Random random;
	private final float luck;
	private final ServerWorld world;
	private final LootManager manager;
	private final Set<LootTable> suppliers = Sets.<LootTable>newLinkedHashSet();
	private final Map<LootContextParameter<?>, Object> parameters;
	private final Map<Identifier, LootContext.Dropper> drops;

	private LootContext(
		Random random,
		float luck,
		ServerWorld world,
		LootManager manager,
		Map<LootContextParameter<?>, Object> parameters,
		Map<Identifier, LootContext.Dropper> drops
	) {
		this.random = random;
		this.luck = luck;
		this.world = world;
		this.manager = manager;
		this.parameters = ImmutableMap.copyOf(parameters);
		this.drops = ImmutableMap.copyOf(drops);
	}

	public boolean hasParameter(LootContextParameter<?> parameter) {
		return this.parameters.containsKey(parameter);
	}

	public void drop(Identifier id, Consumer<ItemStack> itemDropper) {
		LootContext.Dropper dropper = (LootContext.Dropper)this.drops.get(id);
		if (dropper != null) {
			dropper.add(this, itemDropper);
		}
	}

	@Nullable
	public <T> T get(LootContextParameter<T> parameter) {
		return (T)this.parameters.get(parameter);
	}

	public boolean addDrop(LootTable supplier) {
		return this.suppliers.add(supplier);
	}

	public void removeDrop(LootTable supplier) {
		this.suppliers.remove(supplier);
	}

	public LootManager getLootManager() {
		return this.manager;
	}

	public Random getRandom() {
		return this.random;
	}

	public float getLuck() {
		return this.luck;
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public static class Builder {
		private final ServerWorld world;
		private final Map<LootContextParameter<?>, Object> parameters = Maps.<LootContextParameter<?>, Object>newIdentityHashMap();
		private final Map<Identifier, LootContext.Dropper> drops = Maps.<Identifier, LootContext.Dropper>newHashMap();
		private Random random;
		private float luck;

		public Builder(ServerWorld world) {
			this.world = world;
		}

		public LootContext.Builder setRandom(Random random) {
			this.random = random;
			return this;
		}

		public LootContext.Builder setRandom(long seed) {
			if (seed != 0L) {
				this.random = new Random(seed);
			}

			return this;
		}

		public LootContext.Builder setRandom(long seed, Random random) {
			if (seed == 0L) {
				this.random = random;
			} else {
				this.random = new Random(seed);
			}

			return this;
		}

		public LootContext.Builder setLuck(float luck) {
			this.luck = luck;
			return this;
		}

		public <T> LootContext.Builder put(LootContextParameter<T> key, T value) {
			this.parameters.put(key, value);
			return this;
		}

		public <T> LootContext.Builder putNullable(LootContextParameter<T> key, @Nullable T value) {
			if (value == null) {
				this.parameters.remove(key);
			} else {
				this.parameters.put(key, value);
			}

			return this;
		}

		public LootContext.Builder putDrop(Identifier id, LootContext.Dropper value) {
			LootContext.Dropper dropper = (LootContext.Dropper)this.drops.put(id, value);
			if (dropper != null) {
				throw new IllegalStateException("Duplicated dynamic drop '" + this.drops + "'");
			} else {
				return this;
			}
		}

		public ServerWorld getWorld() {
			return this.world;
		}

		public <T> T get(LootContextParameter<T> parameter) {
			T object = (T)this.parameters.get(parameter);
			if (object == null) {
				throw new IllegalArgumentException("No parameter " + parameter);
			} else {
				return object;
			}
		}

		@Nullable
		public <T> T getNullable(LootContextParameter<T> parameter) {
			return (T)this.parameters.get(parameter);
		}

		public LootContext build(LootContextType type) {
			Set<LootContextParameter<?>> set = Sets.<LootContextParameter<?>>difference(this.parameters.keySet(), type.getAllowed());
			if (!set.isEmpty()) {
				throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
			} else {
				Set<LootContextParameter<?>> set2 = Sets.<LootContextParameter<?>>difference(type.getRequired(), this.parameters.keySet());
				if (!set2.isEmpty()) {
					throw new IllegalArgumentException("Missing required parameters: " + set2);
				} else {
					Random random = this.random;
					if (random == null) {
						random = new Random();
					}

					return new LootContext(random, this.luck, this.world, this.world.getServer().getLootManager(), this.parameters, this.drops);
				}
			}
		}
	}

	@FunctionalInterface
	public interface Dropper {
		void add(LootContext context, Consumer<ItemStack> consumer);
	}

	public static enum EntityTarget {
		THIS("this", LootContextParameters.THIS_ENTITY),
		KILLER("killer", LootContextParameters.KILLER_ENTITY),
		DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY),
		KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

		private final String type;
		private final LootContextParameter<? extends Entity> identifier;

		private EntityTarget(String type, LootContextParameter<? extends Entity> parameter) {
			this.type = type;
			this.identifier = parameter;
		}

		public LootContextParameter<? extends Entity> getIdentifier() {
			return this.identifier;
		}

		public static LootContext.EntityTarget fromString(String type) {
			for (LootContext.EntityTarget entityTarget : values()) {
				if (entityTarget.type.equals(type)) {
					return entityTarget;
				}
			}

			throw new IllegalArgumentException("Invalid entity target " + type);
		}

		public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
			public void write(JsonWriter jsonWriter, LootContext.EntityTarget entityTarget) throws IOException {
				jsonWriter.value(entityTarget.type);
			}

			public LootContext.EntityTarget read(JsonReader jsonReader) throws IOException {
				return LootContext.EntityTarget.fromString(jsonReader.nextString());
			}
		}
	}
}
