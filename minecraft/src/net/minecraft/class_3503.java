package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3503<T> {
	private static final Logger field_15607 = LogManager.getLogger();
	private static final Gson field_15608 = new Gson();
	private static final int field_15603 = ".json".length();
	private Map<class_2960, class_3494<T>> field_15602 = ImmutableMap.of();
	private final Function<class_2960, Optional<T>> field_15609;
	private final String field_15605;
	private final boolean field_15601;
	private final String field_15606;

	public class_3503(Function<class_2960, Optional<T>> function, String string, boolean bl, String string2) {
		this.field_15609 = function;
		this.field_15605 = string;
		this.field_15601 = bl;
		this.field_15606 = string2;
	}

	@Nullable
	public class_3494<T> method_15193(class_2960 arg) {
		return (class_3494<T>)this.field_15602.get(arg);
	}

	public class_3494<T> method_15188(class_2960 arg) {
		class_3494<T> lv = (class_3494<T>)this.field_15602.get(arg);
		return lv == null ? new class_3494<>(arg) : lv;
	}

	public Collection<class_2960> method_15189() {
		return this.field_15602.keySet();
	}

	@Environment(EnvType.CLIENT)
	public Collection<class_2960> method_15191(T object) {
		List<class_2960> list = Lists.<class_2960>newArrayList();

		for (Entry<class_2960, class_3494<T>> entry : this.field_15602.entrySet()) {
			if (((class_3494)entry.getValue()).method_15141(object)) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public CompletableFuture<Map<class_2960, class_3494.class_3495<T>>> method_15192(class_3300 arg, Executor executor) {
		return CompletableFuture.supplyAsync(
			() -> {
				Map<class_2960, class_3494.class_3495<T>> map = Maps.<class_2960, class_3494.class_3495<T>>newHashMap();

				for (class_2960 lv : arg.method_14488(this.field_15605, stringx -> stringx.endsWith(".json"))) {
					String string = lv.method_12832();
					class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(this.field_15605.length() + 1, string.length() - field_15603));

					try {
						for (class_3298 lv3 : arg.method_14489(lv)) {
							try {
								InputStream inputStream = lv3.method_14482();
								Throwable var10 = null;

								try {
									Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
									Throwable var12 = null;

									try {
										JsonObject jsonObject = class_3518.method_15276(field_15608, reader, JsonObject.class);
										if (jsonObject == null) {
											field_15607.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.field_15606, lv2, lv, lv3.method_14480());
										} else {
											((class_3494.class_3495)map.computeIfAbsent(
													lv2, argxx -> class_156.method_654(class_3494.class_3495.method_15146(), argxxx -> argxxx.method_15154(this.field_15601))
												))
												.method_15147(this.field_15609, jsonObject);
										}
									} catch (Throwable var53) {
										var12 = var53;
										throw var53;
									} finally {
										if (reader != null) {
											if (var12 != null) {
												try {
													reader.close();
												} catch (Throwable var52) {
													var12.addSuppressed(var52);
												}
											} else {
												reader.close();
											}
										}
									}
								} catch (Throwable var55) {
									var10 = var55;
									throw var55;
								} finally {
									if (inputStream != null) {
										if (var10 != null) {
											try {
												inputStream.close();
											} catch (Throwable var51) {
												var10.addSuppressed(var51);
											}
										} else {
											inputStream.close();
										}
									}
								}
							} catch (RuntimeException | IOException var57) {
								field_15607.error("Couldn't read {} tag list {} from {} in data pack {}", this.field_15606, lv2, lv, lv3.method_14480(), var57);
							} finally {
								IOUtils.closeQuietly(lv3);
							}
						}
					} catch (IOException var59) {
						field_15607.error("Couldn't read {} tag list {} from {}", this.field_15606, lv2, lv, var59);
					}
				}

				return map;
			},
			executor
		);
	}

	public void method_18242(Map<class_2960, class_3494.class_3495<T>> map) {
		Map<class_2960, class_3494<T>> map2 = Maps.<class_2960, class_3494<T>>newHashMap();

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<class_2960, class_3494.class_3495<T>>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<class_2960, class_3494.class_3495<T>> entry = (Entry<class_2960, class_3494.class_3495<T>>)iterator.next();
				class_3494.class_3495<T> lv = (class_3494.class_3495<T>)entry.getValue();
				if (lv.method_15152(map2::get)) {
					bl = true;
					class_2960 lv2 = (class_2960)entry.getKey();
					map2.put(lv2, lv.method_15144(lv2));
					iterator.remove();
				}
			}

			if (!bl) {
				map.forEach(
					(arg, arg2) -> field_15607.error(
							"Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.field_15606, arg
						)
				);
				break;
			}
		}

		map.forEach((arg, arg2) -> {
			class_3494 var10000 = (class_3494)map2.put(arg, arg2.method_15144(arg));
		});
		this.method_20735(map2);
	}

	protected void method_20735(Map<class_2960, class_3494<T>> map) {
		this.field_15602 = ImmutableMap.copyOf(map);
	}

	public Map<class_2960, class_3494<T>> method_15196() {
		return this.field_15602;
	}
}
