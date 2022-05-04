package net.minecraft.data;

import com.google.common.hash.HashCode;
import java.io.IOException;
import java.nio.file.Path;

public interface DataWriter {
	void write(Path path, byte[] data, HashCode hashCode) throws IOException;
}
