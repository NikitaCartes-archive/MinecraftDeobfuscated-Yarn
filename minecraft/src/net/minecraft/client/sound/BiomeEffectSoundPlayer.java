package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
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
	private static final int field_32994 = 40;
	private static final float field_32995 = 0.001F;
	private final ClientPlayerEntity player;
	private final SoundManager soundManager;
	private final BiomeAccess biomeAccess;
	private final Random random;
	private final Object2ObjectArrayMap<Biome, BiomeEffectSoundPlayer.MusicLoop> soundLoops = new Object2ObjectArrayMap<>();
	private Optional<BiomeMoodSound> moodSound = Optional.empty();
	private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
	private float moodPercentage;
	@Nullable
	private Biome activeBiome;

	public BiomeEffectSoundPlayer(ClientPlayerEntity player, SoundManager soundManager, BiomeAccess biomeAccess) {
		this.random = player.world.getRandom();
		this.player = player;
		this.soundManager = soundManager;
		this.biomeAccess = biomeAccess;
	}

	public float getMoodPercentage() {
		return this.moodPercentage;
	}

	@Override
	public void tick() {
		this.soundLoops.values().removeIf(MovingSoundInstance::isDone);
		Biome biome = this.biomeAccess.getBiomeForNoiseGen(this.player.getX(), this.player.getY(), this.player.getZ()).value();
		if (biome != this.activeBiome) {
			this.activeBiome = biome;
			this.moodSound = biome.getMoodSound();
			this.additionsSound = biome.getAdditionsSound();
			this.soundLoops.values().forEach(BiomeEffectSoundPlayer.MusicLoop::fadeOut);
			biome.getLoopSound().ifPresent(sound -> this.soundLoops.compute(biome, (soundx, loop) -> {
					if (loop == null) {
						loop = new BiomeEffectSoundPlayer.MusicLoop(sound);
						this.soundManager.play(loop);
					}

					loop.fadeIn();
					return loop;
				}));
		}

		this.additionsSound.ifPresent(sound -> {
			if (this.random.nextDouble() < sound.getChance()) {
				this.soundManager.play(PositionedSoundInstance.ambient(sound.getSound()));
			}
		});
		this.moodSound
			.ifPresent(
				sound -> {
					World world = this.player.world;
					int i = sound.getSpawnRange() * 2 + 1;
					BlockPos blockPos = new BlockPos(
						this.player.getX() + (double)this.random.nextInt(i) - (double)sound.getSpawnRange(),
						this.player.getEyeY() + (double)this.random.nextInt(i) - (double)sound.getSpawnRange(),
						this.player.getZ() + (double)this.random.nextInt(i) - (double)sound.getSpawnRange()
					);
					int j = world.getLightLevel(LightType.SKY, blockPos);
					if (j > 0) {
						this.moodPercentage = this.moodPercentage - (float)j / (float)world.getMaxLightLevel() * 0.001F;
					} else {
						this.moodPercentage = this.moodPercentage - (float)(world.getLightLevel(LightType.BLOCK, blockPos) - 1) / (float)sound.getCultivationTicks();
					}

					if (this.moodPercentage >= 1.0F) {
						double d = (double)blockPos.getX() + 0.5;
						double e = (double)blockPos.getY() + 0.5;
						double f = (double)blockPos.getZ() + 0.5;
						double g = d - this.player.getX();
						double h = e - this.player.getEyeY();
						double k = f - this.player.getZ();
						double l = Math.sqrt(g * g + h * h + k * k);
						double m = l + sound.getExtraDistance();
						PositionedSoundInstance positionedSoundInstance = PositionedSoundInstance.ambient(
							sound.getSound(), this.player.getX() + g / l * m, this.player.getEyeY() + h / l * m, this.player.getZ() + k / l * m
						);
						this.soundManager.play(positionedSoundInstance);
						this.moodPercentage = 0.0F;
					} else {
						this.moodPercentage = Math.max(this.moodPercentage, 0.0F);
					}
				}
			);
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
			this.relative = true;
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
