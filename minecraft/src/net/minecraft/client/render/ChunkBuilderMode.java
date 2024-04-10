package net.minecraft.client.render;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(EnvType.CLIENT)
public enum ChunkBuilderMode implements TranslatableOption {
	NONE(0, "options.prioritizeChunkUpdates.none"),
	PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
	NEARBY(2, "options.prioritizeChunkUpdates.nearby");

	private static final IntFunction<ChunkBuilderMode> BY_ID = ValueLists.createIdToValueFunction(
		ChunkBuilderMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP
	);
	private final int id;
	private final String name;

	private ChunkBuilderMode(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getTranslationKey() {
		return this.name;
	}

	public static ChunkBuilderMode get(int id) {
		return (ChunkBuilderMode)BY_ID.apply(id);
	}
}
