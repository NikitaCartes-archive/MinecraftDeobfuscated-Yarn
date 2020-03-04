package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Environment(EnvType.CLIENT)
public class BiomeEffectSoundPlayer implements ClientPlayerTickable {
	private final ClientPlayerEntity player;
	private final SoundManager soundManager;
	private final BiomeAccess biomeAccess;
	private final Random random;
	private Object2ObjectArrayMap<Biome, BiomeEffectSoundPlayer.MusicLoop> soundLoops;
	private Optional<SoundEvent> moodSound;
	private Optional<SoundEvent> additionsSound;
	private int remainingTicks;
	private Biome activeBiome;

	public BiomeEffectSoundPlayer(ClientPlayerEntity player, SoundManager soundManager, BiomeAccess biomeAccess) {
		this.random = player.world.getRandom();
		this.player = player;
		this.soundManager = soundManager;
		this.biomeAccess = biomeAccess;
		this.soundLoops = new Object2ObjectArrayMap<>();
		this.moodSound = Optional.empty();
		this.additionsSound = Optional.empty();
		this.remainingTicks = chooseDuration(this.random);
	}

	@Override
	public void tick() {
		this.soundLoops.values().removeIf(MovingSoundInstance::isDone);
		Biome biome = this.biomeAccess.getBiome(this.player.getX(), this.player.getY(), this.player.getZ());
		if (biome != this.activeBiome) {
			this.activeBiome = biome;
			this.moodSound = biome.getMoodSound();
			this.additionsSound = biome.getAdditionsSound();
			this.soundLoops.values().forEach(BiomeEffectSoundPlayer.MusicLoop::fadeOut);
			biome.getLoopSound().ifPresent(soundEvent -> {
				BiomeEffectSoundPlayer.MusicLoop var10000 = (BiomeEffectSoundPlayer.MusicLoop)this.soundLoops.compute(biome, (biomexx, musicLoop) -> {
					if (musicLoop == null) {
						musicLoop = new BiomeEffectSoundPlayer.MusicLoop(soundEvent);
						this.soundManager.play(musicLoop);
					}

					musicLoop.fadeIn();
					return musicLoop;
				});
			});
		}

		this.additionsSound.ifPresent(soundEvent -> {
			if (this.random.nextDouble() < 0.0111F) {
				this.soundManager.play(PositionedSoundInstance.ambient(soundEvent));
			}
		});
		if (this.remainingTicks > 0) {
			this.remainingTicks--;
		} else {
			this.moodSound.ifPresent(soundEvent -> {
				BlockPos blockPos = this.findDarkCoveredPosition();
				if (blockPos != null) {
					this.soundManager.play(PositionedSoundInstance.ambient(soundEvent, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ()));
					this.remainingTicks = chooseDuration(this.random);
				}
			});
		}
	}

	@Nullable
	private BlockPos findDarkCoveredPosition() {
		BlockPos blockPos = this.player.getSenseCenterPos();
		World world = this.player.world;
		int i = 9;
		BlockPos blockPos2 = blockPos.add(this.random.nextInt(9) - 4, this.random.nextInt(9) - 4, this.random.nextInt(9) - 4);
		double d = blockPos.getSquaredDistance(blockPos2);
		if (d >= 4.0 && d <= 256.0) {
			BlockState blockState = world.getBlockState(blockPos2);
			if (blockState.isAir() && world.getBaseLightLevel(blockPos2, 0) <= this.random.nextInt(8) && world.getLightLevel(LightType.SKY, blockPos2) <= 0) {
				return blockPos2;
			}
		}

		return null;
	}

	private static int chooseDuration(Random random) {
		return random.nextInt(12000) + 6000;
	}

	@Environment(EnvType.CLIENT)
	public static class MusicLoop extends MovingSoundInstance {
		private int delta;
		private int strength;

		public MusicLoop(SoundEvent sound) {
			super(sound, SoundCategory.AMBIENT);
			this.repeat = true;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.looping = true;
		}

		@Override
		public void tick() {
			if (this.strength < 0) {
				this.setDone();
			}

			this.strength = this.strength + this.delta;
			this.volume = MathHelper.clamp((float)this.strength / 40.0F, 0.0F, 1.0F);
		}

		public void fadeOut() {
			this.strength = Math.min(this.strength, 40);
			this.delta = -1;
		}

		public void fadeIn() {
			this.strength = Math.max(0, this.strength);
			this.delta = 1;
		}
	}
}
