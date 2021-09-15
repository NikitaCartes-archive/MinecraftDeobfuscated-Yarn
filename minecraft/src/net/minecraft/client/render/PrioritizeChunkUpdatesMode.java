package net.minecraft.client.render;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum PrioritizeChunkUpdatesMode {
	NONE(0, "options.prioritizeChunkUpdates.none"),
	PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
	NEARBY(2, "options.prioritizeChunkUpdates.nearby");

	private static final PrioritizeChunkUpdatesMode[] modes = (PrioritizeChunkUpdatesMode[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(PrioritizeChunkUpdatesMode::getId))
		.toArray(PrioritizeChunkUpdatesMode[]::new);
	private final int id;
	private final String name;

	private PrioritizeChunkUpdatesMode(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public static PrioritizeChunkUpdatesMode get(int id) {
		return modes[MathHelper.floorMod(id, modes.length)];
	}
}
