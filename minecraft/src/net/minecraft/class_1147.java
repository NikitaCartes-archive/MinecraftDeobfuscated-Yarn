package net.minecraft;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;

@Environment(EnvType.CLIENT)
public class class_1147 extends Source {
	private class_1137 field_5605 = (class_1137)this.channel;
	private IntBuffer field_5607;
	private FloatBuffer field_5603;
	private FloatBuffer field_5604;
	private FloatBuffer field_5606;

	public class_1147(
		FloatBuffer floatBuffer,
		IntBuffer intBuffer,
		boolean bl,
		boolean bl2,
		boolean bl3,
		String string,
		FilenameURL filenameURL,
		SoundBuffer soundBuffer,
		float f,
		float g,
		float h,
		int i,
		float j,
		boolean bl4
	) {
		super(bl, bl2, bl3, string, filenameURL, soundBuffer, f, g, h, i, j, bl4);
		if (this.codec != null) {
			this.codec.reverseByteOrder(true);
		}

		this.field_5603 = floatBuffer;
		this.field_5607 = intBuffer;
		this.libraryType = class_1138.class;
		this.pitch = 1.0F;
		this.method_4890();
	}

	public class_1147(FloatBuffer floatBuffer, IntBuffer intBuffer, Source source, SoundBuffer soundBuffer) {
		super(source, soundBuffer);
		if (this.codec != null) {
			this.codec.reverseByteOrder(true);
		}

		this.field_5603 = floatBuffer;
		this.field_5607 = intBuffer;
		this.libraryType = class_1138.class;
		this.pitch = 1.0F;
		this.method_4890();
	}

	public class_1147(FloatBuffer floatBuffer, AudioFormat audioFormat, boolean bl, String string, float f, float g, float h, int i, float j) {
		super(audioFormat, bl, string, f, g, h, i, j);
		this.field_5603 = floatBuffer;
		this.libraryType = class_1138.class;
		this.pitch = 1.0F;
		this.method_4890();
	}

	@Override
	public boolean incrementSoundSequence() {
		if (!this.toStream) {
			this.errorMessage("Method 'incrementSoundSequence' may only be used for streaming sources.");
			return false;
		} else {
			synchronized (this.soundSequenceLock) {
				if (this.soundSequenceQueue != null && !this.soundSequenceQueue.isEmpty()) {
					this.filenameURL = (FilenameURL)this.soundSequenceQueue.remove(0);
					if (this.codec != null) {
						this.codec.cleanup();
					}

					this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
					if (this.codec == null) {
						return true;
					} else {
						this.codec.reverseByteOrder(true);
						if (this.codec.getAudioFormat() == null) {
							this.codec.initialize(this.filenameURL.getURL());
						}

						AudioFormat audioFormat = this.codec.getAudioFormat();
						if (audioFormat == null) {
							this.errorMessage("Audio Format null in method 'incrementSoundSequence'");
							return false;
						} else {
							int i;
							if (audioFormat.getChannels() == 1) {
								if (audioFormat.getSampleSizeInBits() == 8) {
									i = 4352;
								} else {
									if (audioFormat.getSampleSizeInBits() != 16) {
										this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
										return false;
									}

									i = 4353;
								}
							} else {
								if (audioFormat.getChannels() != 2) {
									this.errorMessage("Audio data neither mono nor stereo in method 'incrementSoundSequence'");
									return false;
								}

								if (audioFormat.getSampleSizeInBits() == 8) {
									i = 4354;
								} else {
									if (audioFormat.getSampleSizeInBits() != 16) {
										this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
										return false;
									}

									i = 4355;
								}
							}

							this.field_5605.method_4828(i, (int)audioFormat.getSampleRate());
							this.preLoad = true;
							return true;
						}
					}
				} else {
					return false;
				}
			}
		}
	}

	@Override
	public void listenerMoved() {
		this.positionChanged();
	}

	@Override
	public void setPosition(float f, float g, float h) {
		super.setPosition(f, g, h);
		if (this.field_5604 == null) {
			this.method_4890();
		} else {
			this.positionChanged();
		}

		this.field_5604.put(0, f);
		this.field_5604.put(1, g);
		this.field_5604.put(2, h);
		if (this.channel != null && this.channel.attachedSource == this && this.field_5605 != null && this.field_5605.field_5539 != null) {
			AL10.alSourcefv(this.field_5605.field_5539.get(0), 4100, this.field_5604);
			this.method_4889();
		}
	}

