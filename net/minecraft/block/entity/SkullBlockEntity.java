/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkullBlockEntity
extends BlockEntity {
    public static final String SKULL_OWNER_KEY = "SkullOwner";
    @Nullable
    private static UserCache userCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private GameProfile owner;
    private int ticksPowered;
    private boolean powered;

    public SkullBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SKULL, pos, state);
    }

    public static void setUserCache(UserCache value) {
        userCache = value;
    }

    public static void setSessionService(MinecraftSessionService value) {
        sessionService = value;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.owner != null) {
            NbtCompound nbtCompound = new NbtCompound();
            NbtHelper.writeGameProfile(nbtCompound, this.owner);
            nbt.put(SKULL_OWNER_KEY, nbtCompound);
        }
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        String string;
        super.readNbt(nbt);
        if (nbt.contains(SKULL_OWNER_KEY, 10)) {
            this.setOwner(NbtHelper.toGameProfile(nbt.getCompound(SKULL_OWNER_KEY)));
        } else if (nbt.contains("ExtraType", 8) && !ChatUtil.isEmpty(string = nbt.getString("ExtraType"))) {
            this.setOwner(new GameProfile(null, string));
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
        if (world.isReceivingRedstonePower(pos)) {
            blockEntity.powered = true;
            ++blockEntity.ticksPowered;
        } else {
            blockEntity.powered = false;
        }
    }

    public float getTicksPowered(float tickDelta) {
        if (this.powered) {
            return (float)this.ticksPowered + tickDelta;
        }
        return this.ticksPowered;
    }

    @Nullable
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, BlockEntityUpdateS2CPacket.SKULL, this.toInitialChunkDataNbt());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.writeNbt(new NbtCompound());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOwner(@Nullable GameProfile gameProfile) {
        SkullBlockEntity skullBlockEntity = this;
        synchronized (skullBlockEntity) {
            this.owner = gameProfile;
        }
        this.loadOwnerProperties();
    }

    private void loadOwnerProperties() {
        SkullBlockEntity.loadProperties(this.owner, gameProfile -> {
            this.owner = gameProfile;
            this.markDirty();
        });
    }

    public static void loadProperties(@Nullable GameProfile gameProfile2, Consumer<GameProfile> consumer) {
        if (gameProfile2 == null || ChatUtil.isEmpty(gameProfile2.getName()) || gameProfile2.isComplete() && gameProfile2.getProperties().containsKey("textures") || userCache == null || sessionService == null) {
            consumer.accept(gameProfile2);
            return;
        }
        userCache.method_37156(gameProfile2.getName(), gameProfile -> {
            Property property = Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
            if (property == null) {
                gameProfile = sessionService.fillProfileProperties((GameProfile)gameProfile, true);
            }
            userCache.add((GameProfile)gameProfile);
            consumer.accept((GameProfile)gameProfile);
        });
    }
}

