package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DataGenerator root;
	private final List<SnbtProvider.class_4460> field_20309 = Lists.<SnbtProvider.class_4460>newArrayList();

	public SnbtProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	public SnbtProvider method_21672(SnbtProvider.class_4460 arg) {
		this.field_20309.add(arg);
		return this;
	}

	private CompoundTag method_21673(String string, CompoundTag compoundTag) {
		CompoundTag compoundTag2 = compoundTag;

		for (SnbtProvider.class_4460 lv : this.field_20309) {
			compoundTag2 = lv.method_21674(string, compoundTag2);
		}

		return compoundTag2;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		Path path = this.root.getOutput();
		List<CompletableFuture<SnbtProvider.class_4511>> list = Lists.<CompletableFuture<SnbtProvider.class_4511>>newArrayList();

		for (Path path2 : this.root.getInputs()) {
			Files.walk(path2)
				.filter(pathx -> pathx.toString().endsWith(".snbt"))
				.forEach(
					path2x -> list.add(CompletableFuture.supplyAsync(() -> this.method_22144(path2x, this.method_10500(path2, path2x)), SystemUtil.getServerWorkerExecutor()))
				);
		}

		((List)SystemUtil.thenCombine(list).join()).stream().filter(Objects::nonNull).forEach(arg -> this.method_10497(dataCache, arg, path));
	}

	@Override
	public String getName() {
		return "SNBT -> NBT";
	}

	private String method_10500(Path path, Path path2) {
		String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".snbt".length());
	}

	@Nullable
	private SnbtProvider.class_4511 method_22144(Path path, String string) {
		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			Throwable var4 = null;

			SnbtProvider.class_4511 var9;
			try {
				String string2 = IOUtils.toString(bufferedReader);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				NbtIo.writeCompressed(this.method_21673(string, StringNbtReader.parse(string2)), byteArrayOutputStream);
				byte[] bs = byteArrayOutputStream.toByteArray();
				String string3 = SHA1.hashBytes(bs).toString();
				var9 = new SnbtProvider.class_4511(string, bs, string3);
			} catch (Throwable var20) {
				var4 = var20;
				throw var20;
			} finally {
				if (bufferedReader != null) {
					if (var4 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var19) {
							var4.addSuppressed(var19);
						}
					} else {
						bufferedReader.close();
					}
				}
			}

			return var9;
		} catch (CommandSyntaxException var22) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", string, path, var22);
		} catch (IOException var23) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", string, path, var23);
		}

		return null;
	}

	private void method_10497(DataCache dataCache, SnbtProvider.class_4511 arg, Path path) {
		Path path2 = path.resolve(arg.field_20538 + ".nbt");

		try {
			if (!Objects.equals(dataCache.getOldSha1(path2), arg.field_20540) || !Files.exists(path2, new LinkOption[0])) {
				Files.createDirectories(path2.getParent());
				OutputStream outputStream = Files.newOutputStream(path2);
				Throwable var6 = null;

				try {
					outputStream.write(arg.field_20539);
				} catch (Throwable var16) {
					var6 = var16;
					throw var16;
				} finally {
					if (outputStream != null) {
						if (var6 != null) {
							try {
								outputStream.close();
							} catch (Throwable var15) {
								var6.addSuppressed(var15);
							}
						} else {
							outputStream.close();
						}
					}
				}
			}

			dataCache.updateSha1(path2, arg.field_20540);
		} catch (IOException var18) {
			LOGGER.error("Couldn't write structure {} at {}", arg.field_20538, path2, var18);
		}
	}

	@FunctionalInterface
	public interface class_4460 {
		CompoundTag method_21674(String string, CompoundTag compoundTag);
	}

	static class class_4511 {
		private final String field_20538;
		private final byte[] field_20539;
		private final String field_20540;

		public class_4511(String string, byte[] bs, String string2) {
			this.field_20538 = string;
			this.field_20539 = bs;
			this.field_20540 = string2;
		}
	}
}
