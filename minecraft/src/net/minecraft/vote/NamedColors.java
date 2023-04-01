package net.minecraft.vote;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public class NamedColors {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final List<NamedColors.NamedColor> NAMED_COLORS;
	public static final Map<String, NamedColors.NamedColor> FROM_NAME;
	public static final Codec<NamedColors.NamedColor> COLOR_CODEC;

	static {
		Builder<NamedColors.NamedColor> builder = ImmutableList.builder();
		InputStream inputStream = NamedColors.class.getResourceAsStream("/rgb.txt");
		if (inputStream != null) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

				try {
					bufferedReader.lines().forEach(string -> {
						if (!string.startsWith("#") && !string.isBlank()) {
							int i = string.indexOf(9);
							if (i == -1) {
								LOGGER.warn("Weird line: {}", string);
							} else {
								String string2 = string.substring(0, i);
								String string3 = string.substring(i + 1);
								if (!string3.startsWith("#")) {
									LOGGER.warn("Weird line: {}", string);
								}

								int j;
								try {
									j = Integer.parseInt(string3.substring(1, 7), 16);
								} catch (NumberFormatException var7x) {
									LOGGER.warn("Weird line: {}", string);
									return;
								}

								builder.add(new NamedColors.NamedColor(string2, j));
							}
						}
					});
				} catch (Throwable var6) {
					try {
						bufferedReader.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}

					throw var6;
				}

				bufferedReader.close();
			} catch (Exception var7) {
				LOGGER.warn("Failed to read rgb.txt", (Throwable)var7);
			}
		} else {
			LOGGER.warn("Where rgb.txt?");
		}

		NAMED_COLORS = builder.build();
		FROM_NAME = (Map<String, NamedColors.NamedColor>)NAMED_COLORS.stream()
			.collect(ImmutableMap.toImmutableMap(NamedColors.NamedColor::name, namedColor -> namedColor));
		COLOR_CODEC = Codecs.idChecked(StringIdentifiable::asString, FROM_NAME::get);
	}

	public static record NamedColor(String name, int rgb) implements StringIdentifiable {
		@Override
		public String asString() {
			return this.name;
		}
	}
}
