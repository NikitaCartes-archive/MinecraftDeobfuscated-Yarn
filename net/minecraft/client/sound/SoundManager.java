/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.ListenerSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEntryDeserializer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundManager
extends SinglePreparationResourceReloadListener<SoundList> {
    public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0f, 1.0f, 1, Sound.RegistrationType.FILE, false, false, 16);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeAdapter((Type)((Object)SoundEntry.class), new SoundEntryDeserializer()).create();
    private static final ParameterizedType TYPE = new ParameterizedType(){

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundEntry.class};
        }

        @Override
        public Type getRawType() {
            return Map.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };
    private final Map<Identifier, WeightedSoundSet> sounds = Maps.newHashMap();
    private final SoundSystem soundSystem;

    public SoundManager(ResourceManager resourceManager, GameOptions gameOptions) {
        this.soundSystem = new SoundSystem(this, gameOptions, resourceManager);
    }

    protected SoundList method_18180(ResourceManager resourceManager, Profiler profiler) {
        SoundList soundList = new SoundList();
        profiler.startTick();
        for (String string : resourceManager.getAllNamespaces()) {
            profiler.push(string);
            try {
                List<Resource> list = resourceManager.getAllResources(new Identifier(string, "sounds.json"));
                for (Resource resource : list) {
                    profiler.push(resource.getResourcePackName());
                    try {
                        profiler.push("parse");
                        Map<String, SoundEntry> map = SoundManager.readSounds(resource.getInputStream());
                        profiler.swap("register");
                        for (Map.Entry<String, SoundEntry> entry : map.entrySet()) {
                            soundList.register(new Identifier(string, entry.getKey()), entry.getValue(), resourceManager);
                        }
                        profiler.pop();
                    } catch (RuntimeException runtimeException) {
                        LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", (Object)resource.getResourcePackName(), (Object)runtimeException);
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

    protected void method_18182(SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
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

    @Nullable
    protected static Map<String, SoundEntry> readSounds(InputStream inputStream) {
        try {
            Map map = (Map)JsonHelper.deserialize(GSON, (Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), (Type)TYPE);
            return map;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static boolean isSoundResourcePresent(Sound sound, Identifier identifier, ResourceManager resourceManager) {
        Identifier identifier2 = sound.getLocation();
        if (!resourceManager.containsResource(identifier2)) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)identifier2, (Object)identifier);
            return false;
        }
        return true;
    }

    @Nullable
    public WeightedSoundSet get(Identifier identifier) {
        return this.sounds.get(identifier);
    }

    public Collection<Identifier> getKeys() {
        return this.sounds.keySet();
    }

    public void play(SoundInstance soundInstance) {
        this.soundSystem.play(soundInstance);
    }

    public void play(SoundInstance soundInstance, int i) {
        this.soundSystem.play(soundInstance, i);
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

    public void updateSoundVolume(SoundCategory soundCategory, float f) {
        if (soundCategory == SoundCategory.MASTER && f <= 0.0f) {
            this.stopAll();
        }
        this.soundSystem.updateSoundVolume(soundCategory, f);
    }

    public void stop(SoundInstance soundInstance) {
        this.soundSystem.stop(soundInstance);
    }

    public boolean isPlaying(SoundInstance soundInstance) {
        return this.soundSystem.isPlaying(soundInstance);
    }

    public void registerListener(ListenerSoundInstance listenerSoundInstance) {
        this.soundSystem.registerListener(listenerSoundInstance);
    }

    public void unregisterListener(ListenerSoundInstance listenerSoundInstance) {
        this.soundSystem.unregisterListener(listenerSoundInstance);
    }

    public void stopSounds(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
        this.soundSystem.stopSounds(identifier, soundCategory);
    }

    public String getDebugString() {
        return this.soundSystem.getDebugString();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, Profiler profiler) {
        return this.method_18180(resourceManager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SoundList {
        private final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.newHashMap();

        protected SoundList() {
        }

        private void register(Identifier identifier, SoundEntry soundEntry, ResourceManager resourceManager) {
            boolean bl;
            WeightedSoundSet weightedSoundSet = this.loadedSounds.get(identifier);
            boolean bl2 = bl = weightedSoundSet == null;
            if (bl || soundEntry.canReplace()) {
                if (!bl) {
                    LOGGER.debug("Replaced sound event location {}", (Object)identifier);
                }
                weightedSoundSet = new WeightedSoundSet(identifier, soundEntry.getSubtitle());
                this.loadedSounds.put(identifier, weightedSoundSet);
            }
            block4: for (final Sound sound : soundEntry.getSounds()) {
                SoundContainer<Sound> soundContainer;
                final Identifier identifier2 = sound.getIdentifier();
                switch (sound.getRegistrationType()) {
                    case FILE: {
                        if (!SoundManager.isSoundResourcePresent(sound, identifier, resourceManager)) continue block4;
                        soundContainer = sound;
                        break;
                    }
                    case SOUND_EVENT: {
                        soundContainer = new SoundContainer<Sound>(){

                            @Override
                            public int getWeight() {
                                WeightedSoundSet weightedSoundSet = (WeightedSoundSet)loadedSounds.get(identifier2);
                                return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
                            }

                            public Sound method_4883() {
                                WeightedSoundSet weightedSoundSet = (WeightedSoundSet)loadedSounds.get(identifier2);
                                if (weightedSoundSet == null) {
                                    return MISSING_SOUND;
                                }
                                Sound sound2 = weightedSoundSet.method_4887();
                                return new Sound(sound2.getIdentifier().toString(), sound2.getVolume() * sound.getVolume(), sound2.getPitch() * sound.getPitch(), sound.getWeight(), Sound.RegistrationType.FILE, sound2.isStreamed() || sound.isStreamed(), sound2.isPreloaded(), sound2.getAttenuation());
                            }

                            @Override
                            public void preload(SoundSystem soundSystem) {
                                WeightedSoundSet weightedSoundSet = (WeightedSoundSet)loadedSounds.get(identifier2);
                                if (weightedSoundSet == null) {
                                    return;
                                }
                                weightedSoundSet.preload(soundSystem);
                            }

                            @Override
                            public /* synthetic */ Object getSound() {
                                return this.method_4883();
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

