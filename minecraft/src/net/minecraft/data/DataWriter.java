package net.minecraft.data;

import com.google.common.hash.HashCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import net.minecraft.util.PathUtil;

public interface DataWriter {
	DataWriter UNCACHED = (path, data, hashCode) -> {
		PathUtil.createDirectories(path.getParent());
		Files.write(path, data, new OpenOption[0]);
	};

	void write(Path path, byte[] data, HashCode hashCode) throws IOException;
}
