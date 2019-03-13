package net.minecraft.client.resource.metadata;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;

@Environment(EnvType.CLIENT)
public class LanguageResourceMetadata {
	public static final LanguageResourceMetadataReader field_5343 = new LanguageResourceMetadataReader();
	private final Collection<LanguageDefinition> definitions;

	public LanguageResourceMetadata(Collection<LanguageDefinition> collection) {
		this.definitions = collection;
	}

	public Collection<LanguageDefinition> getLanguageDefinitions() {
		return this.definitions;
	}
}
