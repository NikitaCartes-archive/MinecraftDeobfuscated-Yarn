package net.minecraft.tag;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
	protected static final RequiredTagList<GameEvent> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.GAME_EVENT_KEY, "tags/game_events");
	public static final Tag.Identified<GameEvent> VIBRATIONS = register("vibrations");
	public static final Tag.Identified<GameEvent> IGNORE_VIBRATIONS_STEPPING_CAREFULLY = register("ignore_vibrations_stepping_carefully");

	private static Tag.Identified<GameEvent> register(String id) {
		return REQUIRED_TAGS.add(id);
	}
}
