package net.minecraft.scoreboard;

import javax.annotation.Nullable;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;

public interface ScoreAccess {
	int getScore();

	void setScore(int score);

	default int incrementScore(int amount) {
		int i = this.getScore() + amount;
		this.setScore(i);
		return i;
	}

	default int incrementScore() {
		return this.incrementScore(1);
	}

	default void resetScore() {
		this.setScore(0);
	}

	boolean isLocked();

	void unlock();

	void lock();

	@Nullable
	Text getDisplayText();

	void setDisplayText(@Nullable Text text);

	void setNumberFormat(@Nullable NumberFormat numberFormat);
}
