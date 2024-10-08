package net.minecraft.loot.context;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.random.Random;

public class LootContext {
	private final LootWorldContext worldContext;
	private final Random random;
	private final RegistryEntryLookup.RegistryLookup lookup;
	private final Set<LootContext.Entry<?>> activeEntries = Sets.<LootContext.Entry<?>>newLinkedHashSet();

	LootContext(LootWorldContext worldContext, Random random, RegistryEntryLookup.RegistryLookup lookup) {
		this.worldContext = worldContext;
		this.random = random;
		this.lookup = lookup;
	}

	public boolean hasParameter(ContextParameter<?> parameter) {
		return this.worldContext.getParameters().contains(parameter);
	}

	public <T> T requireParameter(ContextParameter<T> parameter) {
		return this.worldContext.getParameters().getOrThrow(parameter);
	}

	@Nullable
	public <T> T get(ContextParameter<T> parameter) {
		return this.worldContext.getParameters().getNullable(parameter);
	}

	public void drop(Identifier id, Consumer<ItemStack> lootConsumer) {
		this.worldContext.addDynamicDrops(id, lootConsumer);
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

	public RegistryEntryLookup.RegistryLookup getLookup() {
		return this.lookup;
	}

	public Random getRandom() {
		return this.random;
	}

	public float getLuck() {
		return this.worldContext.getLuck();
	}

	public ServerWorld getWorld() {
		return this.worldContext.getWorld();
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
		private final LootWorldContext worldContext;
		@Nullable
		private Random random;

		public Builder(LootWorldContext worldContext) {
			this.worldContext = worldContext;
		}

		public LootContext.Builder random(long seed) {
			if (seed != 0L) {
				this.random = Random.create(seed);
			}

			return this;
		}

		public LootContext.Builder random(Random random) {
			this.random = random;
			return this;
		}

		public ServerWorld getWorld() {
			return this.worldContext.getWorld();
		}

		public LootContext build(Optional<Identifier> randomId) {
			ServerWorld serverWorld = this.getWorld();
			MinecraftServer minecraftServer = serverWorld.getServer();
			Random random = (Random)Optional.ofNullable(this.random).or(() -> randomId.map(serverWorld::getOrCreateRandom)).orElseGet(serverWorld::getRandom);
			return new LootContext(this.worldContext, random, minecraftServer.getReloadableRegistries().createRegistryLookup());
		}
	}

	public static enum EntityTarget implements StringIdentifiable {
		THIS("this", LootContextParameters.THIS_ENTITY),
		ATTACKER("attacker", LootContextParameters.ATTACKING_ENTITY),
		DIRECT_ATTACKER("direct_attacker", LootContextParameters.DIRECT_ATTACKING_ENTITY),
		ATTACKING_PLAYER("attacking_player", LootContextParameters.LAST_DAMAGE_PLAYER);

		public static final StringIdentifiable.EnumCodec<LootContext.EntityTarget> CODEC = StringIdentifiable.createCodec(LootContext.EntityTarget::values);
		private final String type;
		private final ContextParameter<? extends Entity> parameter;

		private EntityTarget(final String type, final ContextParameter<? extends Entity> parameter) {
			this.type = type;
			this.parameter = parameter;
		}

		public ContextParameter<? extends Entity> getParameter() {
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
