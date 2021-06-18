package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class SplashTextResourceSupplier extends SinglePreparationResourceReloader<List<String>> {
	private static final Identifier RESOURCE_ID = new Identifier("texts/splashes.txt");
	private static final Random RANDOM = new Random();
	private final List<String> splashTexts = Lists.<String>newArrayList();
	private final Session session;

	public SplashTextResourceSupplier(Session session) {
		this.session = session;
	}

	protected List<String> prepare(ResourceManager resourceManager, Profiler profiler) {
		try {
			Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(RESOURCE_ID);

			List var5;
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

				try {
					var5 = (List)bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != 125780783).collect(Collectors.toList());
				} catch (Throwable var9) {
					try {
						bufferedReader.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}

					throw var9;
				}

				bufferedReader.close();
			} catch (Throwable var10) {
				if (resource != null) {
					try {
						resource.close();
					} catch (Throwable var7) {
						var10.addSuppressed(var7);
					}
				}

				throw var10;
			}

			if (resource != null) {
				resource.close();
			}

			return var5;
		} catch (IOException var11) {
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
