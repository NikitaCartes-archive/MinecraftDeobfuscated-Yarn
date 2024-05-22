package net.minecraft.block.jukebox;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class JukeboxManager {
	public static final int TICKS_PER_SECOND = 20;
	private long ticksSinceSongStarted;
	@Nullable
	private RegistryEntry<JukeboxSong> song;
	private final BlockPos pos;
	private final JukeboxManager.ChangeNotifier changeNotifier;

	public JukeboxManager(JukeboxManager.ChangeNotifier changeNotifier, BlockPos pos) {
		this.changeNotifier = changeNotifier;
		this.pos = pos;
	}

	public boolean isPlaying() {
		return this.song != null;
	}

	@Nullable
	public JukeboxSong getSong() {
		return this.song == null ? null : this.song.value();
	}

	public long getTicksSinceSongStarted() {
		return this.ticksSinceSongStarted;
	}

	public void setValues(RegistryEntry<JukeboxSong> song, long ticksPlaying) {
		if (!song.value().shouldStopPlaying(ticksPlaying)) {
			this.song = song;
			this.ticksSinceSongStarted = ticksPlaying;
		}
	}

	public int getComparatorOutput() {
		return this.song != null ? this.song.value().comparatorOutput() : 0;
	}

	public void startPlaying(WorldAccess world, RegistryEntry<JukeboxSong> song) {
		this.song = song;
		this.ticksSinceSongStarted = 0L;
		int i = world.getRegistryManager().get(RegistryKeys.JUKEBOX_SONG).getRawId(this.song.value());
		world.syncWorldEvent(null, WorldEvents.JUKEBOX_STARTS_PLAYING, this.pos, i);
		this.changeNotifier.notifyChange();
	}

	public void stopPlaying(WorldAccess world, @Nullable BlockState state) {
		if (this.song != null) {
			this.song = null;
			this.ticksSinceSongStarted = 0L;
			world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.pos, GameEvent.Emitter.of(state));
			world.syncWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, this.pos, 0);
			this.changeNotifier.notifyChange();
		}
	}

	public void tick(WorldAccess world, @Nullable BlockState state) {
		if (this.song != null) {
			if (this.song.value().shouldStopPlaying(this.ticksSinceSongStarted)) {
				this.stopPlaying(world, state);
			} else {
				if (this.hasSecondPassed()) {
					world.emitGameEvent(GameEvent.JUKEBOX_PLAY, this.pos, GameEvent.Emitter.of(state));
					spawnNoteParticles(world, this.pos);
				}

				this.ticksSinceSongStarted++;
			}
		}
	}

	private boolean hasSecondPassed() {
		return this.ticksSinceSongStarted % 20L == 0L;
	}

	private static void spawnNoteParticles(WorldAccess world, BlockPos pos) {
		if (world instanceof ServerWorld serverWorld) {
			Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0.0, 1.2F, 0.0);
			float f = (float)world.getRandom().nextInt(4) / 24.0F;
			serverWorld.spawnParticles(ParticleTypes.NOTE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0, (double)f, 0.0, 0.0, 1.0);
		}
	}

	@FunctionalInterface
	public interface ChangeNotifier {
		void notifyChange();
	}
}
