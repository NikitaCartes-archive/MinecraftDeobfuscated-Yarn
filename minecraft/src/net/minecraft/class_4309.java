package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_4309 extends class_4080<Map<class_2960, JsonObject>> {
	private static final Logger field_19377 = LogManager.getLogger();
	private static final int field_19378 = ".json".length();
	private final Gson field_19379;
	private final String field_19380;

	public class_4309(Gson gson, String string) {
		this.field_19379 = gson;
		this.field_19380 = string;
	}

	protected Map<class_2960, JsonObject> method_20731(class_3300 arg, class_3695 arg2) {
		Map<class_2960, JsonObject> map = Maps.<class_2960, JsonObject>newHashMap();
		int i = this.field_19380.length() + 1;

		for (class_2960 lv : arg.method_14488(this.field_19380, stringx -> stringx.endsWith(".json"))) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(i, string.length() - field_19378));

			try {
				class_3298 lv3 = arg.method_14486(lv);
				Throwable var10 = null;

				try {
					InputStream inputStream = lv3.method_14482();
					Throwable var12 = null;

					try {
						Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
						Throwable var14 = null;

						try {
							JsonObject jsonObject = class_3518.method_15276(this.field_19379, reader, JsonObject.class);
							if (jsonObject != null) {
								JsonObject jsonObject2 = (JsonObject)map.put(lv2, jsonObject);
								if (jsonObject2 != null) {
									throw new IllegalStateException("Duplicate data file ignored with ID " + lv2);
								}
							} else {
								field_19377.error("Couldn't load data file {} from {} as it's null or empty", lv2, lv);
							}
						} catch (Throwable var62) {
							var14 = var62;
							throw var62;
						} finally {
							if (reader != null) {
								if (var14 != null) {
									try {
										reader.close();
									} catch (Throwable var61) {
										var14.addSuppressed(var61);
									}
								} else {
									reader.close();
								}
							}
						}
					} catch (Throwable var64) {
						var12 = var64;
						throw var64;
					} finally {
						if (inputStream != null) {
							if (var12 != null) {
								try {
									inputStream.close();
								} catch (Throwable var60) {
									var12.addSuppressed(var60);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (Throwable var66) {
					var10 = var66;
					throw var66;
				} finally {
					if (lv3 != null) {
						if (var10 != null) {
							try {
								lv3.close();
							} catch (Throwable var59) {
								var10.addSuppressed(var59);
							}
						} else {
							lv3.close();
						}
					}
				}
			} catch (IllegalArgumentException | IOException | JsonParseException var68) {
				field_19377.error("Couldn't parse data file {} from {}", lv2, lv, var68);
			}
		}

		return map;
	}
}
