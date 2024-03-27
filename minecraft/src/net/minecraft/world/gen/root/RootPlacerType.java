package net.minecraft.world.gen.root;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RootPlacerType<P extends RootPlacer> {
	public static final RootPlacerType<MangroveRootPlacer> MANGROVE_ROOT_PLACER = register("mangrove_root_placer", MangroveRootPlacer.CODEC);
	private final MapCodec<P> codec;

	private static <P extends RootPlacer> RootPlacerType<P> register(String id, MapCodec<P> codec) {
		return Registry.register(Registries.ROOT_PLACER_TYPE, id, new RootPlacerType<>(codec));
	}

	private RootPlacerType(MapCodec<P> codec) {
		this.codec = codec;
	}

	public MapCodec<P> getCodec() {
		return this.codec;
	}
}
