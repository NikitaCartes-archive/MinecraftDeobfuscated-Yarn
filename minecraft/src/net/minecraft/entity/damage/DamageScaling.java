package net.minecraft.entity.damage;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum DamageScaling implements StringIdentifiable {
	NEVER("never"),
	WHEN_CAUSED_BY_LIVING_NON_PLAYER("when_caused_by_living_non_player"),
	ALWAYS("always");

	public static final Codec<DamageScaling> CODEC = StringIdentifiable.createCodec(DamageScaling::values);
	private final String id;

	private DamageScaling(final String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
