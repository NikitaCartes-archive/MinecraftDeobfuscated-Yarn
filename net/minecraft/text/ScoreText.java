/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.class_7417;
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
import org.jetbrains.annotations.Nullable;

public class ScoreText
implements class_7417 {
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

    public ScoreText(String name, String string) {
        this.name = name;
        this.selector = ScoreText.parseEntitySelector(name);
        this.objective = string;
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
    public MutableText parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (serverCommandSource == null) {
            return Text.method_43473();
        }
        String string = this.getPlayerName(serverCommandSource);
        String string2 = entity != null && string.equals(SENDER_PLACEHOLDER) ? entity.getEntityName() : string;
        return Text.method_43470(this.getScore(string2, serverCommandSource));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ScoreText)) return false;
        ScoreText scoreText = (ScoreText)object;
        if (!this.name.equals(scoreText.name)) return false;
        if (!this.objective.equals(scoreText.objective)) return false;
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

