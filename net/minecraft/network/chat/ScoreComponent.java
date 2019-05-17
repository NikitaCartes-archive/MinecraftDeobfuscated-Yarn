/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentWithSelectors;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.ChatUtil;
import org.jetbrains.annotations.Nullable;

public class ScoreComponent
extends BaseComponent
implements ComponentWithSelectors {
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;
    private String text = "";

    public ScoreComponent(String string, String string2) {
        this.name = string;
        this.objective = string2;
        EntitySelector entitySelector = null;
        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
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

    public void setText(String string) {
        this.text = string;
    }

    @Override
    public String getText() {
        return this.text;
    }

    private void resolve(ServerCommandSource serverCommandSource) {
        MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
        if (minecraftServer != null && minecraftServer.method_3814() && ChatUtil.isEmpty(this.text)) {
            ScoreboardObjective scoreboardObjective;
            ServerScoreboard scoreboard = minecraftServer.getScoreboard();
            if (scoreboard.playerHasObjective(this.name, scoreboardObjective = scoreboard.getNullableObjective(this.objective))) {
                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(this.name, scoreboardObjective);
                this.setText(String.format("%d", scoreboardPlayerScore.getScore()));
            } else {
                this.text = "";
            }
        }
    }

    public ScoreComponent method_10929() {
        ScoreComponent scoreComponent = new ScoreComponent(this.name, this.objective);
        scoreComponent.setText(this.text);
        return scoreComponent;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Component resolve(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
        String string;
        if (serverCommandSource == null) {
            return this.method_10929();
        }
        if (this.selector != null) {
            List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
            if (list.isEmpty()) {
                string = this.name;
            } else {
                if (list.size() != 1) throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
                string = list.get(0).getEntityName();
            }
        } else {
            string = this.name;
        }
        String string2 = entity != null && string.equals("*") ? entity.getEntityName() : string;
        ScoreComponent scoreComponent = new ScoreComponent(string2, this.objective);
        scoreComponent.setText(this.text);
        scoreComponent.resolve(serverCommandSource);
        return scoreComponent;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ScoreComponent) {
            ScoreComponent scoreComponent = (ScoreComponent)object;
            return this.name.equals(scoreComponent.name) && this.objective.equals(scoreComponent.objective) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    @Override
    public /* synthetic */ Component copyShallow() {
        return this.method_10929();
    }
}

