package net.minecraft.entity.data;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;

public class TrackedDataHandlerRegistry {
	private static final Int2ObjectBiMap<TrackedDataHandler<?>> field_13328 = new Int2ObjectBiMap<>(16);
	public static final TrackedDataHandler<Byte> BYTE = new TrackedDataHandler<Byte>() {
		public void write(PacketByteBuf packetByteBuf, Byte byte_) {
			packetByteBuf.writeByte(byte_);
		}

		public Byte read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readByte();
		}

		public Byte copy(Byte byte_) {
			return byte_;
		}
	};
	public static final TrackedDataHandler<Integer> INTEGER = new TrackedDataHandler<Integer>() {
		public void write(PacketByteBuf packetByteBuf, Integer integer) {
			packetByteBuf.writeVarInt(integer);
		}

		public Integer read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readVarInt();
		}

		public Integer copy(Integer integer) {
			return integer;
		}
	};
	public static final TrackedDataHandler<Float> FLOAT = new TrackedDataHandler<Float>() {
		public void write(PacketByteBuf packetByteBuf, Float float_) {
			packetByteBuf.writeFloat(float_);
		}

		public Float read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readFloat();
		}

		public Float copy(Float float_) {
			return float_;
		}
	};
	public static final TrackedDataHandler<String> STRING = new TrackedDataHandler<String>() {
		public void write(PacketByteBuf packetByteBuf, String string) {
			packetByteBuf.writeString(string);
		}

		public String read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readString(32767);
		}

		public String copy(String string) {
			return string;
		}
	};
	public static final TrackedDataHandler<Text> TEXT_COMPONENT = new TrackedDataHandler<Text>() {
		public void write(PacketByteBuf packetByteBuf, Text text) {
			packetByteBuf.writeText(text);
		}

		public Text read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readText();
		}

		public Text copy(Text text) {
			return text;
		}
	};
	public static final TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = new TrackedDataHandler<Optional<Text>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<Text> optional) {
			if (optional.isPresent()) {
				packetByteBuf.writeBoolean(true);
				packetByteBuf.writeText((Text)optional.get());
			} else {
				packetByteBuf.writeBoolean(false);
			}
		}

		public Optional<Text> read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBoolean() ? Optional.of(packetByteBuf.readText()) : Optional.empty();
		}

		public Optional<Text> copy(Optional<Text> optional) {
			return optional;
		}
	};
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
	public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new TrackedDataHandler<Optional<BlockState>>() {
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

		public Optional<BlockState> copy(Optional<BlockState> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<Boolean> BOOLEAN = new TrackedDataHandler<Boolean>() {
		public void write(PacketByteBuf packetByteBuf, Boolean boolean_) {
			packetByteBuf.writeBoolean(boolean_);
		}

		public Boolean read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBoolean();
		}

		public Boolean copy(Boolean boolean_) {
			return boolean_;
		}
	};
	public static final TrackedDataHandler<ParticleEffect> PARTICLE = new TrackedDataHandler<ParticleEffect>() {
		public void write(PacketByteBuf packetByteBuf, ParticleEffect particleEffect) {
			packetByteBuf.writeVarInt(Registry.PARTICLE_TYPE.getRawId(particleEffect.getType()));
			particleEffect.write(packetByteBuf);
		}

		public ParticleEffect read(PacketByteBuf packetByteBuf) {
			return this.method_12744(packetByteBuf, (ParticleType<ParticleEffect>)Registry.PARTICLE_TYPE.get(packetByteBuf.readVarInt()));
		}

		private <T extends ParticleEffect> T method_12744(PacketByteBuf packetByteBuf, ParticleType<T> particleType) {
			return particleType.getParametersFactory().read(particleType, packetByteBuf);
		}

		public ParticleEffect copy(ParticleEffect particleEffect) {
			return particleEffect;
		}
	};
	public static final TrackedDataHandler<EulerAngle> ROTATION = new TrackedDataHandler<EulerAngle>() {
		public void write(PacketByteBuf packetByteBuf, EulerAngle eulerAngle) {
			packetByteBuf.writeFloat(eulerAngle.getPitch());
			packetByteBuf.writeFloat(eulerAngle.getYaw());
			packetByteBuf.writeFloat(eulerAngle.getRoll());
		}

		public EulerAngle read(PacketByteBuf packetByteBuf) {
			return new EulerAngle(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}

		public EulerAngle copy(EulerAngle eulerAngle) {
			return eulerAngle;
		}
	};
	public static final TrackedDataHandler<BlockPos> BLOCK_POS = new TrackedDataHandler<BlockPos>() {
		public void write(PacketByteBuf packetByteBuf, BlockPos blockPos) {
			packetByteBuf.writeBlockPos(blockPos);
		}

		public BlockPos read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBlockPos();
		}

		public BlockPos copy(BlockPos blockPos) {
			return blockPos;
		}
	};
	public static final TrackedDataHandler<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new TrackedDataHandler<Optional<BlockPos>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<BlockPos> optional) {
			packetByteBuf.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				packetByteBuf.writeBlockPos((BlockPos)optional.get());
			}
		}

		public Optional<BlockPos> read(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.empty() : Optional.of(packetByteBuf.readBlockPos());
		}

		public Optional<BlockPos> copy(Optional<BlockPos> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<Direction> FACING = new TrackedDataHandler<Direction>() {
		public void write(PacketByteBuf packetByteBuf, Direction direction) {
			packetByteBuf.writeEnumConstant(direction);
		}

		public Direction read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readEnumConstant(Direction.class);
		}

		public Direction copy(Direction direction) {
			return direction;
		}
	};
	public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID = new TrackedDataHandler<Optional<UUID>>() {
		public void write(PacketByteBuf packetByteBuf, Optional<UUID> optional) {
			packetByteBuf.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				packetByteBuf.writeUuid((UUID)optional.get());
			}
		}

		public Optional<UUID> read(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.empty() : Optional.of(packetByteBuf.readUuid());
		}

		public Optional<UUID> copy(Optional<UUID> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<CompoundTag> TAG_COMPOUND = new TrackedDataHandler<CompoundTag>() {
		public void write(PacketByteBuf packetByteBuf, CompoundTag compoundTag) {
			packetByteBuf.writeCompoundTag(compoundTag);
		}

		public CompoundTag read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readCompoundTag();
		}

		public CompoundTag copy(CompoundTag compoundTag) {
			return compoundTag.copy();
		}
	};
	public static final TrackedDataHandler<VillagerData> VILLAGER_DATA = new TrackedDataHandler<VillagerData>() {
		public void write(PacketByteBuf packetByteBuf, VillagerData villagerData) {
			packetByteBuf.writeVarInt(Registry.VILLAGER_TYPE.getRawId(villagerData.getType()));
			packetByteBuf.writeVarInt(Registry.VILLAGER_PROFESSION.getRawId(villagerData.getProfession()));
			packetByteBuf.writeVarInt(villagerData.getLevel());
		}

		public VillagerData read(PacketByteBuf packetByteBuf) {
			return new VillagerData(
				Registry.VILLAGER_TYPE.get(packetByteBuf.readVarInt()), Registry.VILLAGER_PROFESSION.get(packetByteBuf.readVarInt()), packetByteBuf.readVarInt()
			);
		}

		public VillagerData copy(VillagerData villagerData) {
			return villagerData;
		}
	};
	public static final TrackedDataHandler<OptionalInt> FIREWORK_DATA = new TrackedDataHandler<OptionalInt>() {
		public void write(PacketByteBuf packetByteBuf, OptionalInt optionalInt) {
			packetByteBuf.writeVarInt(optionalInt.orElse(-1) + 1);
		}

		public OptionalInt read(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
		}

		public OptionalInt copy(OptionalInt optionalInt) {
			return optionalInt;
		}
	};
	public static final TrackedDataHandler<EntityPose> ENTITY_POSE = new TrackedDataHandler<EntityPose>() {
		public void write(PacketByteBuf packetByteBuf, EntityPose entityPose) {
			packetByteBuf.writeEnumConstant(entityPose);
		}

		public EntityPose read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readEnumConstant(EntityPose.class);
		}

		public EntityPose copy(EntityPose entityPose) {
			return entityPose;
		}
	};

	public static void register(TrackedDataHandler<?> handler) {
		field_13328.add(handler);
	}

	@Nullable
	public static TrackedDataHandler<?> get(int id) {
		return field_13328.get(id);
	}

	public static int getId(TrackedDataHandler<?> handler) {
		return field_13328.getRawId(handler);
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
		register(TAG_COMPOUND);
		register(PARTICLE);
		register(VILLAGER_DATA);
		register(FIREWORK_DATA);
		register(ENTITY_POSE);
	}
}
