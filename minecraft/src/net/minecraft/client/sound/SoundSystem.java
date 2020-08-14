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
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
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
	private final List<SoundInstanceListener> listeners = Lists.<SoundInstanceListener>newArrayList();
	private final List<TickableSoundInstance> soundsToPlayNextTick = Lists.<TickableSoundInstance>newArrayList();
	private final List<Sound> preloadedSounds = Lists.<Sound>newArrayList();

	public SoundSystem(SoundManager loader, GameOptions settings, ResourceManager resourceManager) {
		this.loader = loader;
		this.settings = settings;
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
				this.listener.setVolume(this.settings.getSoundVolume(SoundCategory.MASTER));
				this.soundLoader.loadStatic(this.preloadedSounds).thenRun(this.preloadedSounds::clear);
				this.started = true;
				LOGGER.info(MARKER, "Sound engine started");
			} catch (RuntimeException var2) {
				LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)var2);
			}
		}
	}

	private float getSoundVolume(@Nullable SoundCategory soundCategory) {
		return soundCategory != null && soundCategory != SoundCategory.MASTER ? this.settings.getSoundVolume(soundCategory) : 1.0F;
	}

	public void updateSoundVolume(SoundCategory soundCategory, float volume) {
		if (this.started) {
			if (soundCategory == SoundCategory.MASTER) {
				this.listener.setVolume(volume);
			} else {
				this.sources.forEach((soundInstance, sourceManager) -> {
					float f = this.getAdjustedVolume(soundInstance);
					sourceManager.run(source -> {
						if (f <= 0.0F) {
							source.stop();
						} else {
							source.setVolume(f);
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
			this.soundsToPlayNextTick.clear();
		}
	}

	public void registerListener(SoundInstanceListener soundInstanceListener) {
		this.listeners.add(soundInstanceListener);
	}

	public void unregisterListener(SoundInstanceListener soundInstanceListener) {
		this.listeners.remove(soundInstanceListener);
	}

	public void tick(boolean bl) {
		if (!bl) {
			this.tick();
		}

		this.channel.tick();
	}

	private void tick() {
		this.ticks++;
		this.soundsToPlayNextTick.stream().filter(SoundInstance::canPlay).forEach(this::play);
		this.soundsToPlayNextTick.clear();

		for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
			if (!tickableSoundInstance.canPlay()) {
				this.stop(tickableSoundInstance);
			}

			tickableSoundInstance.tick();
			if (tickableSoundInstance.isDone()) {
				this.stop(tickableSoundInstance);
			} else {
				float f = this.getAdjustedVolume(tickableSoundInstance);
				float g = this.getAdjustedPitch(tickableSoundInstance);
				Vec3d vec3d = new Vec3d(tickableSoundInstance.getX(), tickableSoundInstance.getY(), tickableSoundInstance.getZ());
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
					if (isRepeatDelayed(soundInstance)) {
						this.startTicks.put(soundInstance, this.ticks + soundInstance.getRepeatDelay());
					}

					iterator.remove();
					LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", sourceManager2);
					this.soundEndTicks.remove(soundInstance);

					try {
						this.sounds.remove(soundInstance.getCategory(), soundInstance);
					} catch (RuntimeException var8) {
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

	private static boolean canRepeatInstantly(SoundInstance soundInstance) {
		return soundInstance.getRepeatDelay() > 0;
	}

	private static boolean isRepeatDelayed(SoundInstance soundInstance) {
		return soundInstance.isRepeatable() && canRepeatInstantly(soundInstance);
	}

	private static boolean shouldRepeatInstantly(SoundInstance soundInstance) {
		return soundInstance.isRepeatable() && !canRepeatInstantly(soundInstance);
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
			if (soundInstance.canPlay()) {
				WeightedSoundSet weightedSoundSet = soundInstance.getSoundSet(this.loader);
				Identifier identifier = soundInstance.getId();
				if (weightedSoundSet == null) {
					if (unknownSounds.add(identifier)) {
						LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", identifier);
					}
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
							Vec3d vec3d = new Vec3d(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ());
							if (!this.listeners.isEmpty()) {
								boolean bl2 = bl || attenuationType == SoundInstance.AttenuationType.NONE || this.listener.method_27268().squaredDistanceTo(vec3d) < (double)(g * g);
								if (bl2) {
									for (SoundInstanceListener soundInstanceListener : this.listeners) {
										soundInstanceListener.onSoundPlayed(soundInstance, weightedSoundSet);
									}
								} else {
									LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", identifier);
								}
							}

							if (this.listener.getVolume() <= 0.0F) {
								LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", identifier);
							} else {
								boolean bl2 = shouldRepeatInstantly(soundInstance);
								boolean bl3 = sound.isStreamed();
								CompletableFuture<Channel.SourceManager> completableFuture = this.channel
									.createSource(sound.isStreamed() ? SoundEngine.RunMode.STREAMING : SoundEngine.RunMode.STATIC);
								Channel.SourceManager sourceManager = (Channel.SourceManager)completableFuture.join();
								if (sourceManager == null) {
									LOGGER.warn("Failed to create new sound handle");
								} else {
									LOGGER.debug(MARKER, "Playing sound {} for event {}", sound.getIdentifier(), identifier);
									this.soundEndTicks.put(soundInstance, this.ticks + 20);
									this.sources.put(soundInstance, sourceManager);
									this.sounds.put(soundCategory, soundInstance);
									sourceManager.run(source -> {
										source.setPitch(i);
										source.setVolume(h);
										if (attenuationType == SoundInstance.AttenuationType.LINEAR) {
											source.setAttenuation(g);
										} else {
											source.disableAttenuation();
										}

										source.setLooping(bl2 && !bl3);
										source.setPosition(vec3d);
										source.setRelative(bl);
									});
									if (!bl3) {
										this.soundLoader.loadStatic(sound.getLocation()).thenAccept(staticSound -> sourceManager.run(source -> {
												source.setBuffer(staticSound);
												source.play();
											}));
									} else {
										this.soundLoader.loadStreamed(sound.getLocation(), bl2).thenAccept(audioStream -> sourceManager.run(source -> {
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
		}
	}

	public void playNextTick(TickableSoundInstance sound) {
		this.soundsToPlayNextTick.add(sound);
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

	public void play(SoundInstance sound, int delay) {
		this.startTicks.put(sound, this.ticks + delay);
	}

	public void updateListenerPosition(Camera camera) {
		if (this.started && camera.isReady()) {
			Vec3d vec3d = camera.getPos();
			Vector3f vector3f = camera.getHorizontalPlane();
			Vector3f vector3f2 = camera.getVerticalPlane();
			this.taskQueue.execute(() -> {
				this.listener.setPosition(vec3d);
				this.listener.setOrientation(vector3f, vector3f2);
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
