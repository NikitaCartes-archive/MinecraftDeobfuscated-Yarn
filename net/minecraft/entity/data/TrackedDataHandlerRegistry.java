/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.data;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
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
import org.jetbrains.annotations.Nullable;

public class TrackedDataHandlerRegistry {
    private static final Int2ObjectBiMap<TrackedDataHandler<?>> DATA_HANDLERS = Int2ObjectBiMap.create(16);
    public static final TrackedDataHandler<Byte> BYTE = TrackedDataHandler.of((buf, byte_) -> buf.writeByte(byte_.byteValue()), PacketByteBuf::readByte);
    public static final TrackedDataHandler<Integer> INTEGER = TrackedDataHandler.of(PacketByteBuf::writeVarInt, PacketByteBuf::readVarInt);
    public static final TrackedDataHandler<Long> LONG = TrackedDataHandler.of(PacketByteBuf::writeVarLong, PacketByteBuf::readVarLong);
    public static final TrackedDataHandler<Float> FLOAT = TrackedDataHandler.of(PacketByteBuf::writeFloat, PacketByteBuf::readFloat);
    public static final TrackedDataHandler<String> STRING = TrackedDataHandler.of(PacketByteBuf::writeString, PacketByteBuf::readString);
    public static final TrackedDataHandler<Text> TEXT_COMPONENT = TrackedDataHandler.of(PacketByteBuf::writeText, PacketByteBuf::readText);
    public static final TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = TrackedDataHandler.ofOptional(PacketByteBuf::writeText, PacketByteBuf::readText);
    public static final TrackedDataHandler<ItemStack> ITEM_STACK = new TrackedDataHandler<ItemStack>(){

        @Override
        public void write(PacketByteBuf packetByteBuf, ItemStack itemStack) {
            packetByteBuf.writeItemStack(itemStack);
        }

        @Override
        public ItemStack read(PacketByteBuf packetByteBuf) {
            return packetByteBuf.readItemStack();
        }

        @Override
        public ItemStack copy(ItemStack itemStack) {
            return itemStack.copy();
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf buf) {
            return this.read(buf);
        }
    };
    public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new TrackedDataHandler.ImmutableHandler<Optional<BlockState>>(){

        @Override
        public void write(PacketByteBuf packetByteBuf, Optional<BlockState> optional) {
            if (optional.isPresent()) {
                packetByteBuf.writeVarInt(Block.getRawIdFromState(optional.get()));
            } else {
                packetByteBuf.writeVarInt(0);
            }
        }

        @Override
        public Optional<BlockState> read(PacketByteBuf packetByteBuf) {
            int i = packetByteBuf.readVarInt();
            if (i == 0) {
                return Optional.empty();
            }
            return Optional.of(Block.getStateFromRawId(i));
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf buf) {
            return this.read(buf);
        }
    };
    public static final TrackedDataHandler<Boolean> BOOLEAN = TrackedDataHandler.of(PacketByteBuf::writeBoolean, PacketByteBuf::readBoolean);
    public static final TrackedDataHandler<ParticleEffect> PARTICLE = new TrackedDataHandler.ImmutableHandler<ParticleEffect>(){

        @Override
        public void write(PacketByteBuf packetByteBuf, ParticleEffect particleEffect) {
            packetByteBuf.writeRegistryValue(Registries.PARTICLE_TYPE, particleEffect.getType());
            particleEffect.write(packetByteBuf);
        }

        @Override
        public ParticleEffect read(PacketByteBuf packetByteBuf) {
            return this.read(packetByteBuf, packetByteBuf.readRegistryValue(Registries.PARTICLE_TYPE));
        }

        private <T extends ParticleEffect> T read(PacketByteBuf buf, ParticleType<T> type) {
            return type.getParametersFactory().read(type, buf);
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf buf) {
            return this.read(buf);
        }
    };
    public static final TrackedDataHandler<EulerAngle> ROTATION = new TrackedDataHandler.ImmutableHandler<EulerAngle>(){

        @Override
        public void write(PacketByteBuf packetByteBuf, EulerAngle eulerAngle) {
            packetByteBuf.writeFloat(eulerAngle.getPitch());
            packetByteBuf.writeFloat(eulerAngle.getYaw());
            packetByteBuf.writeFloat(eulerAngle.getRoll());
        }

        @Override
        public EulerAngle read(PacketByteBuf packetByteBuf) {
            return new EulerAngle(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf buf) {
            return this.read(buf);
        }
    };
    public static final TrackedDataHandler<BlockPos> BLOCK_POS = TrackedDataHandler.of(PacketByteBuf::writeBlockPos, PacketByteBuf::readBlockPos);
    public static final TrackedDataHandler<Optional<BlockPos>> OPTIONAL_BLOCK_POS = TrackedDataHandler.ofOptional(PacketByteBuf::writeBlockPos, PacketByteBuf::readBlockPos);
    public static final TrackedDataHandler<Direction> FACING = TrackedDataHandler.ofEnum(Direction.class);
    public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID = TrackedDataHandler.ofOptional(PacketByteBuf::writeUuid, PacketByteBuf::readUuid);
    public static final TrackedDataHandler<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = TrackedDataHandler.ofOptional(PacketByteBuf::writeGlobalPos, PacketByteBuf::readGlobalPos);
    public static final TrackedDataHandler<NbtCompound> NBT_COMPOUND = new TrackedDataHandler<NbtCompound>(){

        @Override
        public void write(PacketByteBuf packetByteBuf, NbtCompound nbtCompound) {
            packetByteBuf.writeNbt(nbtCompound);
        }

        @Override
        public NbtCompound read(PacketByteBuf packetByteBuf) {
            return packetByteBuf.readNbt();
        }

        @Override
        public NbtCompound copy(NbtCompound nbtCompound) {
            return nbtCompound.copy();
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf buf) {
            return this.read(buf);
        }
    };
    public static final TrackedDataHandler<VillagerData> VILLAGER_DATA = new TrackedDataHandler.ImmutableHandler<VillagerData>(){

        @Override
        public void write(PacketByteBuf packetByteBuf, VillagerData villagerData) {
            packetByteBuf.writeRegistryValue(Registries.VILLAGER_TYPE, villagerData.getType());
            packetByteBuf.writeRegistryValue(Registries.VILLAGER_PROFESSION, villagerData.getProfession());
            packetByteBuf.writeVarInt(villagerData.getLevel());
        }

        @Override
        public VillagerData read(PacketByteBuf packetByteBuf) {
            return new VillagerData(packetByteBuf.readRegistryValue(Registries.VILLAGER_TYPE), packetByteBuf.readRegistryValue(Registries.VILLAGER_PROFESSION), packetByteBuf.readVarInt());
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf buf) {
            return this.read(buf);
        }
    };
    public static final TrackedDataHandler<OptionalInt> OPTIONAL_INT = new TrackedDataHandler.ImmutableHandler<OptionalInt>(){

        @Override
        public void write(PacketByteBuf packetByteBuf, OptionalInt optionalInt) {
            packetByteBuf.writeVarInt(optionalInt.orElse(-1) + 1);
        }

        @Override
        public OptionalInt read(PacketByteBuf packetByteBuf) {
            int i = packetByteBuf.readVarInt();
            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf buf) {
            return this.read(buf);
        }
    };
    public static final TrackedDataHandler<EntityPose> ENTITY_POSE = TrackedDataHandler.ofEnum(EntityPose.class);
    public static final TrackedDataHandler<CatVariant> CAT_VARIANT = TrackedDataHandler.of(Registries.CAT_VARIANT);
    public static final TrackedDataHandler<FrogVariant> FROG_VARIANT = TrackedDataHandler.of(Registries.FROG_VARIANT);
    public static final TrackedDataHandler<RegistryEntry<PaintingVariant>> PAINTING_VARIANT = TrackedDataHandler.of(Registries.PAINTING_VARIANT.getIndexedEntries());

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
        TrackedDataHandlerRegistry.register(BYTE);
        TrackedDataHandlerRegistry.register(INTEGER);
        TrackedDataHandlerRegistry.register(LONG);
        TrackedDataHandlerRegistry.register(FLOAT);
        TrackedDataHandlerRegistry.register(STRING);
        TrackedDataHandlerRegistry.register(TEXT_COMPONENT);
        TrackedDataHandlerRegistry.register(OPTIONAL_TEXT_COMPONENT);
        TrackedDataHandlerRegistry.register(ITEM_STACK);
        TrackedDataHandlerRegistry.register(BOOLEAN);
        TrackedDataHandlerRegistry.register(ROTATION);
        TrackedDataHandlerRegistry.register(BLOCK_POS);
        TrackedDataHandlerRegistry.register(OPTIONAL_BLOCK_POS);
        TrackedDataHandlerRegistry.register(FACING);
        TrackedDataHandlerRegistry.register(OPTIONAL_UUID);
        TrackedDataHandlerRegistry.register(OPTIONAL_BLOCK_STATE);
        TrackedDataHandlerRegistry.register(NBT_COMPOUND);
        TrackedDataHandlerRegistry.register(PARTICLE);
        TrackedDataHandlerRegistry.register(VILLAGER_DATA);
        TrackedDataHandlerRegistry.register(OPTIONAL_INT);
        TrackedDataHandlerRegistry.register(ENTITY_POSE);
        TrackedDataHandlerRegistry.register(CAT_VARIANT);
        TrackedDataHandlerRegistry.register(FROG_VARIANT);
        TrackedDataHandlerRegistry.register(OPTIONAL_GLOBAL_POS);
        TrackedDataHandlerRegistry.register(PAINTING_VARIANT);
    }
}

