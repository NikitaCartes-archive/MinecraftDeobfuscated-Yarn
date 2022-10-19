package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class SignType {
	private static final Set<SignType> VALUES = new ObjectArraySet<>();
	public static final SignType OAK = register(new SignType("oak"));
	public static final SignType SPRUCE = register(new SignType("spruce"));
	public static final SignType BIRCH = register(new SignType("birch"));
	public static final SignType ACACIA = register(new SignType("acacia"));
	public static final SignType JUNGLE = register(new SignType("jungle"));
	public static final SignType DARK_OAK = register(new SignType("dark_oak"));
	public static final SignType CRIMSON = register(new SignType("crimson"));
	public static final SignType WARPED = register(new SignType("warped"));
	public static final SignType MANGROVE = register(new SignType("mangrove"));
	public static final SignType BAMBOO = register(new SignType("bamboo"));
	private final String name;

	protected SignType(String name) {
		this.name = name;
	}

	private static SignType register(SignType type) {
		VALUES.add(type);
		return type;
	}

	public static Stream<SignType> stream() {
		return VALUES.stream();
	}

	public String getName() {
		return this.name;
	}
}
