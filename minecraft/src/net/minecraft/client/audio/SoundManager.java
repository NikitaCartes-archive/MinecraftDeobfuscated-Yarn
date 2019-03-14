package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.paulscode.LibraryLWJGL3;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;

@Environment(EnvType.CLIENT)
public class SoundManager {
	private static final Marker MARKER = MarkerManager.getMarker("SOUNDS");
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<Identifier> unknownSounds = Sets.<Identifier>newHashSet();
	private final SoundLoader loader;
	private final GameOptions settings;
	private SoundManager.System system;
	private boolean initialized;
	private int ticks;
	private final Map<String, SoundInstance> playingSounds = HashBiMap.create();
	private final Map<SoundInstance, String> playingSoundIds = ((BiMap)this.playingSounds).inverse();
	private final Multimap<SoundCategory, String> playingSoundsByCategory = HashMultimap.create();
	private final List<TickableSoundInstance> tickingSounds = Lists.<TickableSoundInstance>newArrayList();
	private final Map<SoundInstance, Integer> field_5566 = Maps.<SoundInstance, Integer>newHashMap();
	private final Map<String, Integer> field_5554 = Maps.<String, Integer>newHashMap();
	private final List<ListenerSoundInstance> listeners = Lists.<ListenerSoundInstance>newArrayList();
	private final List<String> pausedSoundIds = Lists.<String>newArrayList();
	private final List<Sound> field_5551 = Lists.<Sound>newArrayList();

