package net.minecraft.client.audio;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

@Environment(EnvType.CLIENT)
public class SoundData {
	@Nullable
	private ByteBuffer buffer;
	private final AudioFormat format;
	private boolean field_18918;
	private int bufferPointer;

	public SoundData(ByteBuffer byteBuffer, AudioFormat audioFormat) {
		this.buffer = byteBuffer;
		this.format = audioFormat;
	}

	OptionalInt getBufferPointer() {
		if (!this.field_18918) {
			if (this.buffer == null) {
				return OptionalInt.empty();
			}

			int i = AlUtil.getFormatId(this.format);
			int[] is = new int[1];
			AL10.alGenBuffers(is);
			if (AlUtil.checkErrors("Creating buffer")) {
				return OptionalInt.empty();
			}

			AL10.alBufferData(is[0], i, this.buffer, (int)this.format.getSampleRate());
			if (AlUtil.checkErrors("Assigning buffer data")) {
				return OptionalInt.empty();
			}

			this.bufferPointer = is[0];
			this.field_18918 = true;
			this.buffer = null;
		}

		return OptionalInt.of(this.bufferPointer);
	}

	public void close() {
		if (this.field_18918) {
			AL11.alDeleteBuffers(new int[]{this.bufferPointer});
			if (AlUtil.checkErrors("Deleting stream buffers")) {
				return;
			}
		}

		this.field_18918 = false;
	}

	public OptionalInt method_19688() {
		OptionalInt optionalInt = this.getBufferPointer();
		this.field_18918 = false;
		return optionalInt;
	}
}
