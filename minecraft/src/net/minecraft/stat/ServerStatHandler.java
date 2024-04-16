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
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
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
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatHandler extends StatHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftServer server;
	private final File file;
	private final Set<Stat<?>> pendingStats = Sets.<Stat<?>>newHashSet();

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

			label47: {
				try {
					jsonReader.setLenient(false);
					JsonElement jsonElement = Streams.parse(jsonReader);
					if (!jsonElement.isJsonNull()) {
						NbtCompound nbtCompound = jsonToCompound(jsonElement.getAsJsonObject());
						nbtCompound = DataFixTypes.STATS.update(dataFixer, nbtCompound, NbtHelper.getDataVersion(nbtCompound, 1343));
						if (!nbtCompound.contains("stats", NbtElement.COMPOUND_TYPE)) {
							break label47;
						}

						NbtCompound nbtCompound2 = nbtCompound.getCompound("stats");
						Iterator var7 = nbtCompound2.getKeys().iterator();

						while (true) {
							if (!var7.hasNext()) {
								break label47;
							}

							String string = (String)var7.next();
							if (nbtCompound2.contains(string, NbtElement.COMPOUND_TYPE)) {
								Util.ifPresentOrElse(
									Registries.STAT_TYPE.getOrEmpty(new Identifier(string)),
									statType -> {
										NbtCompound nbtCompound2x = nbtCompound2.getCompound(string);

										for (String string2 : nbtCompound2x.getKeys()) {
											if (nbtCompound2x.contains(string2, NbtElement.NUMBER_TYPE)) {
												Util.ifPresentOrElse(
													this.createStat(statType, string2),
													id -> this.statMap.put(id, nbtCompound2x.getInt(string2)),
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

					LOGGER.error("Unable to parse Stat data from {}", this.file);
				} catch (Throwable var10) {
					try {
						jsonReader.close();
					} catch (Throwable var9) {
						var10.addSuppressed(var9);
					}

					throw var10;
				}

				jsonReader.close();
				return;
			}

			jsonReader.close();
		} catch (IOException | JsonParseException var11) {
			LOGGER.error("Unable to parse Stat data from {}", this.file, var11);
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
			jsonObject.add(Registries.STAT_TYPE.getId((StatType<?>)entry2.getKey()).toString(), (JsonElement)entry2.getValue());
		}

		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.add("stats", jsonObject);
		jsonObject2.addProperty("DataVersion", SharedConstants.getGameVersion().getSaveVersion().getId());
		return jsonObject2.toString();
	}

	private static <T> Identifier getStatId(Stat<T> stat) {
		return stat.getType().getRegistry().getId(stat.getValue());
	}

	public void updateStatSet() {
		this.pendingStats.addAll(this.statMap.keySet());
	}

	public void sendStats(ServerPlayerEntity player) {
		Object2IntMap<Stat<?>> object2IntMap = new Object2IntOpenHashMap<>();

		for (Stat<?> stat : this.takePendingStats()) {
			object2IntMap.put(stat, this.getStat(stat));
		}

		player.networkHandler.sendPacket(new StatisticsS2CPacket(object2IntMap));
	}
}
