package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;

public record Sherds(Optional<Item> back, Optional<Item> left, Optional<Item> right, Optional<Item> front) {
	public static final Sherds DEFAULT = new Sherds(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	public static final Codec<Sherds> CODEC = Registries.ITEM.getCodec().sizeLimitedListOf(4).xmap(Sherds::new, Sherds::stream);
	public static final PacketCodec<RegistryByteBuf, Sherds> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.ITEM)
		.collect(PacketCodecs.toList(4))
		.xmap(Sherds::new, Sherds::stream);

	private Sherds(List<Item> sherds) {
		this(getSherd(sherds, 0), getSherd(sherds, 1), getSherd(sherds, 2), getSherd(sherds, 3));
	}

	public Sherds(Item back, Item left, Item right, Item front) {
		this(List.of(back, left, right, front));
	}

	private static Optional<Item> getSherd(List<Item> sherds, int index) {
		if (index >= sherds.size()) {
			return Optional.empty();
		} else {
			Item item = (Item)sherds.get(index);
			return item == Items.BRICK ? Optional.empty() : Optional.of(item);
		}
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		if (this.equals(DEFAULT)) {
			return nbt;
		} else {
			nbt.put("sherds", CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
			return nbt;
		}
	}

	public List<Item> stream() {
		return Stream.of(this.back, this.left, this.right, this.front).map(item -> (Item)item.orElse(Items.BRICK)).toList();
	}

	public static Sherds fromNbt(@Nullable NbtCompound nbt) {
		return nbt != null && nbt.contains("sherds") ? (Sherds)CODEC.parse(NbtOps.INSTANCE, nbt.get("sherds")).result().orElse(DEFAULT) : DEFAULT;
	}
}
