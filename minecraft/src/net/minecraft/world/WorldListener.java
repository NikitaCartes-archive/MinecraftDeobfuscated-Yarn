package net.minecraft.world;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public interface WorldListener {
	void onBlockUpdate(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, int i);

	default void scheduleRangeRender(int i, int j, int k, int l, int m, int n) {
		for (int o = k - 1; o <= n + 1; o++) {
			for (int p = i - 1; p <= l + 1; p++) {
				for (int q = j - 1; q <= m + 1; q++) {
					this.scheduleChunkRender(p >> 4, q >> 4, o >> 4);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	default void scheduleNeighborChunksRender(int i, int j, int k) {
		for (int l = k - 1; l <= k + 1; l++) {
			for (int m = i - 1; m <= i + 1; m++) {
				for (int n = j - 1; n <= j + 1; n++) {
					this.scheduleChunkRender(m, n, l);
				}
			}
		}
	}

	default void scheduleChunkRender(int i, int j, int k) {
	}

	void onSound(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, double d, double e, double f, float g, float h);

	void onSoundFromEntity(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float f, float g);

	void playRecord(SoundEvent soundEvent, BlockPos blockPos);

	void method_8568(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i);

	void method_8563(ParticleParameters particleParameters, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i);

	void onEntityAdded(Entity entity);

	void onEntityRemoved(Entity entity);

	void onGlobalWorldEvent(int i, BlockPos blockPos, int j);

	void onWorldEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int j);

	void onBlockBreakingStage(int i, BlockPos blockPos, int j);
}
