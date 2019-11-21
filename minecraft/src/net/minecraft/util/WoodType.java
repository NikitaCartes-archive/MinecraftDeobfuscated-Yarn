package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class WoodType {
	private static final Set<WoodType> VALUES = new ObjectArraySet<>();
	public static final WoodType OAK = register(new WoodType("oak"));
	public static final WoodType SPRUCE = register(new WoodType("spruce"));
	public static final WoodType BIRCH = register(new WoodType("birch"));
	public static final WoodType ACACIA = register(new WoodType("acacia"));
	public static final WoodType JUNGLE = register(new WoodType("jungle"));
	public static final WoodType DARK_OAK = register(new WoodType("dark_oak"));
	private final String name;

	protected WoodType(String name) {
		this.name = name;
	}

	private static WoodType register(WoodType woodType) {
		VALUES.add(woodType);
		return woodType;
	}

	@Environment(EnvType.CLIENT)
	public static Stream<WoodType> stream() {
		return VALUES.stream();
	}

	@Environment(EnvType.CLIENT)
	public String getName() {
		return this.name;
	}
}
