/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
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

    public void setOwner(@Nullable GameProfile owner) {
        this.owner = owner;
        this.loadOwnerProperties();
    }

    private void loadOwnerProperties() {
        this.owner = SkullBlockEntity.loadProperties(this.owner);
        this.markDirty();
    }

    @Nullable
    public static GameProfile loadProperties(@Nullable GameProfile profile) {
        if (profile == null || ChatUtil.isEmpty(profile.getName())) {
            return profile;
        }
        if (profile.isComplete() && profile.getProperties().containsKey("textures")) {
            return profile;
        }
        if (userCache == null || sessionService == null) {
            return profile;
        }
        GameProfile gameProfile = userCache.findByName(profile.getName());
        if (gameProfile == null) {
            return profile;
        }
        Property property = Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
        if (property == null) {
            gameProfile = sessionService.fillProfileProperties(gameProfile, true);
        }
        return gameProfile;
    }
}

