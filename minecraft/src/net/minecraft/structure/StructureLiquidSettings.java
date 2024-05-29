package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum StructureLiquidSettings implements StringIdentifiable {
	IGNORE_WATERLOGGING("ignore_waterlogging"),
	APPLY_WATERLOGGING("apply_waterlogging");

	public static Codec<StructureLiquidSettings> codec = StringIdentifiable.createBasicCodec(StructureLiquidSettings::values);
	private final String id;

	private StructureLiquidSettings(final String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
