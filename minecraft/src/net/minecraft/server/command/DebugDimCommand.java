package net.minecraft.server.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.Dimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugDimCommand {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger LOGGER = LogManager.getLogger();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("debugdim").executes(commandContext -> execute(commandContext.getSource())));
	}

	private static int execute(ServerCommandSource source) {
		Dimension dimension = source.getWorld().getDimension();
		File file = source.getWorld().getSaveHandler().getWorldDir();
		File file2 = new File(file, "debug");
		file2.mkdirs();
		Dynamic<JsonElement> dynamic = dimension.method_26496(JsonOps.INSTANCE);
		int i = Registry.DIMENSION_TYPE.getRawId(dimension.getType());
		File file3 = new File(file2, "dim-" + i + ".json");

		try {
			Writer writer = Files.newBufferedWriter(file3.toPath());
			Throwable var8 = null;

			try {
				GSON.toJson(dynamic.getValue(), writer);
			} catch (Throwable var18) {
				var8 = var18;
				throw var18;
			} finally {
				if (writer != null) {
					if (var8 != null) {
						try {
							writer.close();
						} catch (Throwable var17) {
							var8.addSuppressed(var17);
						}
					} else {
						writer.close();
					}
				}
			}
		} catch (IOException var20) {
			LOGGER.warn("Failed to save file {}", file3.getAbsolutePath(), var20);
		}

		dimension.method_26498().forEach(biome -> {
			int ix = Registry.BIOME.getRawId(biome);
			Dynamic<JsonElement> dynamicx = biome.method_26448(JsonOps.INSTANCE);
			File file2x = new File(file2, "biome-" + ix + ".json");

			try {
				Writer writer = Files.newBufferedWriter(file2x.toPath());
				Throwable var6x = null;

				try {
					GSON.toJson(dynamicx.getValue(), writer);
				} catch (Throwable var16) {
					var6x = var16;
					throw var16;
				} finally {
					if (writer != null) {
						if (var6x != null) {
							try {
								writer.close();
							} catch (Throwable var15) {
								var6x.addSuppressed(var15);
							}
						} else {
							writer.close();
						}
					}
				}
			} catch (IOException var18x) {
				LOGGER.warn("Failed to save file {}", file2x.getAbsolutePath(), var18x);
			}
		});
		source.sendFeedback(new LiteralText("Saved to file: " + file2), false);
		return 0;
	}
}
