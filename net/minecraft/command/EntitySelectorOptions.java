/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.FloatRangeArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.NumberRange;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class EntitySelectorOptions {
    private static final Map<String, SelectorOption> options = Maps.newHashMap();
    public static final DynamicCommandExceptionType UNKNOWN_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.unknown", object));
    public static final DynamicCommandExceptionType INAPPLICABLE_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.inapplicable", object));
    public static final SimpleCommandExceptionType NEGATIVE_DISTANCE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.entity.options.distance.negative"));
    public static final SimpleCommandExceptionType NEGATIVE_LEVEL_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.entity.options.level.negative"));
    public static final SimpleCommandExceptionType TOO_SMALL_LEVEL_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.entity.options.limit.toosmall"));
    public static final DynamicCommandExceptionType IRREVERSIBLE_SORT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.sort.irreversible", object));
    public static final DynamicCommandExceptionType INVALID_MODE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.mode.invalid", object));
    public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.type.invalid", object));

    private static void putOption(String id, SelectorHandler handler, Predicate<EntitySelectorReader> condition, Text description) {
        options.put(id, new SelectorOption(handler, condition, description));
    }

    public static void register() {
        if (!options.isEmpty()) {
            return;
        }
        EntitySelectorOptions.putOption("name", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            boolean bl = entitySelectorReader.readNegationCharacter();
            String string = entitySelectorReader.getReader().readString();
            if (entitySelectorReader.excludesName() && !bl) {
                entitySelectorReader.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), "name");
            }
            if (bl) {
                entitySelectorReader.setExcludesName(true);
            } else {
                entitySelectorReader.setSelectsName(true);
            }
            entitySelectorReader.setPredicate(entity -> entity.getName().getString().equals(string) != bl);
        }, entitySelectorReader -> !entitySelectorReader.selectsName(), new TranslatableText("argument.entity.options.name.description"));
        EntitySelectorOptions.putOption("distance", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            NumberRange.FloatRange floatRange = NumberRange.FloatRange.parse(entitySelectorReader.getReader());
            if (floatRange.getMin() != null && ((Float)floatRange.getMin()).floatValue() < 0.0f || floatRange.getMax() != null && ((Float)floatRange.getMax()).floatValue() < 0.0f) {
                entitySelectorReader.getReader().setCursor(i);
                throw NEGATIVE_DISTANCE_EXCEPTION.createWithContext(entitySelectorReader.getReader());
            }
            entitySelectorReader.setDistance(floatRange);
            entitySelectorReader.setLocalWorldOnly();
        }, entitySelectorReader -> entitySelectorReader.getDistance().isDummy(), new TranslatableText("argument.entity.options.distance.description"));
        EntitySelectorOptions.putOption("level", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            NumberRange.IntRange intRange = NumberRange.IntRange.parse(entitySelectorReader.getReader());
            if (intRange.getMin() != null && (Integer)intRange.getMin() < 0 || intRange.getMax() != null && (Integer)intRange.getMax() < 0) {
                entitySelectorReader.getReader().setCursor(i);
                throw NEGATIVE_LEVEL_EXCEPTION.createWithContext(entitySelectorReader.getReader());
            }
            entitySelectorReader.setLevelRange(intRange);
            entitySelectorReader.setIncludesNonPlayers(false);
        }, entitySelectorReader -> entitySelectorReader.getLevelRange().isDummy(), new TranslatableText("argument.entity.options.level.description"));
        EntitySelectorOptions.putOption("x", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setX(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getX() == null, new TranslatableText("argument.entity.options.x.description"));
        EntitySelectorOptions.putOption("y", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setY(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getY() == null, new TranslatableText("argument.entity.options.y.description"));
        EntitySelectorOptions.putOption("z", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setZ(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getZ() == null, new TranslatableText("argument.entity.options.z.description"));
        EntitySelectorOptions.putOption("dx", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setDx(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getDx() == null, new TranslatableText("argument.entity.options.dx.description"));
        EntitySelectorOptions.putOption("dy", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setDy(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getDy() == null, new TranslatableText("argument.entity.options.dy.description"));
        EntitySelectorOptions.putOption("dz", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setDz(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getDz() == null, new TranslatableText("argument.entity.options.dz.description"));
        EntitySelectorOptions.putOption("x_rotation", entitySelectorReader -> entitySelectorReader.setPitchRange(FloatRangeArgument.parse(entitySelectorReader.getReader(), true, MathHelper::wrapDegrees)), entitySelectorReader -> entitySelectorReader.getPitchRange() == FloatRangeArgument.ANY, new TranslatableText("argument.entity.options.x_rotation.description"));
        EntitySelectorOptions.putOption("y_rotation", entitySelectorReader -> entitySelectorReader.setYawRange(FloatRangeArgument.parse(entitySelectorReader.getReader(), true, MathHelper::wrapDegrees)), entitySelectorReader -> entitySelectorReader.getYawRange() == FloatRangeArgument.ANY, new TranslatableText("argument.entity.options.y_rotation.description"));
        EntitySelectorOptions.putOption("limit", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            int j = entitySelectorReader.getReader().readInt();
            if (j < 1) {
                entitySelectorReader.getReader().setCursor(i);
                throw TOO_SMALL_LEVEL_EXCEPTION.createWithContext(entitySelectorReader.getReader());
            }
            entitySelectorReader.setLimit(j);
            entitySelectorReader.setHasLimit(true);
        }, entitySelectorReader -> !entitySelectorReader.isSenderOnly() && !entitySelectorReader.hasLimit(), new TranslatableText("argument.entity.options.limit.description"));
        EntitySelectorOptions.putOption("sort", entitySelectorReader -> {
            BiConsumer<Vec3d, List<? extends Entity>> biConsumer;
            int i = entitySelectorReader.getReader().getCursor();
            String string = entitySelectorReader.getReader().readUnquotedString();
            entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> CommandSource.suggestMatching(Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsBuilder));
            switch (string) {
                case "nearest": {
                    biConsumer = EntitySelectorReader.NEAREST;
                    break;
                }
                case "furthest": {
                    biConsumer = EntitySelectorReader.FURTHEST;
                    break;
                }
                case "random": {
                    biConsumer = EntitySelectorReader.RANDOM;
                    break;
                }
                case "arbitrary": {
                    biConsumer = EntitySelectorReader.ARBITRARY;
                    break;
                }
                default: {
                    entitySelectorReader.getReader().setCursor(i);
                    throw IRREVERSIBLE_SORT_EXCEPTION.createWithContext(entitySelectorReader.getReader(), string);
                }
            }
            entitySelectorReader.setSorter(biConsumer);
            entitySelectorReader.setHasSorter(true);
        }, entitySelectorReader -> !entitySelectorReader.isSenderOnly() && !entitySelectorReader.hasSorter(), new TranslatableText("argument.entity.options.sort.description"));
        EntitySelectorOptions.putOption("gamemode", entitySelectorReader -> {
            entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> {
                String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
                boolean bl = !entitySelectorReader.excludesGameMode();
                boolean bl2 = true;
                if (!string.isEmpty()) {
                    if (string.charAt(0) == '!') {
                        bl = false;
                        string = string.substring(1);
                    } else {
                        bl2 = false;
                    }
                }
                for (GameMode gameMode : GameMode.values()) {
                    if (gameMode == GameMode.NOT_SET || !gameMode.getName().toLowerCase(Locale.ROOT).startsWith(string)) continue;
                    if (bl2) {
                        suggestionsBuilder.suggest('!' + gameMode.getName());
                    }
                    if (!bl) continue;
                    suggestionsBuilder.suggest(gameMode.getName());
                }
                return suggestionsBuilder.buildFuture();
            });
            int i = entitySelectorReader.getReader().getCursor();
            boolean bl = entitySelectorReader.readNegationCharacter();
            if (entitySelectorReader.excludesGameMode() && !bl) {
                entitySelectorReader.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), "gamemode");
            }
            String string = entitySelectorReader.getReader().readUnquotedString();
            GameMode gameMode = GameMode.byName(string, GameMode.NOT_SET);
            if (gameMode == GameMode.NOT_SET) {
                entitySelectorReader.getReader().setCursor(i);
                throw INVALID_MODE_EXCEPTION.createWithContext(entitySelectorReader.getReader(), string);
            }
            entitySelectorReader.setIncludesNonPlayers(false);
            entitySelectorReader.setPredicate(entity -> {
                if (!(entity instanceof ServerPlayerEntity)) {
                    return false;
                }
                GameMode gameMode2 = ((ServerPlayerEntity)entity).interactionManager.getGameMode();
                return bl ? gameMode2 != gameMode : gameMode2 == gameMode;
            });
            if (bl) {
                entitySelectorReader.setHasNegatedGameMode(true);
            } else {
                entitySelectorReader.setSelectsGameMode(true);
            }
        }, entitySelectorReader -> !entitySelectorReader.selectsGameMode(), new TranslatableText("argument.entity.options.gamemode.description"));
        EntitySelectorOptions.putOption("team", entitySelectorReader -> {
            boolean bl = entitySelectorReader.readNegationCharacter();
            String string = entitySelectorReader.getReader().readUnquotedString();
            entitySelectorReader.setPredicate(entity -> {
                if (!(entity instanceof LivingEntity)) {
                    return false;
                }
                AbstractTeam abstractTeam = entity.getScoreboardTeam();
                String string2 = abstractTeam == null ? "" : abstractTeam.getName();
                return string2.equals(string) != bl;
            });
            if (bl) {
                entitySelectorReader.setExcludesTeam(true);
            } else {
                entitySelectorReader.setSelectsTeam(true);
            }
        }, entitySelectorReader -> !entitySelectorReader.selectsTeam(), new TranslatableText("argument.entity.options.team.description"));
        EntitySelectorOptions.putOption("type", entitySelectorReader -> {
            entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> {
                CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder, String.valueOf('!'));
                CommandSource.suggestIdentifiers(EntityTypeTags.getTagGroup().getTagIds(), suggestionsBuilder, "!#");
                if (!entitySelectorReader.excludesEntityType()) {
                    CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder);
                    CommandSource.suggestIdentifiers(EntityTypeTags.getTagGroup().getTagIds(), suggestionsBuilder, String.valueOf('#'));
                }
                return suggestionsBuilder.buildFuture();
            });
            int i = entitySelectorReader.getReader().getCursor();
            boolean bl = entitySelectorReader.readNegationCharacter();
            if (entitySelectorReader.excludesEntityType() && !bl) {
                entitySelectorReader.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(entitySelectorReader.getReader(), "type");
            }
            if (bl) {
                entitySelectorReader.setExcludesEntityType();
            }
            if (entitySelectorReader.readTagCharacter()) {
                Identifier identifier = Identifier.fromCommandInput(entitySelectorReader.getReader());
                entitySelectorReader.setPredicate(entity -> entity.getServer().getTagManager().getEntityTypes().getTagOrEmpty(identifier).contains(entity.getType()) != bl);
            } else {
                Identifier identifier = Identifier.fromCommandInput(entitySelectorReader.getReader());
                EntityType<?> entityType = Registry.ENTITY_TYPE.getOrEmpty(identifier).orElseThrow(() -> {
                    entitySelectorReader.getReader().setCursor(i);
                    return INVALID_TYPE_EXCEPTION.createWithContext(entitySelectorReader.getReader(), identifier.toString());
                });
                if (Objects.equals(EntityType.PLAYER, entityType) && !bl) {
                    entitySelectorReader.setIncludesNonPlayers(false);
                }
                entitySelectorReader.setPredicate(entity -> Objects.equals(entityType, entity.getType()) != bl);
                if (!bl) {
                    entitySelectorReader.setEntityType(entityType);
                }
            }
        }, entitySelectorReader -> !entitySelectorReader.selectsEntityType(), new TranslatableText("argument.entity.options.type.description"));
        EntitySelectorOptions.putOption("tag", entitySelectorReader -> {
            boolean bl = entitySelectorReader.readNegationCharacter();
            String string = entitySelectorReader.getReader().readUnquotedString();
            entitySelectorReader.setPredicate(entity -> {
                if ("".equals(string)) {
                    return entity.getScoreboardTags().isEmpty() != bl;
                }
                return entity.getScoreboardTags().contains(string) != bl;
            });
        }, entitySelectorReader -> true, new TranslatableText("argument.entity.options.tag.description"));
        EntitySelectorOptions.putOption("nbt", entitySelectorReader -> {
            boolean bl = entitySelectorReader.readNegationCharacter();
            CompoundTag compoundTag = new StringNbtReader(entitySelectorReader.getReader()).parseCompoundTag();
            entitySelectorReader.setPredicate(entity -> {
                ItemStack itemStack;
                CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
                if (entity instanceof ServerPlayerEntity && !(itemStack = ((ServerPlayerEntity)entity).inventory.getMainHandStack()).isEmpty()) {
                    compoundTag2.put("SelectedItem", itemStack.toTag(new CompoundTag()));
                }
                return NbtHelper.matches(compoundTag, compoundTag2, true) != bl;
            });
        }, entitySelectorReader -> true, new TranslatableText("argument.entity.options.nbt.description"));
        EntitySelectorOptions.putOption("scores", entitySelectorReader -> {
            StringReader stringReader = entitySelectorReader.getReader();
            HashMap<String, NumberRange.IntRange> map = Maps.newHashMap();
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
                if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                stringReader.skip();
            }
            stringReader.expect('}');
            if (!map.isEmpty()) {
                entitySelectorReader.setPredicate(entity -> {
                    ServerScoreboard scoreboard = entity.getServer().getScoreboard();
                    String string = entity.getEntityName();
                    for (Map.Entry entry : map.entrySet()) {
                        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective((String)entry.getKey());
                        if (scoreboardObjective == null) {
                            return false;
                        }
                        if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
                            return false;
                        }
                        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
                        int i = scoreboardPlayerScore.getScore();
                        if (((NumberRange.IntRange)entry.getValue()).test(i)) continue;
                        return false;
                    }
                    return true;
                });
            }
            entitySelectorReader.setSelectsScores(true);
        }, entitySelectorReader -> !entitySelectorReader.selectsScores(), new TranslatableText("argument.entity.options.scores.description"));
        EntitySelectorOptions.putOption("advancements", entitySelectorReader -> {
            StringReader stringReader = entitySelectorReader.getReader();
            HashMap<Identifier, Predicate<AdvancementProgress>> map = Maps.newHashMap();
            stringReader.expect('{');
            stringReader.skipWhitespace();
            while (stringReader.canRead() && stringReader.peek() != '}') {
                stringReader.skipWhitespace();
                Identifier identifier = Identifier.fromCommandInput(stringReader);
                stringReader.skipWhitespace();
                stringReader.expect('=');
                stringReader.skipWhitespace();
                if (stringReader.canRead() && stringReader.peek() == '{') {
                    HashMap<String, Predicate<CriterionProgress>> map2 = Maps.newHashMap();
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
                        map2.put(string, criterionProgress -> criterionProgress.isObtained() == bl);
                        stringReader.skipWhitespace();
                        if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                        stringReader.skip();
                    }
                    stringReader.skipWhitespace();
                    stringReader.expect('}');
                    stringReader.skipWhitespace();
                    map.put(identifier, advancementProgress -> {
                        for (Map.Entry entry : map2.entrySet()) {
                            CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
                            if (criterionProgress != null && ((Predicate)entry.getValue()).test(criterionProgress)) continue;
                            return false;
                        }
                        return true;
                    });
                } else {
                    boolean bl2 = stringReader.readBoolean();
                    map.put(identifier, advancementProgress -> advancementProgress.isDone() == bl2);
                }
                stringReader.skipWhitespace();
                if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                stringReader.skip();
            }
            stringReader.expect('}');
            if (!map.isEmpty()) {
                entitySelectorReader.setPredicate(entity -> {
                    if (!(entity instanceof ServerPlayerEntity)) {
                        return false;
                    }
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                    PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
                    ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();
                    for (Map.Entry entry : map.entrySet()) {
                        Advancement advancement = serverAdvancementLoader.get((Identifier)entry.getKey());
                        if (advancement != null && ((Predicate)entry.getValue()).test(playerAdvancementTracker.getProgress(advancement))) continue;
                        return false;
                    }
                    return true;
                });
                entitySelectorReader.setIncludesNonPlayers(false);
            }
            entitySelectorReader.setSelectsAdvancements(true);
        }, entitySelectorReader -> !entitySelectorReader.selectsAdvancements(), new TranslatableText("argument.entity.options.advancements.description"));
        EntitySelectorOptions.putOption("predicate", entitySelectorReader -> {
            boolean bl = entitySelectorReader.readNegationCharacter();
            Identifier identifier = Identifier.fromCommandInput(entitySelectorReader.getReader());
            entitySelectorReader.setPredicate(entity -> {
                if (!(entity.world instanceof ServerWorld)) {
                    return false;
                }
                ServerWorld serverWorld = (ServerWorld)entity.world;
                LootCondition lootCondition = serverWorld.getServer().getPredicateManager().get(identifier);
                if (lootCondition == null) {
                    return false;
                }
                LootContext lootContext = new LootContext.Builder(serverWorld).parameter(LootContextParameters.THIS_ENTITY, entity).parameter(LootContextParameters.POSITION, entity.getBlockPos()).build(LootContextTypes.SELECTOR);
                return bl ^ lootCondition.test(lootContext);
            });
        }, entitySelectorReader -> true, new TranslatableText("argument.entity.options.predicate.description"));
    }

    public static SelectorHandler getHandler(EntitySelectorReader reader, String option, int restoreCursor) throws CommandSyntaxException {
        SelectorOption selectorOption = options.get(option);
        if (selectorOption != null) {
            if (selectorOption.condition.test(reader)) {
                return selectorOption.handler;
            }
            throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), option);
        }
        reader.getReader().setCursor(restoreCursor);
        throw UNKNOWN_OPTION_EXCEPTION.createWithContext(reader.getReader(), option);
    }

    public static void suggestOptions(EntitySelectorReader reader, SuggestionsBuilder suggestionBuilder) {
        String string = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (Map.Entry<String, SelectorOption> entry : options.entrySet()) {
            if (!entry.getValue().condition.test(reader) || !entry.getKey().toLowerCase(Locale.ROOT).startsWith(string)) continue;
            suggestionBuilder.suggest(entry.getKey() + '=', (Message)entry.getValue().description);
        }
    }

    static class SelectorOption {
        public final SelectorHandler handler;
        public final Predicate<EntitySelectorReader> condition;
        public final Text description;

        private SelectorOption(SelectorHandler handler, Predicate<EntitySelectorReader> condition, Text description) {
            this.handler = handler;
            this.condition = condition;
            this.description = description;
        }
    }

    public static interface SelectorHandler {
        public void handle(EntitySelectorReader var1) throws CommandSyntaxException;
    }
}

