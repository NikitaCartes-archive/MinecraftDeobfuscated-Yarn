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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.session.Session;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class SplashTextResourceSupplier extends SinglePreparationResourceReloader<List<String>> {
	private static final Identifier RESOURCE_ID = Identifier.ofVanilla("texts/splashes.txt");
	private static final Random RANDOM = Random.create();
	private final List<String> splashTexts = Lists.<String>newArrayList();
	private final Session session;

	public SplashTextResourceSupplier(Session session) {
		this.session = session;
	}

	protected List<String> prepare(ResourceManager resourceManager, Profiler profiler) {
		try {
			BufferedReader bufferedReader = MinecraftClient.getInstance().getResourceManager().openAsReader(RESOURCE_ID);

			List var4;
			try {
				var4 = (List)bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != 125780783).collect(Collectors.toList());
			} catch (Throwable var7) {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}

			return var4;
		} catch (IOException var8) {
			return Collections.emptyList();
		}
	}

	protected void apply(List<String> list, ResourceManager resourceManager, Profiler profiler) {
		this.splashTexts.clear();
		this.splashTexts.addAll(list);
	}

	@Nullable
	public SplashTextRenderer get() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			return SplashTextRenderer.MERRY_X_MAS_;
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			return SplashTextRenderer.HAPPY_NEW_YEAR_;
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			return SplashTextRenderer.OOOOO_O_O_OOOOO__SPOOKY_;
		} else if (this.splashTexts.isEmpty()) {
			return null;
		} else {
			return this.session != null && RANDOM.nextInt(this.splashTexts.size()) == 42
				? new SplashTextRenderer(this.session.getUsername().toUpperCase(Locale.ROOT) + " IS YOU")
				: new SplashTextRenderer((String)this.splashTexts.get(RANDOM.nextInt(this.splashTexts.size())));
		}
	}
}
