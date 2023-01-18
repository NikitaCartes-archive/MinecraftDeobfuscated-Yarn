/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkullBlockEntity
extends BlockEntity {
    public static final String SKULL_OWNER_KEY = "SkullOwner";
    public static final String NOTE_BLOCK_SOUND_KEY = "note_block_sound";
    @Nullable
    private static UserCache userCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor executor;
    @Nullable
    private GameProfile owner;
    @Nullable
    private Identifier noteBlockSound;
    private int poweredTicks;
    private boolean powered;

    public SkullBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SKULL, pos, state);
    }

    public static void setServices(ApiServices apiServices, Executor executor) {
        userCache = apiServices.userCache();
        sessionService = apiServices.sessionService();
        SkullBlockEntity.executor = executor;
    }

    public static void clearServices() {
        userCache = null;
        sessionService = null;
        executor = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.owner != null) {
            NbtCompound nbtCompound = new NbtCompound();
            NbtHelper.writeGameProfile(nbtCompound, this.owner);
            nbt.put(SKULL_OWNER_KEY, nbtCompound);
        }
        if (this.noteBlockSound != null) {
            nbt.putString(NOTE_BLOCK_SOUND_KEY, this.noteBlockSound.toString());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        String string;
        super.readNbt(nbt);
        if (nbt.contains(SKULL_OWNER_KEY, NbtElement.COMPOUND_TYPE)) {
            this.setOwner(NbtHelper.toGameProfile(nbt.getCompound(SKULL_OWNER_KEY)));
        } else if (nbt.contains("ExtraType", NbtElement.STRING_TYPE) && !StringHelper.isEmpty(string = nbt.getString("ExtraType"))) {
            this.setOwner(new GameProfile(null, string));
        }
        if (nbt.contains(NOTE_BLOCK_SOUND_KEY, NbtElement.STRING_TYPE)) {
            this.noteBlockSound = Identifier.tryParse(nbt.getString(NOTE_BLOCK_SOUND_KEY));
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
        if (world.isReceivingRedstonePower(pos)) {
            blockEntity.powered = true;
            ++blockEntity.poweredTicks;
        } else {
            blockEntity.powered = false;
        }
    }

    public float getPoweredTicks(float tickDelta) {
        if (this.powered) {
            return (float)this.poweredTicks + tickDelta;
        }
        return this.poweredTicks;
    }

    @Nullable
    public GameProfile getOwner() {
        return this.owner;
    }

    @Nullable
    public Identifier getNoteBlockSound() {
        return this.noteBlockSound;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOwner(@Nullable GameProfile owner) {
        SkullBlockEntity skullBlockEntity = this;
        synchronized (skullBlockEntity) {
            this.owner = owner;
        }
        this.loadOwnerProperties();
    }

    private void loadOwnerProperties() {
        SkullBlockEntity.loadProperties(this.owner, owner -> {
            this.owner = owner;
            this.markDirty();
        });
    }

    public static void loadProperties(@Nullable GameProfile owner, Consumer<GameProfile> callback) {
        if (owner == null || StringHelper.isEmpty(owner.getName()) || owner.isComplete() && owner.getProperties().containsKey("textures") || userCache == null || sessionService == null) {
            callback.accept(owner);
            return;
        }
        userCache.findByNameAsync(owner.getName(), profile -> Util.getMainWorkerExecutor().execute(() -> Util.ifPresentOrElse(profile, profile -> {
            Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
            if (property == null) {
                profile = sessionService.fillProfileProperties((GameProfile)profile, true);
            }
            GameProfile gameProfile = profile;
            executor.execute(() -> {
                userCache.add(gameProfile);
                callback.accept(gameProfile);
            });
        }, () -> executor.execute(() -> callback.accept(owner)))));
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

