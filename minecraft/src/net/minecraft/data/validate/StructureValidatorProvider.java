package net.minecraft.data.validate;

import com.google.common.base.Charsets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.datafixers.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.structure.Structure;
import net.minecraft.util.TagHelper;
import org.apache.commons.io.IOUtils;

public class StructureValidatorProvider implements DataProvider {
	private final DataGenerator generator;

	public StructureValidatorProvider(DataGenerator dataGenerator) {
		this.generator = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		for (Path path : this.generator.getInputs()) {
			Path path2 = path.resolve("data/minecraft/structures/");
			if (Files.isDirectory(path2, new LinkOption[0])) {
				method_16879(Schemas.getFixer(), path2);
			}
		}
	}

	@Override
	public String getName() {
		return "Structure validator";
	}

	private static void method_16879(DataFixer dataFixer, Path path) throws IOException {
		Stream<Path> stream = Files.walk(path);
		Throwable var3 = null;

		try {
			stream.forEach(pathx -> {
				if (Files.isRegularFile(pathx, new LinkOption[0])) {
					method_16881(dataFixer, pathx);
				}
			});
		} catch (Throwable var12) {
			var3 = var12;
			throw var12;
		} finally {
			if (stream != null) {
				if (var3 != null) {
					try {
						stream.close();
					} catch (Throwable var11) {
						var3.addSuppressed(var11);
					}
				} else {
					stream.close();
				}
			}
		}
	}

	private static void method_16881(DataFixer dataFixer, Path path) {
		try {
			String string = path.getFileName().toString();
			if (string.endsWith(".snbt")) {
				method_16882(dataFixer, path);
			} else {
				if (!string.endsWith(".nbt")) {
					throw new IllegalArgumentException("Unrecognized format of file");
				}

				method_16883(dataFixer, path);
			}
		} catch (Exception var3) {
			throw new StructureValidatorProvider.class_3844(path, var3);
		}
	}

	private static void method_16882(DataFixer dataFixer, Path path) throws Exception {
		InputStream inputStream = Files.newInputStream(path);
		Throwable var4 = null;

		CompoundTag compoundTag;
		try {
			String string = IOUtils.toString(inputStream, Charsets.UTF_8);
			compoundTag = JsonLikeTagParser.parse(string);
		} catch (Throwable var13) {
			var4 = var13;
			throw var13;
		} finally {
			if (inputStream != null) {
				if (var4 != null) {
					try {
						inputStream.close();
					} catch (Throwable var12) {
						var4.addSuppressed(var12);
					}
				} else {
					inputStream.close();
				}
			}
		}

		method_16878(dataFixer, method_16880(compoundTag));
	}

	private static void method_16883(DataFixer dataFixer, Path path) throws Exception {
		InputStream inputStream = Files.newInputStream(path);
		Throwable var4 = null;

		CompoundTag compoundTag;
		try {
			compoundTag = NbtIo.readCompressed(inputStream);
		} catch (Throwable var13) {
			var4 = var13;
			throw var13;
		} finally {
			if (inputStream != null) {
				if (var4 != null) {
					try {
						inputStream.close();
					} catch (Throwable var12) {
						var4.addSuppressed(var12);
					}
				} else {
					inputStream.close();
				}
			}
		}

		method_16878(dataFixer, method_16880(compoundTag));
	}

	private static CompoundTag method_16880(CompoundTag compoundTag) {
		if (!compoundTag.containsKey("DataVersion", 99)) {
			compoundTag.putInt("DataVersion", 500);
		}

		return compoundTag;
	}

	private static CompoundTag method_16878(DataFixer dataFixer, CompoundTag compoundTag) {
		Structure structure = new Structure();
		structure.fromTag(TagHelper.update(dataFixer, DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
		return structure.toTag(new CompoundTag());
	}

	static class class_3844 extends RuntimeException {
		public class_3844(Path path, Throwable throwable) {
			super("Failed to process file: " + path.toAbsolutePath().toString(), throwable);
		}
	}
}
