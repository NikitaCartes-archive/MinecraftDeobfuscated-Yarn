package net.minecraft.client.resource.language;

import com.mojang.bridge.game.Language;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageDefinition implements Language, Comparable<LanguageDefinition> {
	private final String code;
	private final String region;
	private final String name;
	private final boolean rightToLeft;

	public LanguageDefinition(String code, String region, String name, boolean rightToLeft) {
		this.code = code;
		this.region = region;
		this.name = name;
		this.rightToLeft = rightToLeft;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getRegion() {
		return this.region;
	}

	public boolean isRightToLeft() {
		return this.rightToLeft;
	}

	public String toString() {
		return String.format("%s (%s)", this.name, this.region);
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
