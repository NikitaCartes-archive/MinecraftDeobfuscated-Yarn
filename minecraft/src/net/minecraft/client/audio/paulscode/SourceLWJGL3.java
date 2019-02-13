package net.minecraft.client.audio.paulscode;

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
public class SourceLWJGL3 extends Source {
	private ChannelLWJGL3 channelOpenAL = (ChannelLWJGL3)this.channel;
	private IntBuffer myBuffer;
	private FloatBuffer listenerPosition;
	private FloatBuffer sourcePosition;
	private FloatBuffer sourceVelocity;

	public SourceLWJGL3(
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

		this.listenerPosition = floatBuffer;
		this.myBuffer = intBuffer;
		this.libraryType = LibraryLWJGL3.class;
		this.pitch = 1.0F;
		this.resetALInformation();
	}

	public SourceLWJGL3(FloatBuffer floatBuffer, IntBuffer intBuffer, Source source, SoundBuffer soundBuffer) {
		super(source, soundBuffer);
		if (this.codec != null) {
			this.codec.reverseByteOrder(true);
		}

		this.listenerPosition = floatBuffer;
		this.myBuffer = intBuffer;
		this.libraryType = LibraryLWJGL3.class;
		this.pitch = 1.0F;
		this.resetALInformation();
	}

	public SourceLWJGL3(FloatBuffer floatBuffer, AudioFormat audioFormat, boolean bl, String string, float f, float g, float h, int i, float j) {
		super(audioFormat, bl, string, f, g, h, i, j);
		this.listenerPosition = floatBuffer;
		this.libraryType = LibraryLWJGL3.class;
		this.pitch = 1.0F;
		this.resetALInformation();
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

							this.channelOpenAL.setFormat(i, (int)audioFormat.getSampleRate());
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
		if (this.sourcePosition == null) {
			this.resetALInformation();
		} else {
			this.positionChanged();
		}

		this.sourcePosition.put(0, f);
		this.sourcePosition.put(1, g);
		this.sourcePosition.put(2, h);
		if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.alSource != null) {
			AL10.alSourcefv(this.channelOpenAL.alSource.get(0), 4100, this.sourcePosition);
			this.checkALError();
		}
	}

	@Override
	public void positionChanged() {
		this.calculateDistance();
		this.calculateGain();
		if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.alSource != null) {
			AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4106, this.gain * this.sourceVolume * Math.abs(this.fadeOutGain) * this.fadeInGain);
			this.checkALError();
		}

		this.checkPitch();
	}

	private void checkPitch() {
		if (this.channel != null
			&& this.channel.attachedSource == this
			&& LibraryLWJGL3.alPitchSupported()
			&& this.channelOpenAL != null
			&& this.channelOpenAL.alSource != null) {
			AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4099, this.pitch);
			this.checkALError();
		}
	}

	@Override
	public void setLooping(boolean bl) {
		super.setLooping(bl);
		if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.alSource != null) {
			AL10.alSourcei(this.channelOpenAL.alSource.get(0), 4103, bl ? 1 : 0);
			this.checkALError();
		}
	}

	@Override
	public void setAttenuation(int i) {
		super.setAttenuation(i);
		if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.alSource != null) {
			if (i == 1) {
				AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4129, this.distOrRoll);
			} else {
				AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4129, 0.0F);
			}

			this.checkALError();
		}
	}

	@Override
	public void setDistOrRoll(float f) {
		super.setDistOrRoll(f);
		if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.alSource != null) {
			if (this.attModel == 1) {
				AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4129, f);
			} else {
				AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4129, 0.0F);
			}

			this.checkALError();
		}
	}

	@Override
	public void setVelocity(float f, float g, float h) {
		super.setVelocity(f, g, h);
		this.sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[]{f, g, h});
		this.sourceVelocity.flip();
		if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.alSource != null) {
			AL10.alSourcefv(this.channelOpenAL.alSource.get(0), 4102, this.sourceVelocity);
			this.checkALError();
		}
	}

	@Override
	public void setPitch(float f) {
		super.setPitch(f);
		this.checkPitch();
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
			this.channelOpenAL = (ChannelLWJGL3)this.channel;
			if (bl) {
				this.setPosition(this.position.x, this.position.y, this.position.z);
				this.checkPitch();
				if (this.channelOpenAL != null && this.channelOpenAL.alSource != null) {
					if (LibraryLWJGL3.alPitchSupported()) {
						AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4099, this.pitch);
						this.checkALError();
					}

					AL10.alSourcefv(this.channelOpenAL.alSource.get(0), 4100, this.sourcePosition);
					this.checkALError();
					AL10.alSourcefv(this.channelOpenAL.alSource.get(0), 4102, this.sourceVelocity);
					this.checkALError();
					if (this.attModel == 1) {
						AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4129, this.distOrRoll);
					} else {
						AL10.alSourcef(this.channelOpenAL.alSource.get(0), 4129, 0.0F);
					}

					this.checkALError();
					if (this.toLoop && !this.toStream) {
						AL10.alSourcei(this.channelOpenAL.alSource.get(0), 4103, 1);
					} else {
						AL10.alSourcei(this.channelOpenAL.alSource.get(0), 4103, 0);
					}

					this.checkALError();
				}

				if (!this.toStream) {
					if (this.myBuffer == null) {
						this.errorMessage("No sound buffer to play");
						return;
					}

					this.channelOpenAL.attachBuffer(this.myBuffer);
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

					this.channelOpenAL.setFormat(i, (int)audioFormat.getSampleRate());
					this.preLoad = true;
				}

				this.channel.play();
				if (this.pitch != 1.0F) {
					this.checkPitch();
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

	private void resetALInformation() {
		this.sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[]{this.position.x, this.position.y, this.position.z});
		this.sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[]{this.velocity.x, this.velocity.y, this.velocity.z});
		this.sourcePosition.flip();
		this.sourceVelocity.flip();
		this.positionChanged();
	}

	private void calculateDistance() {
		if (this.listenerPosition != null) {
			double d = (double)(this.position.x - this.listenerPosition.get(0));
			double e = (double)(this.position.y - this.listenerPosition.get(1));
			double f = (double)(this.position.z - this.listenerPosition.get(2));
			this.distanceFromListener = (float)Math.sqrt(d * d + e * e + f * f);
		}
	}

	private void calculateGain() {
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

	private boolean checkALError() {
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
