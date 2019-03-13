package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.TagHelper;
import net.minecraft.util.Tickable;
import net.minecraft.util.UserCache;

public class SkullBlockEntity extends BlockEntity implements Tickable {
	private GameProfile owner;
	private int ticksPowered;
	private boolean isPowered;
	private static UserCache field_12089;
	private static MinecraftSessionService sessionService;

	public SkullBlockEntity() {
		super(BlockEntityType.SKULL);
	}

	public static void method_11337(UserCache userCache) {
		field_12089 = userCache;
	}

	public static void setSessionService(MinecraftSessionService minecraftSessionService) {
		sessionService = minecraftSessionService;
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (this.owner != null) {
			CompoundTag compoundTag2 = new CompoundTag();
			TagHelper.serializeProfile(compoundTag2, this.owner);
			compoundTag.method_10566("Owner", compoundTag2);
		}

		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		if (compoundTag.containsKey("Owner", 10)) {
			this.setOwnerAndType(TagHelper.deserializeProfile(compoundTag.getCompound("Owner")));
		} else if (compoundTag.containsKey("ExtraType", 8)) {
			String string = compoundTag.getString("ExtraType");
			if (!ChatUtil.isEmpty(string)) {
				this.setOwnerAndType(new GameProfile(null, string));
			}
		}
	}

	@Override
	public void tick() {
		Block block = this.method_11010().getBlock();
		if (block == Blocks.field_10337 || block == Blocks.field_10472) {
			if (this.world.method_8479(this.field_11867)) {
				this.isPowered = true;
				this.ticksPowered++;
			} else {
				this.isPowered = false;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public float getTicksPowered(float f) {
		return this.isPowered ? (float)this.ticksPowered + f : (float)this.ticksPowered;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public GameProfile getOwner() {
		return this.owner;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 4, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
	}

	public void setOwnerAndType(@Nullable GameProfile gameProfile) {
		this.owner = gameProfile;
		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		this.owner = loadProperties(this.owner);
		this.markDirty();
	}

	public static GameProfile loadProperties(GameProfile gameProfile) {
		if (gameProfile != null && !ChatUtil.isEmpty(gameProfile.getName())) {
			if (gameProfile.isComplete() && gameProfile.getProperties().containsKey("textures")) {
				return gameProfile;
			} else if (field_12089 != null && sessionService != null) {
				GameProfile gameProfile2 = field_12089.findByName(gameProfile.getName());
				if (gameProfile2 == null) {
					return gameProfile;
				} else {
					Property property = Iterables.getFirst(gameProfile2.getProperties().get("textures"), null);
					if (property == null) {
						gameProfile2 = sessionService.fillProfileProperties(gameProfile2, true);
					}

					return gameProfile2;
				}
			} else {
				return gameProfile;
			}
		} else {
			return gameProfile;
		}
	}
}
