package net.minecraft;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

@Environment(EnvType.CLIENT)
public class class_4231 {
	@Nullable
	private ByteBuffer field_18916;
	private final AudioFormat field_18917;
	private boolean field_18918;
	private int field_18919;

	public class_4231(ByteBuffer byteBuffer, AudioFormat audioFormat) {
		this.field_18916 = byteBuffer;
		this.field_18917 = audioFormat;
	}

	OptionalInt method_19686() {
		if (!this.field_18918) {
			if (this.field_18916 == null) {
				return OptionalInt.empty();
			}

			int i = class_4230.method_19685(this.field_18917);
			int[] is = new int[1];
			AL10.alGenBuffers(is);
			if (class_4230.method_19684("Creating buffer")) {
				return OptionalInt.empty();
			}

			AL10.alBufferData(is[0], i, this.field_18916, (int)this.field_18917.getSampleRate());
			if (class_4230.method_19684("Assigning buffer data")) {
				return OptionalInt.empty();
			}

			this.field_18919 = is[0];
			this.field_18918 = true;
			this.field_18916 = null;
		}

		return OptionalInt.of(this.field_18919);
	}

	public void method_19687() {
		if (this.field_18918) {
			AL11.alDeleteBuffers(new int[]{this.field_18919});
			if (class_4230.method_19684("Deleting stream buffers")) {
				return;
			}
		}

		this.field_18918 = false;
	}

	public OptionalInt method_19688() {
		OptionalInt optionalInt = this.method_19686();
		this.field_18918 = false;
		return optionalInt;
	}
}
