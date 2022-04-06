package net.minecraft.entity.data;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;

public class TrackedDataHandlerRegistry {
	private static final Int2ObjectBiMap<TrackedDataHandler<?>> DATA_HANDLERS = Int2ObjectBiMap.create(16);
	public static final TrackedDataHandler<Byte> BYTE = TrackedDataHandler.of((packetByteBuf, byte_) -> packetByteBuf.writeByte(byte_), PacketByteBuf::readByte);
	public static final TrackedDataHandler<Integer> INTEGER = TrackedDataHandler.of(PacketByteBuf::writeVarInt, PacketByteBuf::readVarInt);
	public static final TrackedDataHandler<Float> FLOAT = TrackedDataHandler.of(PacketByteBuf::writeFloat, PacketByteBuf::readFloat);
	public static final TrackedDataHandler<String> STRING = TrackedDataHandler.of(PacketByteBuf::writeString, PacketByteBuf::readString);
	public static final TrackedDataHandler<Text> TEXT_COMPONENT = TrackedDataHandler.of(PacketByteBuf::writeText, PacketByteBuf::readText);
	public static final TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = TrackedDataHandler.ofOptional(
		PacketByteBuf::writeText, PacketByteBuf::readText
	);
	public static final TrackedDataHandler<ItemStack> ITEM_STACK = new TrackedDataHandler<ItemStack>() {
		public void write(PacketByteBuf packetByteBuf, ItemStack itemStack) {
			packetByteBuf.writeItemStack(itemStack);
		}

		public ItemStack read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readItemStack();
		}

