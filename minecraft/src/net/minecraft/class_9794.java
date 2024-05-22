package net.minecraft;

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

public class class_9794 {
	public static final int field_52032 = 20;
	private long field_52033;
	@Nullable
	private RegistryEntry<class_9793> field_52034;
	private final BlockPos field_52035;
	private final class_9794.class_9795 field_52036;

	public class_9794(class_9794.class_9795 arg, BlockPos blockPos) {
		this.field_52036 = arg;
		this.field_52035 = blockPos;
	}

	public boolean method_60754() {
		return this.field_52034 != null;
	}

	@Nullable
	public class_9793 method_60759() {
		return this.field_52034 == null ? null : this.field_52034.value();
	}

	public long method_60761() {
		return this.field_52033;
	}

	public void method_60758(RegistryEntry<class_9793> registryEntry, long l) {
		if (!registryEntry.value().method_60751(l)) {
			this.field_52034 = registryEntry;
			this.field_52033 = l;
		}
	}

	public int method_60762() {
		return this.field_52034 != null ? this.field_52034.value().comparatorOutput() : 0;
	}

	public void method_60757(WorldAccess worldAccess, RegistryEntry<class_9793> registryEntry) {
		this.field_52034 = registryEntry;
		this.field_52033 = 0L;
		int i = worldAccess.getRegistryManager().get(RegistryKeys.JUKEBOX_SONG).getRawId(this.field_52034.value());
		worldAccess.syncWorldEvent(null, WorldEvents.JUKEBOX_STARTS_PLAYING, this.field_52035, i);
		this.field_52036.notifyChange();
	}

	public void method_60755(WorldAccess worldAccess, @Nullable BlockState blockState) {
		if (this.field_52034 != null) {
			this.field_52034 = null;
			this.field_52033 = 0L;
			worldAccess.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.field_52035, GameEvent.Emitter.of(blockState));
			worldAccess.syncWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, this.field_52035, 0);
			this.field_52036.notifyChange();
		}
	}

	public void method_60760(WorldAccess worldAccess, @Nullable BlockState blockState) {
		if (this.field_52034 != null) {
			if (this.field_52034.value().method_60751(this.field_52033)) {
				this.method_60755(worldAccess, blockState);
			} else {
				if (this.method_60763()) {
					worldAccess.emitGameEvent(GameEvent.JUKEBOX_PLAY, this.field_52035, GameEvent.Emitter.of(blockState));
					method_60756(worldAccess, this.field_52035);
				}

				this.field_52033++;
			}
		}
	}

	private boolean method_60763() {
		return this.field_52033 % 20L == 0L;
	}

	private static void method_60756(WorldAccess worldAccess, BlockPos blockPos) {
		if (worldAccess instanceof ServerWorld serverWorld) {
			Vec3d vec3d = Vec3d.ofBottomCenter(blockPos).add(0.0, 1.2F, 0.0);
			float f = (float)worldAccess.getRandom().nextInt(4) / 24.0F;
			serverWorld.spawnParticles(ParticleTypes.NOTE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0, (double)f, 0.0, 0.0, 1.0);
		}
	}

	@FunctionalInterface
	public interface class_9795 {
		void notifyChange();
	}
}
