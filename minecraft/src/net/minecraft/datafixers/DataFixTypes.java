package net.minecraft.datafixers;

import com.mojang.datafixers.DSL.TypeReference;

public enum DataFixTypes {
	field_19212(TypeReferences.LEVEL),
	field_19213(TypeReferences.PLAYER),
	field_19214(TypeReferences.CHUNK),
	field_19215(TypeReferences.HOTBAR),
	field_19216(TypeReferences.OPTIONS),
	field_19217(TypeReferences.STRUCTURE),
	field_19218(TypeReferences.STATS),
	field_19219(TypeReferences.SAVED_DATA),
	field_19220(TypeReferences.ADVANCEMENTS),
	field_19221(TypeReferences.POI_CHUNK);

	private final TypeReference typeReference;

	private DataFixTypes(TypeReference typeReference) {
		this.typeReference = typeReference;
	}

	public TypeReference getTypeReference() {
		return this.typeReference;
	}
}