	@Override
	public void positionChanged() {
		this.method_4891();
		this.method_4892();
		if (this.channel != null && this.channel.attachedSource == this && this.field_5605 != null && this.field_5605.field_5539 != null) {
			AL10.alSourcef(this.field_5605.field_5539.get(0), 4106, this.gain * this.sourceVolume * Math.abs(this.fadeOutGain) * this.fadeInGain);
			this.method_4889();
		}

		this.method_4888();
	}

	private void method_4888() {
		if (this.channel != null && this.channel.attachedSource == this && class_1138.method_4831() && this.field_5605 != null && this.field_5605.field_5539 != null) {
			AL10.alSourcef(this.field_5605.field_5539.get(0), 4099, this.pitch);
			this.method_4889();
		}
	}

	@Override
	public void setLooping(boolean bl) {
		super.setLooping(bl);
		if (this.channel != null && this.channel.attachedSource == this && this.field_5605 != null && this.field_5605.field_5539 != null) {
			AL10.alSourcei(this.field_5605.field_5539.get(0), 4103, bl ? 1 : 0);
			this.method_4889();
		}
	}

	@Override
	public void setAttenuation(int i) {
		super.setAttenuation(i);
		if (this.channel != null && this.channel.attachedSource == this && this.field_5605 != null && this.field_5605.field_5539 != null) {
			if (i == 1) {
				AL10.alSourcef(this.field_5605.field_5539.get(0), 4129, this.distOrRoll);
			} else {
				AL10.alSourcef(this.field_5605.field_5539.get(0), 4129, 0.0F);
			}

			this.method_4889();
		}
	}

	@Override
	public void setDistOrRoll(float f) {
		super.setDistOrRoll(f);
		if (this.channel != null && this.channel.attachedSource == this && this.field_5605 != null && this.field_5605.field_5539 != null) {
			if (this.attModel == 1) {
				AL10.alSourcef(this.field_5605.field_5539.get(0), 4129, f);
			} else {
				AL10.alSourcef(this.field_5605.field_5539.get(0), 4129, 0.0F);
			}

			this.method_4889();
		}
	}

	@Override
	public void setVelocity(float f, float g, float h) {
		super.setVelocity(f, g, h);
		this.field_5606 = BufferUtils.createFloatBuffer(3).put(new float[]{f, g, h});
		this.field_5606.flip();
		if (this.channel != null && this.channel.attachedSource == this && this.field_5605 != null && this.field_5605.field_5539 != null) {
			AL10.alSourcefv(this.field_5605.field_5539.get(0), 4102, this.field_5606);
			this.method_4889();
		}
	}

	@Override
	public void setPitch(float f) {
		super.setPitch(f);
		this.method_4888();
	}

