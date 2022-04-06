package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.MultipliedFloatSupplier;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SoundManager extends SinglePreparationResourceReloader<SoundManager.SoundList> {
	public static final Sound MISSING_SOUND = new Sound(
		"meta:missing_sound", ConstantFloatProvider.create(1.0F), ConstantFloatProvider.create(1.0F), 1, Sound.RegistrationType.FILE, false, false, 16
	);
	static final Logger LOGGER = LogUtils.getLogger();
	private static final String SOUNDS_JSON = "sounds.json";
	private static final Gson GSON = new GsonBuilder()
		.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
		.registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer())
		.create();
	private static final TypeToken<Map<String, SoundEntry>> TYPE = new TypeToken<Map<String, SoundEntry>>() {
	};
	private final Map<Identifier, WeightedSoundSet> sounds = Maps.<Identifier, WeightedSoundSet>newHashMap();
	private final SoundSystem soundSystem;

	public SoundManager(ResourceManager resourceManager, GameOptions gameOptions) {
		this.soundSystem = new SoundSystem(this, gameOptions, resourceManager);
	}

	protected SoundManager.SoundList prepare(ResourceManager resourceManager, Profiler profiler) {
		SoundManager.SoundList soundList = new SoundManager.SoundList();
		profiler.startTick();

		for (String string : resourceManager.getAllNamespaces()) {
			profiler.push(string);

			try {
				for (Resource resource : resourceManager.getAllResources(new Identifier(string, "sounds.json"))) {
					profiler.push(resource.getResourcePackName());

					try {
						Reader reader = resource.getReader();

						try {
							profiler.push("parse");
							Map<String, SoundEntry> map = JsonHelper.deserialize(GSON, reader, TYPE);
							profiler.swap("register");

							for (Entry<String, SoundEntry> entry : map.entrySet()) {
								soundList.register(new Identifier(string, (String)entry.getKey()), (SoundEntry)entry.getValue(), resourceManager);
							}

							profiler.pop();
						} catch (Throwable var14) {
							if (reader != null) {
								try {
									reader.close();
								} catch (Throwable var13) {
									var14.addSuppressed(var13);
								}
							}

							throw var14;
						}

						if (reader != null) {
							reader.close();
						}
					} catch (RuntimeException var15) {
						LOGGER.warn("Invalid {} in resourcepack: '{}'", "sounds.json", resource.getResourcePackName(), var15);
					}

					profiler.pop();
				}
			} catch (IOException var16) {
			}

			profiler.pop();
		}

		profiler.endTick();
		return soundList;
	}

	protected void apply(SoundManager.SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
		soundList.reload(this.sounds, this.soundSystem);
		if (SharedConstants.isDevelopment) {
			for (Identifier identifier : this.sounds.keySet()) {
				WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
				if (weightedSoundSet.getSubtitle() instanceof TranslatableText) {
					String string = ((TranslatableText)weightedSoundSet.getSubtitle()).getKey();
					if (!I18n.hasTranslation(string) && Registry.SOUND_EVENT.containsId(identifier)) {
						LOGGER.error("Missing subtitle {} for sound event: {}", string, identifier);
					}
				}
			}
		}

		if (LOGGER.isDebugEnabled()) {
			for (Identifier identifierx : this.sounds.keySet()) {
				if (!Registry.SOUND_EVENT.containsId(identifierx)) {
					LOGGER.debug("Not having sound event for: {}", identifierx);
				}
			}
		}

		this.soundSystem.reloadSounds();
	}

	public List<String> getSoundDevices() {
		return this.soundSystem.getSoundDevices();
	}

	static boolean isSoundResourcePresent(Sound sound, Identifier id, ResourceManager resourceManager) {
		Identifier identifier = sound.getLocation();
		if (resourceManager.getResource(identifier).isEmpty()) {
			LOGGER.warn("File {} does not exist, cannot add it to event {}", identifier, id);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	public WeightedSoundSet get(Identifier id) {
		return (WeightedSoundSet)this.sounds.get(id);
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
		if (category == SoundCategory.MASTER && volume <= 0.0F) {
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

	@Environment(EnvType.CLIENT)
	protected static class SoundList {
		final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.<Identifier, WeightedSoundSet>newHashMap();

		void register(Identifier id, SoundEntry entry, ResourceManager resourceManager) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.loadedSounds.get(id);
			boolean bl = weightedSoundSet == null;
			if (bl || entry.canReplace()) {
				if (!bl) {
					SoundManager.LOGGER.debug("Replaced sound event location {}", id);
				}

				weightedSoundSet = new WeightedSoundSet(id, entry.getSubtitle());
				this.loadedSounds.put(id, weightedSoundSet);
			}

			for (final Sound sound : entry.getSounds()) {
				final Identifier identifier = sound.getIdentifier();
				SoundContainer<Sound> soundContainer;
				switch (sound.getRegistrationType()) {
					case FILE:
						if (!SoundManager.isSoundResourcePresent(sound, id, resourceManager)) {
							continue;
						}

						soundContainer = sound;
						break;
					case SOUND_EVENT:
						soundContainer = new SoundContainer<Sound>() {
							@Override
							public int getWeight() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier);
								return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
							}

							public Sound getSound(AbstractRandom abstractRandom) {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier);
								if (weightedSoundSet == null) {
									return SoundManager.MISSING_SOUND;
								} else {
									Sound sound = weightedSoundSet.getSound(abstractRandom);
									return new Sound(
										sound.getIdentifier().toString(),
										new MultipliedFloatSupplier(sound.getVolume(), sound.getVolume()),
										new MultipliedFloatSupplier(sound.getPitch(), sound.getPitch()),
										sound.getWeight(),
										Sound.RegistrationType.FILE,
										sound.isStreamed() || sound.isStreamed(),
										sound.isPreloaded(),
										sound.getAttenuation()
									);
								}
							}

							@Override
							public void preload(SoundSystem soundSystem) {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier);
								if (weightedSoundSet != null) {
									weightedSoundSet.preload(soundSystem);
								}
							}
						};
						break;
					default:
						throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getRegistrationType());
				}

				weightedSoundSet.add(soundContainer);
			}
		}

		public void reload(Map<Identifier, WeightedSoundSet> sounds, SoundSystem soundSystem) {
			sounds.clear();

			for (Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
				sounds.put((Identifier)entry.getKey(), (WeightedSoundSet)entry.getValue());
				((WeightedSoundSet)entry.getValue()).preload(soundSystem);
			}
		}
	}
}
