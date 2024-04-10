package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(EnvType.CLIENT)
public enum AttackIndicator implements TranslatableOption {
	OFF(0, "options.off"),
	CROSSHAIR(1, "options.attack.crosshair"),
	HOTBAR(2, "options.attack.hotbar");

	private static final IntFunction<AttackIndicator> BY_ID = ValueLists.createIdToValueFunction(
		AttackIndicator::getId, values(), ValueLists.OutOfBoundsHandling.WRAP
	);
	private final int id;
	private final String translationKey;

	private AttackIndicator(final int id, final String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getTranslationKey() {
		return this.translationKey;
	}

	public static AttackIndicator byId(int id) {
		return (AttackIndicator)BY_ID.apply(id);
	}
}
