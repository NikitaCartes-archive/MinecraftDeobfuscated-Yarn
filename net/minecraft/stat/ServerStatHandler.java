/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler
extends StatHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_29821 = 300;
    private final MinecraftServer server;
    private final File file;
    private final Set<Stat<?>> pendingStats = Sets.newHashSet();
    private int lastStatsUpdate = -300;

    public ServerStatHandler(MinecraftServer server, File file) {
        this.server = server;
        this.file = file;
        if (file.isFile()) {
            try {
                this.parse(server.getDataFixer(), FileUtils.readFileToString(file));
            } catch (IOException iOException) {
                LOGGER.error("Couldn't read statistics file {}", (Object)file, (Object)iOException);
            } catch (JsonParseException jsonParseException) {
                LOGGER.error("Couldn't parse statistics file {}", (Object)file, (Object)jsonParseException);
            }
        }
    }

    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.asString());
        } catch (IOException iOException) {
            LOGGER.error("Couldn't save stats", (Throwable)iOException);
        }
    }

    @Override
    public void setStat(PlayerEntity player, Stat<?> stat, int value) {
        super.setStat(player, stat, value);
        this.pendingStats.add(stat);
    }

    private Set<Stat<?>> takePendingStats() {
        HashSet<Stat<?>> set = Sets.newHashSet(this.pendingStats);
        this.pendingStats.clear();
        return set;
    }

    public void parse(DataFixer dataFixer, String json) {
        try (JsonReader jsonReader = new JsonReader(new StringReader(json));){
            jsonReader.setLenient(false);
            JsonElement jsonElement = Streams.parse(jsonReader);
            if (jsonElement.isJsonNull()) {
                LOGGER.error("Unable to parse Stat data from {}", (Object)this.file);
                return;
            }
            NbtCompound nbtCompound = ServerStatHandler.jsonToCompound(jsonElement.getAsJsonObject());
            if (!nbtCompound.contains("DataVersion", 99)) {
                nbtCompound.putInt("DataVersion", 1343);
            }
            if ((nbtCompound = NbtHelper.update(dataFixer, DataFixTypes.STATS, nbtCompound, nbtCompound.getInt("DataVersion"))).contains("stats", 10)) {
                NbtCompound nbtCompound2 = nbtCompound.getCompound("stats");
                for (String string : nbtCompound2.getKeys()) {
                    if (!nbtCompound2.contains(string, 10)) continue;
                    Util.ifPresentOrElse(Registry.STAT_TYPE.getOrEmpty(new Identifier(string)), statType -> {
                        NbtCompound nbtCompound2 = nbtCompound2.getCompound(string);
                        for (String string2 : nbtCompound2.getKeys()) {
                            if (nbtCompound2.contains(string2, 99)) {
                                Util.ifPresentOrElse(this.createStat((StatType)statType, string2), stat -> this.statMap.put(stat, nbtCompound2.getInt(string2)), () -> LOGGER.warn("Invalid statistic in {}: Don't know what {} is", (Object)this.file, (Object)string2));
                                continue;
                            }
                            LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", (Object)this.file, (Object)nbtCompound2.get(string2), (Object)string2);
                        }
                    }, () -> LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", (Object)this.file, (Object)string));
                }
            }
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.file, (Object)exception);
        }
    }

    private <T> Optional<Stat<T>> createStat(StatType<T> type, String id) {
        return Optional.ofNullable(Identifier.tryParse(id)).flatMap(type.getRegistry()::getOrEmpty).map(type::getOrCreateStat);
    }

    private static NbtCompound jsonToCompound(JsonObject json) {
        NbtCompound nbtCompound = new NbtCompound();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonPrimitive jsonPrimitive;
            JsonElement jsonElement = entry.getValue();
            if (jsonElement.isJsonObject()) {
                nbtCompound.put(entry.getKey(), ServerStatHandler.jsonToCompound(jsonElement.getAsJsonObject()));
                continue;
            }
            if (!jsonElement.isJsonPrimitive() || !(jsonPrimitive = jsonElement.getAsJsonPrimitive()).isNumber()) continue;
            nbtCompound.putInt(entry.getKey(), jsonPrimitive.getAsInt());
        }
        return nbtCompound;
    }

    protected String asString() {
        HashMap<StatType, JsonObject> map = Maps.newHashMap();
        for (Object2IntMap.Entry entry : this.statMap.object2IntEntrySet()) {
            Stat stat = (Stat)entry.getKey();
            map.computeIfAbsent(stat.getType(), statType -> new JsonObject()).addProperty(ServerStatHandler.getStatId(stat).toString(), entry.getIntValue());
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry entry : map.entrySet()) {
            jsonObject.add(Registry.STAT_TYPE.getId((StatType<?>)entry.getKey()).toString(), (JsonElement)entry.getValue());
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
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        if (i - this.lastStatsUpdate > 300) {
            this.lastStatsUpdate = i;
            for (Stat<?> stat : this.takePendingStats()) {
                object2IntMap.put(stat, this.getStat(stat));
            }
        }
        player.networkHandler.sendPacket(new StatisticsS2CPacket(object2IntMap));
    }
}

