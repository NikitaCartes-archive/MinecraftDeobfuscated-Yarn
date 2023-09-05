package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

public class class_8796 {
	public static void method_53856(String[] strings) throws IOException {
		SharedConstants.setGameVersion(MinecraftVersion.CURRENT);
		Bootstrap.initialize();

		for (String string : strings) {
			method_53854(string);
		}
	}

	private static void method_53854(String string) throws IOException {
		Stream<Path> stream = Files.walk(Paths.get(string));

		try {
			stream.filter(path -> path.toString().endsWith(".snbt")).forEach(path -> {
				try {
					String stringx = Files.readString(path);
					NbtCompound nbtCompound = NbtHelper.fromNbtProviderString(stringx);
					NbtCompound nbtCompound2 = StructureValidatorProvider.update(path.toString(), nbtCompound);
					NbtProvider.writeTo(DataWriter.UNCACHED, path, NbtHelper.toNbtProviderString(nbtCompound2));
				} catch (IOException | CommandSyntaxException var4x) {
					throw new RuntimeException(var4x);
				}
			});
		} catch (Throwable var5) {
			if (stream != null) {
				try {
					stream.close();
				} catch (Throwable var4) {
					var5.addSuppressed(var4);
				}
			}

			throw var5;
		}

		if (stream != null) {
			stream.close();
		}
	}
}
