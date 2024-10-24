package net.minecraft.predicate.entity;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.NumberRange;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameModeList;

public record PlayerPredicate(
	NumberRange.IntRange experienceLevel,
	GameModeList gameMode,
	List<PlayerPredicate.StatMatcher<?>> stats,
	Object2BooleanMap<RegistryKey<Recipe<?>>> recipes,
	Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements,
	Optional<EntityPredicate> lookingAt,
	Optional<InputPredicate> input
) implements EntitySubPredicate {
	public static final int LOOKING_AT_DISTANCE = 100;
	public static final MapCodec<PlayerPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					NumberRange.IntRange.CODEC.optionalFieldOf("level", NumberRange.IntRange.ANY).forGetter(PlayerPredicate::experienceLevel),
					GameModeList.CODEC.optionalFieldOf("gamemode", GameModeList.ALL).forGetter(PlayerPredicate::gameMode),
					PlayerPredicate.StatMatcher.CODEC.listOf().optionalFieldOf("stats", List.of()).forGetter(PlayerPredicate::stats),
					Codecs.object2BooleanMap(RegistryKey.createCodec(RegistryKeys.RECIPE))
						.optionalFieldOf("recipes", Object2BooleanMaps.emptyMap())
						.forGetter(PlayerPredicate::recipes),
					Codec.unboundedMap(Identifier.CODEC, PlayerPredicate.AdvancementPredicate.CODEC)
						.optionalFieldOf("advancements", Map.of())
						.forGetter(PlayerPredicate::advancements),
					EntityPredicate.CODEC.optionalFieldOf("looking_at").forGetter(PlayerPredicate::lookingAt),
					InputPredicate.CODEC.optionalFieldOf("input").forGetter(PlayerPredicate::input)
				)
				.apply(instance, PlayerPredicate::new)
	);

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		if (!(entity instanceof ServerPlayerEntity serverPlayerEntity)) {
			return false;
		} else if (!this.experienceLevel.test(serverPlayerEntity.experienceLevel)) {
			return false;
		} else if (!this.gameMode.contains(serverPlayerEntity.interactionManager.getGameMode())) {
			return false;
		} else {
			StatHandler statHandler = serverPlayerEntity.getStatHandler();

			for (PlayerPredicate.StatMatcher<?> statMatcher : this.stats) {
				if (!statMatcher.test(statHandler)) {
					return false;
				}
			}

			ServerRecipeBook serverRecipeBook = serverPlayerEntity.getRecipeBook();

			for (Entry<RegistryKey<Recipe<?>>> entry : this.recipes.object2BooleanEntrySet()) {
				if (serverRecipeBook.isUnlocked((RegistryKey<Recipe<?>>)entry.getKey()) != entry.getBooleanValue()) {
					return false;
				}
			}

			if (!this.advancements.isEmpty()) {
				PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
				ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();

				for (java.util.Map.Entry<Identifier, PlayerPredicate.AdvancementPredicate> entry2 : this.advancements.entrySet()) {
					AdvancementEntry advancementEntry = serverAdvancementLoader.get((Identifier)entry2.getKey());
					if (advancementEntry == null || !((PlayerPredicate.AdvancementPredicate)entry2.getValue()).test(playerAdvancementTracker.getProgress(advancementEntry))) {
						return false;
					}
				}
			}

			if (this.lookingAt.isPresent()) {
				Vec3d vec3d = serverPlayerEntity.getEyePos();
				Vec3d vec3d2 = serverPlayerEntity.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * 100.0, vec3d2.y * 100.0, vec3d2.z * 100.0);
				EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(
					serverPlayerEntity.getWorld(), serverPlayerEntity, vec3d, vec3d3, new Box(vec3d, vec3d3).expand(1.0), hitEntity -> !hitEntity.isSpectator(), 0.0F
				);
				if (entityHitResult == null || entityHitResult.getType() != HitResult.Type.ENTITY) {
					return false;
				}

				Entity entity2 = entityHitResult.getEntity();
				if (!((EntityPredicate)this.lookingAt.get()).test(serverPlayerEntity, entity2) || !serverPlayerEntity.canSee(entity2)) {
					return false;
				}
			}

			return !this.input.isPresent() || ((InputPredicate)this.input.get()).matches(serverPlayerEntity.getPlayerInput());
		}
	}

	@Override
	public MapCodec<PlayerPredicate> getCodec() {
		return EntitySubPredicateTypes.PLAYER;
	}

	static record AdvancementCriteriaPredicate(Object2BooleanMap<String> criteria) implements PlayerPredicate.AdvancementPredicate {
		public static final Codec<PlayerPredicate.AdvancementCriteriaPredicate> CODEC = Codecs.object2BooleanMap(Codec.STRING)
			.xmap(PlayerPredicate.AdvancementCriteriaPredicate::new, PlayerPredicate.AdvancementCriteriaPredicate::criteria);

		public boolean test(AdvancementProgress advancementProgress) {
			for (Entry<String> entry : this.criteria.object2BooleanEntrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress == null || criterionProgress.isObtained() != entry.getBooleanValue()) {
					return false;
				}
			}

			return true;
		}
	}

	interface AdvancementPredicate extends Predicate<AdvancementProgress> {
		Codec<PlayerPredicate.AdvancementPredicate> CODEC = Codec.either(
				PlayerPredicate.CompletedAdvancementPredicate.CODEC, PlayerPredicate.AdvancementCriteriaPredicate.CODEC
			)
			.xmap(Either::unwrap, predicate -> {
				if (predicate instanceof PlayerPredicate.CompletedAdvancementPredicate completedAdvancementPredicate) {
					return Either.left(completedAdvancementPredicate);
				} else if (predicate instanceof PlayerPredicate.AdvancementCriteriaPredicate advancementCriteriaPredicate) {
					return Either.right(advancementCriteriaPredicate);
				} else {
					throw new UnsupportedOperationException();
				}
			});
	}

	public static class Builder {
		private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
		private GameModeList gameMode = GameModeList.ALL;
		private final ImmutableList.Builder<PlayerPredicate.StatMatcher<?>> stats = ImmutableList.builder();
		private final Object2BooleanMap<RegistryKey<Recipe<?>>> recipes = new Object2BooleanOpenHashMap<>();
		private final Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements = Maps.<Identifier, PlayerPredicate.AdvancementPredicate>newHashMap();
		private Optional<EntityPredicate> lookingAt = Optional.empty();
		private Optional<InputPredicate> input = Optional.empty();

		public static PlayerPredicate.Builder create() {
			return new PlayerPredicate.Builder();
		}

		public PlayerPredicate.Builder experienceLevel(NumberRange.IntRange experienceLevel) {
			this.experienceLevel = experienceLevel;
			return this;
		}

		public <T> PlayerPredicate.Builder stat(StatType<T> statType, RegistryEntry.Reference<T> value, NumberRange.IntRange range) {
			this.stats.add(new PlayerPredicate.StatMatcher<>(statType, value, range));
			return this;
		}

		public PlayerPredicate.Builder recipe(RegistryKey<Recipe<?>> recipeKey, boolean unlocked) {
			this.recipes.put(recipeKey, unlocked);
			return this;
		}

		public PlayerPredicate.Builder gameMode(GameModeList gameMode) {
			this.gameMode = gameMode;
			return this;
		}

		public PlayerPredicate.Builder lookingAt(EntityPredicate.Builder lookingAt) {
			this.lookingAt = Optional.of(lookingAt.build());
			return this;
		}

		public PlayerPredicate.Builder advancement(Identifier id, boolean done) {
			this.advancements.put(id, new PlayerPredicate.CompletedAdvancementPredicate(done));
			return this;
		}

		public PlayerPredicate.Builder advancement(Identifier id, Map<String, Boolean> criteria) {
			this.advancements.put(id, new PlayerPredicate.AdvancementCriteriaPredicate(new Object2BooleanOpenHashMap<>(criteria)));
			return this;
		}

		public PlayerPredicate.Builder input(InputPredicate input) {
			this.input = Optional.of(input);
			return this;
		}

		public PlayerPredicate build() {
			return new PlayerPredicate(this.experienceLevel, this.gameMode, this.stats.build(), this.recipes, this.advancements, this.lookingAt, this.input);
		}
	}

	static record CompletedAdvancementPredicate(boolean done) implements PlayerPredicate.AdvancementPredicate {
		public static final Codec<PlayerPredicate.CompletedAdvancementPredicate> CODEC = Codec.BOOL
			.xmap(PlayerPredicate.CompletedAdvancementPredicate::new, PlayerPredicate.CompletedAdvancementPredicate::done);

		public boolean test(AdvancementProgress advancementProgress) {
			return advancementProgress.isDone() == this.done;
		}
	}

	static record StatMatcher<T>(StatType<T> type, RegistryEntry<T> value, NumberRange.IntRange range, Supplier<Stat<T>> stat) {
		public static final Codec<PlayerPredicate.StatMatcher<?>> CODEC = Registries.STAT_TYPE
			.getCodec()
			.dispatch(PlayerPredicate.StatMatcher::type, PlayerPredicate.StatMatcher::createCodec);

		public StatMatcher(StatType<T> type, RegistryEntry<T> value, NumberRange.IntRange range) {
			this(type, value, range, Suppliers.memoize(() -> type.getOrCreateStat(value.value())));
		}

		private static <T> MapCodec<PlayerPredicate.StatMatcher<T>> createCodec(StatType<T> type) {
			return RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							type.getRegistry().getEntryCodec().fieldOf("stat").forGetter(PlayerPredicate.StatMatcher::value),
							NumberRange.IntRange.CODEC.optionalFieldOf("value", NumberRange.IntRange.ANY).forGetter(PlayerPredicate.StatMatcher::range)
						)
						.apply(instance, (value, range) -> new PlayerPredicate.StatMatcher<>(type, value, range))
			);
		}

		public boolean test(StatHandler statHandler) {
			return this.range.test(statHandler.getStat((Stat<?>)this.stat.get()));
		}
	}
}
