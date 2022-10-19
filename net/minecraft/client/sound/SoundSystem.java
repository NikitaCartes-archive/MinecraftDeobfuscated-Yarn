/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundListener;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Environment(value=EnvType.CLIENT)
public class SoundSystem {
    private static final Marker MARKER = MarkerFactory.getMarker("SOUNDS");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float MIN_PITCH = 0.5f;
    private static final float MAX_PITCH = 2.0f;
    private static final float MIN_VOLUME = 0.0f;
    private static final float MAX_VOLUME = 1.0f;
    private static final int field_33025 = 20;
    private static final Set<Identifier> UNKNOWN_SOUNDS = Sets.newHashSet();
    private static final long MIN_TIME_INTERVAL_TO_RELOAD_SOUNDS = 1000L;
    public static final String FOR_THE_DEBUG = "FOR THE DEBUG!";
    public static final String OPENAL_SOFT_ON = "OpenAL Soft on ";
    public static final int OPENAL_SOFT_ON_LENGTH = "OpenAL Soft on ".length();
    private final SoundManager loader;
    private final GameOptions settings;
    private boolean started;
    private final SoundEngine soundEngine = new SoundEngine();
    private final SoundListener listener = this.soundEngine.getListener();
    private final SoundLoader soundLoader;
    private final SoundExecutor taskQueue = new SoundExecutor();
    private final Channel channel = new Channel(this.soundEngine, this.taskQueue);
    private int ticks;
    private long lastSoundDeviceCheckTime;
    private final AtomicReference<DeviceChangeStatus> deviceChangeStatus = new AtomicReference<DeviceChangeStatus>(DeviceChangeStatus.NO_CHANGE);
    private final Map<SoundInstance, Channel.SourceManager> sources = Maps.newHashMap();
    private final Multimap<SoundCategory, SoundInstance> sounds = HashMultimap.create();
    private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
    private final Map<SoundInstance, Integer> startTicks = Maps.newHashMap();
    private final Map<SoundInstance, Integer> soundEndTicks = Maps.newHashMap();
    private final List<SoundInstanceListener> listeners = Lists.newArrayList();
    private final List<TickableSoundInstance> soundsToPlayNextTick = Lists.newArrayList();
    private final List<Sound> preloadedSounds = Lists.newArrayList();

    public SoundSystem(SoundManager loader, GameOptions settings, ResourceFactory resourceFactory) {
        this.loader = loader;
        this.settings = settings;
        this.soundLoader = new SoundLoader(resourceFactory);
    }

    public void reloadSounds() {
        UNKNOWN_SOUNDS.clear();
        for (SoundEvent soundEvent : Registry.SOUND_EVENT) {
            Identifier identifier = soundEvent.getId();
            if (this.loader.get(identifier) != null) continue;
            LOGGER.warn("Missing sound for event: {}", (Object)Registry.SOUND_EVENT.getId(soundEvent));
            UNKNOWN_SOUNDS.add(identifier);
        }
        this.stop();
        this.start();
    }

