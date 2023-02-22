package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.Heightmap;

public class HeightmapArgumentType extends EnumArgumentType<Heightmap.Type> {
	private static final Codec<Heightmap.Type> HEIGHTMAP_CODEC = StringIdentifiable.createCodec(
		HeightmapArgumentType::getHeightmapTypes, name -> name.toLowerCase(Locale.ROOT)
	);

	private static Heightmap.Type[] getHeightmapTypes() {
		return (Heightmap.Type[])Arrays.stream(Heightmap.Type.values()).filter(Heightmap.Type::isStoredServerSide).toArray(Heightmap.Type[]::new);
	}

	private HeightmapArgumentType() {
		super(HEIGHTMAP_CODEC, HeightmapArgumentType::getHeightmapTypes);
	}

	public static HeightmapArgumentType heightmap() {
		return new HeightmapArgumentType();
	}

	public static Heightmap.Type getHeightmap(CommandContext<ServerCommandSource> context, String id) {
		return context.getArgument(id, Heightmap.Type.class);
	}

	@Override
	protected String transformValueName(String name) {
		return name.toLowerCase(Locale.ROOT);
	}
}
