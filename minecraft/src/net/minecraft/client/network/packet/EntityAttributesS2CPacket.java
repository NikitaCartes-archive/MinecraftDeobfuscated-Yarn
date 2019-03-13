package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAttributesS2CPacket implements Packet<ClientPlayPacketListener> {
	private int field_12719;
	private final List<EntityAttributesS2CPacket.Entry> entries = Lists.<EntityAttributesS2CPacket.Entry>newArrayList();

	public EntityAttributesS2CPacket() {
	}

	public EntityAttributesS2CPacket(int i, Collection<EntityAttributeInstance> collection) {
		this.field_12719 = i;

		for (EntityAttributeInstance entityAttributeInstance : collection) {
			this.entries
				.add(
					new EntityAttributesS2CPacket.Entry(
						entityAttributeInstance.getAttribute().getId(), entityAttributeInstance.getBaseValue(), entityAttributeInstance.getModifiers()
					)
				);
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12719 = packetByteBuf.readVarInt();
		int i = packetByteBuf.readInt();

		for (int j = 0; j < i; j++) {
			String string = packetByteBuf.readString(64);
			double d = packetByteBuf.readDouble();
			List<EntityAttributeModifier> list = Lists.<EntityAttributeModifier>newArrayList();
			int k = packetByteBuf.readVarInt();

			for (int l = 0; l < k; l++) {
				UUID uUID = packetByteBuf.readUuid();
				list.add(
					new EntityAttributeModifier(
						uUID, "Unknown synced attribute modifier", packetByteBuf.readDouble(), EntityAttributeModifier.Operation.method_6190(packetByteBuf.readByte())
					)
				);
			}

			this.entries.add(new EntityAttributesS2CPacket.Entry(string, d, list));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12719);
		packetByteBuf.writeInt(this.entries.size());

		for (EntityAttributesS2CPacket.Entry entry : this.entries) {
			packetByteBuf.writeString(entry.method_11940());
			packetByteBuf.writeDouble(entry.method_11941());
			packetByteBuf.writeVarInt(entry.method_11939().size());

			for (EntityAttributeModifier entityAttributeModifier : entry.method_11939()) {
				packetByteBuf.writeUuid(entityAttributeModifier.getId());
				packetByteBuf.writeDouble(entityAttributeModifier.getAmount());
				packetByteBuf.writeByte(entityAttributeModifier.getOperation().getId());
			}
		}
	}

	public void method_11936(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11149(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11937() {
		return this.field_12719;
	}

	@Environment(EnvType.CLIENT)
	public List<EntityAttributesS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	public class Entry {
		private final String field_12724;
		private final double field_12722;
		private final Collection<EntityAttributeModifier> field_12723;

		public Entry(String string, double d, Collection<EntityAttributeModifier> collection) {
			this.field_12724 = string;
			this.field_12722 = d;
			this.field_12723 = collection;
		}

		public String method_11940() {
			return this.field_12724;
		}

		public double method_11941() {
			return this.field_12722;
		}

		public Collection<EntityAttributeModifier> method_11939() {
			return this.field_12723;
		}
	}
}
