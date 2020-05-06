package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class LocalDifficulty {
	private final Difficulty globalDifficulty;
	private final float localDifficulty;

	public LocalDifficulty(Difficulty difficulty, long timeOfDay, long inhabitedTime, float moonSize) {
		this.globalDifficulty = difficulty;
		this.localDifficulty = this.setLocalDifficulty(difficulty, timeOfDay, inhabitedTime, moonSize);
	}

	public Difficulty getGlobalDifficulty() {
		return this.globalDifficulty;
	}

	public float getLocalDifficulty() {
		return this.localDifficulty;
	}

	public boolean isHarderThan(float difficulty) {
		return this.localDifficulty > difficulty;
	}

	public float getClampedLocalDifficulty() {
		if (this.localDifficulty < 2.0F) {
			return 0.0F;
		} else {
			return this.localDifficulty > 4.0F ? 1.0F : (this.localDifficulty - 2.0F) / 2.0F;
		}
	}

	private float setLocalDifficulty(Difficulty difficulty, long timeOfDay, long inhabitedTime, float moonSize) {
		if (difficulty == Difficulty.PEACEFUL) {
			return 0.0F;
		} else {
			boolean bl = difficulty == Difficulty.HARD;
			float f = 0.75F;
			float g = MathHelper.clamp(((float)timeOfDay + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
			f += g;
			float h = 0.0F;
			h += MathHelper.clamp((float)inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (bl ? 1.0F : 0.75F);
			h += MathHelper.clamp(moonSize * 0.25F, 0.0F, g);
			if (difficulty == Difficulty.EASY) {
				h *= 0.5F;
			}

			f += h;
			return (float)difficulty.getId() * f;
		}
	}
}
