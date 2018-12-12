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
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.JsonLikeTagParser;
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

	public static void method_9960() {
		if (options.isEmpty()) {
			putOption("name", entitySelectorReader -> {
				int i = entitySelectorReader.getReader().getCursor();
				boolean bl = entitySelectorReader.method_9892();
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
				NumberRange.Float float_ = NumberRange.Float.method_9049(entitySelectorReader.getReader());
				if ((float_.getMin() == null || !((Float)float_.getMin() < 0.0F)) && (float_.getMax() == null || !((Float)float_.getMax() < 0.0F))) {
					entitySelectorReader.method_9870(float_);
					entitySelectorReader.method_9852();
				} else {
					entitySelectorReader.getReader().setCursor(i);
					throw NEGATIVE_DISTANCE_EXCEPTION.createWithContext(entitySelectorReader.getReader());
				}
			}, entitySelectorReader -> entitySelectorReader.method_9873().isDummy(), new TranslatableTextComponent("argument.entity.options.distance.description"));
			putOption("level", entitySelectorReader -> {
				int i = entitySelectorReader.getReader().getCursor();
				NumberRange.Integer integer = NumberRange.Integer.method_9060(entitySelectorReader.getReader());
				if ((integer.getMin() == null || (Integer)integer.getMin() >= 0) && (integer.getMax() == null || (Integer)integer.getMax() >= 0)) {
					entitySelectorReader.method_9846(integer);
					entitySelectorReader.method_9841(false);
				} else {
					entitySelectorReader.getReader().setCursor(i);
					throw NEGATIVE_LEVEL_EXCEPTION.createWithContext(entitySelectorReader.getReader());
				}
			}, entitySelectorReader -> entitySelectorReader.method_9895().isDummy(), new TranslatableTextComponent("argument.entity.options.level.description"));
			putOption("x", entitySelectorReader -> {
				entitySelectorReader.method_9852();
				entitySelectorReader.method_9850(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.method_9902() == null, new TranslatableTextComponent("argument.entity.options.x.description"));
			putOption("y", entitySelectorReader -> {
				entitySelectorReader.method_9852();
				entitySelectorReader.method_9864(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.method_9884() == null, new TranslatableTextComponent("argument.entity.options.y.description"));
			putOption("z", entitySelectorReader -> {
				entitySelectorReader.method_9852();
				entitySelectorReader.method_9879(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.method_9868() == null, new TranslatableTextComponent("argument.entity.options.z.description"));
			putOption("dx", entitySelectorReader -> {
				entitySelectorReader.method_9852();
				entitySelectorReader.method_9891(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.method_9851() == null, new TranslatableTextComponent("argument.entity.options.dx.description"));
			putOption("dy", entitySelectorReader -> {
				entitySelectorReader.method_9852();
				entitySelectorReader.method_9905(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.method_9840() == null, new TranslatableTextComponent("argument.entity.options.dy.description"));
			putOption("dz", entitySelectorReader -> {
				entitySelectorReader.method_9852();
				entitySelectorReader.method_9918(entitySelectorReader.getReader().readDouble());
			}, entitySelectorReader -> entitySelectorReader.method_9907() == null, new TranslatableTextComponent("argument.entity.options.dz.description"));
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
						entitySelectorReader.method_9900(j);
						entitySelectorReader.method_9877(true);
					}
				},
				entitySelectorReader -> !entitySelectorReader.method_9885() && !entitySelectorReader.method_9866(),
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

					entitySelectorReader.setOrdering(biConsumer);
					entitySelectorReader.method_9887(true);
				},
				entitySelectorReader -> !entitySelectorReader.method_9885() && !entitySelectorReader.method_9889(),
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
				boolean bl = entitySelectorReader.method_9892();
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
						entitySelectorReader.method_9841(false);
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
				boolean bl = entitySelectorReader.method_9892();
				String string = entitySelectorReader.getReader().readUnquotedString();
				entitySelectorReader.setPredicate(entity -> {
					if (!(entity instanceof LivingEntity)) {
						return false;
					} else {
						AbstractScoreboardTeam abstractScoreboardTeam = entity.getScoreboardTeam();
						String string2 = abstractScoreboardTeam == null ? "" : abstractScoreboardTeam.getName();
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
					CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.keys(), suggestionsBuilder, String.valueOf('!'));
					CommandSource.suggestIdentifiers(EntityTags.getContainer().getKeys(), suggestionsBuilder, "!#");
					if (!entitySelectorReader.method_9910()) {
						CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.keys(), suggestionsBuilder);
						CommandSource.suggestIdentifiers(EntityTags.getContainer().getKeys(), suggestionsBuilder, String.valueOf('#'));
					}

					return suggestionsBuilder.buildFuture();
				});
				int i = entitySelectorReader.getReader().getCursor();
				boolean bl = entitySelectorReader.method_9892();
				if (entitySelectorReader.method_9910() && !bl) {
					entitySelectorReader.getReader().setCursor(i);
					throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), "type");
				} else {
					if (bl) {
						entitySelectorReader.method_9860();
					}

					if (entitySelectorReader.method_9915()) {
						Identifier identifier = Identifier.parse(entitySelectorReader.getReader());
						Tag<EntityType<?>> tag = EntityTags.getContainer().get(identifier);
						if (tag == null) {
							entitySelectorReader.getReader().setCursor(i);
							throw INVALID_TYPE_EXCEPTION.createWithContext(entitySelectorReader.getReader(), identifier.toString());
						}

						entitySelectorReader.setPredicate(entity -> tag.contains(entity.getType()) != bl);
					} else {
						Identifier identifier = Identifier.parse(entitySelectorReader.getReader());
						EntityType<? extends Entity> entityType = (EntityType<? extends Entity>)Registry.ENTITY_TYPE.get(identifier);
						if (entityType == null) {
							entitySelectorReader.getReader().setCursor(i);
							throw INVALID_TYPE_EXCEPTION.createWithContext(entitySelectorReader.getReader(), identifier.toString());
						}

						if (Objects.equals(EntityType.PLAYER, entityType) && !bl) {
							entitySelectorReader.method_9841(false);
						}

						entitySelectorReader.setPredicate(entity -> Objects.equals(entityType, entity.getType()) != bl);
						if (!bl) {
							entitySelectorReader.setEntityType(entityType.getEntityClass());
						}
					}
				}
			}, entitySelectorReader -> !entitySelectorReader.hasEntityType(), new TranslatableTextComponent("argument.entity.options.type.description"));
			putOption(
				"tag",
				entitySelectorReader -> {
					boolean bl = entitySelectorReader.method_9892();
					String string = entitySelectorReader.getReader().readUnquotedString();
					entitySelectorReader.setPredicate(
						entity -> "".equals(string) ? entity.getScoreboardTags().isEmpty() != bl : entity.getScoreboardTags().contains(string) != bl
					);
				},
				entitySelectorReader -> true,
				new TranslatableTextComponent("argument.entity.options.tag.description")
			);
			putOption("nbt", entitySelectorReader -> {
				boolean bl = entitySelectorReader.method_9892();
				CompoundTag compoundTag = new JsonLikeTagParser(entitySelectorReader.getReader()).parseCompoundTag();
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
				Map<String, NumberRange.Integer> map = Maps.<String, NumberRange.Integer>newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while (stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					String string = stringReader.readUnquotedString();
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					NumberRange.Integer integer = NumberRange.Integer.method_9060(stringReader);
					map.put(string, integer);
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

						for (Entry<String, NumberRange.Integer> entry : map.entrySet()) {
							ScoreboardObjective scoreboardObjective = scoreboard.method_1170((String)entry.getKey());
							if (scoreboardObjective == null) {
								return false;
							}

							if (!scoreboard.playerHasObjective(stringx, scoreboardObjective)) {
								return false;
							}

							ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(stringx, scoreboardObjective);
							int i = scoreboardPlayerScore.getScore();
							if (!((NumberRange.Integer)entry.getValue()).test(i)) {
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
							ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().method_3851();

							for (Entry<Identifier, Predicate<AdvancementProgress>> entry : map.entrySet()) {
								SimpleAdvancement simpleAdvancement = serverAdvancementLoader.get((Identifier)entry.getKey());
								if (simpleAdvancement == null || !((Predicate)entry.getValue()).test(playerAdvancementTracker.getProgress(simpleAdvancement))) {
									return false;
								}
							}

							return true;
						}
					});
					entitySelectorReader.method_9841(false);
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
