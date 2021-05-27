package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SkullBlockEntity extends BlockEntity {
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
			nbt.put("SkullOwner", nbtCompound);
		}

		return nbt;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
			this.setOwner(NbtHelper.toGameProfile(nbt.getCompound("SkullOwner")));
		} else if (nbt.contains("ExtraType", NbtElement.STRING_TYPE)) {
			String string = nbt.getString("ExtraType");
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

	public float getTicksPowered(float tickDelta) {
		return this.powered ? (float)this.ticksPowered + tickDelta : (float)this.ticksPowered;
	}

	@Nullable
	public GameProfile getOwner() {
		return this.owner;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, BlockEntityUpdateS2CPacket.SKULL, this.toInitialChunkDataNbt());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.writeNbt(new NbtCompound());
	}

	public void setOwner(@Nullable GameProfile gameProfile) {
		synchronized (this) {
			this.owner = gameProfile;
		}

		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		loadProperties(this.owner, gameProfile -> {
			this.owner = gameProfile;
			this.markDirty();
		});
	}

	public static void loadProperties(@Nullable GameProfile gameProfile, Consumer<GameProfile> consumer) {
		if (gameProfile != null
			&& !ChatUtil.isEmpty(gameProfile.getName())
			&& (!gameProfile.isComplete() || !gameProfile.getProperties().containsKey("textures"))
			&& userCache != null
			&& sessionService != null) {
			userCache.method_37156(gameProfile.getName(), gameProfilex -> {
				Property property = Iterables.getFirst(gameProfilex.getProperties().get("textures"), null);
				if (property == null) {
					gameProfilex = sessionService.fillProfileProperties(gameProfilex, true);
				}

				userCache.add(gameProfilex);
				consumer.accept(gameProfilex);
			});
		} else {
			consumer.accept(gameProfile);
		}
	}
}
