package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_378 implements AutoCloseable {
	private static final Logger field_2261 = LogManager.getLogger();
	private final Map<class_2960, class_327> field_2259 = Maps.<class_2960, class_327>newHashMap();
	private final Set<class_390> field_18013 = Sets.<class_390>newHashSet();
	private final class_1060 field_2260;
	private boolean field_2258;
	private final class_3302 field_18215 = new class_4080<Map<class_2960, List<class_390>>>() {
		protected Map<class_2960, List<class_390>> method_18638(class_3300 arg, class_3695 arg2) {
			arg2.method_16065();
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			Map<class_2960, List<class_390>> map = Maps.<class_2960, List<class_390>>newHashMap();

			for (class_2960 lv : arg.method_14488("font", stringx -> stringx.endsWith(".json"))) {
				String string = lv.method_12832();
				class_2960 lv2 = new class_2960(lv.method_12836(), string.substring("font/".length(), string.length() - ".json".length()));
				List<class_390> list = (List<class_390>)map.computeIfAbsent(lv2, argx -> Lists.<class_390>newArrayList(new class_376()));
				arg2.method_15400(lv2::toString);

				try {
					for (class_3298 lv3 : arg.method_14489(lv)) {
						arg2.method_15400(lv3::method_14480);

						try {
							InputStream inputStream = lv3.method_14482();
							Throwable var13 = null;

							try {
								Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
								Throwable var15 = null;

								try {
									arg2.method_15396("reading");
									JsonArray jsonArray = class_3518.method_15261(class_3518.method_15276(gson, reader, JsonObject.class), "providers");
									arg2.method_15405("parsing");

									for (int i = jsonArray.size() - 1; i >= 0; i--) {
										JsonObject jsonObject = class_3518.method_15295(jsonArray.get(i), "providers[" + i + "]");

										try {
											String string2 = class_3518.method_15265(jsonObject, "type");
											class_394 lv4 = class_394.method_2048(string2);
											if (!class_378.this.field_2258 || lv4 == class_394.field_2313 || !lv2.equals(class_310.field_1740)) {
												arg2.method_15396(string2);
												list.add(lv4.method_2047(jsonObject).method_2039(arg));
												arg2.method_15407();
											}
										} catch (RuntimeException var48) {
											class_378.field_2261.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", lv2, lv3.method_14480(), var48.getMessage());
										}
									}

									arg2.method_15407();
								} catch (Throwable var49) {
									var15 = var49;
									throw var49;
								} finally {
									if (reader != null) {
										if (var15 != null) {
											try {
												reader.close();
											} catch (Throwable var47) {
												var15.addSuppressed(var47);
											}
										} else {
											reader.close();
										}
									}
								}
							} catch (Throwable var51) {
								var13 = var51;
								throw var51;
							} finally {
								if (inputStream != null) {
									if (var13 != null) {
										try {
											inputStream.close();
										} catch (Throwable var46) {
											var13.addSuppressed(var46);
										}
									} else {
										inputStream.close();
									}
								}
							}
						} catch (RuntimeException var53) {
							class_378.field_2261.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", lv2, lv3.method_14480(), var53.getMessage());
						}

						arg2.method_15407();
					}
				} catch (IOException var54) {
					class_378.field_2261.warn("Unable to load font '{}' in fonts.json: {}", lv2, var54.getMessage());
				}

				arg2.method_15396("caching");

				for (char c = 0; c < '\uffff'; c++) {
					if (c != ' ') {
						for (class_390 lv5 : Lists.reverse(list)) {
							if (lv5.method_2040(c) != null) {
								break;
							}
						}
					}
				}

				arg2.method_15407();
				arg2.method_15407();
			}

			arg2.method_16066();
			return map;
		}

		protected void method_18635(Map<class_2960, List<class_390>> map, class_3300 arg, class_3695 arg2) {
			arg2.method_16065();
			arg2.method_15396("reloading");
			Stream.concat(class_378.this.field_2259.keySet().stream(), map.keySet().stream())
				.distinct()
				.forEach(
					argx -> {
						List<class_390> list = (List<class_390>)map.getOrDefault(argx, Collections.emptyList());
						Collections.reverse(list);
						((class_327)class_378.this.field_2259
								.computeIfAbsent(argx, argxx -> new class_327(class_378.this.field_2260, new class_377(class_378.this.field_2260, argxx))))
							.method_1715(list);
					}
				);
			map.values().forEach(class_378.this.field_18013::addAll);
			arg2.method_15407();
			arg2.method_16066();
		}
	};

	public class_378(class_1060 arg, boolean bl) {
		this.field_2260 = arg;
		this.field_2258 = bl;
	}

	@Nullable
	public class_327 method_2019(class_2960 arg) {
		return (class_327)this.field_2259.computeIfAbsent(arg, argx -> {
			class_327 lv = new class_327(this.field_2260, new class_377(this.field_2260, argx));
			lv.method_1715(Lists.<class_390>newArrayList(new class_376()));
			return lv;
		});
	}

	public void method_2018(boolean bl, Executor executor, Executor executor2) {
		if (bl != this.field_2258) {
			this.field_2258 = bl;
			class_3300 lv = class_310.method_1551().method_1478();
			class_3302.class_4045 lv2 = new class_3302.class_4045() {
				@Override
				public <T> CompletableFuture<T> method_18352(T object) {
					return CompletableFuture.completedFuture(object);
				}
			};
			this.field_18215.method_26769(lv2, lv, class_3694.field_16280, class_3694.field_16280, executor, executor2);
		}
	}

	public class_3302 method_18627() {
		return this.field_18215;
	}

	public void close() {
		this.field_2259.values().forEach(class_327::close);
		this.field_18013.forEach(class_390::close);
	}
}
