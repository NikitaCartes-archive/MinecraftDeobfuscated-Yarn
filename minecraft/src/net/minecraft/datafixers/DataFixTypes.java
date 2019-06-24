package net.minecraft.datafixers;

import com.mojang.datafixers.DSL.TypeReference;

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
	POI_CHUNK(TypeReferences.POI_CHUNK);

	private final TypeReference typeReference;

	private DataFixTypes(TypeReference typeReference) {
		this.typeReference = typeReference;
	}

	public TypeReference getTypeReference() {
		return this.typeReference;
	}
}
