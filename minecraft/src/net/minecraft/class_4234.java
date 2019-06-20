package net.minecraft;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_4234 extends Closeable {
	AudioFormat method_19719();

	ByteBuffer method_19721() throws IOException;

	@Nullable
	ByteBuffer method_19720(int i) throws IOException;
}
