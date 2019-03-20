package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import net.minecraft.resource.SupplyingResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SoundManager extends SupplyingResourceReloadListener<SoundManager.Result> {
	public static final Sound SOUND_MISSING = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.RegistrationType.FILE, false, false, 16);
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer())
		.registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer())
		.create();
	private static final ParameterizedType TYPE = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{String.class, SoundEntry.class};
		}

		public Type getRawType() {
			return Map.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};
	private final Map<Identifier, WeightedSoundSet> sounds = Maps.<Identifier, WeightedSoundSet>newHashMap();
	private final SoundSystem soundSystem;

	public SoundManager(ResourceManager resourceManager, GameOptions gameOptions) {
		this.soundSystem = new SoundSystem(this, gameOptions, resourceManager);
	}

	protected SoundManager.Result method_18180(ResourceManager resourceManager, Profiler profiler) {
		SoundManager.Result result = new SoundManager.Result();
		profiler.startTick();

		for (String string : resourceManager.getAllNamespaces()) {
			profiler.push(string);

			try {
				for (Resource resource : resourceManager.getAllResources(new Identifier(string, "sounds.json"))) {
					profiler.push(resource.getResourcePackName());

					try {
						profiler.push("parse");
						Map<String, SoundEntry> map = readSounds(resource.getInputStream());
						profiler.swap("register");

						for (Entry<String, SoundEntry> entry : map.entrySet()) {
							result.method_18187(new Identifier(string, (String)entry.getKey()), (SoundEntry)entry.getValue(), resourceManager);
						}

						profiler.pop();
					} catch (RuntimeException var12) {
						LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", resource.getResourcePackName(), var12);
					}

					profiler.pop();
				}
			} catch (IOException var13) {
			}

			profiler.pop();
		}

		profiler.endTick();
		return result;
	}

	protected void method_18182(SoundManager.Result result, ResourceManager resourceManager, Profiler profiler) {
		result.addTo(this.sounds, this.soundSystem);

		for (Identifier identifier : this.sounds.keySet()) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
			if (weightedSoundSet.getSubtitle() instanceof TranslatableTextComponent) {
				String string = ((TranslatableTextComponent)weightedSoundSet.getSubtitle()).getKey();
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

	@Nullable
	protected static Map<String, SoundEntry> readSounds(InputStream inputStream) {
		Map var1;
		try {
			var1 = JsonHelper.deserialize(GSON, new InputStreamReader(inputStream, StandardCharsets.UTF_8), TYPE);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return var1;
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
		this.soundSystem.close();
	}

	public void tick() {
		this.soundSystem.tick();
	}

	public void playAll() {
		this.soundSystem.playAll();
	}

	public void updateSoundVolume(SoundCategory soundCategory, float f) {
		if (soundCategory == SoundCategory.field_15250 && f <= 0.0F) {
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

	@Environment(EnvType.CLIENT)
	public static class Result {
		private final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.<Identifier, WeightedSoundSet>newHashMap();

		protected Result() {
		}

		private void method_18187(Identifier identifier, SoundEntry soundEntry, ResourceManager resourceManager) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.loadedSounds.get(identifier);
			boolean bl = weightedSoundSet == null;
			if (bl || soundEntry.isReplaceable()) {
				if (!bl) {
					SoundManager.LOGGER.debug("Replaced sound event location {}", identifier);
				}

				weightedSoundSet = new WeightedSoundSet(identifier, soundEntry.getSubtitle());
				this.loadedSounds.put(identifier, weightedSoundSet);
			}

			for (final Sound sound : soundEntry.getSounds()) {
				final Identifier identifier2 = sound.getIdentifier();
				SoundContainer<Sound> soundContainer;
				switch (sound.getRegistrationType()) {
					case FILE:
						if (!SoundManager.isSoundResourcePresent(sound, identifier, resourceManager)) {
							continue;
						}

						soundContainer = sound;
						break;
					case EVENT:
						soundContainer = new SoundContainer<Sound>() {
							@Override
							public int getWeight() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)Result.this.loadedSounds.get(identifier2);
								return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
							}

							public Sound method_4883() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)Result.this.loadedSounds.get(identifier2);
								if (weightedSoundSet == null) {
									return SoundManager.SOUND_MISSING;
								} else {
									Sound sound = weightedSoundSet.method_4887();
									return new Sound(
										sound.getIdentifier().toString(),
										sound.getVolume() * sound.getVolume(),
										sound.getPitch() * sound.getPitch(),
										sound.getWeight(),
										Sound.RegistrationType.FILE,
										sound.isStreamed() || sound.isStreamed(),
										sound.method_4764(),
										sound.method_4770()
									);
								}
							}

							@Override
							public void addTo(SoundSystem soundSystem) {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)Result.this.loadedSounds.get(identifier2);
								if (weightedSoundSet != null) {
									weightedSoundSet.addTo(soundSystem);
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
				((WeightedSoundSet)entry.getValue()).addTo(soundSystem);
			}
		}
	}
}
