package net.minecraft.entity.attribute;

public class EntityAttribute {
	private final double fallback;
	private boolean tracked;
	private final String translationKey;

	protected EntityAttribute(String translationKey, double fallback) {
		this.fallback = fallback;
		this.translationKey = translationKey;
	}

	public double getDefaultValue() {
		return this.fallback;
	}

	public boolean isTracked() {
		return this.tracked;
	}

	public EntityAttribute setTracked(boolean tracked) {
		this.tracked = tracked;
		return this;
	}

	public double clamp(double value) {
		return value;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}
}
