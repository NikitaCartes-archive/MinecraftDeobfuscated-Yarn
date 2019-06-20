package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2409 implements class_2405 {
	private static final Logger field_11290 = LogManager.getLogger();
	private static final Gson field_11291 = new GsonBuilder().setPrettyPrinting().create();
	private final class_2403 field_11288;
	private final List<Consumer<Consumer<class_161>>> field_11289 = ImmutableList.of(
		new class_2419(), new class_2414(), new class_2412(), new class_2416(), new class_2417()
	);

	public class_2409(class_2403 arg) {
		this.field_11288 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		Path path = this.field_11288.method_10313();
		Set<class_2960> set = Sets.<class_2960>newHashSet();
		Consumer<class_161> consumer = arg2 -> {
			if (!set.add(arg2.method_688())) {
				throw new IllegalStateException("Duplicate advancement " + arg2.method_688());
			} else {
				Path path2 = method_10334(path, arg2);

				try {
					class_2405.method_10320(field_11291, arg, arg2.method_689().method_698(), path2);
				} catch (IOException var6x) {
					field_11290.error("Couldn't save advancement {}", path2, var6x);
				}
			}
		};

		for (Consumer<Consumer<class_161>> consumer2 : this.field_11289) {
			consumer2.accept(consumer);
		}
	}

	private static Path method_10334(Path path, class_161 arg) {
		return path.resolve("data/" + arg.method_688().method_12836() + "/advancements/" + arg.method_688().method_12832() + ".json");
	}

	@Override
	public String method_10321() {
		return "Advancements";
	}
}
