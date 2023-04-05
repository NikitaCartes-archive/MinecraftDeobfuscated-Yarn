package net.minecraft.loot.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataLookup;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class LootContext {
	private final Random random;
	private final float luck;
	private final ServerWorld world;
	private final LootDataLookup dataLookup;
	private final Set<LootContext.Entry<?>> activeEntries = Sets.<LootContext.Entry<?>>newLinkedHashSet();
	private final Map<LootContextParameter<?>, Object> parameters;
	private final Map<Identifier, LootContext.Dropper> drops;

	LootContext(
		Random random,
		float luck,
		ServerWorld world,
		LootDataLookup dataLookup,
		Map<LootContextParameter<?>, Object> parameters,
		Map<Identifier, LootContext.Dropper> drops
	) {
		this.random = random;
		this.luck = luck;
		this.world = world;
		this.dataLookup = dataLookup;
		this.parameters = ImmutableMap.copyOf(parameters);
		this.drops = ImmutableMap.copyOf(drops);
	}

	public boolean hasParameter(LootContextParameter<?> parameter) {
		return this.parameters.containsKey(parameter);
	}

	public <T> T requireParameter(LootContextParameter<T> parameter) {
		T object = (T)this.parameters.get(parameter);
		if (object == null) {
			throw new NoSuchElementException(parameter.getId().toString());
		} else {
			return object;
		}
	}

	public void drop(Identifier id, Consumer<ItemStack> lootConsumer) {
		LootContext.Dropper dropper = (LootContext.Dropper)this.drops.get(id);
		if (dropper != null) {
			dropper.add(this, lootConsumer);
		}
	}

	@Nullable
	public <T> T get(LootContextParameter<T> parameter) {
		return (T)this.parameters.get(parameter);
	}

	public boolean isActive(LootContext.Entry<?> entry) {
		return this.activeEntries.contains(entry);
	}

	public boolean markActive(LootContext.Entry<?> entry) {
		return this.activeEntries.add(entry);
	}

	public void markInactive(LootContext.Entry<?> entry) {
		this.activeEntries.remove(entry);
	}

	public LootDataLookup getDataLookup() {
		return this.dataLookup;
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

	public static LootContext.Entry<LootTable> table(LootTable table) {
		return new LootContext.Entry<>(LootDataType.LOOT_TABLES, table);
	}

	public static LootContext.Entry<LootCondition> predicate(LootCondition predicate) {
		return new LootContext.Entry<>(LootDataType.PREDICATES, predicate);
	}

	public static LootContext.Entry<LootFunction> itemModifier(LootFunction itemModifier) {
		return new LootContext.Entry<>(LootDataType.ITEM_MODIFIERS, itemModifier);
	}

	public static class Builder {
		private final ServerWorld world;
		private final Map<LootContextParameter<?>, Object> parameters = Maps.<LootContextParameter<?>, Object>newIdentityHashMap();
		private final Map<Identifier, LootContext.Dropper> drops = Maps.<Identifier, LootContext.Dropper>newHashMap();
		@Nullable
		private Random random;
		private float luck;

		public Builder(ServerWorld world) {
			this.world = world;
		}

		public LootContext.Builder random(Random random) {
			this.random = random;
			return this;
		}

		public LootContext.Builder random(long seed) {
			if (seed != 0L) {
				this.random = Random.create(seed);
			}

			return this;
		}

		public LootContext.Builder random(long seed, Random random) {
			if (seed == 0L) {
				this.random = random;
			} else {
				this.random = Random.create(seed);
			}

			return this;
		}

		public LootContext.Builder luck(float luck) {
			this.luck = luck;
			return this;
		}

		public <T> LootContext.Builder parameter(LootContextParameter<T> key, T value) {
			this.parameters.put(key, value);
			return this;
		}

		public <T> LootContext.Builder optionalParameter(LootContextParameter<T> key, @Nullable T value) {
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
						random = Random.create();
					}

					MinecraftServer minecraftServer = this.world.getServer();
					return new LootContext(random, this.luck, this.world, minecraftServer.getLootManager(), this.parameters, this.drops);
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

		final String type;
		private final LootContextParameter<? extends Entity> parameter;

		private EntityTarget(String type, LootContextParameter<? extends Entity> parameter) {
			this.type = type;
			this.parameter = parameter;
		}

		public LootContextParameter<? extends Entity> getParameter() {
			return this.parameter;
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

	public static record Entry<T>(LootDataType<T> type, T value) {
	}
}
