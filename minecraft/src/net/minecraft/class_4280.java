package net.minecraft;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4280 implements class_4234 {
	private final class_4234 field_19181;

	public class_4280(class_4234 arg) {
		this.field_19181 = arg;
	}

	@Override
	public AudioFormat method_19719() {
		return this.field_19181.method_19719();
	}

	private ByteBuffer method_20243(@Nullable ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			return null;
		} else {
			AudioFormat audioFormat = this.field_19181.method_19719();
			if (audioFormat.getChannels() == 1) {
				this.method_20244(byteBuffer);
			} else {
				this.method_20245(byteBuffer);
			}

			return byteBuffer;
		}
	}

	private void method_20244(ByteBuffer byteBuffer) {
		short s = 0;
		int i = 0;

		while (byteBuffer.hasRemaining()) {
			if (i == 0) {
				byteBuffer.mark();
				s = (short)(byteBuffer.getShort() & -4);
				byteBuffer.reset();
				i = 15;
			} else {
				i--;
			}

			byteBuffer.putShort(s);
		}

		byteBuffer.flip();
	}

	private void method_20245(ByteBuffer byteBuffer) {
		short s = 0;
		short t = 0;
		int i = 0;

		while (byteBuffer.hasRemaining()) {
			if (i == 0) {
				byteBuffer.mark();
				s = (short)(byteBuffer.getShort() & -4);
				t = (short)(byteBuffer.getShort() & -4);
				byteBuffer.reset();
				i = 15;
			} else {
				i--;
			}

			byteBuffer.putShort(s);
			byteBuffer.putShort(t);
		}

		byteBuffer.flip();
	}

	@Override
	public ByteBuffer method_19721() throws IOException {
		return this.method_20243(this.field_19181.method_19721());
	}

	@Nullable
	@Override
	public ByteBuffer method_19720(int i) throws IOException {
		return this.method_20243(this.field_19181.method_19720(i));
	}

	public void close() throws IOException {
		this.field_19181.close();
	}
}
