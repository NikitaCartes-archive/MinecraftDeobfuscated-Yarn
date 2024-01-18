package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class StatType<T> implements Iterable<Stat<T>> {
	private final Registry<T> registry;
	private final Map<T, Stat<T>> stats = new IdentityHashMap();
	private final Text name;
	private final PacketCodec<RegistryByteBuf, Stat<T>> packetCodec;

	public StatType(Registry<T> registry, Text name) {
		this.registry = registry;
		this.name = name;
		this.packetCodec = PacketCodecs.registryValue(registry.getKey()).xmap(this::getOrCreateStat, Stat::getValue);
	}

	public PacketCodec<RegistryByteBuf, Stat<T>> getPacketCodec() {
		return this.packetCodec;
	}

	public boolean hasStat(T key) {
		return this.stats.containsKey(key);
	}

	public Stat<T> getOrCreateStat(T key, StatFormatter formatter) {
		return (Stat<T>)this.stats.computeIfAbsent(key, value -> new Stat<>(this, (T)value, formatter));
	}

	public Registry<T> getRegistry() {
		return this.registry;
	}

	public Iterator<Stat<T>> iterator() {
		return this.stats.values().iterator();
	}

	public Stat<T> getOrCreateStat(T key) {
		return this.getOrCreateStat(key, StatFormatter.DEFAULT);
	}

	public Text getName() {
		return this.name;
	}
}
