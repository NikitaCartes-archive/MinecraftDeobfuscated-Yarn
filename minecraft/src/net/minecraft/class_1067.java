package net.minecraft;

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

@Environment(EnvType.CLIENT)
public class class_1067 extends class_1064 {
	private final File field_5299;

	public class_1067(File file) {
		this.field_5299 = file;
	}

	@Override
	public File method_4630(class_2960 arg) {
		return new File(this.field_5299, arg.toString().replace(':', '/'));
	}

	@Override
	public File method_4631(String string) {
		return new File(this.field_5299, string);
	}

	@Override
	public Collection<String> method_4632(String string, int i, Predicate<String> predicate) {
		Path path = this.field_5299.toPath().resolve("minecraft/");

		try {
			Stream<Path> stream = Files.walk(path.resolve(string), i, new FileVisitOption[0]);
			Throwable var6 = null;

			Collection var7;
			try {
				var7 = (Collection)stream.filter(pathx -> Files.isRegularFile(pathx, new LinkOption[0]))
					.filter(pathx -> !pathx.endsWith(".mcmeta"))
					.map(path::relativize)
					.map(Object::toString)
					.map(stringx -> stringx.replaceAll("\\\\", "/"))
					.filter(predicate)
					.collect(Collectors.toList());
			} catch (Throwable var18) {
				var6 = var18;
				throw var18;
			} finally {
				if (stream != null) {
					if (var6 != null) {
						try {
							stream.close();
						} catch (Throwable var17) {
							var6.addSuppressed(var17);
						}
					} else {
						stream.close();
					}
				}
			}

			return var7;
		} catch (NoSuchFileException var20) {
		} catch (IOException var21) {
			field_5290.warn("Unable to getFiles on {}", string, var21);
		}

		return Collections.emptyList();
	}
}
