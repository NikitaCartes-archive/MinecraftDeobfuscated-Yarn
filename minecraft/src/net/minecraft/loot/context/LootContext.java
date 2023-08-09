package net.minecraft.loot.context;

import com.google.common.collect.Sets;
import java.util.Optional;
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
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

public class LootContext {
	private final LootContextParameterSet parameters;
	private final Random random;
	private final LootDataLookup dataLookup;
	private final Set<LootContext.Entry<?>> activeEntries = Sets.<LootContext.Entry<?>>newLinkedHashSet();

	LootContext(LootContextParameterSet parameters, Random random, LootDataLookup dataLookup) {
		this.parameters = parameters;
		this.random = random;
		this.dataLookup = dataLookup;
	}

	public boolean hasParameter(LootContextParameter<?> parameter) {
		return this.parameters.contains(parameter);
	}

	public <T> T requireParameter(LootContextParameter<T> parameter) {
		return this.parameters.get(parameter);
	}

	public void drop(Identifier id, Consumer<ItemStack> lootConsumer) {
		this.parameters.addDynamicDrops(id, lootConsumer);
	}

	@Nullable
	public <T> T get(LootContextParameter<T> parameter) {
		return this.parameters.getOptional(parameter);
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
		return this.parameters.getLuck();
	}

	public ServerWorld getWorld() {
		return this.parameters.getWorld();
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
		private final LootContextParameterSet parameters;
		@Nullable
		private Random random;

		public Builder(LootContextParameterSet parameters) {
			this.parameters = parameters;
		}

		public LootContext.Builder random(long seed) {
			if (seed != 0L) {
				this.random = Random.create(seed);
			}

			return this;
		}

		public ServerWorld getWorld() {
			return this.parameters.getWorld();
		}

		public LootContext build(Optional<Identifier> randomId) {
			ServerWorld serverWorld = this.getWorld();
			MinecraftServer minecraftServer = serverWorld.getServer();
			Random random = (Random)Optional.ofNullable(this.random).or(() -> randomId.map(serverWorld::getOrCreateRandom)).orElseGet(serverWorld::getRandom);
			return new LootContext(this.parameters, random, minecraftServer.getLootManager());
		}
	}

	public static enum EntityTarget implements StringIdentifiable {
		THIS("this", LootContextParameters.THIS_ENTITY),
		KILLER("killer", LootContextParameters.KILLER_ENTITY),
		DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY),
		KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

		public static final StringIdentifiable.Codec<LootContext.EntityTarget> CODEC = StringIdentifiable.createCodec(LootContext.EntityTarget::values);
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
			LootContext.EntityTarget entityTarget = (LootContext.EntityTarget)CODEC.byId(type);
			if (entityTarget != null) {
				return entityTarget;
			} else {
				throw new IllegalArgumentException("Invalid entity target " + type);
			}
		}

		@Override
		public String asString() {
			return this.type;
		}
	}

	public static record Entry<T>(LootDataType<T> type, T value) {
	}
}
