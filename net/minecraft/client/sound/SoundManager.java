/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEntryDeserializer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundManager
extends SinglePreparationResourceReloader<SoundList> {
    public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0f, 1.0f, 1, Sound.RegistrationType.FILE, false, false, 16);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String field_33026 = "sounds.json";
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeAdapter((Type)((Object)SoundEntry.class), new SoundEntryDeserializer()).create();
    private static final TypeToken<Map<String, SoundEntry>> TYPE = new TypeToken<Map<String, SoundEntry>>(){};
    private final Map<Identifier, WeightedSoundSet> sounds = Maps.newHashMap();
    private final SoundSystem soundSystem;

    public SoundManager(ResourceManager resourceManager, GameOptions gameOptions) {
        this.soundSystem = new SoundSystem(this, gameOptions, resourceManager);
    }

    @Override
    protected SoundList prepare(ResourceManager resourceManager, Profiler profiler) {
        SoundList soundList = new SoundList();
        profiler.startTick();
        for (String string : resourceManager.getAllNamespaces()) {
            profiler.push(string);
            try {
                List<Resource> list = resourceManager.getAllResources(new Identifier(string, field_33026));
                for (Resource resource : list) {
                    profiler.push(resource.getResourcePackName());
                    try (InputStream inputStream = resource.getInputStream();
                         InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
                        profiler.push("parse");
                        Map<String, SoundEntry> map = JsonHelper.deserialize(GSON, (Reader)reader, TYPE);
                        profiler.swap("register");
                        for (Map.Entry<String, SoundEntry> entry : map.entrySet()) {
                            soundList.register(new Identifier(string, entry.getKey()), entry.getValue(), resourceManager);
                        }
                        profiler.pop();
                    } catch (RuntimeException runtimeException) {
                        LOGGER.warn("Invalid {} in resourcepack: '{}'", (Object)field_33026, (Object)resource.getResourcePackName(), (Object)runtimeException);
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
        soundList.addTo(this.sounds, this.soundSystem);
        for (Identifier identifier : this.sounds.keySet()) {
            String string;
            WeightedSoundSet weightedSoundSet = this.sounds.get(identifier);
            if (!(weightedSoundSet.getSubtitle() instanceof TranslatableText) || I18n.hasTranslation(string = ((TranslatableText)weightedSoundSet.getSubtitle()).getKey())) continue;
            LOGGER.debug("Missing subtitle {} for event: {}", (Object)string, (Object)identifier);
        }
        if (LOGGER.isDebugEnabled()) {
            for (Identifier identifier : this.sounds.keySet()) {
                if (Registry.SOUND_EVENT.containsId(identifier)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)identifier);
            }
        }
        this.soundSystem.reloadSounds();
    }

    private static boolean isSoundResourcePresent(Sound sound, Identifier id, ResourceManager resourceManager) {
        Identifier identifier = sound.getLocation();
        if (!resourceManager.containsResource(identifier)) {
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

    public void tick(boolean bl) {
        this.soundSystem.tick(bl);
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

    public void stop(SoundInstance soundInstance) {
        this.soundSystem.stop(soundInstance);
    }

    public boolean isPlaying(SoundInstance soundInstance) {
        return this.soundSystem.isPlaying(soundInstance);
    }

    public void registerListener(SoundInstanceListener soundInstanceListener) {
        this.soundSystem.registerListener(soundInstanceListener);
    }

    public void unregisterListener(SoundInstanceListener soundInstanceListener) {
        this.soundSystem.unregisterListener(soundInstanceListener);
    }

    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory soundCategory) {
        this.soundSystem.stopSounds(id, soundCategory);
    }

    public String getDebugString() {
        return this.soundSystem.getDebugString();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SoundList {
        private final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.newHashMap();

        protected SoundList() {
        }

        private void register(Identifier id, SoundEntry entry, ResourceManager resourceManager) {
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
            block4: for (final Sound sound : entry.getSounds()) {
                SoundContainer<Sound> soundContainer;
                final Identifier identifier = sound.getIdentifier();
                switch (sound.getRegistrationType()) {
                    case FILE: {
                        if (!SoundManager.isSoundResourcePresent(sound, id, resourceManager)) continue block4;
                        soundContainer = sound;
                        break;
                    }
                    case SOUND_EVENT: {
                        soundContainer = new SoundContainer<Sound>(){

                            @Override
                            public int getWeight() {
                                WeightedSoundSet weightedSoundSet = (WeightedSoundSet)loadedSounds.get(identifier);
                                return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
                            }

                            @Override
                            public Sound getSound() {
                                WeightedSoundSet weightedSoundSet = (WeightedSoundSet)loadedSounds.get(identifier);
                                if (weightedSoundSet == null) {
                                    return MISSING_SOUND;
                                }
                                Sound sound2 = weightedSoundSet.getSound();
                                return new Sound(sound2.getIdentifier().toString(), sound2.getVolume() * sound.getVolume(), sound2.getPitch() * sound.getPitch(), sound.getWeight(), Sound.RegistrationType.FILE, sound2.isStreamed() || sound.isStreamed(), sound2.isPreloaded(), sound2.getAttenuation());
                            }

                            @Override
                            public void preload(SoundSystem soundSystem) {
                                WeightedSoundSet weightedSoundSet = (WeightedSoundSet)loadedSounds.get(identifier);
                                if (weightedSoundSet == null) {
                                    return;
                                }
                                weightedSoundSet.preload(soundSystem);
                            }

                            @Override
                            public /* synthetic */ Object getSound() {
                                return this.getSound();
                            }
                        };
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + (Object)((Object)sound.getRegistrationType()));
                    }
                }
                weightedSoundSet.add(soundContainer);
            }
        }

        public void addTo(Map<Identifier, WeightedSoundSet> map, SoundSystem soundSystem) {
            map.clear();
            for (Map.Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
                entry.getValue().preload(soundSystem);
            }
        }
    }
}

