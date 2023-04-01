package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class SplashTextResourceSupplier extends SinglePreparationResourceReloader<List<String>> {
	private static final Identifier RESOURCE_ID = new Identifier("texts/splashes.txt");
	private static final Random RANDOM = Random.create();
	private final List<String> splashTexts = Lists.<String>newArrayList();
	private final Session session;

	public SplashTextResourceSupplier(Session session) {
		this.session = session;
	}

	protected List<String> prepare(ResourceManager resourceManager, Profiler profiler) {
		try {
			BufferedReader bufferedReader = MinecraftClient.getInstance().getResourceManager().openAsReader(RESOURCE_ID);

			List var6;
			try {
				Stream<String> stream = bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != 125780783);
				Stream<String> stream2 = Stream.of("Exactly " + Registries.field_44443.size() + " rules to vote on!");
				var6 = (List)Stream.concat(stream, stream2).collect(Collectors.toList());
			} catch (Throwable var8) {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}

			return var6;
		} catch (IOException var9) {
			return Collections.emptyList();
		}
	}

	protected void apply(List<String> list, ResourceManager resourceManager, Profiler profiler) {
		this.splashTexts.clear();
		this.splashTexts.addAll(list);
	}

	@Nullable
	public String get() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			return "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			return "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			return "OOoooOOOoooo! Spooky!";
		} else if (this.splashTexts.isEmpty()) {
			return null;
		} else {
			return this.session != null && RANDOM.nextInt(this.splashTexts.size()) == 42
				? this.session.getUsername().toUpperCase(Locale.ROOT) + " IS YOU"
				: (String)this.splashTexts.get(RANDOM.nextInt(this.splashTexts.size()));
		}
	}
}
