package net.minecraft.client.resource.metadata;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;

@Environment(EnvType.CLIENT)
public class LanguageResourceMetadata {
	public static final LanguageResourceMetadataReader READER = new LanguageResourceMetadataReader();
	public static final boolean field_32978 = false;
	private final Collection<LanguageDefinition> definitions;

	public LanguageResourceMetadata(Collection<LanguageDefinition> definitions) {
		this.definitions = definitions;
	}

	public Collection<LanguageDefinition> getLanguageDefinitions() {
		return this.definitions;
	}
}
