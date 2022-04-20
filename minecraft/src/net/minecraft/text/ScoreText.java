package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_7417;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class ScoreText implements class_7417 {
	private static final String SENDER_PLACEHOLDER = "*";
	private final String name;
	@Nullable
	private final EntitySelector selector;
	private final String objective;

	@Nullable
	private static EntitySelector parseEntitySelector(String name) {
		try {
			return new EntitySelectorReader(new StringReader(name)).read();
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public ScoreText(String name, String string) {
		this.name = name;
		this.selector = parseEntitySelector(name);
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
		if (this.selector != null) {
			List<? extends Entity> list = this.selector.getEntities(source);
			if (!list.isEmpty()) {
				if (list.size() != 1) {
					throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
				}

				return ((Entity)list.get(0)).getEntityName();
			}
		}

		return this.name;
	}

	private String getScore(String playerName, ServerCommandSource source) {
		MinecraftServer minecraftServer = source.getServer();
		if (minecraftServer != null) {
			Scoreboard scoreboard = minecraftServer.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(this.objective);
			if (scoreboard.playerHasObjective(playerName, scoreboardObjective)) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(playerName, scoreboardObjective);
				return Integer.toString(scoreboardPlayerScore.getScore());
			}
		}

		return "";
	}

	@Override
	public MutableText parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
		if (serverCommandSource == null) {
			return Text.method_43473();
		} else {
			String string = this.getPlayerName(serverCommandSource);
			String string2 = entity != null && string.equals("*") ? entity.getEntityName() : string;
			return Text.method_43470(this.getScore(string2, serverCommandSource));
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof ScoreText scoreText && this.name.equals(scoreText.name) && this.objective.equals(scoreText.objective)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		int i = this.name.hashCode();
		return 31 * i + this.objective.hashCode();
	}

	public String toString() {
		return "score{name='" + this.name + "', objective='" + this.objective + "'}";
	}
}
