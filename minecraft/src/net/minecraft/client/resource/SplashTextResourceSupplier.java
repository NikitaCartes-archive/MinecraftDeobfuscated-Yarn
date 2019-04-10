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
import net.minecraft.resource.SupplyingResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class SplashTextResourceSupplier extends SupplyingResourceReloadListener<List<String>> {
	private static final Identifier RESOURCE_ID = new Identifier("texts/splashes.txt");
	private static final Random RANDOM = new Random();
	private final List<String> splashTexts = Lists.<String>newArrayList();
	private final Session field_18934;

	public SplashTextResourceSupplier(Session session) {
		this.field_18934 = session;
	}

	protected List<String> method_18176(ResourceManager resourceManager, Profiler profiler) {
		try {
			Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(RESOURCE_ID);
			Throwable var4 = null;

			List var7;
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
				Throwable var6 = null;

				try {
					var7 = (List)bufferedReader.lines().map(String::trim).filter(string -> string.hashCode() != 125780783).collect(Collectors.toList());
				} catch (Throwable var32) {
					var6 = var32;
					throw var32;
				} finally {
					if (bufferedReader != null) {
						if (var6 != null) {
							try {
								bufferedReader.close();
							} catch (Throwable var31) {
								var6.addSuppressed(var31);
							}
						} else {
							bufferedReader.close();
						}
					}
				}
			} catch (Throwable var34) {
				var4 = var34;
				throw var34;
			} finally {
				if (resource != null) {
					if (var4 != null) {
						try {
							resource.close();
						} catch (Throwable var30) {
							var4.addSuppressed(var30);
						}
					} else {
						resource.close();
					}
				}
			}

			return var7;
		} catch (IOException var36) {
			return Collections.emptyList();
		}
	}

	protected void method_18175(List<String> list, ResourceManager resourceManager, Profiler profiler) {
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
			return this.field_18934 != null && RANDOM.nextInt(this.splashTexts.size()) == 42
				? this.field_18934.getUsername().toUpperCase(Locale.ROOT) + " IS YOU"
				: (String)this.splashTexts.get(RANDOM.nextInt(this.splashTexts.size()));
		}
	}
}
