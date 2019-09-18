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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

public class SkullBlockEntity
extends BlockEntity
implements Tickable {
    private GameProfile owner;
    private int ticksPowered;
    private boolean isPowered;
    private static UserCache userCache;
    private static MinecraftSessionService sessionService;

    public SkullBlockEntity() {
        super(BlockEntityType.SKULL);
    }

    public static void setUserCache(UserCache userCache) {
        SkullBlockEntity.userCache = userCache;
    }

    public static void setSessionService(MinecraftSessionService minecraftSessionService) {
        sessionService = minecraftSessionService;
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (this.owner != null) {
            CompoundTag compoundTag2 = new CompoundTag();
            NbtHelper.fromGameProfile(compoundTag2, this.owner);
            compoundTag.put("Owner", compoundTag2);
        }
        return compoundTag;
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        String string;
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("Owner", 10)) {
            this.setOwnerAndType(NbtHelper.toGameProfile(compoundTag.getCompound("Owner")));
        } else if (compoundTag.containsKey("ExtraType", 8) && !ChatUtil.isEmpty(string = compoundTag.getString("ExtraType"))) {
            this.setOwnerAndType(new GameProfile(null, string));
        }
    }

    @Override
    public void tick() {
        Block block = this.getCachedState().getBlock();
        if (block == Blocks.DRAGON_HEAD || block == Blocks.DRAGON_WALL_HEAD) {
            if (this.world.isReceivingRedstonePower(this.pos)) {
                this.isPowered = true;
                ++this.ticksPowered;
            } else {
                this.isPowered = false;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getTicksPowered(float f) {
        if (this.isPowered) {
            return (float)this.ticksPowered + f;
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

    public static GameProfile loadProperties(GameProfile gameProfile) {
        if (gameProfile == null || ChatUtil.isEmpty(gameProfile.getName())) {
            return gameProfile;
        }
        if (gameProfile.isComplete() && gameProfile.getProperties().containsKey("textures")) {
            return gameProfile;
        }
        if (userCache == null || sessionService == null) {
            return gameProfile;
        }
        GameProfile gameProfile2 = userCache.findByName(gameProfile.getName());
        if (gameProfile2 == null) {
            return gameProfile;
        }
        Property property = Iterables.getFirst(gameProfile2.getProperties().get("textures"), null);
        if (property == null) {
            gameProfile2 = sessionService.fillProfileProperties(gameProfile2, true);
        }
        return gameProfile2;
    }
}

