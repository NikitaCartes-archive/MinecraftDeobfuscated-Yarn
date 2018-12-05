package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;

public class class_1256 {
	private static final Pattern field_5752 = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
	private final File field_5754;
	private final Map<DimensionType, List<ChunkPos>> field_5753;

	public class_1256(File file) {
		this.field_5754 = file;
		Builder<DimensionType, List<ChunkPos>> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.getAll()) {
			builder.put(dimensionType, this.method_5389(dimensionType));
		}

		this.field_5753 = builder.build();
	}

	private List<ChunkPos> method_5389(DimensionType dimensionType) {
		ArrayList<ChunkPos> arrayList = Lists.newArrayList();
		File file = dimensionType.getFile(this.field_5754);
		List<File> list = this.method_5392(file);

		for (File file2 : list) {
			arrayList.addAll(this.method_5388(file2));
		}

		list.sort(File::compareTo);
		return arrayList;
	}

	private List<ChunkPos> method_5388(File file) {
		List<ChunkPos> list = Lists.<ChunkPos>newArrayList();
		RegionFile regionFile = null;

		try {
			Matcher matcher = field_5752.matcher(file.getName());
			if (!matcher.matches()) {
				return list;
			}

			int i = Integer.parseInt(matcher.group(1)) << 5;
			int j = Integer.parseInt(matcher.group(2)) << 5;
			regionFile = new RegionFile(file);

			for (int k = 0; k < 32; k++) {
				for (int l = 0; l < 32; l++) {
					if (regionFile.method_12420(k, l)) {
						list.add(new ChunkPos(k + i, l + j));
					}
				}
			}
		} catch (Throwable var18) {
			return Lists.<ChunkPos>newArrayList();
		} finally {
			if (regionFile != null) {
				try {
					regionFile.close();
				} catch (IOException var17) {
				}
			}
		}

		return list;
	}

	private List<File> method_5392(File file) {
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mca"));
		return files != null ? Lists.<File>newArrayList(files) : Lists.<File>newArrayList();
	}

	public List<ChunkPos> method_5391(DimensionType dimensionType) {
		return (List<ChunkPos>)this.field_5753.get(dimensionType);
	}
}
