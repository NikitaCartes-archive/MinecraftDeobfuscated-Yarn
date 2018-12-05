package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class LocalDifficulty {
	private final Difficulty field_5798;
	private final float field_5799;

	public LocalDifficulty(Difficulty difficulty, long l, long m, float f) {
		this.field_5798 = difficulty;
		this.field_5799 = this.method_5456(difficulty, l, m, f);
	}

	public Difficulty method_5454() {
		return this.field_5798;
	}

	public float method_5457() {
		return this.field_5799;
	}

	public boolean method_5455(float f) {
		return this.field_5799 > f;
	}

	public float method_5458() {
		if (this.field_5799 < 2.0F) {
			return 0.0F;
		} else {
			return this.field_5799 > 4.0F ? 1.0F : (this.field_5799 - 2.0F) / 2.0F;
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
