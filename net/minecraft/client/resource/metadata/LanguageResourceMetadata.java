/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.metadata;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.metadata.LanguageResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class LanguageResourceMetadata {
    public static final LanguageResourceMetadataReader READER = new LanguageResourceMetadataReader();
    private final Collection<LanguageDefinition> definitions;

    public LanguageResourceMetadata(Collection<LanguageDefinition> collection) {
        this.definitions = collection;
    }

    public Collection<LanguageDefinition> getLanguageDefinitions() {
        return this.definitions;
    }
}

