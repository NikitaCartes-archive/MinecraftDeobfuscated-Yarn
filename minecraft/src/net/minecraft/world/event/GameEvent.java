package net.minecraft.world.event;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.listener.GameEventListener;

public class GameEvent {
	public static final GameEvent BLOCK_ACTIVATE = register("block_activate");
	public static final GameEvent BLOCK_ATTACH = register("block_attach");
	public static final GameEvent BLOCK_CHANGE = register("block_change");
	public static final GameEvent BLOCK_CLOSE = register("block_close");
	public static final GameEvent BLOCK_DEACTIVATE = register("block_deactivate");
	public static final GameEvent BLOCK_DESTROY = register("block_destroy");
	public static final GameEvent BLOCK_DETACH = register("block_detach");
	public static final GameEvent BLOCK_OPEN = register("block_open");
	public static final GameEvent BLOCK_PLACE = register("block_place");
	public static final GameEvent CONTAINER_CLOSE = register("container_close");
	public static final GameEvent CONTAINER_OPEN = register("container_open");
	public static final GameEvent DRINK = register("drink");
	public static final GameEvent EAT = register("eat");
	public static final GameEvent ELYTRA_GLIDE = register("elytra_glide");
	public static final GameEvent ENTITY_DAMAGE = register("entity_damage");
	public static final GameEvent ENTITY_DIE = register("entity_die");
	public static final GameEvent ENTITY_DISMOUNT = register("entity_dismount");
	public static final GameEvent ENTITY_INTERACT = register("entity_interact");
	public static final GameEvent ENTITY_MOUNT = register("entity_mount");
	public static final GameEvent ENTITY_PLACE = register("entity_place");
	public static final GameEvent ENTITY_ROAR = register("entity_roar");
	public static final GameEvent ENTITY_SHAKE = register("entity_shake");
	public static final GameEvent EQUIP = register("equip");
	public static final GameEvent EXPLODE = register("explode");
	public static final GameEvent FLAP = register("flap");
	public static final GameEvent FLUID_PICKUP = register("fluid_pickup");
	public static final GameEvent FLUID_PLACE = register("fluid_place");
	public static final GameEvent HIT_GROUND = register("hit_ground");
	public static final GameEvent INSTRUMENT_PLAY = register("instrument_play");
	public static final GameEvent ITEM_INTERACT_FINISH = register("item_interact_finish");
	public static final GameEvent ITEM_INTERACT_START = register("item_interact_start");
	public static final GameEvent JUKEBOX_PLAY = register("jukebox_play", 10);
	public static final GameEvent JUKEBOX_STOP_PLAY = register("jukebox_stop_play", 10);
	public static final GameEvent LIGHTNING_STRIKE = register("lightning_strike");
	public static final GameEvent NOTE_BLOCK_PLAY = register("note_block_play");
	public static final GameEvent PRIME_FUSE = register("prime_fuse");
	public static final GameEvent PROJECTILE_LAND = register("projectile_land");
	public static final GameEvent PROJECTILE_SHOOT = register("projectile_shoot");
	public static final GameEvent SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
	public static final GameEvent SHEAR = register("shear");
	public static final GameEvent SHRIEK = register("shriek", 32);
	public static final GameEvent SPLASH = register("splash");
	public static final GameEvent STEP = register("step");
	public static final GameEvent SWIM = register("swim");
	public static final GameEvent TELEPORT = register("teleport");
	public static final GameEvent RESONATE_1 = register("resonate_1");
	public static final GameEvent RESONATE_2 = register("resonate_2");
	public static final GameEvent RESONATE_3 = register("resonate_3");
	public static final GameEvent RESONATE_4 = register("resonate_4");
	public static final GameEvent RESONATE_5 = register("resonate_5");
	public static final GameEvent RESONATE_6 = register("resonate_6");
	public static final GameEvent RESONATE_7 = register("resonate_7");
	public static final GameEvent RESONATE_8 = register("resonate_8");
	public static final GameEvent RESONATE_9 = register("resonate_9");
	public static final GameEvent RESONATE_10 = register("resonate_10");
	public static final GameEvent RESONATE_11 = register("resonate_11");
	public static final GameEvent RESONATE_12 = register("resonate_12");
	public static final GameEvent RESONATE_13 = register("resonate_13");
	public static final GameEvent RESONATE_14 = register("resonate_14");
	public static final GameEvent RESONATE_15 = register("resonate_15");
	public static final int DEFAULT_RANGE = 16;
	private final String id;
	private final int range;
	private final RegistryEntry.Reference<GameEvent> registryEntry = Registries.GAME_EVENT.createEntry(this);

	public GameEvent(String id, int range) {
		this.id = id;
		this.range = range;
	}

	public String getId() {
		return this.id;
	}

	public int getRange() {
		return this.range;
	}

	private static GameEvent register(String id) {
		return register(id, 16);
	}

	private static GameEvent register(String id, int range) {
		return Registry.register(Registries.GAME_EVENT, id, new GameEvent(id, range));
	}

	public String toString() {
		return "Game Event{ " + this.id + " , " + this.range + "}";
	}

	@Deprecated
	public RegistryEntry.Reference<GameEvent> getRegistryEntry() {
		return this.registryEntry;
	}

	public boolean isIn(TagKey<GameEvent> tag) {
		return this.registryEntry.isIn(tag);
	}

	public static record Emitter(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
		public static GameEvent.Emitter of(@Nullable Entity sourceEntity) {
			return new GameEvent.Emitter(sourceEntity, null);
		}

		public static GameEvent.Emitter of(@Nullable BlockState affectedState) {
			return new GameEvent.Emitter(null, affectedState);
		}

		public static GameEvent.Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
			return new GameEvent.Emitter(sourceEntity, affectedState);
		}
	}

	public static final class Message implements Comparable<GameEvent.Message> {
		private final GameEvent event;
		private final Vec3d emitterPos;
		private final GameEvent.Emitter emitter;
		private final GameEventListener listener;
		private final double distanceTraveled;

		public Message(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter, GameEventListener listener, Vec3d listenerPos) {
			this.event = event;
			this.emitterPos = emitterPos;
			this.emitter = emitter;
			this.listener = listener;
			this.distanceTraveled = emitterPos.squaredDistanceTo(listenerPos);
		}

		public int compareTo(GameEvent.Message message) {
			return Double.compare(this.distanceTraveled, message.distanceTraveled);
		}

		public GameEvent getEvent() {
			return this.event;
		}

		public Vec3d getEmitterPos() {
			return this.emitterPos;
		}

		public GameEvent.Emitter getEmitter() {
			return this.emitter;
		}

		public GameEventListener getListener() {
			return this.listener;
		}
	}
}
