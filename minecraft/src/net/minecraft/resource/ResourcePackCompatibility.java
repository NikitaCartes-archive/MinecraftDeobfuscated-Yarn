package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Range;

public enum ResourcePackCompatibility {
	TOO_OLD("old"),
	TOO_NEW("new"),
	COMPATIBLE("compatible");

	private final Text notification;
	private final Text confirmMessage;

	private ResourcePackCompatibility(final String translationSuffix) {
		this.notification = Text.translatable("pack.incompatible." + translationSuffix).formatted(Formatting.GRAY);
		this.confirmMessage = Text.translatable("pack.incompatible.confirm." + translationSuffix);
	}

	public boolean isCompatible() {
		return this == COMPATIBLE;
	}

	public static ResourcePackCompatibility from(Range<Integer> range, int current) {
		if ((Integer)range.maxInclusive() < current) {
			return TOO_OLD;
		} else {
			return current < range.minInclusive() ? TOO_NEW : COMPATIBLE;
		}
	}

	public Text getNotification() {
		return this.notification;
	}

	public Text getConfirmMessage() {
		return this.confirmMessage;
	}
}
