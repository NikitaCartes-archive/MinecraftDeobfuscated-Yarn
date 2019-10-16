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
	private final Function<Identifier, LootTable> supplierGetter;
	private final Set<LootTable> suppliers = Sets.<LootTable>newLinkedHashSet();
	private final Function<Identifier, LootCondition> conditionGetter;
	private final Set<LootCondition> conditions = Sets.<LootCondition>newLinkedHashSet();
	private final Map<LootContextParameter<?>, Object> parameters;
	private final Map<Identifier, LootContext.Dropper> drops;

	private LootContext(
		Random random,
		float f,
		ServerWorld serverWorld,
		Function<Identifier, LootTable> function,
		Function<Identifier, LootCondition> function2,
		Map<LootContextParameter<?>, Object> map,
		Map<Identifier, LootContext.Dropper> map2
	) {
		this.random = random;
		this.luck = f;
		this.world = serverWorld;
		this.supplierGetter = function;
		this.conditionGetter = function2;
		this.parameters = ImmutableMap.copyOf(map);
		this.drops = ImmutableMap.copyOf(map2);
	}

	public boolean hasParameter(LootContextParameter<?> lootContextParameter) {
		return this.parameters.containsKey(lootContextParameter);
	}

	public void drop(Identifier identifier, Consumer<ItemStack> consumer) {
		LootContext.Dropper dropper = (LootContext.Dropper)this.drops.get(identifier);
		if (dropper != null) {
			dropper.add(this, consumer);
		}
	}

	@Nullable
	public <T> T get(LootContextParameter<T> lootContextParameter) {
		return (T)this.parameters.get(lootContextParameter);
	}

	public boolean addDrop(LootTable lootTable) {
		return this.suppliers.add(lootTable);
	}

	public void removeDrop(LootTable lootTable) {
		this.suppliers.remove(lootTable);
	}

	public boolean addCondition(LootCondition lootCondition) {
		return this.conditions.add(lootCondition);
	}

	public void removeCondition(LootCondition lootCondition) {
		this.conditions.remove(lootCondition);
	}

	public LootTable getSupplier(Identifier identifier) {
		return (LootTable)this.supplierGetter.apply(identifier);
	}

	public LootCondition getCondition(Identifier identifier) {
		return (LootCondition)this.conditionGetter.apply(identifier);
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

		public Builder(ServerWorld serverWorld) {
			this.world = serverWorld;
		}

		public LootContext.Builder setRandom(Random random) {
			this.random = random;
			return this;
		}

		public LootContext.Builder setRandom(long l) {
			if (l != 0L) {
				this.random = new Random(l);
			}

			return this;
		}

		public LootContext.Builder setRandom(long l, Random random) {
			if (l == 0L) {
				this.random = random;
			} else {
				this.random = new Random(l);
			}

			return this;
		}

		public LootContext.Builder setLuck(float f) {
			this.luck = f;
			return this;
		}

		public <T> LootContext.Builder put(LootContextParameter<T> lootContextParameter, T object) {
			this.parameters.put(lootContextParameter, object);
			return this;
		}

		public <T> LootContext.Builder putNullable(LootContextParameter<T> lootContextParameter, @Nullable T object) {
			if (object == null) {
				this.parameters.remove(lootContextParameter);
			} else {
				this.parameters.put(lootContextParameter, object);
			}

			return this;
		}

		public LootContext.Builder putDrop(Identifier identifier, LootContext.Dropper dropper) {
			LootContext.Dropper dropper2 = (LootContext.Dropper)this.drops.put(identifier, dropper);
			if (dropper2 != null) {
				throw new IllegalStateException("Duplicated dynamic drop '" + this.drops + "'");
			} else {
				return this;
			}
		}

		public ServerWorld getWorld() {
			return this.world;
		}

		public <T> T get(LootContextParameter<T> lootContextParameter) {
			T object = (T)this.parameters.get(lootContextParameter);
			if (object == null) {
				throw new IllegalArgumentException("No parameter " + lootContextParameter);
			} else {
				return object;
			}
		}

		@Nullable
		public <T> T getNullable(LootContextParameter<T> lootContextParameter) {
			return (T)this.parameters.get(lootContextParameter);
		}

		public LootContext build(LootContextType lootContextType) {
			Set<LootContextParameter<?>> set = Sets.<LootContextParameter<?>>difference(this.parameters.keySet(), lootContextType.getAllowed());
			if (!set.isEmpty()) {
				throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
			} else {
				Set<LootContextParameter<?>> set2 = Sets.<LootContextParameter<?>>difference(lootContextType.getRequired(), this.parameters.keySet());
				if (!set2.isEmpty()) {
					throw new IllegalArgumentException("Missing required parameters: " + set2);
				} else {
					Random random = this.random;
					if (random == null) {
						random = new Random();
					}

					MinecraftServer minecraftServer = this.world.getServer();
					return new LootContext(
						random, this.luck, this.world, minecraftServer.getLootManager()::getSupplier, minecraftServer.getPredicateManager()::get, this.parameters, this.drops
					);
				}
			}
		}
	}

	@FunctionalInterface
	public interface Dropper {
		void add(LootContext lootContext, Consumer<ItemStack> consumer);
	}

	public static enum EntityTarget {
		THIS("this", LootContextParameters.THIS_ENTITY),
		KILLER("killer", LootContextParameters.KILLER_ENTITY),
		DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY),
		KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

		private final String type;
		private final LootContextParameter<? extends Entity> parameter;

		private EntityTarget(String string2, LootContextParameter<? extends Entity> lootContextParameter) {
			this.type = string2;
			this.parameter = lootContextParameter;
		}

		public LootContextParameter<? extends Entity> getParameter() {
			return this.parameter;
		}

		public static LootContext.EntityTarget fromString(String string) {
			for (LootContext.EntityTarget entityTarget : values()) {
				if (entityTarget.type.equals(string)) {
					return entityTarget;
				}
			}

			throw new IllegalArgumentException("Invalid entity target " + string);
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
