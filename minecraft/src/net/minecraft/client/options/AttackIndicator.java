package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum AttackIndicator {
	field_18151(0, "options.off"),
	field_18152(1, "options.attack.crosshair"),
	field_18153(2, "options.attack.hotbar");

	private static final AttackIndicator[] VALUES = (AttackIndicator[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(AttackIndicator::getId))
		.toArray(AttackIndicator[]::new);
	private final int id;
	private final String translationKey;

	private AttackIndicator(int j, String string2) {
		this.id = j;
		this.translationKey = string2;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public static AttackIndicator byId(int i) {
		return VALUES[MathHelper.floorMod(i, VALUES.length)];
	}
}
