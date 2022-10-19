package net.minecraft.client.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelIdentifier extends Identifier {
	@VisibleForTesting
	static final char SEPARATOR = '#';
	private final String variant;

	private ModelIdentifier(String namespace, String path, String variant, @Nullable Identifier.ExtraData extraData) {
		super(namespace, path, extraData);
		this.variant = variant;
	}

	public ModelIdentifier(String namespace, String path, String variant) {
		super(namespace, path);
		this.variant = toLowerCase(variant);
	}

	public ModelIdentifier(Identifier id, String variant) {
		this(id.getNamespace(), id.getPath(), toLowerCase(variant), null);
	}

	public static ModelIdentifier ofVanilla(String path, String variant) {
		return new ModelIdentifier("minecraft", path, variant);
	}

	private static String toLowerCase(String string) {
		return string.toLowerCase(Locale.ROOT);
	}

	public String getVariant() {
		return this.variant;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ModelIdentifier && super.equals(object)) {
			ModelIdentifier modelIdentifier = (ModelIdentifier)object;
			return this.variant.equals(modelIdentifier.variant);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + this.variant.hashCode();
	}

	@Override
	public String toString() {
		return super.toString() + "#" + this.variant;
	}
}
