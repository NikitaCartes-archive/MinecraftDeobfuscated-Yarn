/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.jetbrains.annotations.Nullable;

public class ScoreTextContent
implements TextContent {
    private static final String SENDER_PLACEHOLDER = "*";
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

    public ScoreTextContent(String name, String objective) {
        this.name = name;
        this.selector = ScoreTextContent.parseEntitySelector(name);
        this.objective = objective;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
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
        MinecraftServer minecraftServer = source.getServer();
        if (minecraftServer != null && (scoreboard = minecraftServer.getScoreboard()).playerHasObjective(playerName, scoreboardObjective = scoreboard.getNullableObjective(this.objective))) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(playerName, scoreboardObjective);
            return Integer.toString(scoreboardPlayerScore.getScore());
        }
        return "";
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null) {
            return Text.empty();
        }
        String string = this.getPlayerName(source);
        String string2 = sender != null && string.equals(SENDER_PLACEHOLDER) ? sender.getEntityName() : string;
        return Text.literal(this.getScore(string2, source));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ScoreTextContent)) return false;
        ScoreTextContent scoreTextContent = (ScoreTextContent)object;
        if (!this.name.equals(scoreTextContent.name)) return false;
        if (!this.objective.equals(scoreTextContent.objective)) return false;
        return true;
    }

    public int hashCode() {
        int i = this.name.hashCode();
        i = 31 * i + this.objective.hashCode();
        return i;
    }

    public String toString() {
        return "score{name='" + this.name + "', objective='" + this.objective + "'}";
    }
}

