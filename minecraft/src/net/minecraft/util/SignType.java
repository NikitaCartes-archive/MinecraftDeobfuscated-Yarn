package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SignType {
	private static final Set<SignType> VALUES = new ObjectArraySet<>();
	public static final SignType OAK = register(new SignType("oak"));
	public static final SignType SPRUCE = register(new SignType("spruce"));
	public static final SignType BIRCH = register(new SignType("birch"));
	public static final SignType ACACIA = register(new SignType("acacia"));
	public static final SignType JUNGLE = register(new SignType("jungle"));
	public static final SignType DARK_OAK = register(new SignType("dark_oak"));
	private final String name;

	protected SignType(String name) {
		this.name = name;
	}

	private static SignType register(SignType type) {
		VALUES.add(type);
		return type;
	}

	@Environment(EnvType.CLIENT)
	public static Stream<SignType> stream() {
		return VALUES.stream();
	}

	@Environment(EnvType.CLIENT)
	public String getName() {
		return this.name;
	}
}