	@Override
	public void play(Channel channel) {
		if (!this.active()) {
			if (this.toLoop) {
				this.toPlay = true;
			}
		} else if (channel == null) {
			this.errorMessage("Unable to play source, because channel was null");
		} else {
			boolean bl = this.channel != channel;
			if (this.channel != null && this.channel.attachedSource != this) {
				bl = true;
			}

			boolean bl2 = this.paused();
			super.play(channel);
			this.field_5605 = (class_1137)this.channel;
			if (bl) {
				this.setPosition(this.position.x, this.position.y, this.position.z);
				this.method_4888();
				if (this.field_5605 != null && this.field_5605.field_5539 != null) {
					if (class_1138.method_4831()) {
						AL10.alSourcef(this.field_5605.field_5539.get(0), 4099, this.pitch);
						this.method_4889();
					}

					AL10.alSourcefv(this.field_5605.field_5539.get(0), 4100, this.field_5604);
					this.method_4889();
					AL10.alSourcefv(this.field_5605.field_5539.get(0), 4102, this.field_5606);
					this.method_4889();
					if (this.attModel == 1) {
						AL10.alSourcef(this.field_5605.field_5539.get(0), 4129, this.distOrRoll);
					} else {
						AL10.alSourcef(this.field_5605.field_5539.get(0), 4129, 0.0F);
					}

					this.method_4889();
					if (this.toLoop && !this.toStream) {
						AL10.alSourcei(this.field_5605.field_5539.get(0), 4103, 1);
					} else {
						AL10.alSourcei(this.field_5605.field_5539.get(0), 4103, 0);
					}

					this.method_4889();
				}

				if (!this.toStream) {
					if (this.field_5607 == null) {
						this.errorMessage("No sound buffer to play");
						return;
					}

					this.field_5605.method_4827(this.field_5607);
				}
			}

			if (!this.playing()) {
				if (this.toStream && !bl2) {
					if (this.codec == null) {
						this.errorMessage("Decoder null in method 'play'");
						return;
					}

					if (this.codec.getAudioFormat() == null) {
						this.codec.initialize(this.filenameURL.getURL());
					}

					AudioFormat audioFormat = this.codec.getAudioFormat();
					if (audioFormat == null) {
						this.errorMessage("Audio Format null in method 'play'");
						return;
					}

					int i;
					if (audioFormat.getChannels() == 1) {
						if (audioFormat.getSampleSizeInBits() == 8) {
							i = 4352;
						} else {
							if (audioFormat.getSampleSizeInBits() != 16) {
								this.errorMessage("Illegal sample size in method 'play'");
								return;
							}

							i = 4353;
						}
					} else {
						if (audioFormat.getChannels() != 2) {
							this.errorMessage("Audio data neither mono nor stereo in method 'play'");
							return;
						}

						if (audioFormat.getSampleSizeInBits() == 8) {
							i = 4354;
						} else {
							if (audioFormat.getSampleSizeInBits() != 16) {
								this.errorMessage("Illegal sample size in method 'play'");
								return;
							}

							i = 4355;
						}
					}

					this.field_5605.method_4828(i, (int)audioFormat.getSampleRate());
					this.preLoad = true;
				}

				this.channel.play();
				if (this.pitch != 1.0F) {
					this.method_4888();
				}
			}
		}
	}

	@Override
	public boolean preLoad() {
		if (this.codec == null) {
			return false;
		} else {
			this.codec.initialize(this.filenameURL.getURL());
			LinkedList<byte[]> linkedList = Lists.newLinkedList();

			for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); i++) {
				this.soundBuffer = this.codec.read();
				if (this.soundBuffer == null || this.soundBuffer.audioData == null) {
					break;
				}

				linkedList.add(this.soundBuffer.audioData);
			}

			this.positionChanged();
			this.channel.preLoadBuffers(linkedList);
			this.preLoad = false;
			return true;
		}
	}

	private void method_4890() {
		this.field_5604 = BufferUtils.createFloatBuffer(3).put(new float[]{this.position.x, this.position.y, this.position.z});
		this.field_5606 = BufferUtils.createFloatBuffer(3).put(new float[]{this.velocity.x, this.velocity.y, this.velocity.z});
		this.field_5604.flip();
		this.field_5606.flip();
		this.positionChanged();
	}

	private void method_4891() {
		if (this.field_5603 != null) {
			double d = (double)(this.position.x - this.field_5603.get(0));
			double e = (double)(this.position.y - this.field_5603.get(1));
			double f = (double)(this.position.z - this.field_5603.get(2));
			this.distanceFromListener = (float)Math.sqrt(d * d + e * e + f * f);
		}
	}

	private void method_4892() {
		if (this.attModel == 2) {
			if (this.distanceFromListener <= 0.0F) {
				this.gain = 1.0F;
			} else if (this.distanceFromListener >= this.distOrRoll) {
				this.gain = 0.0F;
			} else {
				this.gain = 1.0F - this.distanceFromListener / this.distOrRoll;
			}

			if (this.gain > 1.0F) {
				this.gain = 1.0F;
			}

			if (this.gain < 0.0F) {
				this.gain = 0.0F;
			}
		} else {
			this.gain = 1.0F;
		}
	}

	private boolean method_4889() {
		switch (AL10.alGetError()) {
			case 0:
				return false;
			case 40961:
				this.errorMessage("Invalid name parameter.");
				return true;
			case 40962:
				this.errorMessage("Invalid parameter.");
				return true;
			case 40963:
				this.errorMessage("Invalid enumerated parameter value.");
				return true;
			case 40964:
				this.errorMessage("Illegal call.");
				return true;
			case 40965:
				this.errorMessage("Unable to allocate memory.");
				return true;
			default:
				this.errorMessage("An unrecognized error occurred.");
				return true;
		}
	}
}
