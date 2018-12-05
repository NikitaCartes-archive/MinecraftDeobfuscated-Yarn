package net.minecraft;

import com.google.common.collect.Maps;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.ListenerData;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;

@Environment(EnvType.CLIENT)
public class class_1138 extends Library {
	private FloatBuffer field_5548;
	private FloatBuffer field_5545;
	private FloatBuffer field_5544;
	private Map<String, IntBuffer> field_5546;
	private static boolean field_5547 = true;
	private String field_5549 = "PreInit";
	private long field_5543;
	private long field_5542;

	public class_1138() throws SoundSystemException {
		this.field_5546 = Maps.<String, IntBuffer>newHashMap();
		this.reverseByteOrder = true;
	}

	@Override
	public void init() throws SoundSystemException {
		boolean bl = false;
		long l = ALC10.alcOpenDevice((ByteBuffer)null);
		if (l == 0L) {
			throw new class_1138.class_1139("Failed to open default device", 101);
		} else {
			ALCCapabilities aLCCapabilities = ALC.createCapabilities(l);
			if (!aLCCapabilities.OpenALC10) {
				throw new class_1138.class_1139("OpenAL 1.0 not supported", 101);
			} else {
				this.field_5542 = ALC10.alcCreateContext(l, (IntBuffer)null);
				ALC10.alcMakeContextCurrent(this.field_5542);
				AL.createCapabilities(aLCCapabilities);
				this.message("OpenAL initialized.");
				this.field_5548 = BufferUtils.createFloatBuffer(3).put(new float[]{this.listener.position.x, this.listener.position.y, this.listener.position.z});
				this.field_5545 = BufferUtils.createFloatBuffer(6)
					.put(new float[]{this.listener.lookAt.x, this.listener.lookAt.y, this.listener.lookAt.z, this.listener.up.x, this.listener.up.y, this.listener.up.z});
				this.field_5544 = BufferUtils.createFloatBuffer(3).put(new float[]{0.0F, 0.0F, 0.0F});
				this.field_5548.flip();
				this.field_5545.flip();
				this.field_5544.flip();
				this.field_5549 = "Post Init";
				AL10.alListenerfv(4100, this.field_5548);
				bl = this.method_4832() || bl;
				AL10.alListenerfv(4111, this.field_5545);
				bl = this.method_4832() || bl;
				AL10.alListenerfv(4102, this.field_5544);
				bl = this.method_4832() || bl;
				AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
				bl = this.method_4832() || bl;
				AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
				bl = this.method_4832() || bl;
				if (bl) {
					this.importantMessage("OpenAL did not initialize properly!");
					throw new class_1138.class_1139("Problem encountered while loading OpenAL or creating the listener. Probable cause: OpenAL not supported", 101);
				} else {
					super.init();
					class_1137 lv = (class_1137)this.normalChannels.get(1);

					try {
						AL10.alSourcef(lv.field_5539.get(0), 4099, 1.0F);
						if (this.method_4832()) {
							method_4833(true, false);
							throw new class_1138.class_1139("OpenAL: AL_PITCH not supported.", 108);
						}

						method_4833(true, true);
					} catch (Exception var7) {
						method_4833(true, false);
						throw new class_1138.class_1139("OpenAL: AL_PITCH not supported.", 108);
					}

					this.field_5549 = "Running";
				}
			}
		}
	}

	@Override
	protected Channel createChannel(int i) {
		IntBuffer intBuffer = BufferUtils.createIntBuffer(1);

		try {
			AL10.alGenSources(intBuffer);
		} catch (Exception var4) {
			AL10.alGetError();
			return null;
		}

		return AL10.alGetError() != 0 ? null : new class_1137(i, intBuffer);
	}

	@Override
	public void cleanup() {
		super.cleanup();

		for (String string : this.bufferMap.keySet()) {
			IntBuffer intBuffer = (IntBuffer)this.field_5546.get(string);
			if (intBuffer != null) {
				AL10.alDeleteBuffers(intBuffer);
				this.method_4832();
				intBuffer.clear();
			}
		}

		this.bufferMap.clear();
		ALC10.alcDestroyContext(this.field_5542);
		if (this.field_5543 != 0L) {
			ALC10.alcCloseDevice(this.field_5543);
		}

		this.bufferMap = null;
		this.field_5548 = null;
		this.field_5545 = null;
		this.field_5544 = null;
	}

