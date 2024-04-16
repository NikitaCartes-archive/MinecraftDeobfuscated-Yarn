package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum AttributeModifierSlot implements StringIdentifiable {
	ANY(0, "any", slot -> true),
	MAINHAND(1, "mainhand", EquipmentSlot.MAINHAND),
	OFFHAND(2, "offhand", EquipmentSlot.OFFHAND),
	HAND(3, "hand", slot -> slot.getType() == EquipmentSlot.Type.HAND),
	FEET(4, "feet", EquipmentSlot.FEET),
	LEGS(5, "legs", EquipmentSlot.LEGS),
	CHEST(6, "chest", EquipmentSlot.CHEST),
	HEAD(7, "head", EquipmentSlot.HEAD),
	ARMOR(8, "armor", EquipmentSlot::isArmorSlot),
	BODY(9, "body", EquipmentSlot.BODY);

	public static final IntFunction<AttributeModifierSlot> ID_TO_VALUE = ValueLists.createIdToValueFunction(
		id -> id.id, values(), ValueLists.OutOfBoundsHandling.ZERO
	);
	public static final Codec<AttributeModifierSlot> CODEC = StringIdentifiable.createCodec(AttributeModifierSlot::values);
	public static final PacketCodec<ByteBuf, AttributeModifierSlot> PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, id -> id.id);
	private final int id;
	private final String name;
	private final Predicate<EquipmentSlot> slotPredicate;

	private AttributeModifierSlot(final int id, final String name, final Predicate<EquipmentSlot> slotPredicate) {
		this.id = id;
		this.name = name;
		this.slotPredicate = slotPredicate;
	}

	private AttributeModifierSlot(final int id, final String name, final EquipmentSlot slot) {
		this(id, name, slotx -> slotx == slot);
	}

	public static AttributeModifierSlot forEquipmentSlot(EquipmentSlot slot) {
		return switch (slot) {
			case MAINHAND -> MAINHAND;
			case OFFHAND -> OFFHAND;
			case FEET -> FEET;
			case LEGS -> LEGS;
			case CHEST -> CHEST;
			case HEAD -> HEAD;
			case BODY -> BODY;
		};
	}

	@Override
	public String asString() {
		return this.name;
	}

	public boolean matches(EquipmentSlot slot) {
		return this.slotPredicate.test(slot);
	}
}
