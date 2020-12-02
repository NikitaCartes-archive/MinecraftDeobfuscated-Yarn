/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkullBlockEntity
extends BlockEntity {
    @Nullable
    private static UserCache userCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private GameProfile owner;
    private int ticksPowered;
    private boolean powered;

    public SkullBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.SKULL, blockPos, blockState);
    }

    public static void setUserCache(UserCache value) {
        userCache = value;
    }

    public static void setSessionService(MinecraftSessionService value) {
        sessionService = value;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.owner != null) {
            CompoundTag compoundTag = new CompoundTag();
            NbtHelper.fromGameProfile(compoundTag, this.owner);
            tag.put("SkullOwner", compoundTag);
        }
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        String string;
        super.fromTag(tag);
        if (tag.contains("SkullOwner", 10)) {
            this.setOwnerAndType(NbtHelper.toGameProfile(tag.getCompound("SkullOwner")));
        } else if (tag.contains("ExtraType", 8) && !ChatUtil.isEmpty(string = tag.getString("ExtraType"))) {
            this.setOwnerAndType(new GameProfile(null, string));
        }
    }

    public static void method_31695(World world, BlockPos blockPos, BlockState blockState, SkullBlockEntity skullBlockEntity) {
        if (world.isReceivingRedstonePower(blockPos)) {
            skullBlockEntity.powered = true;
            ++skullBlockEntity.ticksPowered;
        } else {
            skullBlockEntity.powered = false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getTicksPowered(float tickDelta) {
        if (this.powered) {
            return (float)this.ticksPowered + tickDelta;
        }
        return this.ticksPowered;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 4, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public void setOwnerAndType(@Nullable GameProfile gameProfile) {
        this.owner = gameProfile;
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

