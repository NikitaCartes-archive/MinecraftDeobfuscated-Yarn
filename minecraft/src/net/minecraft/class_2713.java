package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_2713 implements Packet<ClientPlayPacketListener> {
	private class_2713.class_2714 field_12408;
	private List<Identifier> field_12414;
	private List<Identifier> field_12409;
	private boolean guiOpen;
	private boolean filteringCraftable;
	private boolean furnaceGuiOpen;
	private boolean furnaceFilteringCraftable;

	public class_2713() {
	}

	public class_2713(
		class_2713.class_2714 arg, Collection<Identifier> collection, Collection<Identifier> collection2, boolean bl, boolean bl2, boolean bl3, boolean bl4
	) {
		this.field_12408 = arg;
		this.field_12414 = ImmutableList.copyOf(collection);
		this.field_12409 = ImmutableList.copyOf(collection2);
		this.guiOpen = bl;
		this.filteringCraftable = bl2;
		this.furnaceGuiOpen = bl3;
		this.furnaceFilteringCraftable = bl4;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnlockRecipes(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12408 = packetByteBuf.readEnumConstant(class_2713.class_2714.class);
		this.guiOpen = packetByteBuf.readBoolean();
		this.filteringCraftable = packetByteBuf.readBoolean();
		this.furnaceGuiOpen = packetByteBuf.readBoolean();
		this.furnaceFilteringCraftable = packetByteBuf.readBoolean();
		int i = packetByteBuf.readVarInt();
		this.field_12414 = Lists.<Identifier>newArrayList();

		for (int j = 0; j < i; j++) {
			this.field_12414.add(packetByteBuf.readIdentifier());
		}

		if (this.field_12408 == class_2713.class_2714.field_12416) {
			i = packetByteBuf.readVarInt();
			this.field_12409 = Lists.<Identifier>newArrayList();

			for (int j = 0; j < i; j++) {
				this.field_12409.add(packetByteBuf.readIdentifier());
			}
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_12408);
		packetByteBuf.writeBoolean(this.guiOpen);
		packetByteBuf.writeBoolean(this.filteringCraftable);
		packetByteBuf.writeBoolean(this.furnaceGuiOpen);
		packetByteBuf.writeBoolean(this.furnaceFilteringCraftable);
		packetByteBuf.writeVarInt(this.field_12414.size());

		for (Identifier identifier : this.field_12414) {
			packetByteBuf.writeIdentifier(identifier);
		}

		if (this.field_12408 == class_2713.class_2714.field_12416) {
			packetByteBuf.writeVarInt(this.field_12409.size());

			for (Identifier identifier : this.field_12409) {
				packetByteBuf.writeIdentifier(identifier);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public List<Identifier> method_11750() {
		return this.field_12414;
	}

	@Environment(EnvType.CLIENT)
	public List<Identifier> method_11757() {
		return this.field_12409;
	}

	@Environment(EnvType.CLIENT)
	public boolean isGuiOpen() {
		return this.guiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFilteringCraftable() {
		return this.filteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceGuiOpen() {
		return this.furnaceGuiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceFilteringCraftable() {
		return this.furnaceFilteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public class_2713.class_2714 method_11751() {
		return this.field_12408;
	}

	public static enum class_2714 {
		field_12416,
		field_12415,
		field_12417;
	}
}
