package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EntityAttributesS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, EntityAttributesS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		EntityAttributesS2CPacket::getEntityId,
		EntityAttributesS2CPacket.Entry.CODEC.collect(PacketCodecs.toList()),
		EntityAttributesS2CPacket::getEntries,
		EntityAttributesS2CPacket::new
	);
	private final int entityId;
	private final List<EntityAttributesS2CPacket.Entry> entries;

	public EntityAttributesS2CPacket(int entityId, Collection<EntityAttributeInstance> attributes) {
		this.entityId = entityId;
		this.entries = Lists.<EntityAttributesS2CPacket.Entry>newArrayList();

		for (EntityAttributeInstance entityAttributeInstance : attributes) {
			this.entries
				.add(
					new EntityAttributesS2CPacket.Entry(entityAttributeInstance.getAttribute(), entityAttributeInstance.getBaseValue(), entityAttributeInstance.getModifiers())
				);
		}
	}

	private EntityAttributesS2CPacket(int entityId, List<EntityAttributesS2CPacket.Entry> attributes) {
		this.entityId = entityId;
		this.entries = attributes;
	}

	@Override
	public PacketType<EntityAttributesS2CPacket> getPacketId() {
		return PlayPackets.UPDATE_ATTRIBUTES;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttributes(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public List<EntityAttributesS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	public static record Entry(RegistryEntry<EntityAttribute> attribute, double base, Collection<EntityAttributeModifier> modifiers) {
		public static final PacketCodec<ByteBuf, EntityAttributeModifier> MODIFIER_CODEC = PacketCodec.tuple(
			Identifier.PACKET_CODEC,
			EntityAttributeModifier::id,
			PacketCodecs.DOUBLE,
			EntityAttributeModifier::value,
			EntityAttributeModifier.Operation.PACKET_CODEC,
			EntityAttributeModifier::operation,
			EntityAttributeModifier::new
		);
		public static final PacketCodec<RegistryByteBuf, EntityAttributesS2CPacket.Entry> CODEC = PacketCodec.tuple(
			EntityAttribute.PACKET_CODEC,
			EntityAttributesS2CPacket.Entry::attribute,
			PacketCodecs.DOUBLE,
			EntityAttributesS2CPacket.Entry::base,
			MODIFIER_CODEC.collect(PacketCodecs.toCollection(ArrayList::new)),
			EntityAttributesS2CPacket.Entry::modifiers,
			EntityAttributesS2CPacket.Entry::new
		);
	}
}
