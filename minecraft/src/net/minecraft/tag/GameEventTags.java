package net.minecraft.tag;

import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
	private static final RequiredTagList<GameEvent> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier("game_event"), TagManager::getGameEvents);
	public static final Tag.Identified<GameEvent> VIBRATIONS = register("vibrations");
	public static final Tag.Identified<GameEvent> IGNORE_VIBRATIONS_STEPPING_CAREFULLY = register("ignore_vibrations_stepping_carefully");

	private static Tag.Identified<GameEvent> register(String id) {
		return REQUIRED_TAGS.add(id);
	}

	public static List<? extends Tag.Identified<GameEvent>> getRequiredTags() {
		return REQUIRED_TAGS.getTags();
	}
}
