package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

public class EntitySelectorOptions {
	private static final Map<String, EntitySelectorOptions.SelectorOption> OPTIONS = Maps.newHashMap();
	public static final DynamicCommandExceptionType UNKNOWN_OPTION_EXCEPTION = new DynamicCommandExceptionType(
		option -> Text.stringifiedTranslatable("argument.entity.options.unknown", option)
	);
	public static final DynamicCommandExceptionType INAPPLICABLE_OPTION_EXCEPTION = new DynamicCommandExceptionType(
		option -> Text.stringifiedTranslatable("argument.entity.options.inapplicable", option)
	);
	public static final SimpleCommandExceptionType NEGATIVE_DISTANCE_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.entity.options.distance.negative")
	);
	public static final SimpleCommandExceptionType NEGATIVE_LEVEL_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.entity.options.level.negative")
	);
	public static final SimpleCommandExceptionType TOO_SMALL_LEVEL_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("argument.entity.options.limit.toosmall")
	);
	public static final DynamicCommandExceptionType IRREVERSIBLE_SORT_EXCEPTION = new DynamicCommandExceptionType(
		sortType -> Text.stringifiedTranslatable("argument.entity.options.sort.irreversible", sortType)
	);
	public static final DynamicCommandExceptionType INVALID_MODE_EXCEPTION = new DynamicCommandExceptionType(
		gameMode -> Text.stringifiedTranslatable("argument.entity.options.mode.invalid", gameMode)
	);
	public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType(
		entity -> Text.stringifiedTranslatable("argument.entity.options.type.invalid", entity)
	);

	private static void putOption(String id, EntitySelectorOptions.SelectorHandler handler, Predicate<EntitySelectorReader> condition, Text description) {
		OPTIONS.put(id, new EntitySelectorOptions.SelectorOption(handler, condition, description));
	}

	public static void register() {
		if (OPTIONS.isEmpty()) {
			putOption("name", reader -> {
				int i = reader.getReader().getCursor();
				boolean bl = reader.readNegationCharacter();
				String string = reader.getReader().readString();
				if (reader.excludesName() && !bl) {
					reader.getReader().setCursor(i);
					throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), "name");
				} else {
					if (bl) {
						reader.setExcludesName(true);
					} else {
						reader.setSelectsName(true);
					}

					reader.setPredicate(entity -> entity.getName().getString().equals(string) != bl);
				}
			}, reader -> !reader.selectsName(), Text.translatable("argument.entity.options.name.description"));
			putOption("distance", reader -> {
				int i = reader.getReader().getCursor();
				NumberRange.DoubleRange doubleRange = NumberRange.DoubleRange.parse(reader.getReader());
				if ((!doubleRange.min().isPresent() || !(doubleRange.min().get() < 0.0)) && (!doubleRange.max().isPresent() || !(doubleRange.max().get() < 0.0))) {
					reader.setDistance(doubleRange);
					reader.setLocalWorldOnly();
				} else {
					reader.getReader().setCursor(i);
					throw NEGATIVE_DISTANCE_EXCEPTION.createWithContext(reader.getReader());
				}
			}, reader -> reader.getDistance().isDummy(), Text.translatable("argument.entity.options.distance.description"));
			putOption("level", reader -> {
				int i = reader.getReader().getCursor();
				NumberRange.IntRange intRange = NumberRange.IntRange.parse(reader.getReader());
				if ((!intRange.min().isPresent() || intRange.min().get() >= 0) && (!intRange.max().isPresent() || intRange.max().get() >= 0)) {
					reader.setLevelRange(intRange);
					reader.setIncludesNonPlayers(false);
				} else {
					reader.getReader().setCursor(i);
					throw NEGATIVE_LEVEL_EXCEPTION.createWithContext(reader.getReader());
				}
			}, reader -> reader.getLevelRange().isDummy(), Text.translatable("argument.entity.options.level.description"));
			putOption("x", reader -> {
				reader.setLocalWorldOnly();
				reader.setX(reader.getReader().readDouble());
			}, reader -> reader.getX() == null, Text.translatable("argument.entity.options.x.description"));
			putOption("y", reader -> {
				reader.setLocalWorldOnly();
				reader.setY(reader.getReader().readDouble());
			}, reader -> reader.getY() == null, Text.translatable("argument.entity.options.y.description"));
			putOption("z", reader -> {
				reader.setLocalWorldOnly();
				reader.setZ(reader.getReader().readDouble());
			}, reader -> reader.getZ() == null, Text.translatable("argument.entity.options.z.description"));
			putOption("dx", reader -> {
				reader.setLocalWorldOnly();
				reader.setDx(reader.getReader().readDouble());
			}, reader -> reader.getDx() == null, Text.translatable("argument.entity.options.dx.description"));
			putOption("dy", reader -> {
				reader.setLocalWorldOnly();
				reader.setDy(reader.getReader().readDouble());
			}, reader -> reader.getDy() == null, Text.translatable("argument.entity.options.dy.description"));
			putOption("dz", reader -> {
				reader.setLocalWorldOnly();
				reader.setDz(reader.getReader().readDouble());
			}, reader -> reader.getDz() == null, Text.translatable("argument.entity.options.dz.description"));
			putOption(
				"x_rotation",
				reader -> reader.setPitchRange(FloatRangeArgument.parse(reader.getReader(), true, MathHelper::wrapDegrees)),
				reader -> reader.getPitchRange() == FloatRangeArgument.ANY,
				Text.translatable("argument.entity.options.x_rotation.description")
			);
			putOption(
				"y_rotation",
				reader -> reader.setYawRange(FloatRangeArgument.parse(reader.getReader(), true, MathHelper::wrapDegrees)),
				reader -> reader.getYawRange() == FloatRangeArgument.ANY,
				Text.translatable("argument.entity.options.y_rotation.description")
			);
			putOption("limit", reader -> {
				int i = reader.getReader().getCursor();
				int j = reader.getReader().readInt();
				if (j < 1) {
					reader.getReader().setCursor(i);
					throw TOO_SMALL_LEVEL_EXCEPTION.createWithContext(reader.getReader());
				} else {
					reader.setLimit(j);
					reader.setHasLimit(true);
				}
			}, reader -> !reader.isSenderOnly() && !reader.hasLimit(), Text.translatable("argument.entity.options.limit.description"));
			putOption("sort", reader -> {
				int i = reader.getReader().getCursor();
				String string = reader.getReader().readUnquotedString();
				reader.setSuggestionProvider((builder, consumer) -> CommandSource.suggestMatching(Arrays.asList("nearest", "furthest", "random", "arbitrary"), builder));
				BiConsumer var10001;
				switch(string) {
					case "nearest":
						var10001 = EntitySelectorReader.NEAREST;
						break;
					case "furthest":
						var10001 = EntitySelectorReader.FURTHEST;
						break;
					case "random":
						var10001 = EntitySelectorReader.RANDOM;
						break;
					case "arbitrary":
						var10001 = EntitySelector.ARBITRARY;
						break;
					default:
						reader.getReader().setCursor(i);
						throw IRREVERSIBLE_SORT_EXCEPTION.createWithContext(reader.getReader(), string);
				}

				reader.setSorter(var10001);
				reader.setHasSorter(true);
			}, reader -> !reader.isSenderOnly() && !reader.hasSorter(), Text.translatable("argument.entity.options.sort.description"));
			putOption("gamemode", reader -> {
				reader.setSuggestionProvider((builder, consumer) -> {
					String stringxx = builder.getRemaining().toLowerCase(Locale.ROOT);
					boolean blxx = !reader.excludesGameMode();
					boolean bl2 = true;
					if (!stringxx.isEmpty()) {
						if (stringxx.charAt(0) == '!') {
							blxx = false;
							stringxx = stringxx.substring(1);
						} else {
							bl2 = false;
						}
					}

					for(GameMode gameModexx : GameMode.values()) {
						if (gameModexx.getName().toLowerCase(Locale.ROOT).startsWith(stringxx)) {
							if (bl2) {
								builder.suggest("!" + gameModexx.getName());
							}

							if (blxx) {
								builder.suggest(gameModexx.getName());
							}
						}
					}

					return builder.buildFuture();
				});
				int i = reader.getReader().getCursor();
				boolean bl = reader.readNegationCharacter();
				if (reader.excludesGameMode() && !bl) {
					reader.getReader().setCursor(i);
					throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), "gamemode");
				} else {
					String string = reader.getReader().readUnquotedString();
					GameMode gameMode = GameMode.byName(string, null);
					if (gameMode == null) {
						reader.getReader().setCursor(i);
						throw INVALID_MODE_EXCEPTION.createWithContext(reader.getReader(), string);
					} else {
						reader.setIncludesNonPlayers(false);
						reader.setPredicate(entity -> {
							if (!(entity instanceof ServerPlayerEntity)) {
								return false;
							} else {
								GameMode gameMode2 = ((ServerPlayerEntity)entity).interactionManager.getGameMode();
								return bl ? gameMode2 != gameMode : gameMode2 == gameMode;
							}
						});
						if (bl) {
							reader.setExcludesGameMode(true);
						} else {
							reader.setSelectsGameMode(true);
						}
					}
				}
			}, reader -> !reader.selectsGameMode(), Text.translatable("argument.entity.options.gamemode.description"));
			putOption("team", reader -> {
				boolean bl = reader.readNegationCharacter();
				String string = reader.getReader().readUnquotedString();
				reader.setPredicate(entity -> {
					if (!(entity instanceof LivingEntity)) {
						return false;
					} else {
						AbstractTeam abstractTeam = entity.getScoreboardTeam();
						String string2 = abstractTeam == null ? "" : abstractTeam.getName();
						return string2.equals(string) != bl;
					}
				});
				if (bl) {
					reader.setExcludesTeam(true);
				} else {
					reader.setSelectsTeam(true);
				}
			}, reader -> !reader.selectsTeam(), Text.translatable("argument.entity.options.team.description"));
			putOption("type", reader -> {
				reader.setSuggestionProvider((builder, consumer) -> {
					CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.getIds(), builder, String.valueOf('!'));
					CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.streamTags().map(TagKey::id), builder, "!#");
					if (!reader.excludesEntityType()) {
						CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.getIds(), builder);
						CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.streamTags().map(TagKey::id), builder, String.valueOf('#'));
					}

					return builder.buildFuture();
				});
				int i = reader.getReader().getCursor();
				boolean bl = reader.readNegationCharacter();
				if (reader.excludesEntityType() && !bl) {
					reader.getReader().setCursor(i);
					throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), "type");
				} else {
					if (bl) {
						reader.setExcludesEntityType();
					}

					if (reader.readTagCharacter()) {
						TagKey<EntityType<?>> tagKey = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.fromCommandInput(reader.getReader()));
						reader.setPredicate(entity -> entity.getType().isIn(tagKey) != bl);
					} else {
						Identifier identifier = Identifier.fromCommandInput(reader.getReader());
						EntityType<?> entityType = (EntityType)Registries.ENTITY_TYPE.getOrEmpty(identifier).orElseThrow(() -> {
							reader.getReader().setCursor(i);
							return INVALID_TYPE_EXCEPTION.createWithContext(reader.getReader(), identifier.toString());
						});
						if (Objects.equals(EntityType.PLAYER, entityType) && !bl) {
							reader.setIncludesNonPlayers(false);
						}

						reader.setPredicate(entity -> Objects.equals(entityType, entity.getType()) != bl);
						if (!bl) {
							reader.setEntityType(entityType);
						}
					}
				}
			}, reader -> !reader.selectsEntityType(), Text.translatable("argument.entity.options.type.description"));
			putOption("tag", reader -> {
				boolean bl = reader.readNegationCharacter();
				String string = reader.getReader().readUnquotedString();
				reader.setPredicate(entity -> {
					if ("".equals(string)) {
						return entity.getCommandTags().isEmpty() != bl;
					} else {
						return entity.getCommandTags().contains(string) != bl;
					}
				});
			}, reader -> true, Text.translatable("argument.entity.options.tag.description"));
			putOption("nbt", reader -> {
				boolean bl = reader.readNegationCharacter();
				NbtCompound nbtCompound = new StringNbtReader(reader.getReader()).parseCompound();
				reader.setPredicate(entity -> {
					NbtCompound nbtCompound2 = entity.writeNbt(new NbtCompound());
					if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
						ItemStack itemStack = serverPlayerEntity.getInventory().getMainHandStack();
						if (!itemStack.isEmpty()) {
							nbtCompound2.put("SelectedItem", itemStack.encode(serverPlayerEntity.getRegistryManager()));
						}
					}

					return NbtHelper.matches(nbtCompound, nbtCompound2, true) != bl;
				});
			}, reader -> true, Text.translatable("argument.entity.options.nbt.description"));
			putOption("scores", reader -> {
				StringReader stringReader = reader.getReader();
				Map<String, NumberRange.IntRange> map = Maps.newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while(stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					String string = stringReader.readUnquotedString();
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					NumberRange.IntRange intRange = NumberRange.IntRange.parse(stringReader);
					map.put(string, intRange);
					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == ',') {
						stringReader.skip();
					}
				}

				stringReader.expect('}');
				if (!map.isEmpty()) {
					reader.setPredicate(entity -> {
						Scoreboard scoreboard = entity.getServer().getScoreboard();

						for(Entry<String, NumberRange.IntRange> entry : map.entrySet()) {
							ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective((String)entry.getKey());
							if (scoreboardObjective == null) {
								return false;
							}

							ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(entity, scoreboardObjective);
							if (readableScoreboardScore == null) {
								return false;
							}

							if (!((NumberRange.IntRange)entry.getValue()).test(readableScoreboardScore.getScore())) {
								return false;
							}
						}

						return true;
					});
				}

				reader.setSelectsScores(true);
			}, reader -> !reader.selectsScores(), Text.translatable("argument.entity.options.scores.description"));
			putOption("advancements", reader -> {
				StringReader stringReader = reader.getReader();
				Map<Identifier, Predicate<AdvancementProgress>> map = Maps.newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while(stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					Identifier identifier = Identifier.fromCommandInput(stringReader);
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == '{') {
						Map<String, Predicate<CriterionProgress>> map2 = Maps.newHashMap();
						stringReader.skipWhitespace();
						stringReader.expect('{');
						stringReader.skipWhitespace();

						while(stringReader.canRead() && stringReader.peek() != '}') {
							stringReader.skipWhitespace();
							String string = stringReader.readUnquotedString();
							stringReader.skipWhitespace();
							stringReader.expect('=');
							stringReader.skipWhitespace();
							boolean bl = stringReader.readBoolean();
							map2.put(string, (Predicate)criterionProgress -> criterionProgress.isObtained() == bl);
							stringReader.skipWhitespace();
							if (stringReader.canRead() && stringReader.peek() == ',') {
								stringReader.skip();
							}
						}

						stringReader.skipWhitespace();
						stringReader.expect('}');
						stringReader.skipWhitespace();
						map.put(identifier, (Predicate)advancementProgress -> {
							for(Entry<String, Predicate<CriterionProgress>> entry : map2.entrySet()) {
								CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
								if (criterionProgress == null || !((Predicate)entry.getValue()).test(criterionProgress)) {
									return false;
								}
							}

							return true;
						});
					} else {
						boolean bl2 = stringReader.readBoolean();
						map.put(identifier, (Predicate)advancementProgress -> advancementProgress.isDone() == bl2);
					}

					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == ',') {
						stringReader.skip();
					}
				}

				stringReader.expect('}');
				if (!map.isEmpty()) {
					reader.setPredicate(entity -> {
						if (!(entity instanceof ServerPlayerEntity)) {
							return false;
						} else {
							ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
							PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
							ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();

							for(Entry<Identifier, Predicate<AdvancementProgress>> entry : map.entrySet()) {
								AdvancementEntry advancementEntry = serverAdvancementLoader.get((Identifier)entry.getKey());
								if (advancementEntry == null || !((Predicate)entry.getValue()).test(playerAdvancementTracker.getProgress(advancementEntry))) {
									return false;
								}
							}

							return true;
						}
					});
					reader.setIncludesNonPlayers(false);
				}

				reader.setSelectsAdvancements(true);
			}, reader -> !reader.selectsAdvancements(), Text.translatable("argument.entity.options.advancements.description"));
			putOption(
				"predicate",
				reader -> {
					boolean bl = reader.readNegationCharacter();
					RegistryKey<LootCondition> registryKey = RegistryKey.of(RegistryKeys.PREDICATE, Identifier.fromCommandInput(reader.getReader()));
					reader.setPredicate(
						entity -> {
							if (!(entity.getWorld() instanceof ServerWorld)) {
								return false;
							} else {
								ServerWorld serverWorld = (ServerWorld)entity.getWorld();
								Optional<LootCondition> optional = serverWorld.getServer()
									.getReloadableRegistries()
									.createRegistryLookup()
									.getOptionalEntry(RegistryKeys.PREDICATE, registryKey)
									.map(RegistryEntry::value);
								if (optional.isEmpty()) {
									return false;
								} else {
									LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
										.add(LootContextParameters.THIS_ENTITY, entity)
										.add(LootContextParameters.ORIGIN, entity.getPos())
										.build(LootContextTypes.SELECTOR);
									LootContext lootContext = new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
									lootContext.markActive(LootContext.predicate((LootCondition)optional.get()));
									return bl ^ ((LootCondition)optional.get()).test(lootContext);
								}
							}
						}
					);
				},
				reader -> true,
				Text.translatable("argument.entity.options.predicate.description")
			);
		}
	}

	public static EntitySelectorOptions.SelectorHandler getHandler(EntitySelectorReader reader, String option, int restoreCursor) throws CommandSyntaxException {
		EntitySelectorOptions.SelectorOption selectorOption = (EntitySelectorOptions.SelectorOption)OPTIONS.get(option);
		if (selectorOption != null) {
			if (selectorOption.condition.test(reader)) {
				return selectorOption.handler;
			} else {
				throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), option);
			}
		} else {
			reader.getReader().setCursor(restoreCursor);
			throw UNKNOWN_OPTION_EXCEPTION.createWithContext(reader.getReader(), option);
		}
	}

	public static void suggestOptions(EntitySelectorReader reader, SuggestionsBuilder suggestionBuilder) {
		String string = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for(Entry<String, EntitySelectorOptions.SelectorOption> entry : OPTIONS.entrySet()) {
			if (((EntitySelectorOptions.SelectorOption)entry.getValue()).condition.test(reader) && ((String)entry.getKey()).toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionBuilder.suggest((String)entry.getKey() + "=", ((EntitySelectorOptions.SelectorOption)entry.getValue()).description);
			}
		}
	}

	public interface SelectorHandler {
		void handle(EntitySelectorReader reader) throws CommandSyntaxException;
	}

	static record SelectorOption(EntitySelectorOptions.SelectorHandler handler, Predicate<EntitySelectorReader> condition, Text description) {
		final EntitySelectorOptions.SelectorHandler handler;
		final Predicate<EntitySelectorReader> condition;
		final Text description;
	}
}
