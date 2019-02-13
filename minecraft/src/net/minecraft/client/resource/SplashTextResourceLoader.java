package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class SplashTextResourceLoader implements ResourceReloadListener {
	private static final Identifier RESOURCE_ID = new Identifier("texts/splashes.txt");
	private static final Random RANDOM = new Random();
	private final List<String> splashTexts = Lists.<String>newArrayList();

	@Override
	public CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> list = Lists.<String>newArrayList();

			try {
				Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(RESOURCE_ID);
				Throwable var2 = null;

				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

					String string;
					while ((string = bufferedReader.readLine()) != null) {
						string = string.trim();
						if (!string.isEmpty() && string.hashCode() != 125780783) {
							list.add(string);
						}
					}
				} catch (Throwable var13) {
					var2 = var13;
					throw var13;
				} finally {
					if (resource != null) {
						if (var2 != null) {
							try {
								resource.close();
							} catch (Throwable var12) {
								var2.addSuppressed(var12);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (IOException var15) {
			}

			return list;
		}, executor).thenCompose(helper::waitForAll).thenAcceptAsync(list -> {
			this.splashTexts.clear();
			this.splashTexts.addAll(list);
		}, executor2);
	}

	public String get() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			return "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			return "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			return "OOoooOOOoooo! Spooky!";
		} else {
			return this.splashTexts.isEmpty() ? "missingno" : (String)this.splashTexts.get(RANDOM.nextInt(this.splashTexts.size()));
		}
	}
}