		public ItemStack copy(ItemStack itemStack) {
			return itemStack.copy();
		}
	};
	public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new TrackedDataHandler.ImmutableHandler<Optional<BlockState>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<BlockState> optional) {
			if (optional.isPresent()) {
				packetByteBuf.writeVarInt(Block.getRawIdFromState((BlockState)optional.get()));
			} else {
				packetByteBuf.writeVarInt(0);
			}
		}

		public Optional<BlockState> read(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			return i == 0 ? Optional.empty() : Optional.of(Block.getStateFromRawId(i));
		}
	};
	public static final TrackedDataHandler<Boolean> BOOLEAN = TrackedDataHandler.of(PacketByteBuf::writeBoolean, PacketByteBuf::readBoolean);
	public static final TrackedDataHandler<ParticleEffect> PARTICLE = new TrackedDataHandler.ImmutableHandler<ParticleEffect>() {
		public void write(PacketByteBuf packetByteBuf, ParticleEffect particleEffect) {
			packetByteBuf.writeRegistryValue(Registry.PARTICLE_TYPE, particleEffect.getType());
			particleEffect.write(packetByteBuf);
		}

		public ParticleEffect read(PacketByteBuf packetByteBuf) {
			return this.read(packetByteBuf, packetByteBuf.readRegistryValue(Registry.PARTICLE_TYPE));
		}

		private <T extends ParticleEffect> T read(PacketByteBuf buf, ParticleType<T> type) {
			return type.getParametersFactory().read(type, buf);
		}
	};
	public static final TrackedDataHandler<EulerAngle> ROTATION = new TrackedDataHandler.ImmutableHandler<EulerAngle>() {
		public void write(PacketByteBuf packetByteBuf, EulerAngle eulerAngle) {
			packetByteBuf.writeFloat(eulerAngle.getPitch());
			packetByteBuf.writeFloat(eulerAngle.getYaw());
			packetByteBuf.writeFloat(eulerAngle.getRoll());
		}

		public EulerAngle read(PacketByteBuf packetByteBuf) {
			return new EulerAngle(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}
	};
	public static final TrackedDataHandler<BlockPos> BLOCK_POS = TrackedDataHandler.of(PacketByteBuf::writeBlockPos, PacketByteBuf::readBlockPos);
	public static final TrackedDataHandler<Optional<BlockPos>> OPTIONAL_BLOCK_POS = TrackedDataHandler.ofOptional(
		PacketByteBuf::writeBlockPos, PacketByteBuf::readBlockPos
	);
	public static final TrackedDataHandler<Direction> FACING = TrackedDataHandler.ofEnum(Direction.class);
	public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID = TrackedDataHandler.ofOptional(PacketByteBuf::writeUuid, PacketByteBuf::readUuid);
	public static final TrackedDataHandler<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = TrackedDataHandler.ofOptional(
		(packetByteBuf, globalPos) -> packetByteBuf.encode(GlobalPos.CODEC, globalPos), packetByteBuf -> packetByteBuf.decode(GlobalPos.CODEC)
	);
	public static final TrackedDataHandler<NbtCompound> NBT_COMPOUND = new TrackedDataHandler<NbtCompound>() {
		public void write(PacketByteBuf packetByteBuf, NbtCompound nbtCompound) {
			packetByteBuf.writeNbt(nbtCompound);
		}

		public NbtCompound read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readNbt();
		}

		public NbtCompound copy(NbtCompound nbtCompound) {
			return nbtCompound.copy();
		}
	};
	public static final TrackedDataHandler<VillagerData> VILLAGER_DATA = new TrackedDataHandler.ImmutableHandler<VillagerData>() {
		public void write(PacketByteBuf packetByteBuf, VillagerData villagerData) {
			packetByteBuf.writeRegistryValue(Registry.VILLAGER_TYPE, villagerData.getType());
			packetByteBuf.writeRegistryValue(Registry.VILLAGER_PROFESSION, villagerData.getProfession());
			packetByteBuf.writeVarInt(villagerData.getLevel());
		}

		public VillagerData read(PacketByteBuf packetByteBuf) {
			return new VillagerData(
				packetByteBuf.readRegistryValue(Registry.VILLAGER_TYPE), packetByteBuf.readRegistryValue(Registry.VILLAGER_PROFESSION), packetByteBuf.readVarInt()
			);
		}
	};
	public static final TrackedDataHandler<OptionalInt> OPTIONAL_INT = new TrackedDataHandler.ImmutableHandler<OptionalInt>() {
		public void write(PacketByteBuf packetByteBuf, OptionalInt optionalInt) {
			packetByteBuf.writeVarInt(optionalInt.orElse(-1) + 1);
		}

		public OptionalInt read(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
		}
	};
	public static final TrackedDataHandler<EntityPose> ENTITY_POSE = TrackedDataHandler.ofEnum(EntityPose.class);
	public static final TrackedDataHandler<CatVariant> CAT_VARIANT = TrackedDataHandler.of(Registry.CAT_VARIANT);
	public static final TrackedDataHandler<FrogVariant> FROG_VARIANT = TrackedDataHandler.of(Registry.FROG_VARIANT);

	public static void register(TrackedDataHandler<?> handler) {
		DATA_HANDLERS.add(handler);
	}

	@Nullable
	public static TrackedDataHandler<?> get(int id) {
		return DATA_HANDLERS.get(id);
	}

	public static int getId(TrackedDataHandler<?> handler) {
		return DATA_HANDLERS.getRawId(handler);
	}

	private TrackedDataHandlerRegistry() {
	}

	static {
		register(BYTE);
		register(INTEGER);
		register(FLOAT);
		register(STRING);
		register(TEXT_COMPONENT);
		register(OPTIONAL_TEXT_COMPONENT);
		register(ITEM_STACK);
		register(BOOLEAN);
		register(ROTATION);
		register(BLOCK_POS);
		register(OPTIONAL_BLOCK_POS);
		register(FACING);
		register(OPTIONAL_UUID);
		register(OPTIONAL_BLOCK_STATE);
		register(NBT_COMPOUND);
		register(PARTICLE);
		register(VILLAGER_DATA);
		register(OPTIONAL_INT);
		register(ENTITY_POSE);
		register(CAT_VARIANT);
		register(FROG_VARIANT);
		register(OPTIONAL_GLOBAL_POS);
	}
}
