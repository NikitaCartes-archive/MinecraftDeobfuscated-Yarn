package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.tag.GameEventTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTagsProvider extends AbstractTagProvider<GameEvent> {
	public GameEventTagsProvider(DataGenerator root) {
		super(root, Registry.GAME_EVENT);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(GameEventTags.VIBRATIONS)
			.add(
				GameEvent.STEP,
				GameEvent.SWIM,
				GameEvent.FLAP,
				GameEvent.ELYTRA_FREE_FALL,
				GameEvent.HIT_GROUND,
				GameEvent.SPLASH,
				GameEvent.PROJECTILE_SHOOT,
				GameEvent.PROJECTILE_LAND,
				GameEvent.ENTITY_HIT,
				GameEvent.BLOCK_PLACE,
				GameEvent.BLOCK_DESTROY,
				GameEvent.FLUID_PLACE,
				GameEvent.FLUID_PICKUP,
				GameEvent.BLOCK_OPEN,
				GameEvent.BLOCK_CLOSE,
				GameEvent.BLOCK_SWITCH,
				GameEvent.BLOCK_UNSWITCH,
				GameEvent.BLOCK_ATTACH,
				GameEvent.BLOCK_DETACH,
				GameEvent.BLOCK_PRESS,
				GameEvent.BLOCK_UNPRESS,
				GameEvent.CONTAINER_OPEN,
				GameEvent.CONTAINER_CLOSE,
				GameEvent.EXPLODE,
				GameEvent.ARMOR_STAND_ADD_ITEM,
				GameEvent.WOLF_SHAKING,
				GameEvent.DISPENSE_FAIL,
				GameEvent.FISHING_ROD_CAST,
				GameEvent.FISHING_ROD_REEL_IN,
				GameEvent.PISTON_EXTEND,
				GameEvent.PISTON_CONTRACT,
				GameEvent.FLINT_AND_STEEL_USE,
				GameEvent.EATING_FINISH,
				GameEvent.LIGHTNING_STRIKE
			);
		this.getOrCreateTagBuilder(GameEventTags.IGNORE_VIBRATIONS_STEPPING_CAREFULLY).add(GameEvent.STEP, GameEvent.HIT_GROUND, GameEvent.PROJECTILE_SHOOT);
	}

	@Override
	protected Path getOutput(Identifier id) {
		return this.root.getOutput().resolve("data/" + id.getNamespace() + "/tags/game_events/" + id.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Game Event Tags";
	}
}
