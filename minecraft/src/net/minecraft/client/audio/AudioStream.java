package net.minecraft.client.audio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface AudioStream extends Closeable {
	AudioFormat getFormat();

	ByteBuffer getBuffer() throws IOException;

	@Nullable
	ByteBuffer method_19720(int i) throws IOException;
}
