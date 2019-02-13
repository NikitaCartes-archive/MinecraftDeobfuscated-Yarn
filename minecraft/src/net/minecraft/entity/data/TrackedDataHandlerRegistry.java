package net.minecraft.entity.data;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Rotation;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;

public class TrackedDataHandlerRegistry {
	private static final Int2ObjectBiMap<TrackedDataHandler<?>> field_13328 = new Int2ObjectBiMap<>(16);
	public static final TrackedDataHandler<Byte> BYTE = new TrackedDataHandler<Byte>() {
		public void method_12741(PacketByteBuf packetByteBuf, Byte byte_) {
			packetByteBuf.writeByte(byte_);
		}

		public Byte read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readByte();
		}

		@Override
		public TrackedData<Byte> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Byte method_12742(Byte byte_) {
			return byte_;
		}
	};
	public static final TrackedDataHandler<Integer> INTEGER = new TrackedDataHandler<Integer>() {
		public void method_12766(PacketByteBuf packetByteBuf, Integer integer) {
			packetByteBuf.writeVarInt(integer);
		}

		public Integer read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readVarInt();
		}

		@Override
		public TrackedData<Integer> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Integer method_12767(Integer integer) {
			return integer;
		}
	};
	public static final TrackedDataHandler<Float> FLOAT = new TrackedDataHandler<Float>() {
		public void method_12769(PacketByteBuf packetByteBuf, Float float_) {
			packetByteBuf.writeFloat(float_);
		}

		public Float read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readFloat();
		}

		@Override
		public TrackedData<Float> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Float method_12770(Float float_) {
			return float_;
		}
	};
	public static final TrackedDataHandler<String> STRING = new TrackedDataHandler<String>() {
		public void method_12723(PacketByteBuf packetByteBuf, String string) {
			packetByteBuf.writeString(string);
		}

		public String read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readString(32767);
		}

		@Override
		public TrackedData<String> create(int i) {
			return new TrackedData<>(i, this);
		}

		public String method_12724(String string) {
			return string;
		}
	};
	public static final TrackedDataHandler<TextComponent> TEXT_COMPONENT = new TrackedDataHandler<TextComponent>() {
		public void method_12727(PacketByteBuf packetByteBuf, TextComponent textComponent) {
			packetByteBuf.writeTextComponent(textComponent);
		}

		public TextComponent read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readTextComponent();
		}

		@Override
		public TrackedData<TextComponent> create(int i) {
			return new TrackedData<>(i, this);
		}

		public TextComponent method_12726(TextComponent textComponent) {
			return textComponent.copy();
		}
	};
	public static final TrackedDataHandler<Optional<TextComponent>> OPTIONAL_TEXT_COMPONENT = new TrackedDataHandler<Optional<TextComponent>>() {
		public void method_12728(PacketByteBuf packetByteBuf, Optional<TextComponent> optional) {
			if (optional.isPresent()) {
				packetByteBuf.writeBoolean(true);
				packetByteBuf.writeTextComponent((TextComponent)optional.get());
			} else {
				packetByteBuf.writeBoolean(false);
			}
		}

		public Optional<TextComponent> read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBoolean() ? Optional.of(packetByteBuf.readTextComponent()) : Optional.empty();
		}

		@Override
		public TrackedData<Optional<TextComponent>> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Optional<TextComponent> method_12730(Optional<TextComponent> optional) {
			return optional.isPresent() ? Optional.of(((TextComponent)optional.get()).copy()) : Optional.empty();
		}
	};
	public static final TrackedDataHandler<ItemStack> ITEM_STACK = new TrackedDataHandler<ItemStack>() {
		public void method_12731(PacketByteBuf packetByteBuf, ItemStack itemStack) {
			packetByteBuf.writeItemStack(itemStack);
		}

		public ItemStack read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readItemStack();
		}

		@Override
		public TrackedData<ItemStack> create(int i) {
			return new TrackedData<>(i, this);
		}

		public ItemStack method_12732(ItemStack itemStack) {
			return itemStack.copy();
		}
	};
	public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new TrackedDataHandler<Optional<BlockState>>() {
		public void method_12734(PacketByteBuf packetByteBuf, Optional<BlockState> optional) {
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

		@Override
		public TrackedData<Optional<BlockState>> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Optional<BlockState> method_12736(Optional<BlockState> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<Boolean> BOOLEAN = new TrackedDataHandler<Boolean>() {
		public void method_12738(PacketByteBuf packetByteBuf, Boolean boolean_) {
			packetByteBuf.writeBoolean(boolean_);
		}

		public Boolean read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBoolean();
		}

		@Override
		public TrackedData<Boolean> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Boolean method_12739(Boolean boolean_) {
			return boolean_;
		}
	};
	public static final TrackedDataHandler<ParticleParameters> PARTICLE = new TrackedDataHandler<ParticleParameters>() {
		public void method_12746(PacketByteBuf packetByteBuf, ParticleParameters particleParameters) {
			packetByteBuf.writeVarInt(Registry.PARTICLE_TYPE.getRawId((ParticleType<? extends ParticleParameters>)particleParameters.getType()));
			particleParameters.write(packetByteBuf);
		}

		public ParticleParameters read(PacketByteBuf packetByteBuf) {
			return this.method_12744(packetByteBuf, Registry.PARTICLE_TYPE.get(packetByteBuf.readVarInt()));
		}

		private <T extends ParticleParameters> T method_12744(PacketByteBuf packetByteBuf, ParticleType<T> particleType) {
			return particleType.getParametersFactory().read(particleType, packetByteBuf);
		}

		@Override
		public TrackedData<ParticleParameters> create(int i) {
			return new TrackedData<>(i, this);
		}

		public ParticleParameters method_12745(ParticleParameters particleParameters) {
			return particleParameters;
		}
	};
	public static final TrackedDataHandler<Rotation> ROTATION = new TrackedDataHandler<Rotation>() {
		public void method_12747(PacketByteBuf packetByteBuf, Rotation rotation) {
			packetByteBuf.writeFloat(rotation.getX());
			packetByteBuf.writeFloat(rotation.getY());
			packetByteBuf.writeFloat(rotation.getZ());
		}

		public Rotation read(PacketByteBuf packetByteBuf) {
			return new Rotation(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}

		@Override
		public TrackedData<Rotation> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Rotation method_12749(Rotation rotation) {
			return rotation;
		}
	};
	public static final TrackedDataHandler<BlockPos> BLOCK_POS = new TrackedDataHandler<BlockPos>() {
		public void method_12751(PacketByteBuf packetByteBuf, BlockPos blockPos) {
			packetByteBuf.writeBlockPos(blockPos);
		}

		public BlockPos read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBlockPos();
		}

		@Override
		public TrackedData<BlockPos> create(int i) {
			return new TrackedData<>(i, this);
		}

		public BlockPos method_12752(BlockPos blockPos) {
			return blockPos;
		}
	};
	public static final TrackedDataHandler<Optional<BlockPos>> OPTIONA_BLOCK_POS = new TrackedDataHandler<Optional<BlockPos>>() {
		public void method_12753(PacketByteBuf packetByteBuf, Optional<BlockPos> optional) {
			packetByteBuf.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				packetByteBuf.writeBlockPos((BlockPos)optional.get());
			}
		}

		public Optional<BlockPos> read(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.empty() : Optional.of(packetByteBuf.readBlockPos());
		}

		@Override
		public TrackedData<Optional<BlockPos>> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Optional<BlockPos> method_12755(Optional<BlockPos> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<Direction> FACING = new TrackedDataHandler<Direction>() {
		public void method_12757(PacketByteBuf packetByteBuf, Direction direction) {
			packetByteBuf.writeEnumConstant(direction);
		}

		public Direction read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readEnumConstant(Direction.class);
		}

		@Override
		public TrackedData<Direction> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Direction method_12758(Direction direction) {
			return direction;
		}
	};
	public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID = new TrackedDataHandler<Optional<UUID>>() {
		public void method_12759(PacketByteBuf packetByteBuf, Optional<UUID> optional) {
			packetByteBuf.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				packetByteBuf.writeUuid((UUID)optional.get());
			}
		}

		public Optional<UUID> read(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.empty() : Optional.of(packetByteBuf.readUuid());
		}

		@Override
		public TrackedData<Optional<UUID>> create(int i) {
			return new TrackedData<>(i, this);
		}

		public Optional<UUID> method_12761(Optional<UUID> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<CompoundTag> TAG_COMPOUND = new TrackedDataHandler<CompoundTag>() {
		public void method_12763(PacketByteBuf packetByteBuf, CompoundTag compoundTag) {
			packetByteBuf.writeCompoundTag(compoundTag);
		}

		public CompoundTag read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readCompoundTag();
		}

		@Override
		public TrackedData<CompoundTag> create(int i) {
			return new TrackedData<>(i, this);
		}

		public CompoundTag method_12762(CompoundTag compoundTag) {
			return compoundTag.copy();
		}
	};
	public static final TrackedDataHandler<VillagerData> VILLAGER_DATA = new TrackedDataHandler<VillagerData>() {
		public void method_17197(PacketByteBuf packetByteBuf, VillagerData villagerData) {
			packetByteBuf.writeVarInt(Registry.VILLAGER_TYPE.getRawId(villagerData.getType()));
			packetByteBuf.writeVarInt(Registry.VILLAGER_PROFESSION.getRawId(villagerData.getProfession()));
			packetByteBuf.writeVarInt(villagerData.getLevel());
		}

		public VillagerData method_17198(PacketByteBuf packetByteBuf) {
			return new VillagerData(
				Registry.VILLAGER_TYPE.get(packetByteBuf.readVarInt()), Registry.VILLAGER_PROFESSION.get(packetByteBuf.readVarInt()), packetByteBuf.readVarInt()
			);
		}

		@Override
		public TrackedData<VillagerData> create(int i) {
			return new TrackedData<>(i, this);
		}

		public VillagerData method_17196(VillagerData villagerData) {
			return villagerData;
		}
	};
	public static final TrackedDataHandler<OptionalInt> field_17910 = new TrackedDataHandler<OptionalInt>() {
		public void method_18189(PacketByteBuf packetByteBuf, OptionalInt optionalInt) {
			packetByteBuf.writeVarInt(optionalInt.orElse(-1) + 1);
		}

		public OptionalInt method_18191(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
		}

		@Override
		public TrackedData<OptionalInt> create(int i) {
			return new TrackedData<>(i, this);
		}

		public OptionalInt method_18190(OptionalInt optionalInt) {
			return optionalInt;
		}
	};

	public static void register(TrackedDataHandler<?> trackedDataHandler) {
		field_13328.add(trackedDataHandler);
	}

	@Nullable
	public static TrackedDataHandler<?> get(int i) {
		return field_13328.get(i);
	}

	public static int getId(TrackedDataHandler<?> trackedDataHandler) {
		return field_13328.getId(trackedDataHandler);
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
		register(OPTIONA_BLOCK_POS);
		register(FACING);
		register(OPTIONAL_UUID);
		register(OPTIONAL_BLOCK_STATE);
		register(TAG_COMPOUND);
		register(PARTICLE);
		register(VILLAGER_DATA);
		register(field_17910);
	}
}
