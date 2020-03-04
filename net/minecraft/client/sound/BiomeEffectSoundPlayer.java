/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BiomeEffectSoundPlayer
implements ClientPlayerTickable {
    private final ClientPlayerEntity player;
    private final SoundManager soundManager;
    private final BiomeAccess biomeAccess;
    private final Random random;
    private Object2ObjectArrayMap<Biome, MusicLoop> soundLoops;
    private Optional<SoundEvent> moodSound;
    private Optional<SoundEvent> additionsSound;
    private int remainingTicks;
    private Biome activeBiome;

    public BiomeEffectSoundPlayer(ClientPlayerEntity player, SoundManager soundManager, BiomeAccess biomeAccess) {
        this.random = player.world.getRandom();
        this.player = player;
        this.soundManager = soundManager;
        this.biomeAccess = biomeAccess;
        this.soundLoops = new Object2ObjectArrayMap();
        this.moodSound = Optional.empty();
        this.additionsSound = Optional.empty();
        this.remainingTicks = BiomeEffectSoundPlayer.chooseDuration(this.random);
    }

    @Override
    public void tick() {
        this.soundLoops.values().removeIf(MovingSoundInstance::isDone);
        Biome biome = this.biomeAccess.getBiome(this.player.getX(), this.player.getY(), this.player.getZ());
        if (biome != this.activeBiome) {
            this.activeBiome = biome;
            this.moodSound = biome.getMoodSound();
            this.additionsSound = biome.getAdditionsSound();
            this.soundLoops.values().forEach(MusicLoop::fadeOut);
            biome.getLoopSound().ifPresent(soundEvent -> this.soundLoops.compute(biome, (biome, musicLoop) -> {
                if (musicLoop == null) {
                    musicLoop = new MusicLoop((SoundEvent)soundEvent);
                    this.soundManager.play((SoundInstance)musicLoop);
                }
                musicLoop.fadeIn();
                return musicLoop;
            }));
        }
        this.additionsSound.ifPresent(soundEvent -> {
            if (this.random.nextDouble() < (double)0.0111f) {
                this.soundManager.play(PositionedSoundInstance.ambient(soundEvent));
            }
        });
        if (this.remainingTicks > 0) {
            --this.remainingTicks;
        } else {
            this.moodSound.ifPresent(soundEvent -> {
                BlockPos blockPos = this.findDarkCoveredPosition();
                if (blockPos != null) {
                    this.soundManager.play(PositionedSoundInstance.ambient(soundEvent, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    this.remainingTicks = BiomeEffectSoundPlayer.chooseDuration(this.random);
                }
            });
        }
    }

    @Nullable
    private BlockPos findDarkCoveredPosition() {
        BlockState blockState;
        BlockPos blockPos = this.player.getSenseCenterPos();
        World world = this.player.world;
        int i = 9;
        BlockPos blockPos2 = blockPos.add(this.random.nextInt(9) - 4, this.random.nextInt(9) - 4, this.random.nextInt(9) - 4);
        double d = blockPos.getSquaredDistance(blockPos2);
        if (d >= 4.0 && d <= 256.0 && (blockState = world.getBlockState(blockPos2)).isAir() && world.getBaseLightLevel(blockPos2, 0) <= this.random.nextInt(8) && world.getLightLevel(LightType.SKY, blockPos2) <= 0) {
            return blockPos2;
        }
        return null;
    }

    private static int chooseDuration(Random random) {
        return random.nextInt(12000) + 6000;
    }

    @Environment(value=EnvType.CLIENT)
    public static class MusicLoop
    extends MovingSoundInstance {
        private int delta;
        private int strength;

        public MusicLoop(SoundEvent sound) {
            super(sound, SoundCategory.AMBIENT);
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.looping = true;
        }

        @Override
        public void tick() {
            if (this.strength < 0) {
                this.setDone();
            }
            this.strength += this.delta;
            this.volume = MathHelper.clamp((float)this.strength / 40.0f, 0.0f, 1.0f);
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

