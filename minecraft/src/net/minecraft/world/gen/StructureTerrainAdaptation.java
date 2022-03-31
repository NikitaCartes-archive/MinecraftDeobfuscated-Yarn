package net.minecraft.world.gen;

import net.minecraft.util.StringIdentifiable;

public enum StructureTerrainAdaptation implements StringIdentifiable {
	NONE("none"),
	BURY("bury"),
	BEARD_THIN("beard_thin"),
	BEARD_BOX("beard_box");

	public static final com.mojang.serialization.Codec<StructureTerrainAdaptation> CODEC = StringIdentifiable.createCodec(StructureTerrainAdaptation::values);
	private final String name;

	private StructureTerrainAdaptation(String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
