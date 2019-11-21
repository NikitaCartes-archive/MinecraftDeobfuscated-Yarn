package net.minecraft.client.resource.language;

import com.mojang.bridge.game.Language;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageDefinition implements Language, Comparable<LanguageDefinition> {
	private final String code;
	private final String name;
	private final String region;
	private final boolean rightToLeft;

	public LanguageDefinition(String code, String name, String region, boolean bl) {
		this.code = code;
		this.name = name;
		this.region = region;
		this.rightToLeft = bl;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getName() {
		return this.region;
	}

	@Override
	public String getRegion() {
		return this.name;
	}

	public boolean isRightToLeft() {
		return this.rightToLeft;
	}

	public String toString() {
		return String.format("%s (%s)", this.region, this.name);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof LanguageDefinition) ? false : this.code.equals(((LanguageDefinition)o).code);
		}
	}

	public int hashCode() {
		return this.code.hashCode();
	}

	public int compareTo(LanguageDefinition languageDefinition) {
		return this.code.compareTo(languageDefinition.code);
	}
}