	public SoundManager(SoundLoader soundLoader, GameOptions gameOptions) {
		this.loader = soundLoader;
		this.settings = gameOptions;

		try {
			SoundSystemConfig.addLibrary(LibraryLWJGL3.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		} catch (SoundSystemException var4) {
			LOGGER.error(MARKER, "Error linking with the LibraryJavaSound plug-in", (Throwable)var4);
		}
	}

	public void reloadSounds() {
		unknownSounds.clear();

		for (SoundEvent soundEvent : Registry.SOUND_EVENT) {
			Identifier identifier = soundEvent.getId();
			if (this.loader.get(identifier) == null) {
				LOGGER.warn("Missing sound for event: {}", Registry.SOUND_EVENT.getId(soundEvent));
				unknownSounds.add(identifier);
			}
		}

		this.deinitialize();
		this.initializeSystem();
	}

	private synchronized void initializeSystem() {
		if (!this.initialized) {
			try {
				Thread thread = new Thread(() -> {
					SoundSystemConfig.setLogger(new SoundSystemLogger() {
						@Override
						public void message(String string, int i) {
							if (!string.isEmpty()) {
								SoundManager.LOGGER.info(string);
							}
						}

						@Override
						public void importantMessage(String string, int i) {
							if (string.startsWith("Author:")) {
								SoundManager.LOGGER.info("SoundSystem {}", string);
							} else if (!string.isEmpty()) {
								SoundManager.LOGGER.warn(string);
							}
						}

						@Override
						public void errorMessage(String string, String string2, int i) {
							if (!string2.isEmpty()) {
								SoundManager.LOGGER.error("Error in class '{}'", string);
								SoundManager.LOGGER.error(string2);
							}
						}
					});
					this.system = new SoundManager.System();
					this.initialized = true;
					this.system.setMasterVolume(this.settings.getSoundVolume(SoundCategory.field_15250));
					Iterator<Sound> iterator = this.field_5551.iterator();

					while (iterator.hasNext()) {
						Sound sound = (Sound)iterator.next();
						this.method_4836(sound);
						iterator.remove();
					}

					LOGGER.info(MARKER, "Sound engine started");
				}, "Sound Library Loader");
				thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
				thread.start();
			} catch (RuntimeException var2) {
				LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)var2);
				this.settings.setSoundVolume(SoundCategory.field_15250, 0.0F);
				this.settings.write();
			}
		}
	}

	private float getSoundVolume(SoundCategory soundCategory) {
		return soundCategory != null && soundCategory != SoundCategory.field_15250 ? this.settings.getSoundVolume(soundCategory) : 1.0F;
	}

	public void updateSoundVolume(SoundCategory soundCategory, float f) {
		if (this.initialized) {
			if (soundCategory == SoundCategory.field_15250) {
				this.system.setMasterVolume(f);
			} else {
				for (String string : this.playingSoundsByCategory.get(soundCategory)) {
					SoundInstance soundInstance = (SoundInstance)this.playingSounds.get(string);
					float g = this.getAdjustedVolume(soundInstance);
					if (g <= 0.0F) {
						this.stop(soundInstance);
					} else {
						this.system.setVolume(string, g);
					}
				}
			}
		}
	}

	public void deinitialize() {
		if (this.initialized) {
			this.stopAll();
			this.system.cleanup();
			this.initialized = false;
		}
	}

	public void stopAll() {
		if (this.initialized) {
			for (String string : this.playingSounds.keySet()) {
				this.system.stop(string);
			}

			this.playingSounds.clear();
			this.field_5566.clear();
			this.tickingSounds.clear();
			this.pausedSoundIds.clear();
			this.playingSoundsByCategory.clear();
			this.field_5554.clear();
		}
	}

	public void registerListener(ListenerSoundInstance listenerSoundInstance) {
		this.listeners.add(listenerSoundInstance);
	}

	public void unregisterListener(ListenerSoundInstance listenerSoundInstance) {
		this.listeners.remove(listenerSoundInstance);
	}

	public void update() {
		this.ticks++;

		for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
			tickableSoundInstance.tick();
			if (tickableSoundInstance.isDone()) {
				this.stop(tickableSoundInstance);
			} else {
				String string = (String)this.playingSoundIds.get(tickableSoundInstance);
				this.system.setVolume(string, this.getAdjustedVolume(tickableSoundInstance));
				this.system.setPitch(string, this.getAdjustedPitch(tickableSoundInstance));
				this.system.setPosition(string, tickableSoundInstance.getX(), tickableSoundInstance.getY(), tickableSoundInstance.getZ());
			}
		}

		Iterator<Entry<String, SoundInstance>> iterator = this.playingSounds.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, SoundInstance> entry = (Entry<String, SoundInstance>)iterator.next();
			String string = (String)entry.getKey();
			SoundInstance soundInstance = (SoundInstance)entry.getValue();
			float f = this.settings.getSoundVolume(soundInstance.getCategory());
			if (f <= 0.0F) {
				this.stop(soundInstance);
			}

			if (!this.system.playing(string)) {
				int i = (Integer)this.field_5554.get(string);
				if (i <= this.ticks) {
					int j = soundInstance.getRepeatDelay();
					if (soundInstance.isRepeatable() && j > 0) {
						this.field_5566.put(soundInstance, this.ticks + j);
					}

					iterator.remove();
					LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", string);
					this.system.removeSource(string);
					this.field_5554.remove(string);

					try {
						this.playingSoundsByCategory.remove(soundInstance.getCategory(), string);
					} catch (RuntimeException var9) {
					}

					if (soundInstance instanceof TickableSoundInstance) {
						this.tickingSounds.remove(soundInstance);
					}
				}
			}
		}

		Iterator<Entry<SoundInstance, Integer>> iterator2 = this.field_5566.entrySet().iterator();

		while (iterator2.hasNext()) {
			Entry<SoundInstance, Integer> entry2 = (Entry<SoundInstance, Integer>)iterator2.next();
			if (this.ticks >= (Integer)entry2.getValue()) {
				SoundInstance soundInstancex = (SoundInstance)entry2.getKey();
				if (soundInstancex instanceof TickableSoundInstance) {
					((TickableSoundInstance)soundInstancex).tick();
				}

				this.play(soundInstancex);
				iterator2.remove();
			}
		}
	}

	public boolean isPlaying(SoundInstance soundInstance) {
		if (!this.initialized) {
			return false;
		} else {
			String string = (String)this.playingSoundIds.get(soundInstance);
			return string == null ? false : this.system.playing(string) || this.field_5554.containsKey(string) && (Integer)this.field_5554.get(string) <= this.ticks;
		}
	}

	public void stop(SoundInstance soundInstance) {
		if (this.initialized) {
			String string = (String)this.playingSoundIds.get(soundInstance);
			if (string != null) {
				this.system.stop(string);
			}
		}
	}

	public void play(SoundInstance soundInstance) {
		if (this.initialized) {
			WeightedSoundSet weightedSoundSet = soundInstance.getAccess(this.loader);
			Identifier identifier = soundInstance.getId();
			if (weightedSoundSet == null) {
				if (unknownSounds.add(identifier)) {
					LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", identifier);
				}
			} else {
				if (!this.listeners.isEmpty()) {
					for (ListenerSoundInstance listenerSoundInstance : this.listeners) {
						listenerSoundInstance.onSoundPlayed(soundInstance, weightedSoundSet);
					}
				}

				if (this.system.getMasterVolume() <= 0.0F) {
					LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", identifier);
				} else {
					Sound sound = soundInstance.getSound();
					if (sound == SoundLoader.SOUND_MISSING) {
						if (unknownSounds.add(identifier)) {
							LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", identifier);
						}
					} else {
						float f = soundInstance.getVolume();
						float g = (float)sound.method_4770();
						if (f > 1.0F) {
							g *= f;
						}

						SoundCategory soundCategory = soundInstance.getCategory();
						float h = this.getAdjustedVolume(soundInstance);
						float i = this.getAdjustedPitch(soundInstance);
						if (h == 0.0F && !soundInstance.method_4785()) {
							LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", sound.getIdentifier());
						} else {
							boolean bl = soundInstance.isRepeatable() && soundInstance.getRepeatDelay() == 0;
							String string = MathHelper.randomUuid(ThreadLocalRandom.current()).toString();
							Identifier identifier2 = sound.getLocation();
							if (sound.isStreamed()) {
								this.system
									.newStreamingSource(
										soundInstance.method_4787(),
										string,
										getSoundURL(identifier2),
										identifier2.toString(),
										bl,
										soundInstance.getX(),
										soundInstance.getY(),
										soundInstance.getZ(),
										soundInstance.getAttenuationType().getType(),
										g
									);
							} else {
								this.system
									.newSource(
										soundInstance.method_4787(),
										string,
										getSoundURL(identifier2),
										identifier2.toString(),
										bl,
										soundInstance.getX(),
										soundInstance.getY(),
										soundInstance.getZ(),
										soundInstance.getAttenuationType().getType(),
										g
									);
							}

							LOGGER.debug(MARKER, "Playing sound {} for event {} as channel {}", sound.getIdentifier(), identifier, string);
							this.system.setPitch(string, i);
							this.system.setVolume(string, h);
							this.system.play(string);
							this.field_5554.put(string, this.ticks + 20);
							this.playingSounds.put(string, soundInstance);
							this.playingSoundsByCategory.put(soundCategory, string);
							if (soundInstance instanceof TickableSoundInstance) {
								this.tickingSounds.add((TickableSoundInstance)soundInstance);
							}
						}
					}
				}
			}
		}
	}

	public void method_4851(Sound sound) {
		this.field_5551.add(sound);
	}

	private void method_4836(Sound sound) {
		Identifier identifier = sound.getLocation();
		LOGGER.info(MARKER, "Preloading sound {}", identifier);
		this.system.loadSound(getSoundURL(identifier), identifier.toString());
	}

	private float getAdjustedPitch(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getPitch(), 0.5F, 2.0F);
	}

	private float getAdjustedVolume(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getVolume() * this.getSoundVolume(soundInstance.getCategory()), 0.0F, 1.0F);
	}

	public void pause() {
		for (Entry<String, SoundInstance> entry : this.playingSounds.entrySet()) {
			String string = (String)entry.getKey();
			boolean bl = this.isPlaying((SoundInstance)entry.getValue());
			if (bl) {
				LOGGER.debug(MARKER, "Pausing channel {}", string);
				this.system.pause(string);
				this.pausedSoundIds.add(string);
			}
		}
	}

	public void resume() {
		for (String string : this.pausedSoundIds) {
			LOGGER.debug(MARKER, "Resuming channel {}", string);
			this.system.play(string);
		}

		this.pausedSoundIds.clear();
	}

	public void play(SoundInstance soundInstance, int i) {
		this.field_5566.put(soundInstance, this.ticks + i);
	}

	private static URL getSoundURL(Identifier identifier) {
		String string = String.format("%s:%s:%s", "mcsounddomain", identifier.getNamespace(), identifier.getPath());
		URLStreamHandler uRLStreamHandler = new URLStreamHandler() {
			protected URLConnection openConnection(URL uRL) {
				return new URLConnection(uRL) {
					public void connect() {
					}

					public InputStream getInputStream() throws IOException {
						return MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream();
					}
				};
			}
		};

		try {
			return new URL(null, string, uRLStreamHandler);
		} catch (MalformedURLException var4) {
			throw new Error("TODO: Sanely handle url exception! :D");
		}
	}

	public void updateListenerPosition(Camera camera) {
		if (this.initialized && camera.isReady()) {
			double d = camera.getPos().x;
			double e = camera.getPos().y;
			double f = camera.getPos().z;
			Vec3d vec3d = camera.method_19335();
			Vec3d vec3d2 = camera.method_19336();
			this.system.setListenerPosition((float)d, (float)e, (float)f);
			this.system.setListenerOrientation((float)vec3d.x, (float)vec3d.y, (float)vec3d.z, (float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z);
		}
	}

	public void stopSounds(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		if (soundCategory != null) {
			for (String string : this.playingSoundsByCategory.get(soundCategory)) {
				SoundInstance soundInstance = (SoundInstance)this.playingSounds.get(string);
				if (identifier == null) {
					this.stop(soundInstance);
				} else if (soundInstance.getId().equals(identifier)) {
					this.stop(soundInstance);
				}
			}
		} else if (identifier == null) {
			this.stopAll();
		} else {
			for (SoundInstance soundInstance2 : this.playingSounds.values()) {
				if (soundInstance2.getId().equals(identifier)) {
					this.stop(soundInstance2);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class System extends SoundSystem {
		private System() {
		}

		@Override
		public boolean playing(String string) {
			synchronized (SoundSystemConfig.THREAD_SYNC) {
				if (this.soundLibrary == null) {
					return false;
				} else {
					Map<String, Source> map = this.soundLibrary.getSources();
					if (map == null) {
						return false;
					} else {
						Source source = (Source)map.get(string);
						return source == null ? false : source.playing() || source.paused() || source.preLoad;
					}
				}
			}
		}
	}
}
