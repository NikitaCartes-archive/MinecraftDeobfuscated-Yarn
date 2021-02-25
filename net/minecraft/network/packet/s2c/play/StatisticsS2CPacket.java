/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.registry.Registry;

public class StatisticsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Object2IntMap<Stat<?>> stats;

    public StatisticsS2CPacket(Object2IntMap<Stat<?>> stats) {
        this.stats = stats;
    }

    public StatisticsS2CPacket(PacketByteBuf buf2) {
        this.stats = buf2.readMap(Object2IntOpenHashMap::new, buf -> {
            int i = buf.readVarInt();
            int j = buf.readVarInt();
            return StatisticsS2CPacket.getStat((StatType)Registry.STAT_TYPE.get(i), j);
        }, PacketByteBuf::readVarInt);
    }

    private static <T> Stat<T> getStat(StatType<T> statType, int id) {
        return statType.getOrCreateStat(statType.getRegistry().get(id));
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onStatistics(this);
    }

    @Override
    public void write(PacketByteBuf buf2) {
        buf2.writeMap(this.stats, (buf, stat) -> {
            buf.writeVarInt(Registry.STAT_TYPE.getRawId(stat.getType()));
            buf.writeVarInt(this.getStatNetworkId((Stat)stat));
        }, PacketByteBuf::writeVarInt);
    }

    private <T> int getStatNetworkId(Stat<T> stat) {
        return stat.getType().getRegistry().getRawId(stat.getValue());
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Stat<?>, Integer> getStatMap() {
        return this.stats;
    }
}

