package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class class_2765 implements Packet<ClientPlayPacketListener> {
	private SoundEvent field_12642;
	private SoundCategory field_12641;
	private int field_12640;
	private float field_12639;
	private float field_12638;

	public class_2765() {
	}

	public class_2765(SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float f, float g) {
		Validate.notNull(soundEvent, "sound");
		this.field_12642 = soundEvent;
		this.field_12641 = soundCategory;
		this.field_12640 = entity.getEntityId();
		this.field_12639 = f;
		this.field_12638 = g;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12642 = Registry.SOUND_EVENT.getInt(packetByteBuf.readVarInt());
		this.field_12641 = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.field_12640 = packetByteBuf.readVarInt();
		this.field_12639 = packetByteBuf.readFloat();
		this.field_12638 = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.field_12642));
		packetByteBuf.writeEnumConstant(this.field_12641);
		packetByteBuf.writeVarInt(this.field_12640);
		packetByteBuf.writeFloat(this.field_12639);
		packetByteBuf.writeFloat(this.field_12638);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent method_11882() {
		return this.field_12642;
	}

	@Environment(EnvType.CLIENT)
	public SoundCategory method_11881() {
		return this.field_12641;
	}

	@Environment(EnvType.CLIENT)
	public int method_11883() {
		return this.field_12640;
	}

	@Environment(EnvType.CLIENT)
	public float method_11885() {
		return this.field_12639;
	}

	@Environment(EnvType.CLIENT)
	public float method_11880() {
		return this.field_12638;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11125(this);
	}
}