    private synchronized void start() {
        if (this.started) {
            return;
        }
        try {
            String string = this.settings.getSoundDevice().getValue();
            this.soundEngine.init("".equals(string) ? null : string, this.settings.getDirectionalAudio().getValue());
            this.listener.init();
            this.listener.setVolume(this.settings.getSoundVolume(SoundCategory.MASTER));
            this.soundLoader.loadStatic(this.preloadedSounds).thenRun(this.preloadedSounds::clear);
            this.started = true;
            LOGGER.info(MARKER, "Sound engine started");
        } catch (RuntimeException runtimeException) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", runtimeException);
        }
    }

    private float getSoundVolume(@Nullable SoundCategory category) {
        if (category == null || category == SoundCategory.MASTER) {
            return 1.0f;
        }
        return this.settings.getSoundVolume(category);
    }

    public void updateSoundVolume(SoundCategory category, float volume) {
        if (!this.started) {
            return;
        }
        if (category == SoundCategory.MASTER) {
            this.listener.setVolume(volume);
            return;
        }
        this.sources.forEach((source2, sourceManager) -> {
            float f = this.getAdjustedVolume((SoundInstance)source2);
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

    public void stop(SoundInstance sound) {
        Channel.SourceManager sourceManager;
        if (this.started && (sourceManager = this.sources.get(sound)) != null) {
            sourceManager.run(Source::stop);
        }
    }

    public void stopAll() {
        if (this.started) {
            this.taskQueue.restart();
            this.sources.values().forEach(source -> source.run(Source::stop));
            this.sources.clear();
            this.channel.close();
            this.startTicks.clear();
            this.tickingSounds.clear();
            this.sounds.clear();
            this.soundEndTicks.clear();
            this.soundsToPlayNextTick.clear();
        }
    }

    public void registerListener(SoundInstanceListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(SoundInstanceListener listener) {
        this.listeners.remove(listener);
    }

    private boolean shouldReloadSounds() {
        boolean bl;
        if (this.soundEngine.isDeviceUnavailable()) {
            LOGGER.info("Audio device was lost!");
            return true;
        }
        long l = Util.getMeasuringTimeMs();
        boolean bl2 = bl = l - this.lastSoundDeviceCheckTime >= 1000L;
        if (bl) {
            this.lastSoundDeviceCheckTime = l;
            if (this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.NO_CHANGE, DeviceChangeStatus.ONGOING)) {
                String string = this.settings.getSoundDevice().getValue();
                Util.getIoWorkerExecutor().execute(() -> {
                    if ("".equals(string)) {
                        if (this.soundEngine.updateDeviceSpecifier()) {
                            LOGGER.info("System default audio device has changed!");
                            this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.ONGOING, DeviceChangeStatus.CHANGE_DETECTED);
                        }
                    } else if (!this.soundEngine.getCurrentDeviceName().equals(string) && this.soundEngine.getSoundDevices().contains(string)) {
                        LOGGER.info("Preferred audio device has become available!");
                        this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.ONGOING, DeviceChangeStatus.CHANGE_DETECTED);
                    }
                    this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.ONGOING, DeviceChangeStatus.NO_CHANGE);
                });
            }
        }
        return this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.CHANGE_DETECTED, DeviceChangeStatus.NO_CHANGE);
    }

    public void tick(boolean paused) {
        if (this.shouldReloadSounds()) {
            this.reloadSounds();
        }
        if (!paused) {
            this.tick();
        }
        this.channel.tick();
    }

    private void tick() {
        ++this.ticks;
        this.soundsToPlayNextTick.stream().filter(SoundInstance::canPlay).forEach(this::play);
        this.soundsToPlayNextTick.clear();
        for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
            if (!tickableSoundInstance.canPlay()) {
                this.stop(tickableSoundInstance);
            }
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
            if (SoundSystem.isRepeatDelayed(soundInstance)) {
                this.startTicks.put(soundInstance, this.ticks + soundInstance.getRepeatDelay());
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

    private static boolean canRepeatInstantly(SoundInstance sound) {
        return sound.getRepeatDelay() > 0;
    }

    private static boolean isRepeatDelayed(SoundInstance sound) {
        return sound.isRepeatable() && SoundSystem.canRepeatInstantly(sound);
    }

    private static boolean shouldRepeatInstantly(SoundInstance sound) {
        return sound.isRepeatable() && !SoundSystem.canRepeatInstantly(sound);
    }

    public boolean isPlaying(SoundInstance sound) {
        if (!this.started) {
            return false;
        }
        if (this.soundEndTicks.containsKey(sound) && this.soundEndTicks.get(sound) <= this.ticks) {
            return true;
        }
        return this.sources.containsKey(sound);
    }

    public void play(SoundInstance sound2) {
        boolean bl2;
        if (!this.started) {
            return;
        }
        if (!sound2.canPlay()) {
            return;
        }
        WeightedSoundSet weightedSoundSet = sound2.getSoundSet(this.loader);
        Identifier identifier = sound2.getId();
        if (weightedSoundSet == null) {
            if (UNKNOWN_SOUNDS.add(identifier)) {
                LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)identifier);
            }
            return;
        }
        Sound sound22 = sound2.getSound();
        if (sound22 == SoundManager.MISSING_SOUND) {
            if (UNKNOWN_SOUNDS.add(identifier)) {
                LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)identifier);
            }
            return;
        }
        float f = sound2.getVolume();
        float g = Math.max(f, 1.0f) * (float)sound22.getAttenuation();
        SoundCategory soundCategory = sound2.getCategory();
        float h = this.getAdjustedVolume(f, soundCategory);
        float i = this.getAdjustedPitch(sound2);
        SoundInstance.AttenuationType attenuationType = sound2.getAttenuationType();
        boolean bl = sound2.isRelative();
        if (h == 0.0f && !sound2.shouldAlwaysPlay()) {
            LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)sound22.getIdentifier());
            return;
        }
        Vec3d vec3d = new Vec3d(sound2.getX(), sound2.getY(), sound2.getZ());
        if (!this.listeners.isEmpty()) {
            boolean bl3 = bl2 = bl || attenuationType == SoundInstance.AttenuationType.NONE || this.listener.getPos().squaredDistanceTo(vec3d) < (double)(g * g);
            if (bl2) {
                for (SoundInstanceListener soundInstanceListener : this.listeners) {
                    soundInstanceListener.onSoundPlayed(sound2, weightedSoundSet);
                }
            } else {
                LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", (Object)identifier);
            }
        }
        if (this.listener.getVolume() <= 0.0f) {
            LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)identifier);
            return;
        }
        bl2 = SoundSystem.shouldRepeatInstantly(sound2);
        boolean bl3 = sound22.isStreamed();
        CompletableFuture<Channel.SourceManager> completableFuture = this.channel.createSource(sound22.isStreamed() ? SoundEngine.RunMode.STREAMING : SoundEngine.RunMode.STATIC);
        Channel.SourceManager sourceManager = completableFuture.join();
        if (sourceManager == null) {
            if (SharedConstants.isDevelopment) {
                LOGGER.warn("Failed to create new sound handle");
            }
            return;
        }
        LOGGER.debug(MARKER, "Playing sound {} for event {}", (Object)sound22.getIdentifier(), (Object)identifier);
        this.soundEndTicks.put(sound2, this.ticks + 20);
        this.sources.put(sound2, sourceManager);
        this.sounds.put(soundCategory, sound2);
        sourceManager.run(source -> {
            source.setPitch(i);
            source.setVolume(h);
            if (attenuationType == SoundInstance.AttenuationType.LINEAR) {
                source.setAttenuation(g);
            } else {
                source.disableAttenuation();
            }
            source.setLooping(bl2 && !bl3);
            source.setPosition(vec3d);
            source.setRelative(bl);
        });
        if (!bl3) {
            this.soundLoader.loadStatic(sound22.getLocation()).thenAccept(sound -> sourceManager.run(source -> {
                source.setBuffer((StaticSound)sound);
                source.play();
            }));
        } else {
            this.soundLoader.loadStreamed(sound22.getLocation(), bl2).thenAccept(stream -> sourceManager.run(source -> {
                source.setStream((AudioStream)stream);
                source.play();
            }));
        }
        if (sound2 instanceof TickableSoundInstance) {
            this.tickingSounds.add((TickableSoundInstance)sound2);
        }
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundsToPlayNextTick.add(sound);
    }

    public void addPreloadedSound(Sound sound) {
        this.preloadedSounds.add(sound);
    }

    private float getAdjustedPitch(SoundInstance sound) {
        return MathHelper.clamp(sound.getPitch(), 0.5f, 2.0f);
    }

    private float getAdjustedVolume(SoundInstance sound) {
        return this.getAdjustedVolume(sound.getVolume(), sound.getCategory());
    }

    private float getAdjustedVolume(float volume, SoundCategory category) {
        return MathHelper.clamp(volume * this.getSoundVolume(category), 0.0f, 1.0f);
    }

    public void pauseAll() {
        if (this.started) {
            this.channel.execute(sources -> sources.forEach(Source::pause));
        }
    }

    public void resumeAll() {
        if (this.started) {
            this.channel.execute(sources -> sources.forEach(Source::resume));
        }
    }

    public void play(SoundInstance sound, int delay) {
        this.startTicks.put(sound, this.ticks + delay);
    }

    public void updateListenerPosition(Camera camera) {
        if (!this.started || !camera.isReady()) {
            return;
        }
        Vec3d vec3d = camera.getPos();
        Vec3f vec3f = camera.getHorizontalPlane();
        Vec3f vec3f2 = camera.getVerticalPlane();
        this.taskQueue.execute(() -> {
            this.listener.setPosition(vec3d);
            this.listener.setOrientation(vec3f, vec3f2);
        });
    }

    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory category) {
        if (category != null) {
            for (SoundInstance soundInstance : this.sounds.get(category)) {
                if (id != null && !soundInstance.getId().equals(id)) continue;
                this.stop(soundInstance);
            }
        } else if (id == null) {
            this.stopAll();
        } else {
            for (SoundInstance soundInstance : this.sources.keySet()) {
                if (!soundInstance.getId().equals(id)) continue;
                this.stop(soundInstance);
            }
        }
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }

    public List<String> getSoundDevices() {
        return this.soundEngine.getSoundDevices();
    }

    @Environment(value=EnvType.CLIENT)
    static enum DeviceChangeStatus {
        ONGOING,
        CHANGE_DETECTED,
        NO_CHANGE;

    }
}

