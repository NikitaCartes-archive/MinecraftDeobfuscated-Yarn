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
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.NumberRange;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
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
import net.minecraft.world.GameMode;

public record PlayerPredicate(
	NumberRange.IntRange experienceLevel,
	Optional<GameMode> gameMode,
	List<PlayerPredicate.StatMatcher<?>> stats,
	Object2BooleanMap<Identifier> recipes,
	Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements,
	Optional<EntityPredicate> lookingAt
) implements TypeSpecificPredicate {
	public static final int LOOKING_AT_DISTANCE = 100;
	public static final MapCodec<PlayerPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "level", NumberRange.IntRange.ANY).forGetter(PlayerPredicate::experienceLevel),
					GameMode.CODEC.optionalFieldOf("gamemode").forGetter(PlayerPredicate::gameMode),
					Codecs.createStrictOptionalFieldCodec(PlayerPredicate.StatMatcher.CODEC.listOf(), "stats", List.of()).forGetter(PlayerPredicate::stats),
					Codecs.createStrictOptionalFieldCodec(Codecs.method_53058(Identifier.CODEC), "recipes", Object2BooleanMaps.emptyMap()).forGetter(PlayerPredicate::recipes),
					Codecs.createStrictOptionalFieldCodec(Codec.unboundedMap(Identifier.CODEC, PlayerPredicate.AdvancementPredicate.CODEC), "advancements", Map.of())
						.forGetter(PlayerPredicate::advancements),
					Codecs.createStrictOptionalFieldCodec(EntityPredicate.CODEC, "looking_at").forGetter(PlayerPredicate::lookingAt)
				)
				.apply(instance, PlayerPredicate::new)
	);

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		if (!(entity instanceof ServerPlayerEntity serverPlayerEntity)) {
			return false;
		} else if (!this.experienceLevel.test(serverPlayerEntity.experienceLevel)) {
			return false;
		} else if (this.gameMode.isPresent() && this.gameMode.get() != serverPlayerEntity.interactionManager.getGameMode()) {
			return false;
		} else {
			StatHandler statHandler = serverPlayerEntity.getStatHandler();

			for (PlayerPredicate.StatMatcher<?> statMatcher : this.stats) {
				if (!statMatcher.test(statHandler)) {
					return false;
				}
			}

			RecipeBook recipeBook = serverPlayerEntity.getRecipeBook();

			for (Entry<Identifier> entry : this.recipes.object2BooleanEntrySet()) {
				if (recipeBook.contains((Identifier)entry.getKey()) != entry.getBooleanValue()) {
					return false;
				}
			}

			if (!this.advancements.isEmpty()) {
				PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
				ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();

				for (java.util.Map.Entry<Identifier, PlayerPredicate.AdvancementPredicate> entry2 : this.advancements.entrySet()) {
					Advancement advancement = serverAdvancementLoader.get((Identifier)entry2.getKey());
					if (advancement == null || !((PlayerPredicate.AdvancementPredicate)entry2.getValue()).test(playerAdvancementTracker.getProgress(advancement))) {
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

			return true;
		}
	}

	@Override
	public TypeSpecificPredicate.Type type() {
		return TypeSpecificPredicate.Deserializers.PLAYER;
	}

	static record AdvancementCriteriaPredicate(Object2BooleanMap<String> criteria) implements PlayerPredicate.AdvancementPredicate {
		public static final Codec<PlayerPredicate.AdvancementCriteriaPredicate> CODEC = Codecs.method_53058(Codec.STRING)
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
			.xmap(
				either -> either.map(completedAdvancementPredicate -> completedAdvancementPredicate, advancementCriteriaPredicate -> advancementCriteriaPredicate),
				advancementPredicate -> {
					if (advancementPredicate instanceof PlayerPredicate.CompletedAdvancementPredicate completedAdvancementPredicate) {
						return Either.left(completedAdvancementPredicate);
					} else if (advancementPredicate instanceof PlayerPredicate.AdvancementCriteriaPredicate advancementCriteriaPredicate) {
						return Either.right(advancementCriteriaPredicate);
					} else {
						throw new UnsupportedOperationException();
					}
				}
			);
	}

	public static class Builder {
		private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
		private Optional<GameMode> gameMode = Optional.empty();
		private final ImmutableList.Builder<PlayerPredicate.StatMatcher<?>> stats = ImmutableList.builder();
		private final Object2BooleanMap<Identifier> recipes = new Object2BooleanOpenHashMap<>();
		private final Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements = Maps.<Identifier, PlayerPredicate.AdvancementPredicate>newHashMap();
		private Optional<EntityPredicate> lookingAt = Optional.empty();

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

		public PlayerPredicate.Builder recipe(Identifier id, boolean unlocked) {
			this.recipes.put(id, unlocked);
			return this;
		}

		public PlayerPredicate.Builder gameMode(GameMode gameMode) {
			this.gameMode = Optional.of(gameMode);
			return this;
		}

		public PlayerPredicate.Builder lookingAt(Optional<EntityPredicate> lookingAt) {
			this.lookingAt = lookingAt;
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

		public PlayerPredicate build() {
			return new PlayerPredicate(this.experienceLevel, this.gameMode, this.stats.build(), this.recipes, this.advancements, this.lookingAt);
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

		private static <T> Codec<PlayerPredicate.StatMatcher<T>> createCodec(StatType<T> type) {
			return RecordCodecBuilder.create(
				instance -> instance.group(
							type.getRegistry().createEntryCodec().fieldOf("stat").forGetter(PlayerPredicate.StatMatcher::value),
							Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "value", NumberRange.IntRange.ANY).forGetter(PlayerPredicate.StatMatcher::range)
						)
						.apply(instance, (registryEntry, intRange) -> new PlayerPredicate.StatMatcher<>(type, registryEntry, intRange))
			);
		}

		public boolean test(StatHandler statHandler) {
			return this.range.test(statHandler.getStat((Stat<?>)this.stat.get()));
		}
	}
}
