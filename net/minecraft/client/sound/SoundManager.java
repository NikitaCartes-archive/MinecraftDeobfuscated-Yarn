/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEntryDeserializer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.MultipliedFloatSupplier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SoundManager
extends SinglePreparationResourceReloader<SoundList> {
    public static final Sound MISSING_SOUND = new Sound("minecraft:empty", ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), 1, Sound.RegistrationType.FILE, false, false, 16);
    public static final Identifier INTENTIONALLY_EMPTY_ID = new Identifier("minecraft", "intentionally_empty");
    public static final WeightedSoundSet INTENTIONALLY_EMPTY_SOUND_SET = new WeightedSoundSet(INTENTIONALLY_EMPTY_ID, null);
    public static final Sound INTENTIONALLY_EMPTY_SOUND = new Sound(INTENTIONALLY_EMPTY_ID.toString(), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), 1, Sound.RegistrationType.FILE, false, false, 16);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String SOUNDS_JSON = "sounds.json";
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeAdapter((Type)((Object)SoundEntry.class), new SoundEntryDeserializer()).create();
    private static final TypeToken<Map<String, SoundEntry>> TYPE = new TypeToken<Map<String, SoundEntry>>(){};
    private final Map<Identifier, WeightedSoundSet> sounds = Maps.newHashMap();
    private final SoundSystem soundSystem;
    private final Map<Identifier, Resource> soundResources = new HashMap<Identifier, Resource>();

    public SoundManager(GameOptions gameOptions) {
        this.soundSystem = new SoundSystem(this, gameOptions, ResourceFactory.fromMap(this.soundResources));
    }

    @Override
    protected SoundList prepare(ResourceManager resourceManager, Profiler profiler) {
        SoundList soundList = new SoundList();
        profiler.startTick();
        profiler.push("list");
        soundList.findSounds(resourceManager);
        profiler.pop();
        for (String string : resourceManager.getAllNamespaces()) {
            profiler.push(string);
            try {
                List<Resource> list = resourceManager.getAllResources(new Identifier(string, SOUNDS_JSON));
                for (Resource resource : list) {
                    profiler.push(resource.getResourcePackName());
                    try (BufferedReader reader = resource.getReader();){
                        profiler.push("parse");
                        Map<String, SoundEntry> map = JsonHelper.deserialize(GSON, (Reader)reader, TYPE);
                        profiler.swap("register");
                        for (Map.Entry<String, SoundEntry> entry : map.entrySet()) {
                            soundList.register(new Identifier(string, entry.getKey()), entry.getValue());
                        }
                        profiler.pop();
                    } catch (RuntimeException runtimeException) {
                        LOGGER.warn("Invalid {} in resourcepack: '{}'", SOUNDS_JSON, resource.getResourcePackName(), runtimeException);
                    }
                    profiler.pop();
                }
            } catch (IOException iOException) {
                // empty catch block
            }
            profiler.pop();
        }
        profiler.endTick();
        return soundList;
    }

    @Override
    protected void apply(SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
        soundList.reload(this.sounds, this.soundResources, this.soundSystem);
        if (SharedConstants.isDevelopment) {
            for (Identifier identifier : this.sounds.keySet()) {
                WeightedSoundSet weightedSoundSet = this.sounds.get(identifier);
                if (Texts.hasTranslation(weightedSoundSet.getSubtitle()) || !Registries.SOUND_EVENT.containsId(identifier)) continue;
                LOGGER.error("Missing subtitle {} for sound event: {}", (Object)weightedSoundSet.getSubtitle(), (Object)identifier);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            for (Identifier identifier : this.sounds.keySet()) {
                if (Registries.SOUND_EVENT.containsId(identifier)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)identifier);
            }
        }
        this.soundSystem.reloadSounds();
    }

    public List<String> getSoundDevices() {
        return this.soundSystem.getSoundDevices();
    }

    static boolean isSoundResourcePresent(Sound sound, Identifier id, ResourceFactory resourceFactory) {
        Identifier identifier = sound.getLocation();
        if (resourceFactory.getResource(identifier).isEmpty()) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)identifier, (Object)id);
            return false;
        }
        return true;
    }

    @Nullable
    public WeightedSoundSet get(Identifier id) {
        return this.sounds.get(id);
    }

    public Collection<Identifier> getKeys() {
        return this.sounds.keySet();
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundSystem.playNextTick(sound);
    }

    public void play(SoundInstance sound) {
        this.soundSystem.play(sound);
    }

    public void play(SoundInstance sound, int delay) {
        this.soundSystem.play(sound, delay);
    }

    public void updateListenerPosition(Camera camera) {
        this.soundSystem.updateListenerPosition(camera);
    }

    public void pauseAll() {
        this.soundSystem.pauseAll();
    }

    public void stopAll() {
        this.soundSystem.stopAll();
    }

    public void close() {
        this.soundSystem.stop();
    }

    public void tick(boolean paused) {
        this.soundSystem.tick(paused);
    }

    public void resumeAll() {
        this.soundSystem.resumeAll();
    }

    public void updateSoundVolume(SoundCategory category, float volume) {
        if (category == SoundCategory.MASTER && volume <= 0.0f) {
            this.stopAll();
        }
        this.soundSystem.updateSoundVolume(category, volume);
    }

    public void stop(SoundInstance sound) {
        this.soundSystem.stop(sound);
    }

    public boolean isPlaying(SoundInstance sound) {
        return this.soundSystem.isPlaying(sound);
    }

    public void registerListener(SoundInstanceListener listener) {
        this.soundSystem.registerListener(listener);
    }

    public void unregisterListener(SoundInstanceListener listener) {
        this.soundSystem.unregisterListener(listener);
    }

    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory soundCategory) {
        this.soundSystem.stopSounds(id, soundCategory);
    }

    public String getDebugString() {
        return this.soundSystem.getDebugString();
    }

    public void reloadSounds() {
        this.soundSystem.reloadSounds();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    protected static class SoundList {
        final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.newHashMap();
        private Map<Identifier, Resource> foundSounds = Map.of();

        protected SoundList() {
        }

        void findSounds(ResourceManager resourceManager) {
            this.foundSounds = Sound.FINDER.findResources(resourceManager);
        }

        void register(Identifier id, SoundEntry entry) {
            boolean bl;
            WeightedSoundSet weightedSoundSet = this.loadedSounds.get(id);
            boolean bl2 = bl = weightedSoundSet == null;
            if (bl || entry.canReplace()) {
                if (!bl) {
                    LOGGER.debug("Replaced sound event location {}", (Object)id);
                }
                weightedSoundSet = new WeightedSoundSet(id, entry.getSubtitle());
                this.loadedSounds.put(id, weightedSoundSet);
            }
            ResourceFactory resourceFactory = ResourceFactory.fromMap(this.foundSounds);
            block4: for (final Sound sound : entry.getSounds()) {
                final Identifier identifier = sound.getIdentifier();
                weightedSoundSet.add(switch (sound.getRegistrationType()) {
                    case Sound.RegistrationType.FILE -> {
                        if (!SoundManager.isSoundResourcePresent(sound, id, resourceFactory)) continue block4;
                        yield sound;
                    }
                    case Sound.RegistrationType.SOUND_EVENT -> new SoundContainer<Sound>(){

                        @Override
                        public int getWeight() {
                            WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                            return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
                        }

                        @Override
                        public Sound getSound(Random random) {
                            WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                            if (weightedSoundSet == null) {
                                return MISSING_SOUND;
                            }
                            Sound sound2 = weightedSoundSet.getSound(random);
                            return new Sound(sound2.getIdentifier().toString(), new MultipliedFloatSupplier(sound2.getVolume(), sound.getVolume()), new MultipliedFloatSupplier(sound2.getPitch(), sound.getPitch()), sound.getWeight(), Sound.RegistrationType.FILE, sound2.isStreamed() || sound.isStreamed(), sound2.isPreloaded(), sound2.getAttenuation());
                        }

                        @Override
                        public void preload(SoundSystem soundSystem) {
                            WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                            if (weightedSoundSet == null) {
                                return;
                            }
                            weightedSoundSet.preload(soundSystem);
                        }

                        @Override
                        public /* synthetic */ Object getSound(Random random) {
                            return this.getSound(random);
                        }
                    };
                    default -> throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getRegistrationType());
                });
            }
        }

        public void reload(Map<Identifier, WeightedSoundSet> sounds, Map<Identifier, Resource> soundResources, SoundSystem system) {
            sounds.clear();
            soundResources.clear();
            soundResources.putAll(this.foundSounds);
            for (Map.Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
                sounds.put(entry.getKey(), entry.getValue());
                entry.getValue().preload(system);
            }
        }
    }
}

