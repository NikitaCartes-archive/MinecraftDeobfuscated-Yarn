package net.minecraft.entity.attribute;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Represents a type of double-valued attribute that a living entity may have.
 * 
 * <p>An attribute is a tracked double value stored on an entity.
 * An attribute has a default value on which attribute modifiers operate.
 */
public class EntityAttribute {
	public static final Codec<RegistryEntry<EntityAttribute>> CODEC = Registries.ATTRIBUTE.getEntryCodec();
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

	/**
	 * Checks if instances of this attribute should synchronize values to clients.
	 */
	public boolean isTracked() {
		return this.tracked;
	}

	/**
	 * Sets all instances of this attribute to synchronize their values to clients.
	 */
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