	@Override
	public boolean loadSound(FilenameURL filenameURL) {
		if (this.bufferMap == null) {
			this.bufferMap = Maps.newHashMap();
			this.importantMessage("Buffer Map was null in method 'loadSound'");
		}

		if (this.field_5546 == null) {
			this.field_5546 = Maps.<String, IntBuffer>newHashMap();
			this.importantMessage("Open AL Buffer Map was null in method 'loadSound'");
		}

		if (this.errorCheck(filenameURL == null, "Filename/URL not specified in method 'loadSound'")) {
			return false;
		} else if (this.bufferMap.get(filenameURL.getFilename()) != null) {
			return true;
		} else {
			ICodec iCodec = SoundSystemConfig.getCodec(filenameURL.getFilename());
			if (this.errorCheck(iCodec == null, "No codec found for file '" + filenameURL.getFilename() + "' in method 'loadSound'")) {
				return false;
			} else {
				iCodec.reverseByteOrder(true);
				URL uRL = filenameURL.getURL();
				if (this.errorCheck(uRL == null, "Unable to open file '" + filenameURL.getFilename() + "' in method 'loadSound'")) {
					return false;
				} else {
					iCodec.initialize(uRL);
					SoundBuffer soundBuffer = iCodec.readAll();
					iCodec.cleanup();
					ICodec var8 = null;
					if (this.errorCheck(soundBuffer == null, "Sound buffer null in method 'loadSound'")) {
						return false;
					} else {
						this.bufferMap.put(filenameURL.getFilename(), soundBuffer);
						AudioFormat audioFormat = soundBuffer.audioFormat;
						int i;
						if (audioFormat.getChannels() == 1) {
							if (audioFormat.getSampleSizeInBits() == 8) {
								i = 4352;
							} else {
								if (audioFormat.getSampleSizeInBits() != 16) {
									this.errorMessage("Illegal sample size in method 'loadSound'");
									return false;
								}

								i = 4353;
							}
						} else {
							if (audioFormat.getChannels() != 2) {
								this.errorMessage("File neither mono nor stereo in method 'loadSound'");
								return false;
							}

							if (audioFormat.getSampleSizeInBits() == 8) {
								i = 4354;
							} else {
								if (audioFormat.getSampleSizeInBits() != 16) {
									this.errorMessage("Illegal sample size in method 'loadSound'");
									return false;
								}

								i = 4355;
							}
						}

						IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
						AL10.alGenBuffers(intBuffer);
						if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when loading " + filenameURL.getFilename())) {
							return false;
						} else {
							AL10.alBufferData(
								intBuffer.get(0),
								i,
								(ByteBuffer)BufferUtils.createByteBuffer(soundBuffer.audioData.length).put(soundBuffer.audioData).flip(),
								(int)audioFormat.getSampleRate()
							);
							if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when loading " + filenameURL.getFilename())
								&& this.errorCheck(intBuffer == null, "Sound buffer was not created for " + filenameURL.getFilename())) {
								return false;
							} else {
								this.field_5546.put(filenameURL.getFilename(), intBuffer);
								return true;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean loadSound(SoundBuffer soundBuffer, String string) {
		if (this.bufferMap == null) {
			this.bufferMap = Maps.newHashMap();
			this.importantMessage("Buffer Map was null in method 'loadSound'");
		}

		if (this.field_5546 == null) {
			this.field_5546 = Maps.<String, IntBuffer>newHashMap();
			this.importantMessage("Open AL Buffer Map was null in method 'loadSound'");
		}

		if (this.errorCheck(string == null, "Identifier not specified in method 'loadSound'")) {
			return false;
		} else if (this.bufferMap.get(string) != null) {
			return true;
		} else if (this.errorCheck(soundBuffer == null, "Sound buffer null in method 'loadSound'")) {
			return false;
		} else {
			this.bufferMap.put(string, soundBuffer);
			AudioFormat audioFormat = soundBuffer.audioFormat;
			int i;
			if (audioFormat.getChannels() == 1) {
				if (audioFormat.getSampleSizeInBits() == 8) {
					i = 4352;
				} else {
					if (audioFormat.getSampleSizeInBits() != 16) {
						this.errorMessage("Illegal sample size in method 'loadSound'");
						return false;
					}

					i = 4353;
				}
			} else {
				if (audioFormat.getChannels() != 2) {
					this.errorMessage("File neither mono nor stereo in method 'loadSound'");
					return false;
				}

				if (audioFormat.getSampleSizeInBits() == 8) {
					i = 4354;
				} else {
					if (audioFormat.getSampleSizeInBits() != 16) {
						this.errorMessage("Illegal sample size in method 'loadSound'");
						return false;
					}

					i = 4355;
				}
			}

			IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
			AL10.alGenBuffers(intBuffer);
			if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when saving " + string)) {
				return false;
			} else {
				AL10.alBufferData(
					intBuffer.get(0),
					i,
					(ByteBuffer)BufferUtils.createByteBuffer(soundBuffer.audioData.length).put(soundBuffer.audioData).flip(),
					(int)audioFormat.getSampleRate()
				);
				if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when saving " + string)
					&& this.errorCheck(intBuffer == null, "Sound buffer was not created for " + string)) {
					return false;
				} else {
					this.field_5546.put(string, intBuffer);
					return true;
				}
			}
		}
	}

	@Override
	public void unloadSound(String string) {
		this.field_5546.remove(string);
		super.unloadSound(string);
	}

	@Override
	public void setMasterVolume(float f) {
		super.setMasterVolume(f);
		AL10.alListenerf(4106, f);
		this.method_4832();
	}

	@Override
	public void newSource(boolean bl, boolean bl2, boolean bl3, String string, FilenameURL filenameURL, float f, float g, float h, int i, float j) {
		IntBuffer intBuffer = null;
		if (!bl2) {
			intBuffer = (IntBuffer)this.field_5546.get(filenameURL.getFilename());
			if (intBuffer == null && !this.loadSound(filenameURL)) {
				this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", string, filenameURL.getFilename()));
				return;
			}

			intBuffer = (IntBuffer)this.field_5546.get(filenameURL.getFilename());
			if (intBuffer == null) {
				this.errorMessage(String.format("Source '%s' was not created because a sound buffer was not found for %s", string, filenameURL.getFilename()));
				return;
			}
		}

		SoundBuffer soundBuffer = null;
		if (!bl2) {
			soundBuffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
			if (soundBuffer == null && !this.loadSound(filenameURL)) {
				this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", string, filenameURL.getFilename()));
				return;
			}

			soundBuffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
			if (soundBuffer == null) {
				this.errorMessage(String.format("Source '%s' was not created because audio data was not found for %s", string, filenameURL.getFilename()));
				return;
			}
		}

		this.sourceMap.put(string, new class_1147(this.field_5548, intBuffer, bl, bl2, bl3, string, filenameURL, soundBuffer, f, g, h, i, j, false));
	}

	@Override
	public void rawDataStream(AudioFormat audioFormat, boolean bl, String string, float f, float g, float h, int i, float j) {
		this.sourceMap.put(string, new class_1147(this.field_5548, audioFormat, bl, string, f, g, h, i, j));
	}

	@Override
	public void quickPlay(boolean bl, boolean bl2, boolean bl3, String string, FilenameURL filenameURL, float f, float g, float h, int i, float j, boolean bl4) {
		IntBuffer intBuffer = null;
		if (!bl2) {
			intBuffer = (IntBuffer)this.field_5546.get(filenameURL.getFilename());
			if (intBuffer == null) {
				this.loadSound(filenameURL);
			}

			intBuffer = (IntBuffer)this.field_5546.get(filenameURL.getFilename());
			if (intBuffer == null) {
				this.errorMessage("Sound buffer was not created for " + filenameURL.getFilename());
				return;
			}
		}

		SoundBuffer soundBuffer = null;
		if (!bl2) {
			soundBuffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
			if (soundBuffer == null && !this.loadSound(filenameURL)) {
				this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", string, filenameURL.getFilename()));
				return;
			}

			soundBuffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
			if (soundBuffer == null) {
				this.errorMessage(String.format("Source '%s' was not created because audio data was not found for %s", string, filenameURL.getFilename()));
				return;
			}
		}

		class_1147 lv = new class_1147(this.field_5548, intBuffer, bl, bl2, bl3, string, filenameURL, soundBuffer, f, g, h, i, j, false);
		this.sourceMap.put(string, lv);
		this.play(lv);
		if (bl4) {
			lv.setTemporary(true);
		}
	}

	@Override
	public void copySources(HashMap<String, Source> hashMap) {
		if (hashMap != null) {
			Set<String> set = hashMap.keySet();
			Iterator<String> iterator = set.iterator();
			if (this.bufferMap == null) {
				this.bufferMap = Maps.newHashMap();
				this.importantMessage("Buffer Map was null in method 'copySources'");
			}

			if (this.field_5546 == null) {
				this.field_5546 = Maps.<String, IntBuffer>newHashMap();
				this.importantMessage("Open AL Buffer Map was null in method 'copySources'");
			}

			this.sourceMap.clear();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				Source source = (Source)hashMap.get(string);
				if (source != null) {
					SoundBuffer soundBuffer = null;
					if (!source.toStream) {
						this.loadSound(source.filenameURL);
						soundBuffer = (SoundBuffer)this.bufferMap.get(source.filenameURL.getFilename());
					}

					if (source.toStream || soundBuffer != null) {
						this.sourceMap.put(string, new class_1147(this.field_5548, (IntBuffer)this.field_5546.get(source.filenameURL.getFilename()), source, soundBuffer));
					}
				}
			}
		}
	}

	@Override
	public void setListenerPosition(float f, float g, float h) {
		super.setListenerPosition(f, g, h);
		this.field_5548.put(0, f);
		this.field_5548.put(1, g);
		this.field_5548.put(2, h);
		AL10.alListenerfv(4100, this.field_5548);
		this.method_4832();
	}

	@Override
	public void setListenerAngle(float f) {
		super.setListenerAngle(f);
		this.field_5545.put(0, this.listener.lookAt.x);
		this.field_5545.put(2, this.listener.lookAt.z);
		AL10.alListenerfv(4111, this.field_5545);
		this.method_4832();
	}

	@Override
	public void setListenerOrientation(float f, float g, float h, float i, float j, float k) {
		super.setListenerOrientation(f, g, h, i, j, k);
		this.field_5545.put(0, f);
		this.field_5545.put(1, g);
		this.field_5545.put(2, h);
		this.field_5545.put(3, i);
		this.field_5545.put(4, j);
		this.field_5545.put(5, k);
		AL10.alListenerfv(4111, this.field_5545);
		this.method_4832();
	}

	@Override
	public void setListenerData(ListenerData listenerData) {
		super.setListenerData(listenerData);
		this.field_5548.put(0, listenerData.position.x);
		this.field_5548.put(1, listenerData.position.y);
		this.field_5548.put(2, listenerData.position.z);
		AL10.alListenerfv(4100, this.field_5548);
		this.method_4832();
		this.field_5545.put(0, listenerData.lookAt.x);
		this.field_5545.put(1, listenerData.lookAt.y);
		this.field_5545.put(2, listenerData.lookAt.z);
		this.field_5545.put(3, listenerData.up.x);
		this.field_5545.put(4, listenerData.up.y);
		this.field_5545.put(5, listenerData.up.z);
		AL10.alListenerfv(4111, this.field_5545);
		this.method_4832();
		this.field_5544.put(0, listenerData.velocity.x);
		this.field_5544.put(1, listenerData.velocity.y);
		this.field_5544.put(2, listenerData.velocity.z);
		AL10.alListenerfv(4102, this.field_5544);
		this.method_4832();
	}

	@Override
	public void setListenerVelocity(float f, float g, float h) {
		super.setListenerVelocity(f, g, h);
		this.field_5544.put(0, this.listener.velocity.x);
		this.field_5544.put(1, this.listener.velocity.y);
		this.field_5544.put(2, this.listener.velocity.z);
		AL10.alListenerfv(4102, this.field_5544);
	}

	@Override
	public void dopplerChanged() {
		super.dopplerChanged();
		AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
		this.method_4832();
		AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
		this.method_4832();
	}

	private boolean method_4832() {
		switch (AL10.alGetError()) {
			case 0:
				return false;
			case 40961:
				this.errorMessage("Invalid name parameter: " + this.field_5549);
				return true;
			case 40962:
				this.errorMessage("Invalid parameter: " + this.field_5549);
				return true;
			case 40963:
				this.errorMessage("Invalid enumerated parameter value: " + this.field_5549);
				return true;
			case 40964:
				this.errorMessage("Illegal call: " + this.field_5549);
				return true;
			case 40965:
				this.errorMessage("Unable to allocate memory: " + this.field_5549);
				return true;
			default:
				this.errorMessage("An unrecognized error occurred: " + this.field_5549);
				return true;
		}
	}

	public static boolean method_4831() {
		return method_4833(false, false);
	}

	private static synchronized boolean method_4833(boolean bl, boolean bl2) {
		if (bl) {
			field_5547 = bl2;
		}

		return field_5547;
	}

	@Override
	public String getClassName() {
		return "LibraryLWJGL3";
	}

	@Environment(EnvType.CLIENT)
	public static class class_1139 extends SoundSystemException {
		public class_1139(String string, int i) {
			super(string, i);
		}
	}
}
