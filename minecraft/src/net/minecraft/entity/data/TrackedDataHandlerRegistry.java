package net.minecraft.entity.data;

import io.netty.buffer.ByteBuf;
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
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Uuids;
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
	public static final TrackedDataHandler<Byte> BYTE = TrackedDataHandler.create(PacketCodecs.BYTE);
	public static final TrackedDataHandler<Integer> INTEGER = TrackedDataHandler.create(PacketCodecs.VAR_INT);
	public static final TrackedDataHandler<Long> LONG = TrackedDataHandler.create(PacketCodecs.VAR_LONG);
	public static final TrackedDataHandler<Float> FLOAT = TrackedDataHandler.create(PacketCodecs.FLOAT);
	public static final TrackedDataHandler<String> STRING = TrackedDataHandler.create(PacketCodecs.STRING);
	public static final TrackedDataHandler<Text> TEXT_COMPONENT = TrackedDataHandler.create(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
	public static final TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = TrackedDataHandler.create(TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC);
	public static final TrackedDataHandler<ItemStack> ITEM_STACK = new TrackedDataHandler<ItemStack>() {
		@Override
		public PacketCodec<? super RegistryByteBuf, ItemStack> codec() {
			return ItemStack.OPTIONAL_PACKET_CODEC;
		}

		public ItemStack copy(ItemStack itemStack) {
			return itemStack.copy();
		}
	};
	public static final TrackedDataHandler<BlockState> BLOCK_STATE = TrackedDataHandler.create(PacketCodecs.entryOf(Block.STATE_IDS));
	private static final PacketCodec<ByteBuf, Optional<BlockState>> OPTIONAL_BLOCK_STATE_CODEC = new PacketCodec<ByteBuf, Optional<BlockState>>() {
		public void encode(ByteBuf byteBuf, Optional<BlockState> optional) {
			if (optional.isPresent()) {
				VarInts.write(byteBuf, Block.getRawIdFromState((BlockState)optional.get()));
			} else {
				VarInts.write(byteBuf, 0);
			}
		}

		public Optional<BlockState> decode(ByteBuf byteBuf) {
			int i = VarInts.read(byteBuf);
			return i == 0 ? Optional.empty() : Optional.of(Block.getStateFromRawId(i));
		}
	};
	public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = TrackedDataHandler.create(OPTIONAL_BLOCK_STATE_CODEC);
	public static final TrackedDataHandler<Boolean> BOOLEAN = TrackedDataHandler.create(PacketCodecs.BOOL);
	public static final TrackedDataHandler<ParticleEffect> PARTICLE = TrackedDataHandler.create(ParticleTypes.PACKET_CODEC);
	public static final TrackedDataHandler<EulerAngle> ROTATION = TrackedDataHandler.create(EulerAngle.PACKET_CODEC);
	public static final TrackedDataHandler<BlockPos> BLOCK_POS = TrackedDataHandler.create(BlockPos.PACKET_CODEC);
	public static final TrackedDataHandler<Optional<BlockPos>> OPTIONAL_BLOCK_POS = TrackedDataHandler.create(
		BlockPos.PACKET_CODEC.collect(PacketCodecs::optional)
	);
	public static final TrackedDataHandler<Direction> FACING = TrackedDataHandler.create(Direction.PACKET_CODEC);
	public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID = TrackedDataHandler.create(Uuids.PACKET_CODEC.collect(PacketCodecs::optional));
	public static final TrackedDataHandler<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = TrackedDataHandler.create(
		GlobalPos.PACKET_CODEC.collect(PacketCodecs::optional)
	);
	public static final TrackedDataHandler<NbtCompound> NBT_COMPOUND = new TrackedDataHandler<NbtCompound>() {
		@Override
		public PacketCodec<? super RegistryByteBuf, NbtCompound> codec() {
			return PacketCodecs.UNLIMITED_NBT_COMPOUND;
		}

		public NbtCompound copy(NbtCompound nbtCompound) {
			return nbtCompound.copy();
		}
	};
	public static final TrackedDataHandler<VillagerData> VILLAGER_DATA = TrackedDataHandler.create(VillagerData.PACKET_CODEC);
	private static final PacketCodec<ByteBuf, OptionalInt> OPTIONAL_INT_CODEC = new PacketCodec<ByteBuf, OptionalInt>() {
		public OptionalInt decode(ByteBuf byteBuf) {
			int i = VarInts.read(byteBuf);
			return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
		}

		public void encode(ByteBuf byteBuf, OptionalInt optionalInt) {
			VarInts.write(byteBuf, optionalInt.orElse(-1) + 1);
		}
	};
	public static final TrackedDataHandler<OptionalInt> OPTIONAL_INT = TrackedDataHandler.create(OPTIONAL_INT_CODEC);
	public static final TrackedDataHandler<EntityPose> ENTITY_POSE = TrackedDataHandler.create(EntityPose.PACKET_CODEC);
	public static final TrackedDataHandler<CatVariant> CAT_VARIANT = TrackedDataHandler.create(PacketCodecs.registryValue(RegistryKeys.CAT_VARIANT));
	public static final TrackedDataHandler<RegistryEntry<WolfVariant>> WOLF_VARIANT = TrackedDataHandler.create(
		PacketCodecs.registryEntry(RegistryKeys.WOLF_VARIANT)
	);
	public static final TrackedDataHandler<FrogVariant> FROG_VARIANT = TrackedDataHandler.create(PacketCodecs.registryValue(RegistryKeys.FROG_VARIANT));
	public static final TrackedDataHandler<RegistryEntry<PaintingVariant>> PAINTING_VARIANT = TrackedDataHandler.create(
		PacketCodecs.registryEntry(RegistryKeys.PAINTING_VARIANT)
	);
	public static final TrackedDataHandler<ArmadilloEntity.State> ARMADILLO_STATE = TrackedDataHandler.create(ArmadilloEntity.State.PACKET_CODEC);
	public static final TrackedDataHandler<SnifferEntity.State> SNIFFER_STATE = TrackedDataHandler.create(SnifferEntity.State.PACKET_CODEC);
	public static final TrackedDataHandler<Vector3f> VECTOR3F = TrackedDataHandler.create(PacketCodecs.VECTOR3F);
	public static final TrackedDataHandler<Quaternionf> QUATERNIONF = TrackedDataHandler.create(PacketCodecs.QUATERNIONF);

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
		register(WOLF_VARIANT);
		register(FROG_VARIANT);
		register(OPTIONAL_GLOBAL_POS);
		register(PAINTING_VARIANT);
		register(SNIFFER_STATE);
		register(ARMADILLO_STATE);
		register(VECTOR3F);
		register(QUATERNIONF);
	}
}
