package net.minecraft.util;

public enum TriState {
	TRUE,
	FALSE,
	DEFAULT;

	public boolean asBoolean(boolean fallback) {
		return switch (this) {
			case TRUE -> true;
			case FALSE -> false;
			default -> fallback;
		};
	}
}
