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
import net.minecraft.text.ParsableText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import org.jetbrains.annotations.Nullable;

public class ScoreText
extends BaseText
implements ParsableText {
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;
    private String score = "";

    public ScoreText(String name, String objective) {
        this.name = name;
        this.objective = objective;
        EntitySelector entitySelector = null;
        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(name));
            entitySelector = entitySelectorReader.read();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        this.selector = entitySelector;
    }

    public String getName() {
        return this.name;
    }

    public String getObjective() {
        return this.objective;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String asString() {
        return this.score;
    }

    private void parse(ServerCommandSource source) {
        MinecraftServer minecraftServer = source.getMinecraftServer();
        if (minecraftServer != null && ChatUtil.isEmpty(this.score)) {
            ScoreboardObjective scoreboardObjective;
            ServerScoreboard scoreboard = minecraftServer.getScoreboard();
            if (scoreboard.playerHasObjective(this.name, scoreboardObjective = scoreboard.getNullableObjective(this.objective))) {
                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(this.name, scoreboardObjective);
                this.setScore(String.format("%d", scoreboardPlayerScore.getScore()));
            } else {
                this.score = "";
            }
        }
    }

    @Override
    public ScoreText copy() {
        ScoreText scoreText = new ScoreText(this.name, this.objective);
        scoreText.setScore(this.score);
        return scoreText;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Text parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        String string;
        if (source == null) {
            return this.copy();
        }
        if (this.selector != null) {
            List<? extends Entity> list = this.selector.getEntities(source);
            if (list.isEmpty()) {
                string = this.name;
            } else {
                if (list.size() != 1) throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
                string = list.get(0).getEntityName();
            }
        } else {
            string = this.name;
        }
        String string2 = sender != null && string.equals("*") ? sender.getEntityName() : string;
        ScoreText scoreText = new ScoreText(string2, this.objective);
        scoreText.setScore(this.score);
        scoreText.parse(source);
        return scoreText;
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
    public /* synthetic */ Text copy() {
        return this.copy();
    }
}

