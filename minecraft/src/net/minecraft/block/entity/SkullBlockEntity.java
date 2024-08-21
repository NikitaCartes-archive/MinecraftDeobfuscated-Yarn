package net.minecraft.block.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
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
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class SkullBlockEntity extends BlockEntity {
	private static final String PROFILE_NBT_KEY = "profile";
	private static final String NOTE_BLOCK_SOUND_NBT_KEY = "note_block_sound";
	private static final String CUSTOM_NAME_NBT_KEY = "custom_name";
	private static final Logger LOGGER = LogUtils.getLogger();
	@Nullable
	private static Executor currentExecutor;
	@Nullable
	private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> nameToProfileCache;
	@Nullable
	private static LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> uuidToProfileCache;
	public static final Executor EXECUTOR = runnable -> {
		Executor executor = currentExecutor;
		if (executor != null) {
			executor.execute(runnable);
		}
	};
	@Nullable
	private ProfileComponent owner;
	@Nullable
	private Identifier noteBlockSound;
	private int poweredTicks;
	private boolean powered;
	@Nullable
	private Text customName;

	public SkullBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SKULL, pos, state);
	}

	public static void setServices(ApiServices apiServices, Executor executor) {
		currentExecutor = executor;
		final BooleanSupplier booleanSupplier = () -> uuidToProfileCache == null;
		nameToProfileCache = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofMinutes(10L))
			.maximumSize(256L)
			.build(new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>() {
				public CompletableFuture<Optional<GameProfile>> load(String string) {
					return SkullBlockEntity.fetchProfileByName(string, apiServices);
				}
			});
		uuidToProfileCache = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofMinutes(10L))
			.maximumSize(256L)
			.build(new CacheLoader<UUID, CompletableFuture<Optional<GameProfile>>>() {
				public CompletableFuture<Optional<GameProfile>> load(UUID uUID) {
					return SkullBlockEntity.fetchProfileByUuid(uUID, apiServices, booleanSupplier);
				}
			});
	}

	static CompletableFuture<Optional<GameProfile>> fetchProfileByName(String name, ApiServices apiServices) {
		return apiServices.userCache()
			.findByNameAsync(name)
			.thenCompose(
				optional -> {
					LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingCache = uuidToProfileCache;
					return loadingCache != null && !optional.isEmpty()
						? loadingCache.getUnchecked(((GameProfile)optional.get()).getId()).thenApply(optional2 -> optional2.or(() -> optional))
						: CompletableFuture.completedFuture(Optional.empty());
				}
			);
	}

	static CompletableFuture<Optional<GameProfile>> fetchProfileByUuid(UUID uuid, ApiServices apiServices, BooleanSupplier booleanSupplier) {
		return CompletableFuture.supplyAsync(() -> {
			if (booleanSupplier.getAsBoolean()) {
				return Optional.empty();
			} else {
				ProfileResult profileResult = apiServices.sessionService().fetchProfile(uuid, true);
				return Optional.ofNullable(profileResult).map(ProfileResult::profile);
			}
		}, Util.getMainWorkerExecutor());
	}

	public static void clearServices() {
		currentExecutor = null;
		nameToProfileCache = null;
		uuidToProfileCache = null;
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		if (this.owner != null) {
			nbt.put("profile", ProfileComponent.CODEC.encodeStart(NbtOps.INSTANCE, this.owner).getOrThrow());
		}

		if (this.noteBlockSound != null) {
			nbt.putString("note_block_sound", this.noteBlockSound.toString());
		}

		if (this.customName != null) {
			nbt.putString("custom_name", Text.Serialization.toJsonString(this.customName, registries));
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		if (nbt.contains("profile")) {
			ProfileComponent.CODEC
				.parse(NbtOps.INSTANCE, nbt.get("profile"))
				.resultOrPartial(string -> LOGGER.error("Failed to load profile from player head: {}", string))
				.ifPresent(this::setOwner);
		}

		if (nbt.contains("note_block_sound", NbtElement.STRING_TYPE)) {
			this.noteBlockSound = Identifier.tryParse(nbt.getString("note_block_sound"));
		}

		if (nbt.contains("custom_name", NbtElement.STRING_TYPE)) {
			this.customName = tryParseCustomName(nbt.getString("custom_name"), registries);
		} else {
			this.customName = null;
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
	public ProfileComponent getOwner() {
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
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
		return this.createComponentlessNbt(registries);
	}

	public void setOwner(@Nullable ProfileComponent profile) {
		synchronized (this) {
			this.owner = profile;
		}

		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		if (this.owner != null && !this.owner.isCompleted()) {
			this.owner.getFuture().thenAcceptAsync(owner -> {
				this.owner = owner;
				this.markDirty();
			}, EXECUTOR);
		} else {
			this.markDirty();
		}
	}

	public static CompletableFuture<Optional<GameProfile>> fetchProfileByName(String name) {
		LoadingCache<String, CompletableFuture<Optional<GameProfile>>> loadingCache = nameToProfileCache;
		return loadingCache != null && StringHelper.isValidPlayerName(name) ? loadingCache.getUnchecked(name) : CompletableFuture.completedFuture(Optional.empty());
	}

	public static CompletableFuture<Optional<GameProfile>> fetchProfileByUuid(UUID uuid) {
		LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingCache = uuidToProfileCache;
		return loadingCache != null ? loadingCache.getUnchecked(uuid) : CompletableFuture.completedFuture(Optional.empty());
	}

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
		super.readComponents(components);
		this.setOwner(components.get(DataComponentTypes.PROFILE));
		this.noteBlockSound = components.get(DataComponentTypes.NOTE_BLOCK_SOUND);
		this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
	}

	@Override
	protected void addComponents(ComponentMap.Builder builder) {
		super.addComponents(builder);
		builder.add(DataComponentTypes.PROFILE, this.owner);
		builder.add(DataComponentTypes.NOTE_BLOCK_SOUND, this.noteBlockSound);
		builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		super.removeFromCopiedStackNbt(nbt);
		nbt.remove("profile");
		nbt.remove("note_block_sound");
		nbt.remove("custom_name");
	}
}
