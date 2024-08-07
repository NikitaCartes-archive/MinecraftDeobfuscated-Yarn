package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class ScoreTextContent implements TextContent {
	public static final MapCodec<ScoreTextContent> INNER_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.STRING.fieldOf("name").forGetter(ScoreTextContent::getName), Codec.STRING.fieldOf("objective").forGetter(ScoreTextContent::getObjective)
				)
				.apply(instance, ScoreTextContent::new)
	);
	public static final MapCodec<ScoreTextContent> CODEC = INNER_CODEC.fieldOf("score");
	public static final TextContent.Type<ScoreTextContent> TYPE = new TextContent.Type<>(CODEC, "score");
	private final String name;
	@Nullable
	private final EntitySelector selector;
	private final String objective;

	@Nullable
	private static EntitySelector parseEntitySelector(String name) {
		try {
			return new EntitySelectorReader(new StringReader(name), true).read();
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public ScoreTextContent(String name, String objective) {
		this.name = name;
		this.selector = parseEntitySelector(name);
		this.objective = objective;
	}

	@Override
	public TextContent.Type<?> getType() {
		return TYPE;
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

	private ScoreHolder getScoreHolder(ServerCommandSource source) throws CommandSyntaxException {
		if (this.selector != null) {
			List<? extends Entity> list = this.selector.getEntities(source);
			if (!list.isEmpty()) {
				if (list.size() != 1) {
					throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
				}

				return (ScoreHolder)list.get(0);
			}
		}

		return ScoreHolder.fromName(this.name);
	}

	private MutableText getScore(ScoreHolder scoreHolder, ServerCommandSource source) {
		MinecraftServer minecraftServer = source.getServer();
		if (minecraftServer != null) {
			Scoreboard scoreboard = minecraftServer.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(this.objective);
			if (scoreboardObjective != null) {
				ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(scoreHolder, scoreboardObjective);
				if (readableScoreboardScore != null) {
					return readableScoreboardScore.getFormattedScore(scoreboardObjective.getNumberFormatOr(StyledNumberFormat.EMPTY));
				}
			}
		}

		return Text.empty();
	}

	@Override
	public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (source == null) {
			return Text.empty();
		} else {
			ScoreHolder scoreHolder = this.getScoreHolder(source);
			ScoreHolder scoreHolder2 = (ScoreHolder)(sender != null && scoreHolder.equals(ScoreHolder.WILDCARD) ? sender : scoreHolder);
			return this.getScore(scoreHolder2, source);
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
