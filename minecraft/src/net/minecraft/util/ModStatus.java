package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModStatus(ModStatus.Confidence confidence, String description) {
	/**
	 * {@return the modification status determined by the brand and whether the
	 * class is signed or not}
	 */
	public static ModStatus check(String vanillaBrand, Supplier<String> brandSupplier, String environment, Class<?> clazz) {
		String string = (String)brandSupplier.get();
		if (!vanillaBrand.equals(string)) {
			return new ModStatus(ModStatus.Confidence.DEFINITELY, environment + " brand changed to '" + string + "'");
		} else {
			return clazz.getSigners() == null
				? new ModStatus(ModStatus.Confidence.VERY_LIKELY, environment + " jar signature invalidated")
				: new ModStatus(ModStatus.Confidence.PROBABLY_NOT, environment + " jar signature and brand is untouched");
		}
	}

	public boolean isModded() {
		return this.confidence.modded;
	}

	/**
	 * {@return the combined modification status with the higher confidence}
	 * 
	 * @apiNote This is used to check if either the client or the integrated server is modded.
	 */
	public ModStatus combine(ModStatus brand) {
		return new ModStatus(ObjectUtils.max(this.confidence, brand.confidence), this.description + "; " + brand.description);
	}

	public String getMessage() {
		return this.confidence.description + " " + this.description;
	}

	public static enum Confidence {
		PROBABLY_NOT("Probably not.", false),
		VERY_LIKELY("Very likely;", true),
		DEFINITELY("Definitely;", true);

		final String description;
		final boolean modded;

		private Confidence(final String description, final boolean modded) {
			this.description = description;
			this.modded = modded;
		}
	}
}
