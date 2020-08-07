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
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LootContext {
	private final Random random;
	private final float luck;
	private final ServerWorld world;
	private final Function<Identifier, LootTable> tableGetter;
	private final Set<LootTable> activeTables = Sets.<LootTable>newLinkedHashSet();
	private final Function<Identifier, LootCondition> conditionGetter;
	private final Set<LootCondition> conditions = Sets.<LootCondition>newLinkedHashSet();
	private final Map<LootContextParameter<?>, Object> parameters;
	private final Map<Identifier, LootContext.Dropper> drops;

	private LootContext(
		Random random,
		float luck,
		ServerWorld world,
		Function<Identifier, LootTable> tableGetter,
		Function<Identifier, LootCondition> conditionSetter,
		Map<LootContextParameter<?>, Object> parameters,
		Map<Identifier, LootContext.Dropper> drops
	) {
		this.random = random;
		this.luck = luck;
		this.world = world;
		this.tableGetter = tableGetter;
		this.conditionGetter = conditionSetter;
		this.parameters = ImmutableMap.copyOf(parameters);
		this.drops = ImmutableMap.copyOf(drops);
	}

	public boolean hasParameter(LootContextParameter<?> parameter) {
		return this.parameters.containsKey(parameter);
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

	public boolean markActive(LootTable table) {
		return this.activeTables.add(table);
	}

	public void markInactive(LootTable table) {
		this.activeTables.remove(table);
	}

	public boolean addCondition(LootCondition condition) {
		return this.conditions.add(condition);
	}

	public void removeCondition(LootCondition condition) {
		this.conditions.remove(condition);
	}

	public LootTable getSupplier(Identifier id) {
		return (LootTable)this.tableGetter.apply(id);
	}

	public LootCondition getCondition(Identifier id) {
		return (LootCondition)this.conditionGetter.apply(id);
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

		public LootContext.Builder random(Random random) {
			this.random = random;
			return this;
		}

		public LootContext.Builder random(long seed) {
			if (seed != 0L) {
				this.random = new Random(seed);
			}

			return this;
		}

		public LootContext.Builder random(long seed, Random random) {
			if (seed == 0L) {
				this.random = random;
			} else {
				this.random = new Random(seed);
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
						random = new Random();
					}

					MinecraftServer minecraftServer = this.world.getServer();
					return new LootContext(
						random, this.luck, this.world, minecraftServer.getLootManager()::getTable, minecraftServer.getPredicateManager()::get, this.parameters, this.drops
					);
				}
			}
		}
	}

	@FunctionalInterface
	public interface Dropper {
		void add(LootContext context, Consumer<ItemStack> consumer);
	}

	public static enum EntityTarget {
		field_935("this", LootContextParameters.field_1226),
		field_936("killer", LootContextParameters.field_1230),
		field_939("direct_killer", LootContextParameters.field_1227),
		field_937("killer_player", LootContextParameters.field_1233);

		private final String type;
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
			public void method_318(JsonWriter jsonWriter, LootContext.EntityTarget entityTarget) throws IOException {
				jsonWriter.value(entityTarget.type);
			}

			public LootContext.EntityTarget method_317(JsonReader jsonReader) throws IOException {
				return LootContext.EntityTarget.fromString(jsonReader.nextString());
			}
		}
	}
}
