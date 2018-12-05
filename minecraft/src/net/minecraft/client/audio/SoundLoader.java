package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.settings.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Tickable;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SoundLoader implements Tickable, ResourceReloadListener {
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
	private final SoundManager soundManager;
	private final ResourceManager resourceManager;

	public SoundLoader(ResourceManager resourceManager, GameOptions gameOptions) {
		this.resourceManager = resourceManager;
		this.soundManager = new SoundManager(this, gameOptions);
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.sounds.clear();

		for (String string : resourceManager.getAllNamespaces()) {
			try {
				for (Resource resource : resourceManager.getAllResources(new Identifier(string, "sounds.json"))) {
					try {
						Map<String, SoundEntry> map = this.readSounds(resource.getInputStream());

						for (Entry<String, SoundEntry> entry : map.entrySet()) {
							this.method_4874(new Identifier(string, (String)entry.getKey()), (SoundEntry)entry.getValue());
						}
					} catch (RuntimeException var10) {
						LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", resource.getPackName(), var10);
					}
				}
			} catch (IOException var11) {
			}
		}

		for (Identifier identifier : this.sounds.keySet()) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
			if (weightedSoundSet.getSubtitle() instanceof TranslatableTextComponent) {
				String string2 = ((TranslatableTextComponent)weightedSoundSet.getSubtitle()).getKey();
				if (!I18n.hasTranslation(string2)) {
					LOGGER.debug("Missing subtitle {} for event: {}", string2, identifier);
				}
			}
		}

		for (Identifier identifierx : this.sounds.keySet()) {
			if (Registry.SOUND_EVENT.get(identifierx) == null) {
				LOGGER.debug("Not having sound event for: {}", identifierx);
			}
		}

		this.soundManager.reloadSounds();
	}

	@Nullable
	protected Map<String, SoundEntry> readSounds(InputStream inputStream) {
		Map var2;
		try {
			var2 = JsonHelper.deserialize(GSON, new InputStreamReader(inputStream, StandardCharsets.UTF_8), TYPE);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return var2;
	}

	private void method_4874(Identifier identifier, SoundEntry soundEntry) {
		WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
		boolean bl = weightedSoundSet == null;
		if (bl || soundEntry.isReplaceable()) {
			if (!bl) {
				LOGGER.debug("Replaced sound event location {}", identifier);
			}

			weightedSoundSet = new WeightedSoundSet(identifier, soundEntry.getSubtitle());
			this.sounds.put(identifier, weightedSoundSet);
		}

		for (final Sound sound : soundEntry.getSounds()) {
			final Identifier identifier2 = sound.getIdentifier();
			SoundContainer<Sound> soundContainer;
			switch (sound.getRegistrationType()) {
				case FILE:
					if (!this.isSoundResourcePresent(sound, identifier)) {
						continue;
					}

					soundContainer = sound;
					break;
				case EVENT:
					soundContainer = new SoundContainer<Sound>() {
						@Override
						public int getWeight() {
							WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundLoader.this.sounds.get(identifier2);
							return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
						}

						public Sound getSound() {
							WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundLoader.this.sounds.get(identifier2);
							if (weightedSoundSet == null) {
								return SoundLoader.SOUND_MISSING;
							} else {
								Sound sound = weightedSoundSet.getSound();
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
					};
					break;
				default:
					throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getRegistrationType());
			}

			if (soundContainer.getSound().method_4764()) {
				this.soundManager.method_4851(soundContainer.getSound());
			}

			weightedSoundSet.add(soundContainer);
		}
	}

	private boolean isSoundResourcePresent(Sound sound, Identifier identifier) {
		Identifier identifier2 = sound.getLocation();
		Resource resource = null;

		boolean var6;
		try {
			resource = this.resourceManager.getResource(identifier2);
			resource.getInputStream();
			return true;
		} catch (FileNotFoundException var11) {
			LOGGER.warn("File {} does not exist, cannot add it to event {}", identifier2, identifier);
			return false;
		} catch (IOException var12) {
			LOGGER.warn("Could not load sound file {}, cannot add it to event {}", identifier2, identifier, var12);
			var6 = false;
		} finally {
			IOUtils.closeQuietly(resource);
		}

		return var6;
	}

	@Nullable
	public WeightedSoundSet get(Identifier identifier) {
		return (WeightedSoundSet)this.sounds.get(identifier);
	}

	public Collection<Identifier> getKeys() {
		return this.sounds.keySet();
	}

	public void play(SoundInstance soundInstance) {
		this.soundManager.play(soundInstance);
	}

	public void method_4872(SoundInstance soundInstance, int i) {
		this.soundManager.method_4852(soundInstance, i);
	}

	public void updateListenerPosition(PlayerEntity playerEntity, float f) {
		this.soundManager.updateListenerPosition(playerEntity, f);
	}

	public void pause() {
		this.soundManager.pause();
	}

	public void stopAll() {
		this.soundManager.stopAll();
	}

	public void deinitialize() {
		this.soundManager.deinitialize();
	}

	@Override
	public void tick() {
		this.soundManager.update();
	}

	public void resume() {
		this.soundManager.resume();
	}

	public void updateSoundVolume(SoundCategory soundCategory, float f) {
		if (soundCategory == SoundCategory.field_15250 && f <= 0.0F) {
			this.stopAll();
		}

		this.soundManager.updateSoundVolume(soundCategory, f);
	}

	public void stop(SoundInstance soundInstance) {
		this.soundManager.stop(soundInstance);
	}

	public boolean isPlaying(SoundInstance soundInstance) {
		return this.soundManager.isPlaying(soundInstance);
	}

	public void registerListener(ListenerSoundInstance listenerSoundInstance) {
		this.soundManager.registerListener(listenerSoundInstance);
	}

	public void unregisterListener(ListenerSoundInstance listenerSoundInstance) {
		this.soundManager.unregisterListener(listenerSoundInstance);
	}

	public void stopSounds(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		this.soundManager.stopSounds(identifier, soundCategory);
	}
}
