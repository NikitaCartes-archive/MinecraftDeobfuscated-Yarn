package net.minecraft.scoreboard;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.MutableText;

public interface ReadableScoreboardScore {
	int getScore();

	boolean isLocked();

	@Nullable
	NumberFormat getNumberFormat();

	default MutableText getFormattedScore(NumberFormat fallbackFormat) {
		return ((NumberFormat)Objects.requireNonNullElse(this.getNumberFormat(), fallbackFormat)).format(this.getScore());
	}

	static MutableText getFormattedScore(@Nullable ReadableScoreboardScore score, NumberFormat fallbackFormat) {
		return score != null ? score.getFormattedScore(fallbackFormat) : fallbackFormat.format(0);
	}
}
