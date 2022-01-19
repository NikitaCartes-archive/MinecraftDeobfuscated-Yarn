package net.minecraft.client.resource;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class DirectResourceIndex extends ResourceIndex {
	private static final Logger field_36375 = LogUtils.getLogger();
	private final File assetDir;

	public DirectResourceIndex(File assetDir) {
		this.assetDir = assetDir;
	}

	@Override
	public File getResource(Identifier identifier) {
		return new File(this.assetDir, identifier.toString().replace(':', '/'));
	}

	@Override
	public File findFile(String path) {
		return new File(this.assetDir, path);
	}

	@Override
	public Collection<Identifier> getFilesRecursively(String prefix, String namespace, int maxDepth, Predicate<String> pathFilter) {
		Path path = this.assetDir.toPath().resolve(namespace);

		try {
			Stream<Path> stream = Files.walk(path.resolve(prefix), maxDepth, new FileVisitOption[0]);

			Collection var7;
			try {
				var7 = (Collection)stream.filter(pathx -> Files.isRegularFile(pathx, new LinkOption[0]))
					.filter(pathx -> !pathx.endsWith(".mcmeta"))
					.filter(pathx -> pathFilter.test(pathx.getFileName().toString()))
					.map(pathx -> new Identifier(namespace, path.relativize(pathx).toString().replaceAll("\\\\", "/")))
					.collect(Collectors.toList());
			} catch (Throwable var10) {
				if (stream != null) {
					try {
						stream.close();
					} catch (Throwable var9) {
						var10.addSuppressed(var9);
					}
				}

				throw var10;
			}

			if (stream != null) {
				stream.close();
			}

			return var7;
		} catch (NoSuchFileException var11) {
		} catch (IOException var12) {
			field_36375.warn("Unable to getFiles on {}", prefix, var12);
		}

		return Collections.emptyList();
	}
}
