package net.minecraft.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SkullBlockEntity extends BlockEntity {
	public static final String SKULL_OWNER_KEY = "SkullOwner";
	public static final String NOTE_BLOCK_SOUND_KEY = "note_block_sound";
	@Nullable
	private static UserCache userCache;
	@Nullable
	private static MinecraftSessionService sessionService;
	@Nullable
	private static Executor currentExecutor;
	private static final Executor EXECUTOR = runnable -> {
		Executor executor = currentExecutor;
		if (executor != null) {
			executor.execute(runnable);
		}
	};
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
		currentExecutor = executor;
	}

	public static void clearServices() {
		userCache = null;
		sessionService = null;
		currentExecutor = null;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (this.owner != null) {
			NbtCompound nbtCompound = new NbtCompound();
			NbtHelper.writeGameProfile(nbtCompound, this.owner);
			nbt.put("SkullOwner", nbtCompound);
		}

		if (this.noteBlockSound != null) {
			nbt.putString("note_block_sound", this.noteBlockSound.toString());
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
			this.setOwner(NbtHelper.toGameProfile(nbt.getCompound("SkullOwner")));
		} else if (nbt.contains("ExtraType", NbtElement.STRING_TYPE)) {
			String string = nbt.getString("ExtraType");
			if (!StringHelper.isEmpty(string)) {
				this.setOwner(new GameProfile(Util.NIL_UUID, string));
			}
		}

		if (nbt.contains("note_block_sound", NbtElement.STRING_TYPE)) {
			this.noteBlockSound = Identifier.tryParse(nbt.getString("note_block_sound"));
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
		if (world.isReceivingRedstonePower(pos)) {
			blockEntity.powered = true;
			blockEntity.poweredTicks++;
		} else {
			blockEntity.powered = false;
		}
	}

	public float getPoweredTicks(float tickDelta) {
		return this.powered ? (float)this.poweredTicks + tickDelta : (float)this.poweredTicks;
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

	public void setOwner(@Nullable GameProfile owner) {
		synchronized (this) {
			this.owner = owner;
		}

		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		if (this.owner != null && !Util.isBlank(this.owner.getName()) && !hasTextures(this.owner)) {
			fetchProfile(this.owner.getName()).thenAcceptAsync(profile -> {
				this.owner = (GameProfile)profile.orElse(this.owner);
				this.markDirty();
			}, EXECUTOR);
		} else {
			this.markDirty();
		}
	}

	@Nullable
	public static GameProfile getProfile(NbtCompound nbt) {
		if (nbt.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
			return NbtHelper.toGameProfile(nbt.getCompound("SkullOwner"));
		} else {
			if (nbt.contains("SkullOwner", NbtElement.STRING_TYPE)) {
				String string = nbt.getString("SkullOwner");
				if (!Util.isBlank(string)) {
					nbt.remove("SkullOwner");
					fillSkullOwner(nbt, string);
				}
			}

			return null;
		}
	}

	public static void fillSkullOwner(NbtCompound nbt) {
		String string = nbt.getString("SkullOwner");
		if (!Util.isBlank(string)) {
			fillSkullOwner(nbt, string);
		}
	}

	private static void fillSkullOwner(NbtCompound nbt, String name) {
		fetchProfile(name)
			.thenAccept(
				profile -> nbt.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), (GameProfile)profile.orElse(new GameProfile(Util.NIL_UUID, name))))
			);
	}

	private static CompletableFuture<Optional<GameProfile>> fetchProfile(String name) {
		UserCache userCache = SkullBlockEntity.userCache;
		return userCache == null
			? CompletableFuture.completedFuture(Optional.empty())
			: userCache.findByNameAsync(name)
				.thenCompose(profile -> profile.isPresent() ? fetchProfileWithTextures((GameProfile)profile.get()) : CompletableFuture.completedFuture(Optional.empty()))
				.thenApplyAsync(profile -> {
					UserCache userCachex = SkullBlockEntity.userCache;
					if (userCachex != null) {
						profile.ifPresent(userCachex::add);
						return profile;
					} else {
						return Optional.empty();
					}
				}, EXECUTOR);
	}

	private static CompletableFuture<Optional<GameProfile>> fetchProfileWithTextures(GameProfile profile) {
		return hasTextures(profile) ? CompletableFuture.completedFuture(Optional.of(profile)) : CompletableFuture.supplyAsync(() -> {
			MinecraftSessionService minecraftSessionService = sessionService;
			if (minecraftSessionService != null) {
				GameProfile gameProfile2 = minecraftSessionService.fetchProfile(profile.getId(), true);
				return Optional.of((GameProfile)Objects.requireNonNullElse(gameProfile2, profile));
			} else {
				return Optional.empty();
			}
		}, Util.getMainWorkerExecutor());
	}

	private static boolean hasTextures(GameProfile profile) {
		return profile.getProperties().containsKey("textures");
	}
}
