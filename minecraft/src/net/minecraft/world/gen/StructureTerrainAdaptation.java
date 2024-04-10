package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum StructureTerrainAdaptation implements StringIdentifiable {
	NONE("none"),
	BURY("bury"),
	BEARD_THIN("beard_thin"),
	BEARD_BOX("beard_box"),
	ENCAPSULATE("encapsulate");

	public static final Codec<StructureTerrainAdaptation> CODEC = StringIdentifiable.createCodec(StructureTerrainAdaptation::values);
	private final String name;

	private StructureTerrainAdaptation(final String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
