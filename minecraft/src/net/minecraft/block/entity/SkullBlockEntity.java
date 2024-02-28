package net.minecraft.block.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SkullBlockEntity extends BlockEntity {
	private static final String SKULL_OWNER_KEY = "SkullOwner";
	private static final String NOTE_BLOCK_SOUND_KEY = "note_block_sound";
	@Nullable
	private static Executor currentExecutor;
	@Nullable
	private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> userCache;
	public static final Executor EXECUTOR = runnable -> {
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
		currentExecutor = executor;
		final BooleanSupplier booleanSupplier = () -> userCache == null;
		userCache = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofMinutes(10L))
			.maximumSize(256L)
			.build(
				new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>() {
					public CompletableFuture<Optional<GameProfile>> load(String string) {
						return booleanSupplier.getAsBoolean()
							? CompletableFuture.completedFuture(Optional.empty())
							: SkullBlockEntity.fetchProfile(string, apiServices, booleanSupplier);
					}
				}
			);
	}

	public static void clearServices() {
		currentExecutor = null;
		userCache = null;
	}

	static CompletableFuture<Optional<GameProfile>> fetchProfile(String name, ApiServices apiServices, BooleanSupplier missingUserCache) {
		return apiServices.userCache().findByNameAsync(name).thenApplyAsync(profile -> {
			if (profile.isPresent() && !missingUserCache.getAsBoolean()) {
				UUID uUID = ((GameProfile)profile.get()).getId();
				ProfileResult profileResult = apiServices.sessionService().fetchProfile(uUID, true);
				return profileResult != null ? Optional.ofNullable(profileResult.profile()) : profile;
			} else {
				return Optional.empty();
			}
		}, Util.getMainWorkerExecutor());
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
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
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
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
		if (state.contains(SkullBlock.POWERED) && (Boolean)state.get(SkullBlock.POWERED)) {
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
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createNbt(registryLookup);
	}

	public void setOwner(@Nullable GameProfile owner) {
		synchronized (this) {
			this.owner = owner;
		}

		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		if (this.owner != null && !StringHelper.isBlank(this.owner.getName()) && !hasTextures(this.owner)) {
			fetchProfile(this.owner.getName()).thenAcceptAsync(profile -> {
				this.owner = (GameProfile)profile.orElse(this.owner);
				this.markDirty();
			}, EXECUTOR);
		} else {
			this.markDirty();
		}
	}

	public static CompletableFuture<Optional<GameProfile>> fetchProfile(String name) {
		LoadingCache<String, CompletableFuture<Optional<GameProfile>>> loadingCache = userCache;
		return loadingCache != null && StringHelper.isValidPlayerName(name) ? loadingCache.getUnchecked(name) : CompletableFuture.completedFuture(Optional.empty());
	}

	private static boolean hasTextures(GameProfile profile) {
		return profile.getProperties().containsKey("textures");
	}

	@Override
	public void readComponents(ComponentMap components) {
		ProfileComponent profileComponent = components.get(DataComponentTypes.PROFILE);
		this.setOwner(profileComponent != null ? profileComponent.gameProfile() : null);
		this.noteBlockSound = components.get(DataComponentTypes.NOTE_BLOCK_SOUND);
	}

	@Override
	public void addComponents(ComponentMap.Builder componentMapBuilder) {
		if (this.owner != null) {
			componentMapBuilder.add(DataComponentTypes.PROFILE, new ProfileComponent(this.owner));
		}

		componentMapBuilder.add(DataComponentTypes.NOTE_BLOCK_SOUND, this.noteBlockSound);
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		super.removeFromCopiedStackNbt(nbt);
		nbt.remove("SkullOwner");
		nbt.remove("note_block_sound");
	}
}
