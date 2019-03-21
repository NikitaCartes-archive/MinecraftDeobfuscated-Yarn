package net.minecraft.client.audio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Environment(EnvType.CLIENT)
public class SoundSystem {
	private static final Marker MARKER = MarkerManager.getMarker("SOUNDS");
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<Identifier> unknownSounds = Sets.<Identifier>newHashSet();
	private final SoundManager loader;
	private final GameOptions settings;
	private boolean initialized;
	private final SoundEngine soundEngine = new SoundEngine();
	private final Listener listener = this.soundEngine.getListener();
	private final SoundLoader soundLoader;
	private final SoundTaskQueue taskQueue = new SoundTaskQueue();
	private final Channel channel = new Channel(this.soundEngine, this.taskQueue);
	private int ticks;
	private final Map<SoundInstance, Channel.SourceManager> field_18950 = Maps.<SoundInstance, Channel.SourceManager>newHashMap();
	private final Multimap<SoundCategory, SoundInstance> lastPlayedSounds = HashMultimap.create();
	private final List<TickableSoundInstance> tickingSounds = Lists.<TickableSoundInstance>newArrayList();
	private final Map<SoundInstance, Integer> field_5566 = Maps.<SoundInstance, Integer>newHashMap();
	private final Map<SoundInstance, Integer> field_18952 = Maps.<SoundInstance, Integer>newHashMap();
	private final List<ListenerSoundInstance> listeners = Lists.<ListenerSoundInstance>newArrayList();
	private final List<Sound> streamedSounds = Lists.<Sound>newArrayList();

	public SoundSystem(SoundManager soundManager, GameOptions gameOptions, ResourceManager resourceManager) {
		this.loader = soundManager;
		this.settings = gameOptions;
		this.soundLoader = new SoundLoader(resourceManager);
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

		this.close();
		this.initializeSystem();
	}

	private synchronized void initializeSystem() {
		if (!this.initialized) {
			try {
				this.initialized = true;
				this.soundEngine.init();
				this.listener.method_19673();
				this.listener.setVolume(this.settings.getSoundVolume(SoundCategory.field_15250));
				this.soundLoader.method_19741(this.streamedSounds).thenRun(this.streamedSounds::clear);
				LOGGER.info(MARKER, "Sound engine started");
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
				this.listener.setVolume(f);
			} else {
				this.field_18950.forEach((soundInstance, sourceManager) -> {
					float fx = this.getAdjustedVolume(soundInstance);
					sourceManager.run(source -> {
						if (fx <= 0.0F) {
							source.stop();
						} else {
							source.setVolume(fx);
						}
					});
				});
			}
		}
	}

	public void close() {
		if (this.initialized) {
			this.stopAll();
			this.soundLoader.method_19738();
			this.soundEngine.close();
			this.initialized = false;
		}
	}

	public void stop(SoundInstance soundInstance) {
		if (this.initialized) {
			Channel.SourceManager sourceManager = (Channel.SourceManager)this.field_18950.remove(soundInstance);
			if (sourceManager != null) {
				this.field_18952.remove(soundInstance);
				sourceManager.run(Source::stop);
			}
		}
	}

	public void stopAll() {
		if (this.initialized) {
			this.taskQueue.restart();
			this.field_18950.values().forEach(sourceManager -> sourceManager.run(Source::stop));
			this.field_18950.clear();
			this.channel.close();
			this.field_5566.clear();
			this.tickingSounds.clear();
			this.lastPlayedSounds.clear();
			this.field_18952.clear();
		}
	}

	public void registerListener(ListenerSoundInstance listenerSoundInstance) {
		this.listeners.add(listenerSoundInstance);
	}

	public void unregisterListener(ListenerSoundInstance listenerSoundInstance) {
		this.listeners.remove(listenerSoundInstance);
	}

	public void tick() {
		this.ticks++;

		for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
			tickableSoundInstance.tick();
			if (tickableSoundInstance.isDone()) {
				this.stop(tickableSoundInstance);
			} else {
				float f = this.getAdjustedVolume(tickableSoundInstance);
				float g = this.getAdjustedPitch(tickableSoundInstance);
				Vec3d vec3d = new Vec3d((double)tickableSoundInstance.getX(), (double)tickableSoundInstance.getY(), (double)tickableSoundInstance.getZ());
				Channel.SourceManager sourceManager = (Channel.SourceManager)this.field_18950.get(tickableSoundInstance);
				if (sourceManager != null) {
					sourceManager.run(source -> {
						source.setVolume(f);
						source.setPitch(g);
						source.setPosition(vec3d);
					});
				}
			}
		}

		Iterator<Entry<SoundInstance, Channel.SourceManager>> iterator = this.field_18950.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<SoundInstance, Channel.SourceManager> entry = (Entry<SoundInstance, Channel.SourceManager>)iterator.next();
			Channel.SourceManager sourceManager2 = (Channel.SourceManager)entry.getValue();
			SoundInstance soundInstance = (SoundInstance)entry.getKey();
			float h = this.settings.getSoundVolume(soundInstance.getCategory());
			if (h <= 0.0F) {
				sourceManager2.run(Source::stop);
				iterator.remove();
			} else if (sourceManager2.isStopped()) {
				int i = (Integer)this.field_18952.get(soundInstance);
				if (i <= this.ticks) {
					int j = soundInstance.getRepeatDelay();
					if (soundInstance.isRepeatable() && j > 0) {
						this.field_5566.put(soundInstance, this.ticks + j);
					}

					iterator.remove();
					LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", sourceManager2);
					this.field_18952.remove(soundInstance);

					try {
						this.lastPlayedSounds.remove(soundInstance.getCategory(), sourceManager2);
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
				SoundInstance soundInstance = (SoundInstance)entry2.getKey();
				if (soundInstance instanceof TickableSoundInstance) {
					((TickableSoundInstance)soundInstance).tick();
				}

				this.play(soundInstance);
				iterator2.remove();
			}
		}

		this.channel.tick();
	}

