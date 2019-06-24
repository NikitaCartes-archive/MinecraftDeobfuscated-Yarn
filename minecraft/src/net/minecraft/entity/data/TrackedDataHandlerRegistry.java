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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.text.Text;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;

public class TrackedDataHandlerRegistry {
	private static final Int2ObjectBiMap<TrackedDataHandler<?>> field_13328 = new Int2ObjectBiMap<>(16);
	public static final TrackedDataHandler<Byte> BYTE = new TrackedDataHandler<Byte>() {
		public void method_12741(PacketByteBuf packetByteBuf, Byte byte_) {
			packetByteBuf.writeByte(byte_);
		}

		public Byte method_12740(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readByte();
		}

		public Byte method_12742(Byte byte_) {
			return byte_;
		}
	};
	public static final TrackedDataHandler<Integer> INTEGER = new TrackedDataHandler<Integer>() {
		public void method_12766(PacketByteBuf packetByteBuf, Integer integer) {
			packetByteBuf.writeVarInt(integer);
		}

		public Integer method_12765(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readVarInt();
		}

		public Integer method_12767(Integer integer) {
			return integer;
		}
	};
	public static final TrackedDataHandler<Float> FLOAT = new TrackedDataHandler<Float>() {
		public void method_12769(PacketByteBuf packetByteBuf, Float float_) {
			packetByteBuf.writeFloat(float_);
		}

		public Float method_12768(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readFloat();
		}

		public Float method_12770(Float float_) {
			return float_;
		}
	};
	public static final TrackedDataHandler<String> STRING = new TrackedDataHandler<String>() {
		public void method_12723(PacketByteBuf packetByteBuf, String string) {
			packetByteBuf.writeString(string);
		}

		public String method_12722(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readString(32767);
		}

		public String method_12724(String string) {
			return string;
		}
	};
	public static final TrackedDataHandler<Text> TEXT_COMPONENT = new TrackedDataHandler<Text>() {
		public void method_12727(PacketByteBuf packetByteBuf, Text text) {
			packetByteBuf.writeText(text);
		}

		public Text method_12725(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readText();
		}

		public Text method_12726(Text text) {
			return text.deepCopy();
		}
	};
	public static final TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = new TrackedDataHandler<Optional<Text>>() {
		public void method_12728(PacketByteBuf packetByteBuf, Optional<Text> optional) {
			if (optional.isPresent()) {
				packetByteBuf.writeBoolean(true);
				packetByteBuf.writeText((Text)optional.get());
			} else {
				packetByteBuf.writeBoolean(false);
			}
		}

		public Optional<Text> method_12729(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBoolean() ? Optional.of(packetByteBuf.readText()) : Optional.empty();
		}

		public Optional<Text> method_12730(Optional<Text> optional) {
			return optional.isPresent() ? Optional.of(((Text)optional.get()).deepCopy()) : Optional.empty();
		}
	};
	public static final TrackedDataHandler<ItemStack> ITEM_STACK = new TrackedDataHandler<ItemStack>() {
		public void method_12731(PacketByteBuf packetByteBuf, ItemStack itemStack) {
			packetByteBuf.writeItemStack(itemStack);
		}

		public ItemStack method_12733(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readItemStack();
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

		public Optional<BlockState> method_12735(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			return i == 0 ? Optional.empty() : Optional.of(Block.getStateFromRawId(i));
		}

		public Optional<BlockState> method_12736(Optional<BlockState> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<Boolean> BOOLEAN = new TrackedDataHandler<Boolean>() {
		public void method_12738(PacketByteBuf packetByteBuf, Boolean boolean_) {
			packetByteBuf.writeBoolean(boolean_);
		}

		public Boolean method_12737(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBoolean();
		}

		public Boolean method_12739(Boolean boolean_) {
			return boolean_;
		}
	};
	public static final TrackedDataHandler<ParticleEffect> PARTICLE = new TrackedDataHandler<ParticleEffect>() {
		public void method_12746(PacketByteBuf packetByteBuf, ParticleEffect particleEffect) {
			packetByteBuf.writeVarInt(Registry.PARTICLE_TYPE.getRawId((ParticleType<? extends ParticleEffect>)particleEffect.getType()));
			particleEffect.write(packetByteBuf);
		}

		public ParticleEffect method_12743(PacketByteBuf packetByteBuf) {
			return this.method_12744(packetByteBuf, Registry.PARTICLE_TYPE.get(packetByteBuf.readVarInt()));
		}

		private <T extends ParticleEffect> T method_12744(PacketByteBuf packetByteBuf, ParticleType<T> particleType) {
			return particleType.getParametersFactory().read(particleType, packetByteBuf);
		}

		public ParticleEffect method_12745(ParticleEffect particleEffect) {
			return particleEffect;
		}
	};
	public static final TrackedDataHandler<EulerAngle> ROTATION = new TrackedDataHandler<EulerAngle>() {
		public void method_12747(PacketByteBuf packetByteBuf, EulerAngle eulerAngle) {
			packetByteBuf.writeFloat(eulerAngle.getPitch());
			packetByteBuf.writeFloat(eulerAngle.getYaw());
			packetByteBuf.writeFloat(eulerAngle.getRoll());
		}

		public EulerAngle method_12748(PacketByteBuf packetByteBuf) {
			return new EulerAngle(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}

		public EulerAngle method_12749(EulerAngle eulerAngle) {
			return eulerAngle;
		}
	};
	public static final TrackedDataHandler<BlockPos> BLOCK_POS = new TrackedDataHandler<BlockPos>() {
		public void method_12751(PacketByteBuf packetByteBuf, BlockPos blockPos) {
			packetByteBuf.writeBlockPos(blockPos);
		}

		public BlockPos method_12750(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readBlockPos();
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

		public Optional<BlockPos> method_12754(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.empty() : Optional.of(packetByteBuf.readBlockPos());
		}

		public Optional<BlockPos> method_12755(Optional<BlockPos> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<Direction> FACING = new TrackedDataHandler<Direction>() {
		public void method_12757(PacketByteBuf packetByteBuf, Direction direction) {
			packetByteBuf.writeEnumConstant(direction);
		}

		public Direction method_12756(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readEnumConstant(Direction.class);
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

		public Optional<UUID> method_12760(PacketByteBuf packetByteBuf) {
			return !packetByteBuf.readBoolean() ? Optional.empty() : Optional.of(packetByteBuf.readUuid());
		}

		public Optional<UUID> method_12761(Optional<UUID> optional) {
			return optional;
		}
	};
	public static final TrackedDataHandler<CompoundTag> TAG_COMPOUND = new TrackedDataHandler<CompoundTag>() {
		public void method_12763(PacketByteBuf packetByteBuf, CompoundTag compoundTag) {
			packetByteBuf.writeCompoundTag(compoundTag);
		}

		public CompoundTag method_12764(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readCompoundTag();
		}

		public CompoundTag method_12762(CompoundTag compoundTag) {
			return compoundTag.method_10553();
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

		public OptionalInt method_18190(OptionalInt optionalInt) {
			return optionalInt;
		}
	};
	public static final TrackedDataHandler<EntityPose> ENTITY_POSE = new TrackedDataHandler<EntityPose>() {
		public void method_18697(PacketByteBuf packetByteBuf, EntityPose entityPose) {
			packetByteBuf.writeEnumConstant(entityPose);
		}

		public EntityPose method_18698(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readEnumConstant(EntityPose.class);
		}

		public EntityPose method_18696(EntityPose entityPose) {
			return entityPose;
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
		register(ENTITY_POSE);
	}
}
