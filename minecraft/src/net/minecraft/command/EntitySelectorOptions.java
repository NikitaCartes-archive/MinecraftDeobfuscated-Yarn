package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.EntityTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class EntitySelectorOptions {
	private static final Map<String, EntitySelectorOptions.SelectorOption> options = Maps.<String, EntitySelectorOptions.SelectorOption>newHashMap();
	public static final DynamicCommandExceptionType UNKNOWN_OPTION_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.entity.options.unknown", object)
	);
	public static final DynamicCommandExceptionType INAPPLICABLE_OPTION_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.entity.options.inapplicable", object)
	);
	public static final SimpleCommandExceptionType NEGATIVE_DISTANCE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.options.distance.negative")
	);
	public static final SimpleCommandExceptionType NEGATIVE_LEVEL_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.options.level.negative")
	);
	public static final SimpleCommandExceptionType TOO_SMALL_LEVEL_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.options.limit.toosmall")
	);
	public static final DynamicCommandExceptionType IRREVERSIBLE_SORT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.entity.options.sort.irreversible", object)
	);
	public static final DynamicCommandExceptionType INVALID_MODE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.entity.options.mode.invalid", object)
	);
	public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.entity.options.type.invalid", object)
	);

	private static void putOption(
		String string, EntitySelectorOptions.SelectorHandler selectorHandler, Predicate<EntitySelectorReader> predicate, TextComponent textComponent
	) {
		options.put(string, new EntitySelectorOptions.SelectorOption(selectorHandler, predicate, textComponent));
	}

	public static void register() {
		if (options.isEmpty()) {
			putOption("name", entitySelectorReader -> {
				int i = entitySelectorReader.getReader().getCursor();
				boolean bl = entitySelectorReader.readNegationCharacter();
				String string = entitySelectorReader.getReader().readString();
				if (entitySelectorReader.method_9844() && !bl) {
					entitySelectorReader.getReader().setCursor(i);
					throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), "name");
				} else {
					if (bl) {
						entitySelectorReader.method_9913(true);
					} else {
						entitySelectorReader.method_9899(true);
					}

					entitySelectorReader.setPredicate(entity -> entity.getName().getText().equals(string) != bl);
				}
			}, entitySelectorReader -> !entitySelectorReader.method_9912(), new TranslatableTextComponent("argument.entity.options.name.description"));
			putOption("distance", entitySelectorReader -> {
				int i = entitySelectorReader.getReader().getCursor();
				NumberRange.FloatRange floatRange = NumberRange.FloatRange.parse(entitySelectorReader.getReader());
				if ((floatRange.getMin() == null || !((Float)floatRange.getMin() < 0.0F)) && (floatRange.getMax() == null || !((Float)floatRange.getMax() < 0.0F))) {
					entitySelectorReader.setDistance(floatRange);
					entitySelectorReader.setLocalWorldOnly();
				} else {
					entitySelectorReader.getReader().setCursor(i);
					throw NEGATIVE_DISTANCE_EXCEPTION.createWithContext(entitySelectorReader.getReader());
				}
			}, entitySelectorReader -> entitySelectorReader.getDistance().isDummy(), new TranslatableTextComponent("argument.entity.options.distance.description"));
			putOption("level", entitySelectorReader -> {
				int i = entitySelectorReader.getReader().getCursor();
				NumberRange.IntRange intRange = NumberRange.IntRange.parse(entitySelectorReader.getReader());
				if ((intRange.getMin() == null || (Integer)intRange.getMin() >= 0) && (intRange.getMax() == null || (Integer)intRange.getMax() >= 0)) {
					entitySelectorReader.setExperienceRange(intRange);
					entitySelectorReader.setIncludingNonPlayer(false);
				} else {
					entitySelectorReader.getReader().setCursor(i);
					throw NEGATIVE_LEVEL_EXCEPTION.createWithContext(entitySelectorReader.getReader());
				}
			}, entitySelectorReader -> entitySelectorReader.getExperienceRange().isDummy(), new TranslatableTextComponent("argument.entity.options.level.description"));
			putOption("x", entitySelectorReader -> {
				entitySelectorReader.setLocalWorldOnly();
				entitySelectorReader.setOffsetX(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.getOffsetX() == null, new TranslatableTextComponent("argument.entity.options.x.description"));
			putOption("y", entitySelectorReader -> {
				entitySelectorReader.setLocalWorldOnly();
				entitySelectorReader.setOffsetY(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.getOffsetY() == null, new TranslatableTextComponent("argument.entity.options.y.description"));
			putOption("z", entitySelectorReader -> {
				entitySelectorReader.setLocalWorldOnly();
				entitySelectorReader.setOffsetZ(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.getOffsetZ() == null, new TranslatableTextComponent("argument.entity.options.z.description"));
			putOption("dx", entitySelectorReader -> {
				entitySelectorReader.setLocalWorldOnly();
				entitySelectorReader.setBoxX(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.getBoxX() == null, new TranslatableTextComponent("argument.entity.options.dx.description"));
			putOption("dy", entitySelectorReader -> {
				entitySelectorReader.setLocalWorldOnly();
				entitySelectorReader.setBoxY(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.getBoxY() == null, new TranslatableTextComponent("argument.entity.options.dy.description"));
			putOption("dz", entitySelectorReader -> {
				entitySelectorReader.setLocalWorldOnly();
				entitySelectorReader.setBoxZ(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.getBoxZ() == null, new TranslatableTextComponent("argument.entity.options.dz.description"));
			putOption(
				"x_rotation",
				entitySelectorReader -> entitySelectorReader.setPitchRange(FloatRange.parse(entitySelectorReader.getReader(), true, MathHelper::wrapDegrees)),
				entitySelectorReader -> entitySelectorReader.getPitchRange() == FloatRange.ANY,
				new TranslatableTextComponent("argument.entity.options.x_rotation.description")
			);
			putOption(
				"y_rotation",
				entitySelectorReader -> entitySelectorReader.setYawRange(FloatRange.parse(entitySelectorReader.getReader(), true, MathHelper::wrapDegrees)),
				entitySelectorReader -> entitySelectorReader.getYawRange() == FloatRange.ANY,
				new TranslatableTextComponent("argument.entity.options.y_rotation.description")
			);
			putOption(
				"limit",
				entitySelectorReader -> {
					int i = entitySelectorReader.getReader().getCursor();
					int j = entitySelectorReader.getReader().readInt();
					if (j < 1) {
						entitySelectorReader.getReader().setCursor(i);
						throw TOO_SMALL_LEVEL_EXCEPTION.createWithContext(entitySelectorReader.getReader());
					} else {
						entitySelectorReader.setCount(j);
						entitySelectorReader.method_9877(true);
					}
				},
				entitySelectorReader -> !entitySelectorReader.isSenderOnly() && !entitySelectorReader.method_9866(),
				new TranslatableTextComponent("argument.entity.options.limit.description")
			);
			putOption(
				"sort",
				entitySelectorReader -> {
					int i = entitySelectorReader.getReader().getCursor();
					String string = entitySelectorReader.getReader().readUnquotedString();
					entitySelectorReader.setSuggestionProvider(
						(suggestionsBuilder, consumer) -> CommandSource.suggestMatching(Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsBuilder)
					);
					BiConsumer<Vec3d, List<? extends Entity>> biConsumer;
					switch (string) {
						case "nearest":
							biConsumer = EntitySelectorReader.NEAREST_FIRST;
							break;
						case "furthest":
							biConsumer = EntitySelectorReader.FURTHEST_FIRST;
							break;
						case "random":
							biConsumer = EntitySelectorReader.RANDOM;
							break;
						case "arbitrary":
							biConsumer = EntitySelectorReader.UNSORTED;
							break;
						default:
							entitySelectorReader.getReader().setCursor(i);
							throw IRREVERSIBLE_SORT_EXCEPTION.createWithContext(entitySelectorReader.getReader(), string);
					}

					entitySelectorReader.setSorter(biConsumer);
					entitySelectorReader.method_9887(true);
				},
				entitySelectorReader -> !entitySelectorReader.isSenderOnly() && !entitySelectorReader.method_9889(),
				new TranslatableTextComponent("argument.entity.options.sort.description")
			);
			putOption("gamemode", entitySelectorReader -> {
				entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> {
					String stringx = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
					boolean blx = !entitySelectorReader.method_9837();
					boolean bl2 = true;
					if (!stringx.isEmpty()) {
						if (stringx.charAt(0) == '!') {
							blx = false;
							stringx = stringx.substring(1);
						} else {
							bl2 = false;
						}
					}

					for (GameMode gameModex : GameMode.values()) {
						if (gameModex != GameMode.INVALID && gameModex.getName().toLowerCase(Locale.ROOT).startsWith(stringx)) {
							if (bl2) {
								suggestionsBuilder.suggest('!' + gameModex.getName());
							}

							if (blx) {
								suggestionsBuilder.suggest(gameModex.getName());
							}
						}
					}

					return suggestionsBuilder.buildFuture();
				});
				int i = entitySelectorReader.getReader().getCursor();
				boolean bl = entitySelectorReader.readNegationCharacter();
				if (entitySelectorReader.method_9837() && !bl) {
					entitySelectorReader.getReader().setCursor(i);
					throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), "gamemode");
				} else {
					String string = entitySelectorReader.getReader().readUnquotedString();
					GameMode gameMode = GameMode.byName(string, GameMode.INVALID);
					if (gameMode == GameMode.INVALID) {
						entitySelectorReader.getReader().setCursor(i);
						throw INVALID_MODE_EXCEPTION.createWithContext(entitySelectorReader.getReader(), string);
					} else {
						entitySelectorReader.setIncludingNonPlayer(false);
						entitySelectorReader.setPredicate(entity -> {
							if (!(entity instanceof ServerPlayerEntity)) {
								return false;
							} else {
								GameMode gameMode2 = ((ServerPlayerEntity)entity).interactionManager.getGameMode();
								return bl ? gameMode2 != gameMode : gameMode2 == gameMode;
							}
						});
						if (bl) {
							entitySelectorReader.method_9857(true);
						} else {
							entitySelectorReader.method_9890(true);
						}
					}
				}
			}, entitySelectorReader -> !entitySelectorReader.method_9839(), new TranslatableTextComponent("argument.entity.options.gamemode.description"));
			putOption("team", entitySelectorReader -> {
				boolean bl = entitySelectorReader.readNegationCharacter();
				String string = entitySelectorReader.getReader().readUnquotedString();
				entitySelectorReader.setPredicate(entity -> {
					if (!(entity instanceof LivingEntity)) {
						return false;
					} else {
						AbstractTeam abstractTeam = entity.getScoreboardTeam();
						String string2 = abstractTeam == null ? "" : abstractTeam.getName();
						return string2.equals(string) != bl;
					}
				});
				if (bl) {
					entitySelectorReader.method_9833(true);
				} else {
					entitySelectorReader.method_9865(true);
				}
			}, entitySelectorReader -> !entitySelectorReader.method_9904(), new TranslatableTextComponent("argument.entity.options.team.description"));
			putOption("type", entitySelectorReader -> {
				entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> {
					CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder, String.valueOf('!'));
					CommandSource.suggestIdentifiers(EntityTags.getContainer().getKeys(), suggestionsBuilder, "!#");
					if (!entitySelectorReader.method_9910()) {
						CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder);
						CommandSource.suggestIdentifiers(EntityTags.getContainer().getKeys(), suggestionsBuilder, String.valueOf('#'));
					}

					return suggestionsBuilder.buildFuture();
				});
				int i = entitySelectorReader.getReader().getCursor();
				boolean bl = entitySelectorReader.readNegationCharacter();
				if (entitySelectorReader.method_9910() && !bl) {
					entitySelectorReader.getReader().setCursor(i);
					throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), "type");
				} else {
					if (bl) {
						entitySelectorReader.method_9860();
					}

					if (entitySelectorReader.readTagCharacter()) {
						Identifier identifier = Identifier.parse(entitySelectorReader.getReader());
						Tag<EntityType<?>> tag = EntityTags.getContainer().get(identifier);
						if (tag == null) {
							entitySelectorReader.getReader().setCursor(i);
							throw INVALID_TYPE_EXCEPTION.createWithContext(entitySelectorReader.getReader(), identifier.toString());
						}

						entitySelectorReader.setPredicate(entity -> tag.contains(entity.getType()) != bl);
					} else {
						Identifier identifier = Identifier.parse(entitySelectorReader.getReader());
						EntityType<?> entityType = (EntityType<?>)Registry.ENTITY_TYPE.getOrEmpty(identifier).orElseThrow(() -> {
							entitySelectorReader.getReader().setCursor(i);
							return INVALID_TYPE_EXCEPTION.createWithContext(entitySelectorReader.getReader(), identifier.toString());
						});
						if (Objects.equals(EntityType.PLAYER, entityType) && !bl) {
							entitySelectorReader.setIncludingNonPlayer(false);
						}

						entitySelectorReader.setPredicate(entity -> Objects.equals(entityType, entity.getType()) != bl);
						if (!bl) {
							entitySelectorReader.setEntityType(entityType);
						}
					}
				}
			}, entitySelectorReader -> !entitySelectorReader.hasEntityType(), new TranslatableTextComponent("argument.entity.options.type.description"));
			putOption(
				"tag",
				entitySelectorReader -> {
					boolean bl = entitySelectorReader.readNegationCharacter();
					String string = entitySelectorReader.getReader().readUnquotedString();
					entitySelectorReader.setPredicate(
						entity -> "".equals(string) ? entity.getScoreboardTags().isEmpty() != bl : entity.getScoreboardTags().contains(string) != bl
					);
				},
				entitySelectorReader -> true,
				new TranslatableTextComponent("argument.entity.options.tag.description")
			);
			putOption("nbt", entitySelectorReader -> {
				boolean bl = entitySelectorReader.readNegationCharacter();
				CompoundTag compoundTag = new StringNbtReader(entitySelectorReader.getReader()).parseCompoundTag();
				entitySelectorReader.setPredicate(entity -> {
					CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
					if (entity instanceof ServerPlayerEntity) {
						ItemStack itemStack = ((ServerPlayerEntity)entity).inventory.getMainHandStack();
						if (!itemStack.isEmpty()) {
							compoundTag2.put("SelectedItem", itemStack.toTag(new CompoundTag()));
						}
					}

					return TagHelper.areTagsEqual(compoundTag, compoundTag2, true) != bl;
				});
			}, entitySelectorReader -> true, new TranslatableTextComponent("argument.entity.options.nbt.description"));
			putOption("scores", entitySelectorReader -> {
				StringReader stringReader = entitySelectorReader.getReader();
				Map<String, NumberRange.IntRange> map = Maps.<String, NumberRange.IntRange>newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while (stringReader.canRead() && stringReader.peek() != '}') {
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
					entitySelectorReader.setPredicate(entity -> {
						Scoreboard scoreboard = entity.getServer().getScoreboard();
						String stringx = entity.getEntityName();

						for (Entry<String, NumberRange.IntRange> entry : map.entrySet()) {
							ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective((String)entry.getKey());
							if (scoreboardObjective == null) {
								return false;
							}

							if (!scoreboard.playerHasObjective(stringx, scoreboardObjective)) {
								return false;
							}

							ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(stringx, scoreboardObjective);
							int i = scoreboardPlayerScore.getScore();
							if (!((NumberRange.IntRange)entry.getValue()).test(i)) {
								return false;
							}
						}

						return true;
					});
				}

				entitySelectorReader.method_9848(true);
			}, entitySelectorReader -> !entitySelectorReader.method_9843(), new TranslatableTextComponent("argument.entity.options.scores.description"));
			putOption("advancements", entitySelectorReader -> {
				StringReader stringReader = entitySelectorReader.getReader();
				Map<Identifier, Predicate<AdvancementProgress>> map = Maps.<Identifier, Predicate<AdvancementProgress>>newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while (stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					Identifier identifier = Identifier.parse(stringReader);
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == '{') {
						Map<String, Predicate<CriterionProgress>> map2 = Maps.<String, Predicate<CriterionProgress>>newHashMap();
						stringReader.skipWhitespace();
						stringReader.expect('{');
						stringReader.skipWhitespace();

						while (stringReader.canRead() && stringReader.peek() != '}') {
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
							for (Entry<String, Predicate<CriterionProgress>> entry : map2.entrySet()) {
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
					entitySelectorReader.setPredicate(entity -> {
						if (!(entity instanceof ServerPlayerEntity)) {
							return false;
						} else {
							ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
							PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementManager();
							ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementManager();

							for (Entry<Identifier, Predicate<AdvancementProgress>> entry : map.entrySet()) {
								Advancement advancement = serverAdvancementLoader.get((Identifier)entry.getKey());
								if (advancement == null || !((Predicate)entry.getValue()).test(playerAdvancementTracker.getProgress(advancement))) {
									return false;
								}
							}

							return true;
						}
					});
					entitySelectorReader.setIncludingNonPlayer(false);
				}

				entitySelectorReader.method_9906(true);
			}, entitySelectorReader -> !entitySelectorReader.method_9861(), new TranslatableTextComponent("argument.entity.options.advancements.description"));
		}
	}

	public static EntitySelectorOptions.SelectorHandler getHandler(EntitySelectorReader entitySelectorReader, String string, int i) throws CommandSyntaxException {
		EntitySelectorOptions.SelectorOption selectorOption = (EntitySelectorOptions.SelectorOption)options.get(string);
		if (selectorOption != null) {
			if (selectorOption.applicable.test(entitySelectorReader)) {
				return selectorOption.handler;
			} else {
				throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), string);
			}
		} else {
			entitySelectorReader.getReader().setCursor(i);
			throw UNKNOWN_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), string);
		}
	}

	public static void suggestOptions(EntitySelectorReader entitySelectorReader, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (Entry<String, EntitySelectorOptions.SelectorOption> entry : options.entrySet()) {
			if (((EntitySelectorOptions.SelectorOption)entry.getValue()).applicable.test(entitySelectorReader)
				&& ((String)entry.getKey()).toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest((String)entry.getKey() + '=', ((EntitySelectorOptions.SelectorOption)entry.getValue()).description);
			}
		}
	}

	public interface SelectorHandler {
		void handle(EntitySelectorReader entitySelectorReader) throws CommandSyntaxException;
	}

	static class SelectorOption {
		public final EntitySelectorOptions.SelectorHandler handler;
		public final Predicate<EntitySelectorReader> applicable;
		public final TextComponent description;

		private SelectorOption(EntitySelectorOptions.SelectorHandler selectorHandler, Predicate<EntitySelectorReader> predicate, TextComponent textComponent) {
			this.handler = selectorHandler;
			this.applicable = predicate;
			this.description = textComponent;
		}
	}
}
