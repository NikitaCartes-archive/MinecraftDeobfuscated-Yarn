package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class LocalDifficulty {
	private final Difficulty globalDifficulty;
	private final float localDifficulty;

	public LocalDifficulty(Difficulty difficulty, long l, long m, float f) {
		this.globalDifficulty = difficulty;
		this.localDifficulty = this.setLocalDifficulty(difficulty, l, m, f);
	}

	public Difficulty getGlobalDifficulty() {
		return this.globalDifficulty;
	}

	public float getLocalDifficulty() {
		return this.localDifficulty;
	}

	public boolean isHarderThan(float f) {
		return this.localDifficulty > f;
	}

	public float getClampedLocalDifficulty() {
		if (this.localDifficulty < 2.0F) {
			return 0.0F;
		} else {
			return this.localDifficulty > 4.0F ? 1.0F : (this.localDifficulty - 2.0F) / 2.0F;
		}
	}

	private float setLocalDifficulty(Difficulty difficulty, long l, long m, float f) {
		if (difficulty == Difficulty.field_5801) {
			return 0.0F;
		} else {
			boolean bl = difficulty == Difficulty.field_5807;
			float g = 0.75F;
			float h = MathHelper.clamp(((float)l + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
			g += h;
			float i = 0.0F;
			i += MathHelper.clamp((float)m / 3600000.0F, 0.0F, 1.0F) * (bl ? 1.0F : 0.75F);
			i += MathHelper.clamp(f * 0.25F, 0.0F, h);
			if (difficulty == Difficulty.field_5805) {
				i *= 0.5F;
			}

			g += i;
			return (float)difficulty.getId() * g;
		}
	}
}
