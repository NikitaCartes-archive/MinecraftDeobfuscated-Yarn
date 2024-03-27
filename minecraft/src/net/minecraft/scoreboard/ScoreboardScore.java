package net.minecraft.scoreboard;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;

public class ScoreboardScore implements ReadableScoreboardScore {
	private static final String SCORE_NBT_KEY = "Score";
	private static final String LOCKED_NBT_KEY = "Locked";
	private static final String DISPLAY_NBT_KEY = "display";
	private static final String FORMAT_NBT_KEY = "format";
	private int score;
	private boolean locked = true;
	@Nullable
	private Text displayText;
	@Nullable
	private NumberFormat numberFormat;

	@Override
	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Nullable
	public Text getDisplayText() {
		return this.displayText;
	}

	public void setDisplayText(@Nullable Text text) {
		this.displayText = text;
	}

	@Nullable
	@Override
	public NumberFormat getNumberFormat() {
		return this.numberFormat;
	}

	public void setNumberFormat(@Nullable NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putInt("Score", this.score);
		nbtCompound.putBoolean("Locked", this.locked);
		if (this.displayText != null) {
			nbtCompound.putString("display", Text.Serialization.toJsonString(this.displayText, registries));
		}

		if (this.numberFormat != null) {
			NumberFormatTypes.CODEC
				.encodeStart(registries.getOps(NbtOps.INSTANCE), this.numberFormat)
				.ifSuccess(formatElement -> nbtCompound.put("format", formatElement));
		}

		return nbtCompound;
	}

	public static ScoreboardScore fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		ScoreboardScore scoreboardScore = new ScoreboardScore();
		scoreboardScore.score = nbt.getInt("Score");
		scoreboardScore.locked = nbt.getBoolean("Locked");
		if (nbt.contains("display", NbtElement.STRING_TYPE)) {
			scoreboardScore.displayText = Text.Serialization.fromJson(nbt.getString("display"), registries);
		}

		if (nbt.contains("format", NbtElement.COMPOUND_TYPE)) {
			NumberFormatTypes.CODEC.parse(registries.getOps(NbtOps.INSTANCE), nbt.get("format")).ifSuccess(format -> scoreboardScore.numberFormat = format);
		}

		return scoreboardScore;
	}
}
