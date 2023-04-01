package net.minecraft.vote;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public enum LightEngineOptimizationType implements StringIdentifiable {
	NONE("none"),
	LOADSHEDDING("loadshedding"),
	NEVER_LIGHT("never_light"),
	ALWAYS_LIGHT("always_light");

	public static final com.mojang.serialization.Codec<LightEngineOptimizationType> CODEC = StringIdentifiable.createCodec(LightEngineOptimizationType::values);
	private static final ThreadLocal<Random> RANDOM = ThreadLocal.withInitial(Random::createLocal);
	private final String id;
	private final Text name;

	private LightEngineOptimizationType(String id) {
		this.id = id;
		this.name = Text.translatable("rule.optimize_light_engine." + id);
	}

	public boolean shouldDisableLight(World world) {
		return switch (this) {
			case NEVER_LIGHT -> true;
			case LOADSHEDDING -> {
				int i = 2400;
				Random random = (Random)RANDOM.get();
				random.setSeed(HashCommon.mix(world.getTime() / 2400L));
				yield random.nextBoolean();
			}
			default -> false;
		};
	}

	public boolean shouldForceLight(World world) {
		return this == ALWAYS_LIGHT;
	}

	@Override
	public String asString() {
		return this.id;
	}

	public Text getName() {
		return this.name;
	}
}
