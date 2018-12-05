package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.StatisticsClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3442 extends class_3469 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftServer server;
	private final File file;
	private final Set<Stat<?>> field_15307 = Sets.<Stat<?>>newHashSet();
	private int field_15306 = -300;

	public class_3442(MinecraftServer minecraftServer, File file) {
		this.server = minecraftServer;
		this.file = file;
		if (file.isFile()) {
			try {
				this.method_14906(minecraftServer.getDataFixer(), FileUtils.readFileToString(file));
			} catch (IOException var4) {
				LOGGER.error("Couldn't read statistics file {}", file, var4);
			} catch (JsonParseException var5) {
				LOGGER.error("Couldn't parse statistics file {}", file, var5);
			}
		}
	}

	public void method_14912() {
		try {
			FileUtils.writeStringToFile(this.file, this.method_14911());
		} catch (IOException var2) {
			LOGGER.error("Couldn't save stats", (Throwable)var2);
		}
	}

	@Override
	public void method_15023(PlayerEntity playerEntity, Stat<?> stat, int i) {
		super.method_15023(playerEntity, stat, i);
		this.field_15307.add(stat);
	}

	private Set<Stat<?>> method_14909() {
		Set<Stat<?>> set = Sets.<Stat<?>>newHashSet(this.field_15307);
		this.field_15307.clear();
		return set;
	}

	public void method_14906(DataFixer dataFixer, String string) {
		try {
			JsonReader jsonReader = new JsonReader(new StringReader(string));
			Throwable var4 = null;

			try {
				jsonReader.setLenient(false);
				JsonElement jsonElement = Streams.parse(jsonReader);
				if (!jsonElement.isJsonNull()) {
					CompoundTag compoundTag = method_14908(jsonElement.getAsJsonObject());
					if (!compoundTag.containsKey("DataVersion", 99)) {
						compoundTag.putInt("DataVersion", 1343);
					}

					compoundTag = TagHelper.update(dataFixer, DataFixTypes.STATS, compoundTag, compoundTag.getInt("DataVersion"));
					if (compoundTag.containsKey("stats", 10)) {
						CompoundTag compoundTag2 = compoundTag.getCompound("stats");

						for (String string2 : compoundTag2.getKeys()) {
							if (compoundTag2.containsKey(string2, 10)) {
								StatType<?> statType = Registry.STAT_TYPE.get(new Identifier(string2));
								if (statType == null) {
									LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, string2);
								} else {
									CompoundTag compoundTag3 = compoundTag2.getCompound(string2);

									for (String string3 : compoundTag3.getKeys()) {
										if (compoundTag3.containsKey(string3, 99)) {
											Stat<?> stat = this.method_14905(statType, string3);
											if (stat == null) {
												LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, string3);
											} else {
												this.field_15431.put(stat, compoundTag3.getInt(string3));
											}
										} else {
											LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, compoundTag3.getTag(string3), string3);
										}
									}
								}
							}
						}
					}

					return;
				}

				LOGGER.error("Unable to parse Stat data from {}", this.file);
			} catch (Throwable var24) {
				var4 = var24;
				throw var24;
			} finally {
				if (jsonReader != null) {
					if (var4 != null) {
						try {
							jsonReader.close();
						} catch (Throwable var23) {
							var4.addSuppressed(var23);
						}
					} else {
						jsonReader.close();
					}
				}
			}
		} catch (IOException | JsonParseException var26) {
			LOGGER.error("Unable to parse Stat data from {}", this.file, var26);
		}
	}

	@Nullable
	private <T> Stat<T> method_14905(StatType<T> statType, String string) {
		Identifier identifier = Identifier.create(string);
		if (identifier == null) {
			return null;
		} else {
			T object = statType.method_14959().get(identifier);
			return object == null ? null : statType.method_14956(object);
		}
	}

	private static CompoundTag method_14908(JsonObject jsonObject) {
		CompoundTag compoundTag = new CompoundTag();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonElement jsonElement = (JsonElement)entry.getValue();
			if (jsonElement.isJsonObject()) {
				compoundTag.put((String)entry.getKey(), method_14908(jsonElement.getAsJsonObject()));
			} else if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					compoundTag.putInt((String)entry.getKey(), jsonPrimitive.getAsInt());
				}
			}
		}

		return compoundTag;
	}

	protected String method_14911() {
		Map<StatType<?>, JsonObject> map = Maps.<StatType<?>, JsonObject>newHashMap();

		for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> entry : this.field_15431.object2IntEntrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			((JsonObject)map.computeIfAbsent(stat.getType(), statType -> new JsonObject())).addProperty(method_14907(stat).toString(), entry.getIntValue());
		}

		JsonObject jsonObject = new JsonObject();

		for (Entry<StatType<?>, JsonObject> entry2 : map.entrySet()) {
			jsonObject.add(Registry.STAT_TYPE.getId((StatType<?>)entry2.getKey()).toString(), (JsonElement)entry2.getValue());
		}

		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.add("stats", jsonObject);
		jsonObject2.addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		return jsonObject2.toString();
	}

	private static <T> Identifier method_14907(Stat<T> stat) {
		return stat.getType().method_14959().getId(stat.getValue());
	}

	public void method_14914() {
		this.field_15307.addAll(this.field_15431.keySet());
	}

	public void method_14910(ServerPlayerEntity serverPlayerEntity) {
		int i = this.server.getTicks();
		Object2IntMap<Stat<?>> object2IntMap = new Object2IntOpenHashMap<>();
		if (i - this.field_15306 > 300) {
			this.field_15306 = i;

			for (Stat<?> stat : this.method_14909()) {
				object2IntMap.put(stat, this.method_15025(stat));
			}
		}

		serverPlayerEntity.networkHandler.sendPacket(new StatisticsClientPacket(object2IntMap));
	}
}
