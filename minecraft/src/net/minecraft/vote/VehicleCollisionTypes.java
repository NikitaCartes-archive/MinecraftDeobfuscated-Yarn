package net.minecraft.vote;

import net.minecraft.util.StringIdentifiable;

public enum VehicleCollisionTypes implements StringIdentifiable {
	NONE("none"),
	BREAK("break"),
	EXPLODE("explode");

	public static final com.mojang.serialization.Codec<VehicleCollisionTypes> CODEC = StringIdentifiable.createCodec(VehicleCollisionTypes::values);
	private final String id;

	private VehicleCollisionTypes(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
