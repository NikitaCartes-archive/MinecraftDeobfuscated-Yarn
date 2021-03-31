package net.minecraft.stat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
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
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler extends StatHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_29821 = 300;
	private final MinecraftServer server;
	private final File file;
	private final Set<Stat<?>> pendingStats = Sets.<Stat<?>>newHashSet();
	private int lastStatsUpdate = -300;

	public ServerStatHandler(MinecraftServer server, File file) {
		this.server = server;
		this.file = file;
		if (file.isFile()) {
			try {
				this.parse(server.getDataFixer(), FileUtils.readFileToString(file));
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
	public void setStat(PlayerEntity player, Stat<?> stat, int value) {
		super.setStat(player, stat, value);
		this.pendingStats.add(stat);
	}

	private Set<Stat<?>> takePendingStats() {
		Set<Stat<?>> set = Sets.<Stat<?>>newHashSet(this.pendingStats);
		this.pendingStats.clear();
		return set;
	}

	public void parse(DataFixer dataFixer, String json) {
		try {
			JsonReader jsonReader = new JsonReader(new StringReader(json));
			Throwable var4 = null;

			try {
				jsonReader.setLenient(false);
				JsonElement jsonElement = Streams.parse(jsonReader);
				if (!jsonElement.isJsonNull()) {
					NbtCompound nbtCompound = jsonToCompound(jsonElement.getAsJsonObject());
					if (!nbtCompound.contains("DataVersion", NbtElement.NUMBER_TYPE)) {
						nbtCompound.putInt("DataVersion", 1343);
					}

					nbtCompound = NbtHelper.update(dataFixer, DataFixTypes.STATS, nbtCompound, nbtCompound.getInt("DataVersion"));
					if (nbtCompound.contains("stats", NbtElement.COMPOUND_TYPE)) {
						NbtCompound nbtCompound2 = nbtCompound.getCompound("stats");

						for (String string : nbtCompound2.getKeys()) {
							if (nbtCompound2.contains(string, NbtElement.COMPOUND_TYPE)) {
								Util.ifPresentOrElse(
									Registry.STAT_TYPE.getOrEmpty(new Identifier(string)),
									statType -> {
										NbtCompound nbtCompound2x = nbtCompound2.getCompound(string);

										for (String string2 : nbtCompound2x.getKeys()) {
											if (nbtCompound2x.contains(string2, NbtElement.NUMBER_TYPE)) {
												Util.ifPresentOrElse(
													this.createStat(statType, string2),
													stat -> this.statMap.put(stat, nbtCompound2x.getInt(string2)),
													() -> LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, string2)
												);
											} else {
												LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, nbtCompound2x.get(string2), string2);
											}
										}
									},
									() -> LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, string)
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

	private <T> Optional<Stat<T>> createStat(StatType<T> type, String id) {
		return Optional.ofNullable(Identifier.tryParse(id)).flatMap(type.getRegistry()::getOrEmpty).map(type::getOrCreateStat);
	}

	private static NbtCompound jsonToCompound(JsonObject json) {
		NbtCompound nbtCompound = new NbtCompound();

		for (Entry<String, JsonElement> entry : json.entrySet()) {
			JsonElement jsonElement = (JsonElement)entry.getValue();
			if (jsonElement.isJsonObject()) {
				nbtCompound.put((String)entry.getKey(), jsonToCompound(jsonElement.getAsJsonObject()));
			} else if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					nbtCompound.putInt((String)entry.getKey(), jsonPrimitive.getAsInt());
				}
			}
		}

		return nbtCompound;
	}

	protected String asString() {
		Map<StatType<?>, JsonObject> map = Maps.<StatType<?>, JsonObject>newHashMap();

		for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> entry : this.statMap.object2IntEntrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			((JsonObject)map.computeIfAbsent(stat.getType(), statType -> new JsonObject())).addProperty(getStatId(stat).toString(), entry.getIntValue());
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

	private static <T> Identifier getStatId(Stat<T> stat) {
		return stat.getType().getRegistry().getId(stat.getValue());
	}

	public void updateStatSet() {
		this.pendingStats.addAll(this.statMap.keySet());
	}

	public void sendStats(ServerPlayerEntity player) {
		int i = this.server.getTicks();
		Object2IntMap<Stat<?>> object2IntMap = new Object2IntOpenHashMap<>();
		if (i - this.lastStatsUpdate > 300) {
			this.lastStatsUpdate = i;

			for (Stat<?> stat : this.takePendingStats()) {
				object2IntMap.put(stat, this.getStat(stat));
			}
		}

		player.networkHandler.sendPacket(new StatisticsS2CPacket(object2IntMap));
	}
}
