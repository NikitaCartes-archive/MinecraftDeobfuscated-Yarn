package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SkullBlockEntity extends BlockEntity {
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
	public CompoundTag writeNbt(CompoundTag tag) {
		super.writeNbt(tag);
		if (this.owner != null) {
			CompoundTag compoundTag = new CompoundTag();
			NbtHelper.fromGameProfile(compoundTag, this.owner);
			tag.put("SkullOwner", compoundTag);
		}

		return tag;
	}

	@Override
	public void readNbt(CompoundTag tag) {
		super.readNbt(tag);
		if (tag.contains("SkullOwner", 10)) {
			this.setOwner(NbtHelper.toGameProfile(tag.getCompound("SkullOwner")));
		} else if (tag.contains("ExtraType", 8)) {
			String string = tag.getString("ExtraType");
			if (!ChatUtil.isEmpty(string)) {
				this.setOwner(new GameProfile(null, string));
			}
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
		if (world.isReceivingRedstonePower(pos)) {
			blockEntity.powered = true;
			blockEntity.ticksPowered++;
		} else {
			blockEntity.powered = false;
		}
	}

	@Environment(EnvType.CLIENT)
	public float getTicksPowered(float tickDelta) {
		return this.powered ? (float)this.ticksPowered + tickDelta : (float)this.ticksPowered;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public GameProfile getOwner() {
		return this.owner;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 4, this.toInitialChunkDataNbt());
	}

	@Override
	public CompoundTag toInitialChunkDataNbt() {
		return this.writeNbt(new CompoundTag());
	}

	public void setOwner(@Nullable GameProfile owner) {
		this.owner = owner;
		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		this.owner = loadProperties(this.owner);
		this.markDirty();
	}

	@Nullable
	public static GameProfile loadProperties(@Nullable GameProfile profile) {
		if (profile != null && !ChatUtil.isEmpty(profile.getName())) {
			if (profile.isComplete() && profile.getProperties().containsKey("textures")) {
				return profile;
			} else if (userCache != null && sessionService != null) {
				GameProfile gameProfile = userCache.findByName(profile.getName());
				if (gameProfile == null) {
					return profile;
				} else {
					Property property = Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
					if (property == null) {
						gameProfile = sessionService.fillProfileProperties(gameProfile, true);
					}

					return gameProfile;
				}
			} else {
				return profile;
			}
		} else {
			return profile;
		}
	}
}