	public boolean isPlaying(SoundInstance soundInstance) {
		if (!this.initialized) {
			return false;
		} else {
			return this.field_18952.containsKey(soundInstance) && this.field_18952.get(soundInstance) <= this.ticks ? true : this.field_18950.containsKey(soundInstance);
		}
	}

	public void play(SoundInstance soundInstance) {
		if (this.initialized) {
			WeightedSoundSet weightedSoundSet = soundInstance.getSoundSet(this.loader);
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

				if (this.listener.getVolume() <= 0.0F) {
					LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", identifier);
				} else {
					Sound sound = soundInstance.getSound();
					if (sound == SoundManager.SOUND_MISSING) {
						if (unknownSounds.add(identifier)) {
							LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", identifier);
						}
					} else {
						float f = soundInstance.getVolume();
						float g = Math.max(f, 1.0F) * (float)sound.method_4770();
						SoundCategory soundCategory = soundInstance.getCategory();
						float h = this.getAdjustedVolume(soundInstance);
						float i = this.getAdjustedPitch(soundInstance);
						SoundInstance.AttenuationType attenuationType = soundInstance.getAttenuationType();
						boolean bl = soundInstance.method_4787();
						if (h == 0.0F && !soundInstance.method_4785()) {
							LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", sound.getIdentifier());
						} else {
							boolean bl2 = soundInstance.isRepeatable() && soundInstance.getRepeatDelay() == 0;
							Vec3d vec3d = new Vec3d((double)soundInstance.getX(), (double)soundInstance.getY(), (double)soundInstance.getZ());
							Channel.SourceManager sourceManager = this.channel.method_19723(sound.isStreamed() ? SoundEngine.RunMode.field_18353 : SoundEngine.RunMode.field_18352);
							LOGGER.debug(MARKER, "Playing sound {} for event {}", sound.getIdentifier(), identifier);
							this.field_18952.put(soundInstance, this.ticks + 20);
							this.field_18950.put(soundInstance, sourceManager);
							this.lastPlayedSounds.put(soundCategory, soundInstance);
							sourceManager.run(source -> {
								source.setPitch(i);
								source.setVolume(h);
								if (attenuationType == SoundInstance.AttenuationType.LINEAR) {
									source.method_19651(g);
								} else {
									source.method_19657();
								}

								source.setLooping(bl2);
								source.setPosition(vec3d);
								source.setRelative(bl);
							});
							if (!sound.isStreamed()) {
								this.soundLoader.method_19743(sound.getLocation()).thenAccept(soundData -> sourceManager.run(source -> {
										source.method_19642(soundData);
										source.method_19650();
									}));
							} else {
								this.soundLoader.method_19744(sound.getLocation()).thenAccept(audioStream -> sourceManager.run(source -> {
										source.method_19643(audioStream);
										source.method_19650();
									}));
							}

							if (soundInstance instanceof TickableSoundInstance) {
								this.tickingSounds.add((TickableSoundInstance)soundInstance);
							}
						}
					}
				}
			}
		}
	}

	public void addStreamedSound(Sound sound) {
		this.streamedSounds.add(sound);
	}

	private float getAdjustedPitch(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getPitch(), 0.5F, 2.0F);
	}

	private float getAdjustedVolume(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getVolume() * this.getSoundVolume(soundInstance.getCategory()), 0.0F, 1.0F);
	}

	public void pauseAll() {
		this.channel.execute(stream -> stream.forEach(Source::pause));
	}

	public void playAll() {
		this.channel.execute(stream -> stream.forEach(Source::play));
	}

	public void play(SoundInstance soundInstance, int i) {
		this.field_5566.put(soundInstance, this.ticks + i);
	}

	public void updateListenerPosition(Camera camera) {
		if (this.initialized && camera.isReady()) {
			Vec3d vec3d = camera.getPos();
			Vec3d vec3d2 = camera.method_19335();
			Vec3d vec3d3 = camera.method_19336();
			this.taskQueue.execute(() -> {
				this.listener.setPosition(vec3d);
				this.listener.setOrientation(vec3d2, vec3d3);
			});
		}
	}

	public void stopSounds(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		if (soundCategory != null) {
			for (SoundInstance soundInstance : this.lastPlayedSounds.get(soundCategory)) {
				if (identifier == null || soundInstance.getId().equals(identifier)) {
					this.stop(soundInstance);
				}
			}
		} else if (identifier == null) {
			this.stopAll();
		} else {
			for (SoundInstance soundInstancex : this.field_18950.keySet()) {
				if (soundInstancex.getId().equals(identifier)) {
					this.stop(soundInstancex);
				}
			}
		}
	}
}
