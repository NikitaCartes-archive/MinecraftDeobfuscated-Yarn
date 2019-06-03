/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.Listener;
import net.minecraft.client.sound.ListenerSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundSystem {
    private static final Marker MARKER = MarkerManager.getMarker("SOUNDS");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Identifier> unknownSounds = Sets.newHashSet();
    private final SoundManager loader;
    private final GameOptions settings;
    private boolean started;
    private final SoundEngine soundEngine = new SoundEngine();
    private final Listener listener = this.soundEngine.getListener();
    private final SoundLoader soundLoader;
    private final SoundExecutor taskQueue = new SoundExecutor();
    private final Channel channel = new Channel(this.soundEngine, this.taskQueue);
    private int ticks;
    private final Map<SoundInstance, Channel.SourceManager> sources = Maps.newHashMap();
    private final Multimap<SoundCategory, SoundInstance> sounds = HashMultimap.create();
    private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
    private final Map<SoundInstance, Integer> startTicks = Maps.newHashMap();
    private final Map<SoundInstance, Integer> soundEndTicks = Maps.newHashMap();
    private final List<ListenerSoundInstance> listeners = Lists.newArrayList();
    private final List<Sound> preloadedSounds = Lists.newArrayList();

    public SoundSystem(SoundManager soundManager, GameOptions gameOptions, ResourceManager resourceManager) {
        this.loader = soundManager;
        this.settings = gameOptions;
        this.soundLoader = new SoundLoader(resourceManager);
    }

    public void reloadSounds() {
        unknownSounds.clear();
        for (SoundEvent soundEvent : Registry.SOUND_EVENT) {
            Identifier identifier = soundEvent.getId();
            if (this.loader.get(identifier) != null) continue;
            LOGGER.warn("Missing sound for event: {}", (Object)Registry.SOUND_EVENT.getId(soundEvent));
            unknownSounds.add(identifier);
        }
        this.stop();
        this.start();
    }

    private synchronized void start() {
        if (this.started) {
            return;
        }
        try {
            this.soundEngine.init();
            this.listener.init();
            this.listener.setVolume(this.settings.getSoundVolume(SoundCategory.MASTER));
            this.soundLoader.loadStatic(this.preloadedSounds).thenRun(this.preloadedSounds::clear);
            this.started = true;
            LOGGER.info(MARKER, "Sound engine started");
        } catch (RuntimeException runtimeException) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeException);
        }
    }

    private float getSoundVolume(SoundCategory soundCategory) {
        if (soundCategory == null || soundCategory == SoundCategory.MASTER) {
            return 1.0f;
        }
        return this.settings.getSoundVolume(soundCategory);
    }

    public void updateSoundVolume(SoundCategory soundCategory, float f) {
        if (!this.started) {
            return;
        }
        if (soundCategory == SoundCategory.MASTER) {
            this.listener.setVolume(f);
            return;
        }
        this.sources.forEach((soundInstance, sourceManager) -> {
            float f = this.getAdjustedVolume((SoundInstance)soundInstance);
            sourceManager.run(source -> {
                if (f <= 0.0f) {
                    source.stop();
                } else {
                    source.setVolume(f);
                }
            });
        });
    }

    public void stop() {
        if (this.started) {
            this.stopAll();
            this.soundLoader.close();
            this.soundEngine.close();
            this.started = false;
        }
    }

    public void stop(SoundInstance soundInstance) {
        Channel.SourceManager sourceManager;
        if (this.started && (sourceManager = this.sources.get(soundInstance)) != null) {
            sourceManager.run(Source::stop);
        }
    }

    public void stopAll() {
        if (this.started) {
            this.taskQueue.restart();
            this.sources.values().forEach(sourceManager -> sourceManager.run(Source::stop));
            this.sources.clear();
            this.channel.close();
            this.startTicks.clear();
            this.tickingSounds.clear();
            this.sounds.clear();
            this.soundEndTicks.clear();
        }
    }

    public void registerListener(ListenerSoundInstance listenerSoundInstance) {
        this.listeners.add(listenerSoundInstance);
    }

    public void unregisterListener(ListenerSoundInstance listenerSoundInstance) {
        this.listeners.remove(listenerSoundInstance);
    }

    public void tick(boolean bl) {
        if (!bl) {
            this.tick();
        }
        this.channel.tick();
    }

    private void tick() {
        ++this.ticks;
        for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
            tickableSoundInstance.tick();
            if (tickableSoundInstance.isDone()) {
                this.stop(tickableSoundInstance);
                continue;
            }
            float f = this.getAdjustedVolume(tickableSoundInstance);
            float g = this.getAdjustedPitch(tickableSoundInstance);
            Vec3d vec3d = new Vec3d(tickableSoundInstance.getX(), tickableSoundInstance.getY(), tickableSoundInstance.getZ());
            Channel.SourceManager sourceManager = this.sources.get(tickableSoundInstance);
            if (sourceManager == null) continue;
            sourceManager.run(source -> {
                source.setVolume(f);
                source.setPitch(g);
                source.setPosition(vec3d);
            });
        }
        Iterator<Map.Entry<SoundInstance, Channel.SourceManager>> iterator = this.sources.entrySet().iterator();
        while (iterator.hasNext()) {
            int i;
            Map.Entry<SoundInstance, Channel.SourceManager> entry = iterator.next();
            Channel.SourceManager sourceManager2 = entry.getValue();
            SoundInstance soundInstance = entry.getKey();
            float h = this.settings.getSoundVolume(soundInstance.getCategory());
            if (h <= 0.0f) {
                sourceManager2.run(Source::stop);
                iterator.remove();
                continue;
            }
            if (!sourceManager2.isStopped() || (i = this.soundEndTicks.get(soundInstance).intValue()) > this.ticks) continue;
            int j = soundInstance.getRepeatDelay();
            if (soundInstance.isRepeatable() && j > 0) {
                this.startTicks.put(soundInstance, this.ticks + j);
            }
            iterator.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)sourceManager2);
            this.soundEndTicks.remove(soundInstance);
            try {
                this.sounds.remove((Object)soundInstance.getCategory(), soundInstance);
            } catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (!(soundInstance instanceof TickableSoundInstance)) continue;
            this.tickingSounds.remove(soundInstance);
        }
        Iterator<Map.Entry<SoundInstance, Integer>> iterator2 = this.startTicks.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry<SoundInstance, Integer> entry2 = iterator2.next();
            if (this.ticks < entry2.getValue()) continue;
            SoundInstance soundInstance = entry2.getKey();
            if (soundInstance instanceof TickableSoundInstance) {
                ((TickableSoundInstance)soundInstance).tick();
            }
            this.play(soundInstance);
            iterator2.remove();
        }
    }

    public boolean isPlaying(SoundInstance soundInstance) {
        if (!this.started) {
            return false;
        }
        if (this.soundEndTicks.containsKey(soundInstance) && this.soundEndTicks.get(soundInstance) <= this.ticks) {
            return true;
        }
        return this.sources.containsKey(soundInstance);
    }

    public void play(SoundInstance soundInstance) {
        if (!this.started) {
            return;
        }
        WeightedSoundSet weightedSoundSet = soundInstance.getSoundSet(this.loader);
        Identifier identifier = soundInstance.getId();
        if (weightedSoundSet == null) {
            if (unknownSounds.add(identifier)) {
                LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)identifier);
            }
            return;
        }
        if (!this.listeners.isEmpty()) {
            for (ListenerSoundInstance listenerSoundInstance : this.listeners) {
                listenerSoundInstance.onSoundPlayed(soundInstance, weightedSoundSet);
            }
        }
        if (this.listener.getVolume() <= 0.0f) {
            LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)identifier);
            return;
        }
        Sound sound = soundInstance.getSound();
        if (sound == SoundManager.MISSING_SOUND) {
            if (unknownSounds.add(identifier)) {
                LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)identifier);
            }
            return;
        }
        float f = soundInstance.getVolume();
        float g = Math.max(f, 1.0f) * (float)sound.getAttenuation();
        SoundCategory soundCategory = soundInstance.getCategory();
        float h = this.getAdjustedVolume(soundInstance);
        float i = this.getAdjustedPitch(soundInstance);
        SoundInstance.AttenuationType attenuationType = soundInstance.getAttenuationType();
        boolean bl = soundInstance.isLooping();
        if (h == 0.0f && !soundInstance.shouldAlwaysPlay()) {
            LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)sound.getIdentifier());
            return;
        }
        boolean bl2 = soundInstance.isRepeatable() && soundInstance.getRepeatDelay() == 0;
        Vec3d vec3d = new Vec3d(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ());
        Channel.SourceManager sourceManager = this.channel.createSource(sound.isStreamed() ? SoundEngine.RunMode.STREAMING : SoundEngine.RunMode.STATIC);
        LOGGER.debug(MARKER, "Playing sound {} for event {}", (Object)sound.getIdentifier(), (Object)identifier);
        this.soundEndTicks.put(soundInstance, this.ticks + 20);
        this.sources.put(soundInstance, sourceManager);
        this.sounds.put(soundCategory, soundInstance);
        sourceManager.run(source -> {
            source.setPitch(i);
            source.setVolume(h);
            if (attenuationType == SoundInstance.AttenuationType.LINEAR) {
                source.setAttenuation(g);
            } else {
                source.disableAttenuation();
            }
            source.setLooping(bl2);
            source.setPosition(vec3d);
            source.setRelative(bl);
        });
        if (!sound.isStreamed()) {
            this.soundLoader.loadStatic(sound.getLocation()).thenAccept(staticSound -> sourceManager.run(source -> {
                source.setBuffer((StaticSound)staticSound);
                source.play();
            }));
        } else {
            this.soundLoader.loadStreamed(sound.getLocation()).thenAccept(audioStream -> sourceManager.run(source -> {
                source.setStream((AudioStream)audioStream);
                source.play();
            }));
        }
        if (soundInstance instanceof TickableSoundInstance) {
            this.tickingSounds.add((TickableSoundInstance)soundInstance);
        }
    }

    public void addPreloadedSound(Sound sound) {
        this.preloadedSounds.add(sound);
    }

    private float getAdjustedPitch(SoundInstance soundInstance) {
        return MathHelper.clamp(soundInstance.getPitch(), 0.5f, 2.0f);
    }

    private float getAdjustedVolume(SoundInstance soundInstance) {
        return MathHelper.clamp(soundInstance.getVolume() * this.getSoundVolume(soundInstance.getCategory()), 0.0f, 1.0f);
    }

    public void pauseAll() {
        if (this.started) {
            this.channel.execute(stream -> stream.forEach(Source::pause));
        }
    }

    public void resumeAll() {
        if (this.started) {
            this.channel.execute(stream -> stream.forEach(Source::resume));
        }
    }

    public void play(SoundInstance soundInstance, int i) {
        this.startTicks.put(soundInstance, this.ticks + i);
    }

    public void updateListenerPosition(Camera camera) {
        if (!this.started || !camera.isReady()) {
            return;
        }
        Vec3d vec3d = camera.getPos();
        Vec3d vec3d2 = camera.getHorizontalPlane();
        Vec3d vec3d3 = camera.getVerticalPlane();
        this.taskQueue.execute(() -> {
            this.listener.setPosition(vec3d);
            this.listener.setOrientation(vec3d2, vec3d3);
        });
    }

    public void stopSounds(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
        if (soundCategory != null) {
            for (SoundInstance soundInstance : this.sounds.get(soundCategory)) {
                if (identifier != null && !soundInstance.getId().equals(identifier)) continue;
                this.stop(soundInstance);
            }
        } else if (identifier == null) {
            this.stopAll();
        } else {
            for (SoundInstance soundInstance : this.sources.keySet()) {
                if (!soundInstance.getId().equals(identifier)) continue;
                this.stop(soundInstance);
            }
        }
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }
}

