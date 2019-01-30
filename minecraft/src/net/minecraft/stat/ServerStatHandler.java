package net.minecraft.stat;

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
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.packet.StatisticsClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TagHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler extends StatHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftServer server;
	private final File file;
	private final Set<Stat<?>> field_15307 = Sets.<Stat<?>>newHashSet();
	private int field_15306 = -300;

	public ServerStatHandler(MinecraftServer minecraftServer, File file) {
		this.server = minecraftServer;
		this.file = file;
		if (file.isFile()) {
			try {
				this.parse(minecraftServer.getDataFixer(), FileUtils.readFileToString(file));
			} catch (IOException var4) {
				LOGGER.error("Couldn't read statistics file {}", file, var4);
			} catch (JsonParseException var5) {
				LOGGER.error("Couldn't parse statistics file {}", file, var5);
			}
		}
	}

	public void save() {
		try {
			FileUtils.writeStringToFile(this.file, this.asString());
		} catch (IOException var2) {
			LOGGER.error("Couldn't save stats", (Throwable)var2);
		}
	}

	@Override
	public void setStat(PlayerEntity playerEntity, Stat<?> stat, int i) {
		super.setStat(playerEntity, stat, i);
		this.field_15307.add(stat);
	}

	private Set<Stat<?>> method_14909() {
		Set<Stat<?>> set = Sets.<Stat<?>>newHashSet(this.field_15307);
		this.field_15307.clear();
		return set;
	}

	public void parse(DataFixer dataFixer, String string) {
		try {
			JsonReader jsonReader = new JsonReader(new StringReader(string));
			Throwable var4 = null;

			try {
				jsonReader.setLenient(false);
				JsonElement jsonElement = Streams.parse(jsonReader);
				if (!jsonElement.isJsonNull()) {
					CompoundTag compoundTag = jsonToCompound(jsonElement.getAsJsonObject());
					if (!compoundTag.containsKey("DataVersion", 99)) {
						compoundTag.putInt("DataVersion", 1343);
					}

					compoundTag = TagHelper.update(dataFixer, DataFixTypes.STATS, compoundTag, compoundTag.getInt("DataVersion"));
					if (compoundTag.containsKey("stats", 10)) {
						CompoundTag compoundTag2 = compoundTag.getCompound("stats");

						for (String string2 : compoundTag2.getKeys()) {
							if (compoundTag2.containsKey(string2, 10)) {
								SystemUtil.method_17974(
									Registry.STAT_TYPE.getOptional(new Identifier(string2)),
									statType -> {
										CompoundTag compoundTag2x = compoundTag2.getCompound(string2);

										for (String string2x : compoundTag2x.getKeys()) {
											if (compoundTag2x.containsKey(string2x, 99)) {
												SystemUtil.method_17974(
													this.method_14905(statType, string2x),
													stat -> this.statMap.put(stat, compoundTag2x.getInt(string2x)),
													() -> LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, string2x)
												);
											} else {
												LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, compoundTag2x.getTag(string2x), string2x);
											}
										}
									},
									() -> LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, string2)
								);
							}
						}
					}

					return;
				}

				LOGGER.error("Unable to parse Stat data from {}", this.file);
			} catch (Throwable var19) {
				var4 = var19;
				throw var19;
			} finally {
				if (jsonReader != null) {
					if (var4 != null) {
						try {
							jsonReader.close();
						} catch (Throwable var18) {
							var4.addSuppressed(var18);
						}
					} else {
						jsonReader.close();
					}
				}
			}
		} catch (IOException | JsonParseException var21) {
			LOGGER.error("Unable to parse Stat data from {}", this.file, var21);
		}
	}

	private <T> Optional<Stat<T>> method_14905(StatType<T> statType, String string) {
		return Optional.ofNullable(Identifier.create(string)).flatMap(statType.getRegistry()::getOptional).map(statType::getOrCreateStat);
	}

	private static CompoundTag jsonToCompound(JsonObject jsonObject) {
		CompoundTag compoundTag = new CompoundTag();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonElement jsonElement = (JsonElement)entry.getValue();
			if (jsonElement.isJsonObject()) {
				compoundTag.put((String)entry.getKey(), jsonToCompound(jsonElement.getAsJsonObject()));
			} else if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					compoundTag.putInt((String)entry.getKey(), jsonPrimitive.getAsInt());
				}
			}
		}

		return compoundTag;
	}

	protected String asString() {
		Map<StatType<?>, JsonObject> map = Maps.<StatType<?>, JsonObject>newHashMap();

		for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> entry : this.statMap.object2IntEntrySet()) {
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
		return stat.getType().getRegistry().getId(stat.getValue());
	}

	public void method_14914() {
		this.field_15307.addAll(this.statMap.keySet());
	}

	public void method_14910(ServerPlayerEntity serverPlayerEntity) {
		int i = this.server.getTicks();
		Object2IntMap<Stat<?>> object2IntMap = new Object2IntOpenHashMap<>();
		if (i - this.field_15306 > 300) {
			this.field_15306 = i;

			for (Stat<?> stat : this.method_14909()) {
				object2IntMap.put(stat, this.getStat(stat));
			}
		}

		serverPlayerEntity.networkHandler.sendPacket(new StatisticsClientPacket(object2IntMap));
	}
}
