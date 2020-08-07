package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.resource.language.I18n;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SoundManager extends SinglePreparationResourceReloadListener<SoundManager.SoundList> {
	public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.RegistrationType.field_5474, false, false, 16);
	private static final Logger LOGGER = LogManager.getLogger();
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

	protected SoundManager.SoundList method_18180(ResourceManager resourceManager, Profiler profiler) {
		SoundManager.SoundList soundList = new SoundManager.SoundList();
		profiler.startTick();

		for (String string : resourceManager.getAllNamespaces()) {
			profiler.push(string);

			try {
				for (Resource resource : resourceManager.getAllResources(new Identifier(string, "sounds.json"))) {
					profiler.push(resource.getResourcePackName());

					try {
						InputStream inputStream = resource.getInputStream();
						Throwable var10 = null;

						try {
							Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
							Throwable var12 = null;

							try {
								profiler.push("parse");
								Map<String, SoundEntry> map = JsonHelper.deserialize(GSON, reader, TYPE);
								profiler.swap("register");

								for (Entry<String, SoundEntry> entry : map.entrySet()) {
									soundList.register(new Identifier(string, (String)entry.getKey()), (SoundEntry)entry.getValue(), resourceManager);
								}

								profiler.pop();
							} catch (Throwable var41) {
								var12 = var41;
								throw var41;
							} finally {
								if (reader != null) {
									if (var12 != null) {
										try {
											reader.close();
										} catch (Throwable var40) {
											var12.addSuppressed(var40);
										}
									} else {
										reader.close();
									}
								}
							}
						} catch (Throwable var43) {
							var10 = var43;
							throw var43;
						} finally {
							if (inputStream != null) {
								if (var10 != null) {
									try {
										inputStream.close();
									} catch (Throwable var39) {
										var10.addSuppressed(var39);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (RuntimeException var45) {
						LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", resource.getResourcePackName(), var45);
					}

					profiler.pop();
				}
			} catch (IOException var46) {
			}

			profiler.pop();
		}

		profiler.endTick();
		return soundList;
	}

	protected void method_18182(SoundManager.SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
		soundList.addTo(this.sounds, this.soundSystem);

		for (Identifier identifier : this.sounds.keySet()) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
			if (weightedSoundSet.getSubtitle() instanceof TranslatableText) {
				String string = ((TranslatableText)weightedSoundSet.getSubtitle()).getKey();
				if (!I18n.hasTranslation(string)) {
					LOGGER.debug("Missing subtitle {} for event: {}", string, identifier);
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

	private static boolean isSoundResourcePresent(Sound sound, Identifier identifier, ResourceManager resourceManager) {
		Identifier identifier2 = sound.getLocation();
		if (!resourceManager.containsResource(identifier2)) {
			LOGGER.warn("File {} does not exist, cannot add it to event {}", identifier2, identifier);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	public WeightedSoundSet get(Identifier identifier) {
		return (WeightedSoundSet)this.sounds.get(identifier);
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
		if (category == SoundCategory.field_15250 && volume <= 0.0F) {
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

	public void stopSounds(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		this.soundSystem.stopSounds(identifier, soundCategory);
	}

	public String getDebugString() {
		return this.soundSystem.getDebugString();
	}

	@Environment(EnvType.CLIENT)
	public static class SoundList {
		private final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.<Identifier, WeightedSoundSet>newHashMap();

		protected SoundList() {
		}

		private void register(Identifier id, SoundEntry entry, ResourceManager resourceManager) {
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
					case field_5474:
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

							public Sound method_4883() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier);
								if (weightedSoundSet == null) {
									return SoundManager.MISSING_SOUND;
								} else {
									Sound sound = weightedSoundSet.method_4887();
									return new Sound(
										sound.getIdentifier().toString(),
										sound.getVolume() * sound.getVolume(),
										sound.getPitch() * sound.getPitch(),
										sound.getWeight(),
										Sound.RegistrationType.field_5474,
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

		public void addTo(Map<Identifier, WeightedSoundSet> map, SoundSystem soundSystem) {
			map.clear();

			for (Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
				((WeightedSoundSet)entry.getValue()).preload(soundSystem);
			}
		}
	}
}
