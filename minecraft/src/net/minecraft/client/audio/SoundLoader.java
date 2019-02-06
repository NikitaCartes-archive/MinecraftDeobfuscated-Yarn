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
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
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
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SoundLoader implements Tickable, ResourceReloadListener<SoundLoader.class_4009> {
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

	public SoundLoader(GameOptions gameOptions) {
		this.soundManager = new SoundManager(this, gameOptions);
	}

	@Override
	public CompletableFuture<SoundLoader.class_4009> prepare(ResourceManager resourceManager, Profiler profiler) {
		SoundLoader.class_4009 lv = new SoundLoader.class_4009();
		return CompletableFuture.supplyAsync(() -> {
			profiler.startTick();

			for (String string : resourceManager.getAllNamespaces()) {
				profiler.push(string);

				try {
					for (Resource resource : resourceManager.getAllResources(new Identifier(string, "sounds.json"))) {
						profiler.push(resource.getPackName());

						try {
							profiler.push("parse");
							Map<String, SoundEntry> map = readSounds(resource.getInputStream());
							profiler.swap("register");

							for (Entry<String, SoundEntry> entry : map.entrySet()) {
								lv.method_18187(new Identifier(string, (String)entry.getKey()), (SoundEntry)entry.getValue(), resourceManager);
							}

							profiler.pop();
						} catch (RuntimeException var11) {
							LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", resource.getPackName(), var11);
						}

						profiler.pop();
					}
				} catch (IOException var12) {
				}

				profiler.pop();
			}

			profiler.endTick();
			return lv;
		});
	}

	public void method_18182(ResourceManager resourceManager, SoundLoader.class_4009 arg, Profiler profiler) {
		arg.method_18186(this.sounds, this.soundManager);

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
				if (!Registry.SOUND_EVENT.contains(identifierx)) {
					LOGGER.debug("Not having sound event for: {}", identifierx);
				}
			}
		}

		this.soundManager.reloadSounds();
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
		if (!resourceManager.method_18234(identifier2)) {
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
		this.soundManager.play(soundInstance);
	}

	public void play(SoundInstance soundInstance, int i) {
		this.soundManager.play(soundInstance, i);
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

	@Environment(EnvType.CLIENT)
	public static class class_4009 {
		private final Map<Identifier, WeightedSoundSet> field_17908 = Maps.<Identifier, WeightedSoundSet>newHashMap();

		protected class_4009() {
		}

		private void method_18187(Identifier identifier, SoundEntry soundEntry, ResourceManager resourceManager) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.field_17908.get(identifier);
			boolean bl = weightedSoundSet == null;
			if (bl || soundEntry.isReplaceable()) {
				if (!bl) {
					SoundLoader.LOGGER.debug("Replaced sound event location {}", identifier);
				}

				weightedSoundSet = new WeightedSoundSet(identifier, soundEntry.getSubtitle());
				this.field_17908.put(identifier, weightedSoundSet);
			}

			for (final Sound sound : soundEntry.getSounds()) {
				final Identifier identifier2 = sound.getIdentifier();
				SoundContainer<Sound> soundContainer;
				switch (sound.getRegistrationType()) {
					case FILE:
						if (!SoundLoader.isSoundResourcePresent(sound, identifier, resourceManager)) {
							continue;
						}

						soundContainer = sound;
						break;
					case EVENT:
						soundContainer = new SoundContainer<Sound>() {
							@Override
							public int getWeight() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)class_4009.this.field_17908.get(identifier2);
								return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
							}

							public Sound getSound() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)class_4009.this.field_17908.get(identifier2);
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

							@Override
							public void method_18188(SoundManager soundManager) {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)class_4009.this.field_17908.get(identifier2);
								if (weightedSoundSet != null) {
									weightedSoundSet.method_18188(soundManager);
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

		public void method_18186(Map<Identifier, WeightedSoundSet> map, SoundManager soundManager) {
			map.clear();

			for (Entry<Identifier, WeightedSoundSet> entry : this.field_17908.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
				((WeightedSoundSet)entry.getValue()).method_18188(soundManager);
			}
		}
	}
}
