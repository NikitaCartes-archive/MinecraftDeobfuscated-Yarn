/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import org.jetbrains.annotations.Nullable;

public class ScoreText
extends BaseText
implements ParsableText {
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;

    @Nullable
    private static EntitySelector parseEntitySelector(String name) {
        try {
            return new EntitySelectorReader(new StringReader(name)).read();
        } catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public ScoreText(String name, String objective) {
        this(name, ScoreText.parseEntitySelector(name), objective);
    }

    private ScoreText(String name, @Nullable EntitySelector selector, String objective) {
        this.name = name;
        this.selector = selector;
        this.objective = objective;
    }

    public String getName() {
        return this.name;
    }

    public String getObjective() {
        return this.objective;
    }

    private String getPlayerName(ServerCommandSource source) throws CommandSyntaxException {
        List<? extends Entity> list;
        if (this.selector != null && !(list = this.selector.getEntities(source)).isEmpty()) {
            if (list.size() != 1) {
                throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
            }
            return list.get(0).getEntityName();
        }
        return this.name;
    }

    private String getScore(String playerName, ServerCommandSource source) {
        ScoreboardObjective scoreboardObjective;
        ServerScoreboard scoreboard;
        MinecraftServer minecraftServer = source.getMinecraftServer();
        if (minecraftServer != null && (scoreboard = minecraftServer.getScoreboard()).playerHasObjective(playerName, scoreboardObjective = scoreboard.getNullableObjective(this.objective))) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(playerName, scoreboardObjective);
            return Integer.toString(scoreboardPlayerScore.getScore());
        }
        return "";
    }

    @Override
    public ScoreText copy() {
        return new ScoreText(this.name, this.selector, this.objective);
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null) {
            return new LiteralText("");
        }
        String string = this.getPlayerName(source);
        String string2 = sender != null && string.equals("*") ? sender.getEntityName() : string;
        return new LiteralText(this.getScore(string2, source));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ScoreText) {
            ScoreText scoreText = (ScoreText)object;
            return this.name.equals(scoreText.name) && this.objective.equals(scoreText.objective) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    @Override
    public /* synthetic */ BaseText copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ MutableText copy() {
        return this.copy();
    }
}

