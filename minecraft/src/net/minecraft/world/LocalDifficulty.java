package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class LocalDifficulty {
	private final Difficulty globalDifficulty;
	private final float localDifficulty;

	public LocalDifficulty(Difficulty difficulty, long l, long m, float f) {
		this.globalDifficulty = difficulty;
		this.localDifficulty = this.method_5456(difficulty, l, m, f);
	}

	public Difficulty method_5454() {
		return this.globalDifficulty;
	}

	public float getLocalDifficulty() {
		return this.localDifficulty;
	}

	public boolean method_5455(float f) {
		return this.localDifficulty > f;
	}

	public float getClampedLocalDifficulty() {
		if (this.localDifficulty < 2.0F) {
			return 0.0F;
		} else {
			return this.localDifficulty > 4.0F ? 1.0F : (this.localDifficulty - 2.0F) / 2.0F;
		}
	}

	private float method_5456(Difficulty difficulty, long l, long m, float f) {
		if (difficulty == Difficulty.PEACEFUL) {
			return 0.0F;
		} else {
			boolean bl = difficulty == Difficulty.HARD;
			float g = 0.75F;
			float h = MathHelper.clamp(((float)l + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
			g += h;
			float i = 0.0F;
			i += MathHelper.clamp((float)m / 3600000.0F, 0.0F, 1.0F) * (bl ? 1.0F : 0.75F);
			i += MathHelper.clamp(f * 0.25F, 0.0F, h);
			if (difficulty == Difficulty.EASY) {
				i *= 0.5F;
			}

			g += i;
			return (float)difficulty.getId() * g;
		}
	}
}
