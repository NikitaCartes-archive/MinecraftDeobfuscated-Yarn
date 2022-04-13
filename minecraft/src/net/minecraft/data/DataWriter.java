package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;

public interface DataWriter {
	void write(Path path, String data) throws IOException;

	void write(Path path, byte[] data, String hash) throws IOException;
}
