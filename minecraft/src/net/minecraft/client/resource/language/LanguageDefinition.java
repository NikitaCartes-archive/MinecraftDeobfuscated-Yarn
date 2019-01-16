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

	public LanguageDefinition(String string, String string2, String string3, boolean bl) {
		this.code = string;
		this.name = string2;
		this.region = string3;
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

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return !(object instanceof LanguageDefinition) ? false : this.code.equals(((LanguageDefinition)object).code);
		}
	}

	public int hashCode() {
		return this.code.hashCode();
	}

	public int compareTo(LanguageDefinition languageDefinition) {
		return this.code.compareTo(languageDefinition.code);
	}
}
