package net.minecraft.entity.data;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TrackedDataHandlerRegistry {
	private static final Int2ObjectBiMap<TrackedDataHandler<?>> DATA_HANDLERS = Int2ObjectBiMap.create(16);
	public static final TrackedDataHandler<Byte> BYTE = TrackedDataHandler.of((buf, byte_) -> buf.writeByte(byte_), PacketByteBuf::readByte);
	public static final TrackedDataHandler<Integer> INTEGER = TrackedDataHandler.of(PacketByteBuf::writeVarInt, PacketByteBuf::readVarInt);
	public static final TrackedDataHandler<Long> LONG = TrackedDataHandler.of(PacketByteBuf::writeVarLong, PacketByteBuf::readVarLong);
	public static final TrackedDataHandler<Float> FLOAT = TrackedDataHandler.of(PacketByteBuf::writeFloat, PacketByteBuf::readFloat);
	public static final TrackedDataHandler<String> STRING = TrackedDataHandler.of(PacketByteBuf::writeString, PacketByteBuf::readString);
	public static final TrackedDataHandler<Text> TEXT_COMPONENT = TrackedDataHandler.of(PacketByteBuf::writeText, PacketByteBuf::readUnlimitedText);
	public static final TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = TrackedDataHandler.ofOptional(
		PacketByteBuf::writeText, PacketByteBuf::readUnlimitedText
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
	public static final TrackedDataHandler<BlockState> BLOCK_STATE = TrackedDataHandler.of(Block.STATE_IDS);
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
			packetByteBuf.writeRegistryValue(Registries.PARTICLE_TYPE, particleEffect.getType());
			particleEffect.write(packetByteBuf);
		}

		public ParticleEffect read(PacketByteBuf packetByteBuf) {
			return this.read(packetByteBuf, packetByteBuf.readRegistryValue(Registries.PARTICLE_TYPE));
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
		PacketByteBuf::writeGlobalPos, PacketByteBuf::readGlobalPos
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
			packetByteBuf.writeRegistryValue(Registries.VILLAGER_TYPE, villagerData.getType());
			packetByteBuf.writeRegistryValue(Registries.VILLAGER_PROFESSION, villagerData.getProfession());
			packetByteBuf.writeVarInt(villagerData.getLevel());
		}

		public VillagerData read(PacketByteBuf packetByteBuf) {
			return new VillagerData(
				packetByteBuf.readRegistryValue(Registries.VILLAGER_TYPE), packetByteBuf.readRegistryValue(Registries.VILLAGER_PROFESSION), packetByteBuf.readVarInt()
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
	public static final TrackedDataHandler<CatVariant> CAT_VARIANT = TrackedDataHandler.of(Registries.CAT_VARIANT);
	public static final TrackedDataHandler<FrogVariant> FROG_VARIANT = TrackedDataHandler.of(Registries.FROG_VARIANT);
	public static final TrackedDataHandler<RegistryEntry<PaintingVariant>> PAINTING_VARIANT = TrackedDataHandler.of(
		Registries.PAINTING_VARIANT.getIndexedEntries()
	);
	public static final TrackedDataHandler<SnifferEntity.State> SNIFFER_STATE = TrackedDataHandler.ofEnum(SnifferEntity.State.class);
	public static final TrackedDataHandler<ArmadilloEntity.State> ARMADILLO_STATE = TrackedDataHandler.ofEnum(ArmadilloEntity.State.class);
	public static final TrackedDataHandler<Vector3f> VECTOR3F = TrackedDataHandler.of(PacketByteBuf::writeVector3f, PacketByteBuf::readVector3f);
	public static final TrackedDataHandler<Quaternionf> QUATERNIONF = TrackedDataHandler.of(PacketByteBuf::writeQuaternionf, PacketByteBuf::readQuaternionf);

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
		register(LONG);
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
		register(BLOCK_STATE);
		register(OPTIONAL_BLOCK_STATE);
		register(NBT_COMPOUND);
		register(PARTICLE);
		register(VILLAGER_DATA);
		register(OPTIONAL_INT);
		register(ENTITY_POSE);
		register(CAT_VARIANT);
		register(FROG_VARIANT);
		register(OPTIONAL_GLOBAL_POS);
		register(PAINTING_VARIANT);
		register(SNIFFER_STATE);
		register(ARMADILLO_STATE);
		register(VECTOR3F);
		register(QUATERNIONF);
	}
}
