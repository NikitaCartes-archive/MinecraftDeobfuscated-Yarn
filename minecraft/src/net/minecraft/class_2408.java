package net.minecraft;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2408 {
	private static final Logger field_11287 = LogManager.getLogger();
	private final Path field_11285;
	private final Path field_11286;
	private int field_11284;
	private final Map<Path, String> field_11282 = Maps.<Path, String>newHashMap();
	private final Map<Path, String> field_11283 = Maps.<Path, String>newHashMap();
	private final Set<Path> field_16743 = Sets.<Path>newHashSet();

	public class_2408(Path path, String string) throws IOException {
		this.field_11285 = path;
		Path path2 = path.resolve(".cache");
		Files.createDirectories(path2);
		this.field_11286 = path2.resolve(string);
		this.method_10328().forEach(pathx -> {
			String var10000 = (String)this.field_11282.put(pathx, "");
		});
		if (Files.isReadable(this.field_11286)) {
			IOUtils.readLines(Files.newInputStream(this.field_11286), Charsets.UTF_8).forEach(stringx -> {
				int i = stringx.indexOf(32);
				this.field_11282.put(path.resolve(stringx.substring(i + 1)), stringx.substring(0, i));
			});
		}
	}

	public void method_10326() throws IOException {
		this.method_10331();

		Writer writer;
		try {
			writer = Files.newBufferedWriter(this.field_11286);
		} catch (IOException var3) {
			field_11287.warn("Unable write cachefile {}: {}", this.field_11286, var3.toString());
			return;
		}

		IOUtils.writeLines(
			(Collection<?>)this.field_11283
				.entrySet()
				.stream()
				.map(entry -> (String)entry.getValue() + ' ' + this.field_11285.relativize((Path)entry.getKey()))
				.collect(Collectors.toList()),
			System.lineSeparator(),
			writer
		);
		writer.close();
		field_11287.debug("Caching: cache hits: {}, created: {} removed: {}", this.field_11284, this.field_11283.size() - this.field_11284, this.field_11282.size());
	}

	@Nullable
	public String method_10323(Path path) {
		return (String)this.field_11282.get(path);
	}

	public void method_10325(Path path, String string) {
		this.field_11283.put(path, string);
		if (Objects.equals(this.field_11282.remove(path), string)) {
			this.field_11284++;
		}
	}

	public boolean method_10330(Path path) {
		return this.field_11282.containsKey(path);
	}

	public void method_16674(Path path) {
		this.field_16743.add(path);
	}

	private void method_10331() throws IOException {
		this.method_10328().forEach(path -> {
			if (this.method_10330(path) && !this.field_16743.contains(path)) {
				try {
					Files.delete(path);
				} catch (IOException var3) {
					field_11287.debug("Unable to delete: {} ({})", path, var3.toString());
				}
			}
		});
	}

	private Stream<Path> method_10328() throws IOException {
		return Files.walk(this.field_11285).filter(path -> !Objects.equals(this.field_11286, path) && !Files.isDirectory(path, new LinkOption[0]));
	}
}
