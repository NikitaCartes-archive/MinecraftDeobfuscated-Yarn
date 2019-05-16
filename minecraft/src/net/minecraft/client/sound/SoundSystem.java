package net.minecraft.client.sound;

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
	private boolean started;
	private final SoundEngine soundEngine = new SoundEngine();
	private final Listener listener = this.soundEngine.getListener();
	private final SoundLoader soundLoader;
	private final SoundExecutor taskQueue = new SoundExecutor();
	private final Channel channel = new Channel(this.soundEngine, this.taskQueue);
	private int ticks;
	private final Map<SoundInstance, Channel.SourceManager> sources = Maps.<SoundInstance, Channel.SourceManager>newHashMap();
	private final Multimap<SoundCategory, SoundInstance> sounds = HashMultimap.create();
	private final List<TickableSoundInstance> tickingSounds = Lists.<TickableSoundInstance>newArrayList();
	private final Map<SoundInstance, Integer> startTicks = Maps.<SoundInstance, Integer>newHashMap();
	private final Map<SoundInstance, Integer> soundEndTicks = Maps.<SoundInstance, Integer>newHashMap();
	private final List<ListenerSoundInstance> listeners = Lists.<ListenerSoundInstance>newArrayList();
	private final List<Sound> preloadedSounds = Lists.<Sound>newArrayList();

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

		this.stop();
		this.start();
	}

	private synchronized void start() {
		if (!this.started) {
			try {
				this.soundEngine.init();
				this.listener.init();
				this.listener.setVolume(this.settings.getSoundVolume(SoundCategory.field_15250));
				this.soundLoader.loadStatic(this.preloadedSounds).thenRun(this.preloadedSounds::clear);
				this.started = true;
				LOGGER.info(MARKER, "Sound engine started");
			} catch (RuntimeException var2) {
				LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)var2);
			}
		}
	}

	private float getSoundVolume(SoundCategory soundCategory) {
		return soundCategory != null && soundCategory != SoundCategory.field_15250 ? this.settings.getSoundVolume(soundCategory) : 1.0F;
	}

	public void updateSoundVolume(SoundCategory soundCategory, float f) {
		if (this.started) {
			if (soundCategory == SoundCategory.field_15250) {
				this.listener.setVolume(f);
			} else {
				this.sources.forEach((soundInstance, sourceManager) -> {
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

	public void stop() {
		if (this.started) {
			this.stopAll();
			this.soundLoader.close();
			this.soundEngine.close();
			this.started = false;
		}
	}

	public void stop(SoundInstance soundInstance) {
		if (this.started) {
			Channel.SourceManager sourceManager = (Channel.SourceManager)this.sources.get(soundInstance);
			if (sourceManager != null) {
				sourceManager.run(Source::stop);
			}
		}
	}

	public void stopAll() {
		if (this.started) {
			this.taskQueue.restart();
			this.sources.values().forEach(sourceManager -> sourceManager.run(Source::stop));
			this.sources.clear();
			this.channel.close();
			this.startTicks.clear();
			this.tickingSounds.clear();
			this.sounds.clear();
			this.soundEndTicks.clear();
		}
	}

	public void registerListener(ListenerSoundInstance listenerSoundInstance) {
		this.listeners.add(listenerSoundInstance);
	}

	public void unregisterListener(ListenerSoundInstance listenerSoundInstance) {
		this.listeners.remove(listenerSoundInstance);
	}

	public void tick(boolean bl) {
		if (!bl) {
			this.tick();
		}

		this.channel.tick();
	}

	private void tick() {
		this.ticks++;

		for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
			tickableSoundInstance.tick();
			if (tickableSoundInstance.isDone()) {
				this.stop(tickableSoundInstance);
			} else {
				float f = this.getAdjustedVolume(tickableSoundInstance);
				float g = this.getAdjustedPitch(tickableSoundInstance);
				Vec3d vec3d = new Vec3d((double)tickableSoundInstance.getX(), (double)tickableSoundInstance.getY(), (double)tickableSoundInstance.getZ());
				Channel.SourceManager sourceManager = (Channel.SourceManager)this.sources.get(tickableSoundInstance);
				if (sourceManager != null) {
					sourceManager.run(source -> {
						source.setVolume(f);
						source.setPitch(g);
						source.setPosition(vec3d);
					});
				}
			}
		}

		Iterator<Entry<SoundInstance, Channel.SourceManager>> iterator = this.sources.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<SoundInstance, Channel.SourceManager> entry = (Entry<SoundInstance, Channel.SourceManager>)iterator.next();
			Channel.SourceManager sourceManager2 = (Channel.SourceManager)entry.getValue();
			SoundInstance soundInstance = (SoundInstance)entry.getKey();
			float h = this.settings.getSoundVolume(soundInstance.getCategory());
			if (h <= 0.0F) {
				sourceManager2.run(Source::stop);
				iterator.remove();
			} else if (sourceManager2.isStopped()) {
				int i = (Integer)this.soundEndTicks.get(soundInstance);
				if (i <= this.ticks) {
					int j = soundInstance.getRepeatDelay();
					if (soundInstance.isRepeatable() && j > 0) {
						this.startTicks.put(soundInstance, this.ticks + j);
					}

					iterator.remove();
					LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", sourceManager2);
					this.soundEndTicks.remove(soundInstance);

					try {
						this.sounds.remove(soundInstance.getCategory(), soundInstance);
					} catch (RuntimeException var9) {
					}

					if (soundInstance instanceof TickableSoundInstance) {
						this.tickingSounds.remove(soundInstance);
					}
				}
			}
		}

		Iterator<Entry<SoundInstance, Integer>> iterator2 = this.startTicks.entrySet().iterator();

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
	}

	public boolean isPlaying(SoundInstance soundInstance) {
		if (!this.started) {
			return false;
		} else {
			return this.soundEndTicks.containsKey(soundInstance) && this.soundEndTicks.get(soundInstance) <= this.ticks ? true : this.sources.containsKey(soundInstance);
		}
	}

	public void play(SoundInstance soundInstance) {
		if (this.started) {
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
					if (sound == SoundManager.MISSING_SOUND) {
						if (unknownSounds.add(identifier)) {
							LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", identifier);
						}
					} else {
						float f = soundInstance.getVolume();
						float g = Math.max(f, 1.0F) * (float)sound.getAttenuation();
						SoundCategory soundCategory = soundInstance.getCategory();
						float h = this.getAdjustedVolume(soundInstance);
						float i = this.getAdjustedPitch(soundInstance);
						SoundInstance.AttenuationType attenuationType = soundInstance.getAttenuationType();
						boolean bl = soundInstance.isLooping();
						if (h == 0.0F && !soundInstance.shouldAlwaysPlay()) {
							LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", sound.getIdentifier());
						} else {
							boolean bl2 = soundInstance.isRepeatable() && soundInstance.getRepeatDelay() == 0;
							Vec3d vec3d = new Vec3d((double)soundInstance.getX(), (double)soundInstance.getY(), (double)soundInstance.getZ());
							Channel.SourceManager sourceManager = this.channel.createSource(sound.isStreamed() ? SoundEngine.RunMode.field_18353 : SoundEngine.RunMode.field_18352);
							LOGGER.debug(MARKER, "Playing sound {} for event {}", sound.getIdentifier(), identifier);
							this.soundEndTicks.put(soundInstance, this.ticks + 20);
							this.sources.put(soundInstance, sourceManager);
							this.sounds.put(soundCategory, soundInstance);
							sourceManager.run(source -> {
								source.setPitch(i);
								source.setVolume(h);
								if (attenuationType == SoundInstance.AttenuationType.field_5476) {
									source.setAttenuation(g);
								} else {
									source.disableAttenuation();
								}

								source.setLooping(bl2);
								source.setPosition(vec3d);
								source.setRelative(bl);
							});
							if (!sound.isStreamed()) {
								this.soundLoader.loadStatic(sound.getLocation()).thenAccept(staticSound -> sourceManager.run(source -> {
										source.setBuffer(staticSound);
										source.play();
									}));
							} else {
								this.soundLoader.loadStreamed(sound.getLocation()).thenAccept(audioStream -> sourceManager.run(source -> {
										source.setStream(audioStream);
										source.play();
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

	public void addPreloadedSound(Sound sound) {
		this.preloadedSounds.add(sound);
	}

	private float getAdjustedPitch(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getPitch(), 0.5F, 2.0F);
	}

	private float getAdjustedVolume(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getVolume() * this.getSoundVolume(soundInstance.getCategory()), 0.0F, 1.0F);
	}

	public void pauseAll() {
		if (this.started) {
			this.channel.execute(stream -> stream.forEach(Source::pause));
		}
	}

	public void resumeAll() {
		if (this.started) {
			this.channel.execute(stream -> stream.forEach(Source::resume));
		}
	}

	public void play(SoundInstance soundInstance, int i) {
		this.startTicks.put(soundInstance, this.ticks + i);
	}

	public void updateListenerPosition(Camera camera) {
		if (this.started && camera.isReady()) {
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
			for (SoundInstance soundInstance : this.sounds.get(soundCategory)) {
				if (identifier == null || soundInstance.getId().equals(identifier)) {
					this.stop(soundInstance);
				}
			}
		} else if (identifier == null) {
			this.stopAll();
		} else {
			for (SoundInstance soundInstancex : this.sources.keySet()) {
				if (soundInstancex.getId().equals(identifier)) {
					this.stop(soundInstancex);
				}
			}
		}
	}

	public String getDebugString() {
		return this.soundEngine.getDebugString();
	}
}
