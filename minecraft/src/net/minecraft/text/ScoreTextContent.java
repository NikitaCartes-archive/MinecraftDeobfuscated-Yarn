package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public record ScoreTextContent(Either<ParsedSelector, String> name, String objective) implements TextContent {
	public static final MapCodec<ScoreTextContent> INNER_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.either(ParsedSelector.CODEC, Codec.STRING).fieldOf("name").forGetter(ScoreTextContent::name),
					Codec.STRING.fieldOf("objective").forGetter(ScoreTextContent::objective)
				)
				.apply(instance, ScoreTextContent::new)
	);
	public static final MapCodec<ScoreTextContent> CODEC = INNER_CODEC.fieldOf("score");
	public static final TextContent.Type<ScoreTextContent> TYPE = new TextContent.Type<>(CODEC, "score");

	@Override
	public TextContent.Type<?> getType() {
		return TYPE;
	}

	private ScoreHolder getScoreHolder(ServerCommandSource source) throws CommandSyntaxException {
		Optional<ParsedSelector> optional = this.name.left();
		if (optional.isPresent()) {
			List<? extends Entity> list = ((ParsedSelector)optional.get()).comp_3068().getEntities(source);
			if (!list.isEmpty()) {
				if (list.size() != 1) {
					throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
				} else {
					return (ScoreHolder)list.getFirst();
				}
			} else {
				return ScoreHolder.fromName(((ParsedSelector)optional.get()).comp_3067());
			}
		} else {
			return ScoreHolder.fromName((String)this.name.right().orElseThrow());
		}
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

	public String toString() {
		return "score{name='" + this.name + "', objective='" + this.objective + "'}";
	}
}
