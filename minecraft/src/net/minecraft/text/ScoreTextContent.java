package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class ScoreTextContent implements TextContent {
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

	public ScoreTextContent(String name, String objective) {
		this.name = name;
		this.selector = parseEntitySelector(name);
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
			if (scoreboardObjective != null && scoreboard.playerHasObjective(playerName, scoreboardObjective)) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(playerName, scoreboardObjective);
				return Integer.toString(scoreboardPlayerScore.getScore());
			}
		}

		return "";
	}

	@Override
	public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (source == null) {
			return Text.empty();
		} else {
			String string = this.getPlayerName(source);
			String string2 = sender != null && string.equals("*") ? sender.getEntityName() : string;
			return Text.literal(this.getScore(string2, source));
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof ScoreTextContent scoreTextContent && this.name.equals(scoreTextContent.name) && this.objective.equals(scoreTextContent.objective)) {
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
