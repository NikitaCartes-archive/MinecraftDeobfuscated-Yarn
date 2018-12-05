package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;

public class RegionFileCache {
	private static final Map<File, RegionFile> CACHE = Maps.<File, RegionFile>newHashMap();

	public static synchronized RegionFile getRegionFile(File file, int i, int j) {
		File file2 = new File(file, "region");
		File file3 = new File(file2, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
		RegionFile regionFile = (RegionFile)CACHE.get(file3);
		if (regionFile != null) {
			return regionFile;
		} else {
			if (!file2.exists()) {
				file2.mkdirs();
			}

			if (CACHE.size() >= 256) {
				clear();
			}

			RegionFile regionFile2 = new RegionFile(file3);
			CACHE.put(file3, regionFile2);
			return regionFile2;
		}
	}

	public static synchronized void clear() {
		for (RegionFile regionFile : CACHE.values()) {
			try {
				if (regionFile != null) {
					regionFile.close();
				}
			} catch (IOException var3) {
				var3.printStackTrace();
			}
		}

		CACHE.clear();
	}

	@Nullable
	public static DataInputStream getChunkDataInputStream(File file, int i, int j) {
		RegionFile regionFile = getRegionFile(file, i, j);
		return regionFile.getChunkDataInputStream(i & 31, j & 31);
	}

	@Nullable
	public static DataOutputStream getChunkDataOutputStream(File file, int i, int j) {
		RegionFile regionFile = getRegionFile(file, i, j);
		return regionFile.getChunkDataOutputStream(i & 31, j & 31);
	}
}
