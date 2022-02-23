/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixer.TypeReferences;

public enum DataFixTypes {
    LEVEL(TypeReferences.LEVEL),
    PLAYER(TypeReferences.PLAYER),
    CHUNK(TypeReferences.CHUNK),
    HOTBAR(TypeReferences.HOTBAR),
    OPTIONS(TypeReferences.OPTIONS),
    STRUCTURE(TypeReferences.STRUCTURE),
    STATS(TypeReferences.STATS),
    SAVED_DATA(TypeReferences.SAVED_DATA),
    ADVANCEMENTS(TypeReferences.ADVANCEMENTS),
    POI_CHUNK(TypeReferences.POI_CHUNK),
    WORLD_GEN_SETTINGS(TypeReferences.WORLD_GEN_SETTINGS),
    ENTITY_CHUNK(TypeReferences.ENTITY_CHUNK);

    private final DSL.TypeReference typeReference;

    private DataFixTypes(DSL.TypeReference typeReference) {
        this.typeReference = typeReference;
    }

    public DSL.TypeReference getTypeReference() {
        return this.typeReference;
    }
}

