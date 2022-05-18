package net.minecraft.data;

import com.google.common.hash.HashCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public interface DataWriter {
	DataWriter field_39439 = (path, bs, hashCode) -> {
		Files.createDirectories(path.getParent());
		Files.write(path, bs, new OpenOption[0]);
	};

	void write(Path path, byte[] data, HashCode hashCode) throws IOException;
}
