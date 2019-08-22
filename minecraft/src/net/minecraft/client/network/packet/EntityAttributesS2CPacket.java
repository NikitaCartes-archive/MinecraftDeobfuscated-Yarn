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
	private int entityId;
	private final List<EntityAttributesS2CPacket.Entry> entries = Lists.<EntityAttributesS2CPacket.Entry>newArrayList();

	public EntityAttributesS2CPacket() {
	}

	public EntityAttributesS2CPacket(int i, Collection<EntityAttributeInstance> collection) {
		this.entityId = i;

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
		this.entityId = packetByteBuf.readVarInt();
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
						uUID, "Unknown synced attribute modifier", packetByteBuf.readDouble(), EntityAttributeModifier.Operation.fromId(packetByteBuf.readByte())
					)
				);
			}

			this.entries.add(new EntityAttributesS2CPacket.Entry(string, d, list));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeInt(this.entries.size());

		for (EntityAttributesS2CPacket.Entry entry : this.entries) {
			packetByteBuf.writeString(entry.getId());
			packetByteBuf.writeDouble(entry.getBaseValue());
			packetByteBuf.writeVarInt(entry.getModifiers().size());

			for (EntityAttributeModifier entityAttributeModifier : entry.getModifiers()) {
				packetByteBuf.writeUuid(entityAttributeModifier.getId());
				packetByteBuf.writeDouble(entityAttributeModifier.getAmount());
				packetByteBuf.writeByte(entityAttributeModifier.getOperation().getId());
			}
		}
	}

	public void method_11936(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttributes(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	public List<EntityAttributesS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	public class Entry {
		private final String id;
		private final double baseValue;
		private final Collection<EntityAttributeModifier> modifiers;

		public Entry(String string, double d, Collection<EntityAttributeModifier> collection) {
			this.id = string;
			this.baseValue = d;
			this.modifiers = collection;
		}

		public String getId() {
			return this.id;
		}

		public double getBaseValue() {
			return this.baseValue;
		}

		public Collection<EntityAttributeModifier> getModifiers() {
			return this.modifiers;
		}
	}
}
