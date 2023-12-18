package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.AcknowledgmentValidator;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageChainTaskQueue;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSignatureStorage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.slf4j.Logger;

public class ServerPlayNetworkHandler
	extends ServerCommonNetworkHandler
	implements ServerPlayPacketListener,
	PlayerAssociatedNetworkHandler,
	TickablePacketListener {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int DEFAULT_SEQUENCE = -1;
	private static final int MAX_PENDING_ACKNOWLEDGMENTS = 4096;
	private static final Text CHAT_VALIDATION_FAILED_TEXT = Text.translatable("multiplayer.disconnect.chat_validation_failed");
	public ServerPlayerEntity player;
	public final ChunkDataSender chunkDataSender;
	private int ticks;
	private int sequence = -1;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private double lastTickX;
	private double lastTickY;
	private double lastTickZ;
	private double updatedX;
	private double updatedY;
	private double updatedZ;
	@Nullable
	private Entity topmostRiddenEntity;
	private double lastTickRiddenX;
	private double lastTickRiddenY;
	private double lastTickRiddenZ;
	private double updatedRiddenX;
	private double updatedRiddenY;
	private double updatedRiddenZ;
	@Nullable
	private Vec3d requestedTeleportPos;
	private int requestedTeleportId;
	private int teleportRequestTick;
	private boolean floating;
	private int floatingTicks;
	private boolean vehicleFloating;
	private int vehicleFloatingTicks;
	private int movePacketsCount;
	private int lastTickMovePacketsCount;
	@Nullable
	private PublicPlayerSession session;
	private MessageChain.Unpacker messageUnpacker;
	private final AcknowledgmentValidator acknowledgmentValidator = new AcknowledgmentValidator(20);
	private final MessageSignatureStorage signatureStorage = MessageSignatureStorage.create();
	private final MessageChainTaskQueue messageChainTaskQueue;
	private boolean requestedReconfiguration;

	public ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
		super(server, connection, clientData);
		this.chunkDataSender = new ChunkDataSender(connection.isLocal());
		connection.setPacketListener(this);
		this.player = player;
		player.networkHandler = this;
		player.getTextStream().onConnect();
		this.messageUnpacker = MessageChain.Unpacker.unsigned(player.getUuid(), server::shouldEnforceSecureProfile);
		this.messageChainTaskQueue = new MessageChainTaskQueue(server);
	}

	@Override
	public void tick() {
		if (this.sequence > -1) {
			this.sendPacket(new PlayerActionResponseS2CPacket(this.sequence));
			this.sequence = -1;
		}

		this.syncWithPlayerPosition();
		this.player.prevX = this.player.getX();
		this.player.prevY = this.player.getY();
		this.player.prevZ = this.player.getZ();
		this.player.playerTick();
		this.player.updatePositionAndAngles(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.getYaw(), this.player.getPitch());
		this.ticks++;
		this.lastTickMovePacketsCount = this.movePacketsCount;
		if (this.floating && !this.player.isSleeping() && !this.player.hasVehicle() && !this.player.isDead()) {
			if (++this.floatingTicks > 80) {
				LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
				this.disconnect(Text.translatable("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.floating = false;
			this.floatingTicks = 0;
		}

		this.topmostRiddenEntity = this.player.getRootVehicle();
		if (this.topmostRiddenEntity != this.player && this.topmostRiddenEntity.getControllingPassenger() == this.player) {
			this.lastTickRiddenX = this.topmostRiddenEntity.getX();
			this.lastTickRiddenY = this.topmostRiddenEntity.getY();
			this.lastTickRiddenZ = this.topmostRiddenEntity.getZ();
			this.updatedRiddenX = this.topmostRiddenEntity.getX();
			this.updatedRiddenY = this.topmostRiddenEntity.getY();
			this.updatedRiddenZ = this.topmostRiddenEntity.getZ();
			if (this.vehicleFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
				if (++this.vehicleFloatingTicks > 80) {
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
					this.disconnect(Text.translatable("multiplayer.disconnect.flying"));
					return;
				}
			} else {
				this.vehicleFloating = false;
				this.vehicleFloatingTicks = 0;
			}
		} else {
			this.topmostRiddenEntity = null;
			this.vehicleFloating = false;
			this.vehicleFloatingTicks = 0;
		}

		this.baseTick();
		if (this.messageCooldown > 0) {
			this.messageCooldown--;
		}

		if (this.creativeItemDropThreshold > 0) {
			this.creativeItemDropThreshold--;
		}

		if (this.player.getLastActionTime() > 0L
			&& this.server.getPlayerIdleTimeout() > 0
			&& Util.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)this.server.getPlayerIdleTimeout() * 1000L * 60L) {
			this.disconnect(Text.translatable("multiplayer.disconnect.idling"));
		}
	}

	public void syncWithPlayerPosition() {
		this.lastTickX = this.player.getX();
		this.lastTickY = this.player.getY();
		this.lastTickZ = this.player.getZ();
		this.updatedX = this.player.getX();
		this.updatedY = this.player.getY();
		this.updatedZ = this.player.getZ();
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen() && !this.requestedReconfiguration;
	}

	@Override
	public boolean accepts(Packet<?> packet) {
		return super.accepts(packet) ? true : this.requestedReconfiguration && this.connection.isOpen() && packet instanceof AcknowledgeReconfigurationC2SPacket;
	}

	@Override
	protected GameProfile getProfile() {
		return this.player.getGameProfile();
	}

	private <T, R> CompletableFuture<R> filterText(T text, BiFunction<TextStream, T, CompletableFuture<R>> filterer) {
		return ((CompletableFuture)filterer.apply(this.player.getTextStream(), text)).thenApply(filtered -> {
			if (!this.isConnectionOpen()) {
				LOGGER.debug("Ignoring packet due to disconnection");
				throw new CancellationException("disconnected");
			} else {
				return filtered;
			}
		});
	}

	private CompletableFuture<FilteredMessage> filterText(String text) {
		return this.filterText(text, TextStream::filterText);
	}

	private CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts) {
		return this.filterText(texts, TextStream::filterTexts);
	}

	@Override
	public void onPlayerInput(PlayerInputC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateInput(packet.getSideways(), packet.getForward(), packet.isJumping(), packet.isSneaking());
	}

	/**
	 * {@return whether this movement is invalid}
	 * 
	 * @implNote This method is used to determine
	 * whether players sending {@linkplain PlayerMoveC2SPacket player}
	 * and {@linkplain VehicleMoveC2SPacket vehicle} movement packets
	 * to the server should be kicked.
	 */
	private static boolean isMovementInvalid(double x, double y, double z, float yaw, float pitch) {
		return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || !Floats.isFinite(pitch) || !Floats.isFinite(yaw);
	}

	private static double clampHorizontal(double d) {
		return MathHelper.clamp(d, -3.0E7, 3.0E7);
	}

	private static double clampVertical(double d) {
		return MathHelper.clamp(d, -2.0E7, 2.0E7);
	}

	@Override
	public void onVehicleMove(VehicleMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (isMovementInvalid(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch())) {
			this.disconnect(Text.translatable("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			Entity entity = this.player.getRootVehicle();
			if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.topmostRiddenEntity) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.getX();
				double e = entity.getY();
				double f = entity.getZ();
				double g = clampHorizontal(packet.getX());
				double h = clampVertical(packet.getY());
				double i = clampHorizontal(packet.getZ());
				float j = MathHelper.wrapDegrees(packet.getYaw());
				float k = MathHelper.wrapDegrees(packet.getPitch());
				double l = g - this.lastTickRiddenX;
				double m = h - this.lastTickRiddenY;
				double n = i - this.lastTickRiddenZ;
				double o = entity.getVelocity().lengthSquared();
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && !this.isHost()) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), l, m, n);
					this.sendPacket(new VehicleMoveS2CPacket(entity));
					return;
				}

				boolean bl = serverWorld.isSpaceEmpty(entity, entity.getBoundingBox().contract(0.0625));
				l = g - this.updatedRiddenX;
				m = h - this.updatedRiddenY - 1.0E-6;
				n = i - this.updatedRiddenZ;
				boolean bl2 = entity.groundCollision;
				if (entity instanceof LivingEntity livingEntity && livingEntity.isClimbing()) {
					livingEntity.onLanding();
				}

				entity.move(MovementType.PLAYER, new Vec3d(l, m, n));
				l = g - entity.getX();
				m = h - entity.getY();
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.getZ();
				p = l * l + m * m + n * n;
				boolean bl3 = false;
				if (p > 0.0625) {
					bl3 = true;
					LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", entity.getName().getString(), this.player.getName().getString(), Math.sqrt(p));
				}

				entity.updatePositionAndAngles(g, h, i, j, k);
				boolean bl4 = serverWorld.isSpaceEmpty(entity, entity.getBoundingBox().contract(0.0625));
				if (bl && (bl3 || !bl4)) {
					entity.updatePositionAndAngles(d, e, f, j, k);
					this.sendPacket(new VehicleMoveS2CPacket(entity));
					return;
				}

				this.player.getServerWorld().getChunkManager().updatePosition(this.player);
				this.player.increaseTravelMotionStats(this.player.getX() - d, this.player.getY() - e, this.player.getZ() - f);
				this.vehicleFloating = m >= -0.03125 && !bl2 && !this.server.isFlightEnabled() && !entity.hasNoGravity() && this.isEntityOnAir(entity);
				this.updatedRiddenX = entity.getX();
				this.updatedRiddenY = entity.getY();
				this.updatedRiddenZ = entity.getZ();
			}
		}
	}

	private boolean isEntityOnAir(Entity entity) {
		return entity.getWorld().getStatesInBox(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)).allMatch(AbstractBlock.AbstractBlockState::isAir);
	}

	@Override
	public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getTeleportId() == this.requestedTeleportId) {
			if (this.requestedTeleportPos == null) {
				this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
				return;
			}

			this.player
				.updatePositionAndAngles(
					this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch()
				);
			this.updatedX = this.requestedTeleportPos.x;
			this.updatedY = this.requestedTeleportPos.y;
			this.updatedZ = this.requestedTeleportPos.z;
			if (this.player.isInTeleportationState()) {
				this.player.onTeleportationDone();
			}

			this.requestedTeleportPos = null;
		}
	}

	@Override
	public void onRecipeBookData(RecipeBookDataC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.server.getRecipeManager().get(packet.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
	}

	@Override
	public void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.getRecipeBook().setCategoryOptions(packet.getCategory(), packet.isGuiOpen(), packet.isFilteringCraftable());
	}

	@Override
	public void onAdvancementTab(AdvancementTabC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			Identifier identifier = (Identifier)Objects.requireNonNull(packet.getTabToOpen());
			AdvancementEntry advancementEntry = this.server.getAdvancementLoader().get(identifier);
			if (advancementEntry != null) {
				this.player.getAdvancementTracker().setDisplayTab(advancementEntry);
			}
		}
	}

	@Override
	public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		StringReader stringReader = new StringReader(packet.getPartialCommand());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
		this.server
			.getCommandManager()
			.getDispatcher()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.sendPacket(new CommandSuggestionsS2CPacket(packet.getCompletionId(), suggestions)));
	}

	@Override
	public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendMessage(Text.translatable("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendMessage(Text.translatable("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = packet.getPos();
			BlockEntity blockEntity = this.player.getWorld().getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = packet.getCommand();
			boolean bl = packet.shouldTrackOutput();
			if (commandBlockExecutor != null) {
				CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
				BlockState blockState = this.player.getWorld().getBlockState(blockPos);
				Direction direction = blockState.get(CommandBlock.FACING);

				BlockState blockState3 = (switch (packet.getType()) {
					case SEQUENCE -> Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
					case AUTO -> Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
					default -> Blocks.COMMAND_BLOCK.getDefaultState();
				}).with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(packet.isConditional()));
				if (blockState3 != blockState) {
					this.player.getWorld().setBlockState(blockPos, blockState3, Block.NOTIFY_LISTENERS);
					blockEntity.setCachedState(blockState3);
					this.player.getWorld().getWorldChunk(blockPos).setBlockEntity(blockEntity);
				}

				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.setTrackOutput(bl);
				if (!bl) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockBlockEntity.setAuto(packet.isAlwaysActive());
				if (type != packet.getType()) {
					commandBlockBlockEntity.updateCommandBlock();
				}

				commandBlockExecutor.markDirty();
				if (!StringHelper.isEmpty(string)) {
					this.player.sendMessage(Text.translatable("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendMessage(Text.translatable("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendMessage(Text.translatable("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = packet.getMinecartCommandExecutor(this.player.getWorld());
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(packet.getCommand());
				commandBlockExecutor.setTrackOutput(packet.shouldTrackOutput());
				if (!packet.shouldTrackOutput()) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockExecutor.markDirty();
				this.player.sendMessage(Text.translatable("advMode.setCommand.success", packet.getCommand()));
			}
		}
	}

	@Override
	public void onPickFromInventory(PickFromInventoryC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.getInventory().swapSlotWithHotbar(packet.getSlot());
		this.player
			.networkHandler
			.sendPacket(
				new ScreenHandlerSlotUpdateS2CPacket(
					-2, 0, this.player.getInventory().selectedSlot, this.player.getInventory().getStack(this.player.getInventory().selectedSlot)
				)
			);
		this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, packet.getSlot(), this.player.getInventory().getStack(packet.getSlot())));
		this.player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.player.getInventory().selectedSlot));
	}

	@Override
	public void onRenameItem(RenameItemC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.currentScreenHandler instanceof AnvilScreenHandler anvilScreenHandler) {
			if (!anvilScreenHandler.canUse(this.player)) {
				LOGGER.debug("Player {} interacted with invalid menu {}", this.player, anvilScreenHandler);
				return;
			}

			anvilScreenHandler.setNewItemName(packet.getName());
		}
	}

	@Override
	public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.currentScreenHandler instanceof BeaconScreenHandler beaconScreenHandler) {
			if (!this.player.currentScreenHandler.canUse(this.player)) {
				LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
				return;
			}

			beaconScreenHandler.setEffects(packet.primaryEffectId(), packet.secondaryEffectId());
		}
	}

	@Override
	public void onUpdateStructureBlock(UpdateStructureBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			BlockState blockState = this.player.getWorld().getBlockState(blockPos);
			if (this.player.getWorld().getBlockEntity(blockPos) instanceof StructureBlockBlockEntity structureBlockBlockEntity) {
				structureBlockBlockEntity.setMode(packet.getMode());
				structureBlockBlockEntity.setTemplateName(packet.getTemplateName());
				structureBlockBlockEntity.setOffset(packet.getOffset());
				structureBlockBlockEntity.setSize(packet.getSize());
				structureBlockBlockEntity.setMirror(packet.getMirror());
				structureBlockBlockEntity.setRotation(packet.getRotation());
				structureBlockBlockEntity.setMetadata(packet.getMetadata());
				structureBlockBlockEntity.setIgnoreEntities(packet.shouldIgnoreEntities());
				structureBlockBlockEntity.setShowAir(packet.shouldShowAir());
				structureBlockBlockEntity.setShowBoundingBox(packet.shouldShowBoundingBox());
				structureBlockBlockEntity.setIntegrity(packet.getIntegrity());
				structureBlockBlockEntity.setSeed(packet.getSeed());
				if (structureBlockBlockEntity.hasStructureName()) {
					String string = structureBlockBlockEntity.getTemplateName();
					if (packet.getAction() == StructureBlockBlockEntity.Action.SAVE_AREA) {
						if (structureBlockBlockEntity.saveStructure()) {
							this.player.sendMessage(Text.translatable("structure_block.save_success", string), false);
						} else {
							this.player.sendMessage(Text.translatable("structure_block.save_failure", string), false);
						}
					} else if (packet.getAction() == StructureBlockBlockEntity.Action.LOAD_AREA) {
						if (!structureBlockBlockEntity.isStructureAvailable()) {
							this.player.sendMessage(Text.translatable("structure_block.load_not_found", string), false);
						} else if (structureBlockBlockEntity.loadAndTryPlaceStructure(this.player.getServerWorld())) {
							this.player.sendMessage(Text.translatable("structure_block.load_success", string), false);
						} else {
							this.player.sendMessage(Text.translatable("structure_block.load_prepare", string), false);
						}
					} else if (packet.getAction() == StructureBlockBlockEntity.Action.SCAN_AREA) {
						if (structureBlockBlockEntity.detectStructureSize()) {
							this.player.sendMessage(Text.translatable("structure_block.size_success", string), false);
						} else {
							this.player.sendMessage(Text.translatable("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player.sendMessage(Text.translatable("structure_block.invalid_structure_name", packet.getTemplateName()), false);
				}

				structureBlockBlockEntity.markDirty();
				this.player.getWorld().updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
			}
		}
	}

	@Override
	public void onUpdateJigsaw(UpdateJigsawC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			BlockState blockState = this.player.getWorld().getBlockState(blockPos);
			if (this.player.getWorld().getBlockEntity(blockPos) instanceof JigsawBlockEntity jigsawBlockEntity) {
				jigsawBlockEntity.setName(packet.getName());
				jigsawBlockEntity.setTarget(packet.getTarget());
				jigsawBlockEntity.setPool(RegistryKey.of(RegistryKeys.TEMPLATE_POOL, packet.getPool()));
				jigsawBlockEntity.setFinalState(packet.getFinalState());
				jigsawBlockEntity.setJoint(packet.getJointType());
				jigsawBlockEntity.setPlacementPriority(packet.getPlacementPriority());
				jigsawBlockEntity.setSelectionPriority(packet.getSelectionPriority());
				jigsawBlockEntity.markDirty();
				this.player.getWorld().updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
			}
		}
	}

	@Override
	public void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			if (this.player.getWorld().getBlockEntity(blockPos) instanceof JigsawBlockEntity jigsawBlockEntity) {
				jigsawBlockEntity.generate(this.player.getServerWorld(), packet.getMaxDepth(), packet.shouldKeepJigsaws());
			}
		}
	}

	@Override
	public void onSelectMerchantTrade(SelectMerchantTradeC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		int i = packet.getTradeId();
		if (this.player.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
			if (!merchantScreenHandler.canUse(this.player)) {
				LOGGER.debug("Player {} interacted with invalid menu {}", this.player, merchantScreenHandler);
				return;
			}

			merchantScreenHandler.setRecipeIndex(i);
			merchantScreenHandler.switchTo(i);
		}
	}

	@Override
	public void onBookUpdate(BookUpdateC2SPacket packet) {
		int i = packet.getSlot();
		if (PlayerInventory.isValidHotbarIndex(i) || i == 40) {
			List<String> list = Lists.<String>newArrayList();
			Optional<String> optional = packet.getTitle();
			optional.ifPresent(list::add);
			packet.getPages().stream().limit(100L).forEach(list::add);
			Consumer<List<FilteredMessage>> consumer = optional.isPresent()
				? texts -> this.addBook((FilteredMessage)texts.get(0), texts.subList(1, texts.size()), i)
				: texts -> this.updateBookContent(texts, i);
			this.filterTexts(list).thenAcceptAsync(consumer, this.server);
		}
	}

	private void updateBookContent(List<FilteredMessage> pages, int slotId) {
		ItemStack itemStack = this.player.getInventory().getStack(slotId);
		if (itemStack.isOf(Items.WRITABLE_BOOK)) {
			this.setTextToBook(pages, UnaryOperator.identity(), itemStack);
		}
	}

	private void addBook(FilteredMessage title, List<FilteredMessage> pages, int slotId) {
		ItemStack itemStack = this.player.getInventory().getStack(slotId);
		if (itemStack.isOf(Items.WRITABLE_BOOK)) {
			ItemStack itemStack2 = new ItemStack(Items.WRITTEN_BOOK);
			NbtCompound nbtCompound = itemStack.getNbt();
			if (nbtCompound != null) {
				itemStack2.setNbt(nbtCompound.copy());
			}

			itemStack2.setSubNbt("author", NbtString.of(this.player.getName().getString()));
			if (this.player.shouldFilterText()) {
				itemStack2.setSubNbt("title", NbtString.of(title.getString()));
			} else {
				itemStack2.setSubNbt("filtered_title", NbtString.of(title.getString()));
				itemStack2.setSubNbt("title", NbtString.of(title.raw()));
			}

			this.setTextToBook(pages, text -> Text.Serialization.toJsonString(Text.literal(text)), itemStack2);
			this.player.getInventory().setStack(slotId, itemStack2);
		}
	}

	private void setTextToBook(List<FilteredMessage> messages, UnaryOperator<String> postProcessor, ItemStack book) {
		NbtList nbtList = new NbtList();
		if (this.player.shouldFilterText()) {
			messages.stream().map(message -> NbtString.of((String)postProcessor.apply(message.getString()))).forEach(nbtList::add);
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			int i = 0;

			for (int j = messages.size(); i < j; i++) {
				FilteredMessage filteredMessage = (FilteredMessage)messages.get(i);
				String string = filteredMessage.raw();
				nbtList.add(NbtString.of((String)postProcessor.apply(string)));
				if (filteredMessage.isFiltered()) {
					nbtCompound.putString(String.valueOf(i), (String)postProcessor.apply(filteredMessage.getString()));
				}
			}

			if (!nbtCompound.isEmpty()) {
				book.setSubNbt("filtered_pages", nbtCompound);
			}
		}

		book.setSubNbt("pages", nbtList);
	}

	@Override
	public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2)) {
			Entity entity = this.player.getWorld().getEntityById(packet.getEntityId());
			if (entity != null) {
				NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
				this.player.networkHandler.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
			}
		}
	}

	@Override
	public void onSlotChangedState(SlotChangedStateC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (!this.player.isSpectator() && packet.screenHandlerId() == this.player.currentScreenHandler.syncId) {
			if (this.player.currentScreenHandler instanceof CrafterScreenHandler crafterScreenHandler
				&& crafterScreenHandler.getInputInventory() instanceof CrafterBlockEntity crafterBlockEntity) {
				crafterBlockEntity.setSlotEnabled(packet.slotId(), packet.newState());
			}
		}
	}

	@Override
	public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2)) {
			BlockEntity blockEntity = this.player.getWorld().getBlockEntity(packet.getPos());
			NbtCompound nbtCompound = blockEntity != null ? blockEntity.createNbt() : null;
			this.player.networkHandler.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (isMovementInvalid(packet.getX(0.0), packet.getY(0.0), packet.getZ(0.0), packet.getYaw(0.0F), packet.getPitch(0.0F))) {
			this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.player.getServerWorld();
			if (!this.player.notInAnyWorld) {
				if (this.ticks == 0) {
					this.syncWithPlayerPosition();
				}

				if (this.requestedTeleportPos != null) {
					if (this.ticks - this.teleportRequestTick > 20) {
						this.teleportRequestTick = this.ticks;
						this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
					}
				} else {
					this.teleportRequestTick = this.ticks;
					double d = clampHorizontal(packet.getX(this.player.getX()));
					double e = clampVertical(packet.getY(this.player.getY()));
					double f = clampHorizontal(packet.getZ(this.player.getZ()));
					float g = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
					float h = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));
					if (this.player.hasVehicle()) {
						this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
						this.player.getServerWorld().getChunkManager().updatePosition(this.player);
					} else {
						double i = this.player.getX();
						double j = this.player.getY();
						double k = this.player.getZ();
						double l = d - this.lastTickX;
						double m = e - this.lastTickY;
						double n = f - this.lastTickZ;
						double o = this.player.getVelocity().lengthSquared();
						double p = l * l + m * m + n * n;
						if (this.player.isSleeping()) {
							if (p > 1.0) {
								this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
							}
						} else {
							if (serverWorld.getTickManager().shouldTick()) {
								this.movePacketsCount++;
								int q = this.movePacketsCount - this.lastTickMovePacketsCount;
								if (q > 5) {
									LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), q);
									q = 1;
								}

								if (!this.player.isInTeleportationState()
									&& (!this.player.getWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
									float r = this.player.isFallFlying() ? 300.0F : 100.0F;
									if (p - o > (double)(r * (float)q) && !this.isHost()) {
										LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), l, m, n);
										this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
										return;
									}
								}
							}

							Box box = this.player.getBoundingBox();
							l = d - this.updatedX;
							m = e - this.updatedY;
							n = f - this.updatedZ;
							boolean bl = m > 0.0;
							if (this.player.isOnGround() && !packet.isOnGround() && bl) {
								this.player.jump();
							}

							boolean bl2 = this.player.groundCollision;
							this.player.move(MovementType.PLAYER, new Vec3d(l, m, n));
							l = d - this.player.getX();
							m = e - this.player.getY();
							if (m > -0.5 || m < 0.5) {
								m = 0.0;
							}

							n = f - this.player.getZ();
							p = l * l + m * m + n * n;
							boolean bl3 = false;
							if (!this.player.isInTeleportationState()
								&& p > 0.0625
								&& !this.player.isSleeping()
								&& !this.player.interactionManager.isCreative()
								&& this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
								bl3 = true;
								LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
							}

							if (this.player.noClip
								|| this.player.isSleeping()
								|| (!bl3 || !serverWorld.isSpaceEmpty(this.player, box)) && !this.isPlayerNotCollidingWithBlocks(serverWorld, box, d, e, f)) {
								this.player.updatePositionAndAngles(d, e, f, g, h);
								this.floating = m >= -0.03125
									&& !bl2
									&& this.player.interactionManager.getGameMode() != GameMode.SPECTATOR
									&& !this.server.isFlightEnabled()
									&& !this.player.getAbilities().allowFlying
									&& !this.player.hasStatusEffect(StatusEffects.LEVITATION)
									&& !this.player.isFallFlying()
									&& !this.player.isUsingRiptide()
									&& this.isEntityOnAir(this.player);
								this.player.getServerWorld().getChunkManager().updatePosition(this.player);
								this.player.handleFall(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k, packet.isOnGround());
								this.player.setOnGround(packet.isOnGround(), new Vec3d(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k));
								if (bl) {
									this.player.onLanding();
								}

								this.player.increaseTravelMotionStats(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k);
								this.updatedX = this.player.getX();
								this.updatedY = this.player.getY();
								this.updatedZ = this.player.getZ();
							} else {
								this.requestTeleport(i, j, k, g, h);
								this.player.handleFall(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k, packet.isOnGround());
							}
						}
					}
				}
			}
		}
	}

	private boolean isPlayerNotCollidingWithBlocks(WorldView world, Box box, double newX, double newY, double newZ) {
		Box box2 = this.player.getBoundingBox().offset(newX - this.player.getX(), newY - this.player.getY(), newZ - this.player.getZ());
		Iterable<VoxelShape> iterable = world.getCollisions(this.player, box2.contract(1.0E-5F));
		VoxelShape voxelShape = VoxelShapes.cuboid(box.contract(1.0E-5F));

		for (VoxelShape voxelShape2 : iterable) {
			if (!VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) {
				return true;
			}
		}

		return false;
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
		this.requestTeleport(x, y, z, yaw, pitch, Collections.emptySet());
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<PositionFlag> flags) {
		double d = flags.contains(PositionFlag.X) ? this.player.getX() : 0.0;
		double e = flags.contains(PositionFlag.Y) ? this.player.getY() : 0.0;
		double f = flags.contains(PositionFlag.Z) ? this.player.getZ() : 0.0;
		float g = flags.contains(PositionFlag.Y_ROT) ? this.player.getYaw() : 0.0F;
		float h = flags.contains(PositionFlag.X_ROT) ? this.player.getPitch() : 0.0F;
		this.requestedTeleportPos = new Vec3d(x, y, z);
		if (++this.requestedTeleportId == Integer.MAX_VALUE) {
			this.requestedTeleportId = 0;
		}

		this.teleportRequestTick = this.ticks;
		this.player.updatePositionAndAngles(x, y, z, yaw, pitch);
		this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x - d, y - e, z - f, yaw - g, pitch - h, flags, this.requestedTeleportId));
	}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		BlockPos blockPos = packet.getPos();
		this.player.updateLastActionTime();
		PlayerActionC2SPacket.Action action = packet.getAction();
		switch (action) {
			case SWAP_ITEM_WITH_OFFHAND:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.getStackInHand(Hand.OFF_HAND);
					this.player.setStackInHand(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
					this.player.setStackInHand(Hand.MAIN_HAND, itemStack);
					this.player.clearActiveItem();
				}

				return;
			case DROP_ITEM:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(false);
				}

				return;
			case DROP_ALL_ITEMS:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(true);
				}

				return;
			case RELEASE_USE_ITEM:
				this.player.stopUsingItem();
				return;
			case START_DESTROY_BLOCK:
			case ABORT_DESTROY_BLOCK:
			case STOP_DESTROY_BLOCK:
				this.player.interactionManager.processBlockBreakingAction(blockPos, action, packet.getDirection(), this.player.getWorld().getTopY(), packet.getSequence());
				this.player.networkHandler.updateSequence(packet.getSequence());
				return;
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	/**
	 * Checks if a player can place a block or fluid from a bucket.
	 * 
	 * <p>For this to return true, the player must not be actively cooling down.
	 */
	private static boolean canPlace(ServerPlayerEntity player, ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		} else {
			Item item = stack.getItem();
			return (item instanceof BlockItem || item instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(item);
		}
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.networkHandler.updateSequence(packet.getSequence());
		ServerWorld serverWorld = this.player.getServerWorld();
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		if (itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
			BlockHitResult blockHitResult = packet.getBlockHitResult();
			Vec3d vec3d = blockHitResult.getPos();
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (this.player.method_55632(blockPos)) {
				Vec3d vec3d2 = vec3d.subtract(Vec3d.ofCenter(blockPos));
				double d = 1.0000001;
				if (Math.abs(vec3d2.getX()) < 1.0000001 && Math.abs(vec3d2.getY()) < 1.0000001 && Math.abs(vec3d2.getZ()) < 1.0000001) {
					Direction direction = blockHitResult.getSide();
					this.player.updateLastActionTime();
					int i = this.player.getWorld().getTopY();
					if (blockPos.getY() < i) {
						if (this.requestedTeleportPos == null && serverWorld.canPlayerModifyAt(this.player, blockPos)) {
							ActionResult actionResult = this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
							if (direction == Direction.UP && !actionResult.isAccepted() && blockPos.getY() >= i - 1 && canPlace(this.player, itemStack)) {
								Text text = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
								this.player.sendMessageToClient(text, true);
							} else if (actionResult.shouldSwingHand()) {
								this.player.swingHand(hand, true);
							}
						}
					} else {
						Text text2 = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
						this.player.sendMessageToClient(text2, true);
					}

					this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
					this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
				} else {
					LOGGER.warn("Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.", this.player.getGameProfile().getName(), vec3d, blockPos);
				}
			}
		}
	}

	@Override
	public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.updateSequence(packet.getSequence());
		ServerWorld serverWorld = this.player.getServerWorld();
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.updateLastActionTime();
		if (!itemStack.isEmpty() && itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
			ActionResult actionResult = this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
			if (actionResult.shouldSwingHand()) {
				this.player.swingHand(hand, true);
			}
		}
	}

	@Override
	public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			for (ServerWorld serverWorld : this.server.getWorlds()) {
				Entity entity = packet.getTarget(serverWorld);
				if (entity != null) {
					this.player.teleport(serverWorld, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
					return;
				}
			}
		}
	}

	@Override
	public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.getControllingVehicle() instanceof BoatEntity boatEntity) {
			boatEntity.setPaddleMovings(packet.isLeftPaddling(), packet.isRightPaddling());
		}
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
		this.cleanUp();
		super.onDisconnected(reason);
	}

	private void cleanUp() {
		this.messageChainTaskQueue.close();
		this.server.forcePlayerSampleUpdate();
		this.server.getPlayerManager().broadcast(Text.translatable("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW), false);
		this.player.onDisconnect();
		this.server.getPlayerManager().remove(this.player);
		this.player.getTextStream().onDisconnect();
	}

	public void updateSequence(int sequence) {
		if (sequence < 0) {
			throw new IllegalArgumentException("Expected packet sequence nr >= 0");
		} else {
			this.sequence = Math.max(sequence, this.sequence);
		}
	}

	@Override
	public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getSelectedSlot() >= 0 && packet.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
			if (this.player.getInventory().selectedSlot != packet.getSelectedSlot() && this.player.getActiveHand() == Hand.MAIN_HAND) {
				this.player.clearActiveItem();
			}

			this.player.getInventory().selectedSlot = packet.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
		}
	}

	@Override
	public void onChatMessage(ChatMessageC2SPacket packet) {
		if (hasIllegalCharacter(packet.chatMessage())) {
			this.disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
		} else {
			Optional<LastSeenMessageList> optional = this.validateMessage(packet.acknowledgment());
			if (optional.isPresent()) {
				this.server.submit(() -> {
					SignedMessage signedMessage;
					try {
						signedMessage = this.getSignedMessage(packet, (LastSeenMessageList)optional.get());
					} catch (MessageChain.MessageChainException var6) {
						this.handleMessageChainException(var6);
						return;
					}

					CompletableFuture<FilteredMessage> completableFuture = this.filterText(signedMessage.getSignedContent());
					Text text = this.server.getMessageDecorator().decorate(this.player, signedMessage.getContent());
					this.messageChainTaskQueue.append(completableFuture, filtered -> {
						SignedMessage signedMessage2 = signedMessage.withUnsignedContent(text).withFilterMask(filtered.mask());
						this.handleDecoratedMessage(signedMessage2);
					});
				});
			}
		}
	}

	@Override
	public void onCommandExecution(CommandExecutionC2SPacket packet) {
		if (hasIllegalCharacter(packet.command())) {
			this.disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
		} else {
			Optional<LastSeenMessageList> optional = this.validateMessage(packet.acknowledgment());
			if (optional.isPresent()) {
				this.server.submit(() -> {
					this.handleCommandExecution(packet, (LastSeenMessageList)optional.get());
					this.checkForSpam();
				});
			}
		}
	}

	private void handleCommandExecution(CommandExecutionC2SPacket packet, LastSeenMessageList lastSeenMessages) {
		ParseResults<ServerCommandSource> parseResults = this.parse(packet.command());

		Map<String, SignedMessage> map;
		try {
			map = this.collectArgumentMessages(packet, SignedArgumentList.of(parseResults), lastSeenMessages);
		} catch (MessageChain.MessageChainException var6) {
			this.handleMessageChainException(var6);
			return;
		}

		SignedCommandArguments signedCommandArguments = new SignedCommandArguments.Impl(map);
		parseResults = CommandManager.withCommandSource(parseResults, source -> source.withSignedArguments(signedCommandArguments, this.messageChainTaskQueue));
		this.server.getCommandManager().execute(parseResults, packet.command());
	}

	private void handleMessageChainException(MessageChain.MessageChainException exception) {
		LOGGER.warn("Failed to update secure chat state for {}: '{}'", this.player.getGameProfile().getName(), exception.getMessageText().getString());
		if (exception.shouldDisconnect()) {
			this.disconnect(exception.getMessageText());
		} else {
			this.player.sendMessage(exception.getMessageText().copy().formatted(Formatting.RED));
		}
	}

	/**
	 * {@return a map of argument name and value as signed messages}
	 */
	private Map<String, SignedMessage> collectArgumentMessages(
		CommandExecutionC2SPacket packet, SignedArgumentList<?> arguments, LastSeenMessageList lastSeenMessages
	) throws MessageChain.MessageChainException {
		Map<String, SignedMessage> map = new Object2ObjectOpenHashMap<>();

		for (SignedArgumentList.ParsedArgument<?> parsedArgument : arguments.arguments()) {
			MessageSignatureData messageSignatureData = packet.argumentSignatures().get(parsedArgument.getNodeName());
			MessageBody messageBody = new MessageBody(parsedArgument.value(), packet.timestamp(), packet.salt(), lastSeenMessages);
			map.put(parsedArgument.getNodeName(), this.messageUnpacker.unpack(messageSignatureData, messageBody));
		}

		return map;
	}

	/**
	 * {@return the result of parsing {@code command}}
	 * 
	 * @param command the command to parse (without the leading slash)
	 */
	private ParseResults<ServerCommandSource> parse(String command) {
		CommandDispatcher<ServerCommandSource> commandDispatcher = this.server.getCommandManager().getDispatcher();
		return commandDispatcher.parse(command, this.player.getCommandSource());
	}

	/**
	 * {@return the validated acknowledgment if the message is valid, or an empty optional
	 * if it is not}
	 * 
	 * <p>This disconnects the player if the message arrives in
	 * improper order or if chat is disabled.
	 */
	private Optional<LastSeenMessageList> validateMessage(LastSeenMessageList.Acknowledgment acknowledgment) {
		Optional<LastSeenMessageList> optional = this.validateAcknowledgment(acknowledgment);
		if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
			this.sendPacket(new GameMessageS2CPacket(Text.translatable("chat.disabled.options").formatted(Formatting.RED), false));
			return Optional.empty();
		} else {
			this.player.updateLastActionTime();
			return optional;
		}
	}

	private Optional<LastSeenMessageList> validateAcknowledgment(LastSeenMessageList.Acknowledgment acknowledgment) {
		synchronized (this.acknowledgmentValidator) {
			Optional<LastSeenMessageList> optional = this.acknowledgmentValidator.validate(acknowledgment);
			if (optional.isEmpty()) {
				LOGGER.warn("Failed to validate message acknowledgements from {}", this.player.getName().getString());
				this.disconnect(CHAT_VALIDATION_FAILED_TEXT);
			}

			return optional;
		}
	}

	/**
	 * {@return whether {@code message} contains an illegal character}
	 * 
	 * @see net.minecraft.SharedConstants#isValidChar(char)
	 */
	private static boolean hasIllegalCharacter(String message) {
		for (int i = 0; i < message.length(); i++) {
			if (!SharedConstants.isValidChar(message.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	private SignedMessage getSignedMessage(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException {
		MessageBody messageBody = new MessageBody(packet.chatMessage(), packet.timestamp(), packet.salt(), lastSeenMessages);
		return this.messageUnpacker.unpack(packet.signature(), messageBody);
	}

	private void handleDecoratedMessage(SignedMessage message) {
		this.server.getPlayerManager().broadcast(message, this.player, MessageType.params(MessageType.CHAT, this.player));
		this.checkForSpam();
	}

	private void checkForSpam() {
		this.messageCooldown += 20;
		if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
			this.disconnect(Text.translatable("disconnect.spam"));
		}
	}

	@Override
	public void onMessageAcknowledgment(MessageAcknowledgmentC2SPacket packet) {
		synchronized (this.acknowledgmentValidator) {
			if (!this.acknowledgmentValidator.removeUntil(packet.offset())) {
				LOGGER.warn("Failed to validate message acknowledgements from {}", this.player.getName().getString());
				this.disconnect(CHAT_VALIDATION_FAILED_TEXT);
			}
		}
	}

	@Override
	public void onHandSwing(HandSwingC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		this.player.swingHand(packet.getHand());
	}

	@Override
	public void onClientCommand(ClientCommandC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		switch (packet.getMode()) {
			case PRESS_SHIFT_KEY:
				this.player.setSneaking(true);
				break;
			case RELEASE_SHIFT_KEY:
				this.player.setSneaking(false);
				break;
			case START_SPRINTING:
				this.player.setSprinting(true);
				break;
			case STOP_SPRINTING:
				this.player.setSprinting(false);
				break;
			case STOP_SLEEPING:
				if (this.player.isSleeping()) {
					this.player.wakeUp(false, true);
					this.requestedTeleportPos = this.player.getPos();
				}
				break;
			case START_RIDING_JUMP:
				if (this.player.getControllingVehicle() instanceof JumpingMount jumpingMount) {
					int i = packet.getMountJumpHeight();
					if (jumpingMount.canJump() && i > 0) {
						jumpingMount.startJumping(i);
					}
				}
				break;
			case STOP_RIDING_JUMP:
				if (this.player.getControllingVehicle() instanceof JumpingMount jumpingMount) {
					jumpingMount.stopJumping();
				}
				break;
			case OPEN_INVENTORY:
				if (this.player.getVehicle() instanceof RideableInventory rideableInventory) {
					rideableInventory.openInventory(this.player);
				}
				break;
			case START_FALL_FLYING:
				if (!this.player.checkFallFlying()) {
					this.player.stopFallFlying();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	public void addPendingAcknowledgment(SignedMessage message) {
		MessageSignatureData messageSignatureData = message.signature();
		if (messageSignatureData != null) {
			this.signatureStorage.add(message.signedBody(), message.signature());
			int i;
			synchronized (this.acknowledgmentValidator) {
				this.acknowledgmentValidator.addPending(messageSignatureData);
				i = this.acknowledgmentValidator.getMessageCount();
			}

			if (i > 4096) {
				this.disconnect(Text.translatable("multiplayer.disconnect.too_many_pending_chats"));
			}
		}
	}

	public void sendChatMessage(SignedMessage message, MessageType.Parameters params) {
		this.sendPacket(
			new ChatMessageS2CPacket(
				message.link().sender(),
				message.link().index(),
				message.signature(),
				message.signedBody().toSerialized(this.signatureStorage),
				message.unsignedContent(),
				message.filterMask(),
				params.toSerialized(this.player.getWorld().getRegistryManager())
			)
		);
		this.addPendingAcknowledgment(message);
	}

	public void sendProfilelessChatMessage(Text message, MessageType.Parameters params) {
		this.sendPacket(new ProfilelessChatMessageS2CPacket(message, params.toSerialized(this.player.getWorld().getRegistryManager())));
	}

	public SocketAddress getConnectionAddress() {
		return this.connection.getAddress();
	}

	public void reconfigure() {
		this.requestedReconfiguration = true;
		this.cleanUp();
		this.sendPacket(new EnterReconfigurationS2CPacket());
	}

	@Override
	public void onQueryPing(QueryPingC2SPacket packet) {
		this.connection.send(new PingResultS2CPacket(packet.getStartTime()));
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		final ServerWorld serverWorld = this.player.getServerWorld();
		final Entity entity = packet.getEntity(serverWorld);
		this.player.updateLastActionTime();
		this.player.setSneaking(packet.isPlayerSneaking());
		if (entity != null) {
			if (!serverWorld.getWorldBorder().contains(entity.getBlockPos())) {
				return;
			}

			Box box = entity.getBoundingBox();
			if (this.player.method_55631(box)) {
				packet.handle(
					new PlayerInteractEntityC2SPacket.Handler() {
						private void processInteract(Hand hand, ServerPlayNetworkHandler.Interaction action) {
							ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(hand);
							if (itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
								ItemStack itemStack2 = itemStack.copy();
								ActionResult actionResult = action.run(ServerPlayNetworkHandler.this.player, entity, hand);
								if (actionResult.isAccepted()) {
									Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerPlayNetworkHandler.this.player, itemStack2, entity);
									if (actionResult.shouldSwingHand()) {
										ServerPlayNetworkHandler.this.player.swingHand(hand, true);
									}
								}
							}
						}

						@Override
						public void interact(Hand hand) {
							this.processInteract(hand, PlayerEntity::interact);
						}

						@Override
						public void interactAt(Hand hand, Vec3d pos) {
							this.processInteract(hand, (player, entityxx, handx) -> entityxx.interactAt(player, pos, handx));
						}

						@Override
						public void attack() {
							if (!(entity instanceof ItemEntity)
								&& !(entity instanceof ExperienceOrbEntity)
								&& !(entity instanceof PersistentProjectileEntity)
								&& entity != ServerPlayNetworkHandler.this.player) {
								ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(Hand.MAIN_HAND);
								if (itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
									ServerPlayNetworkHandler.this.player.attack(entity);
								}
							} else {
								ServerPlayNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
								ServerPlayNetworkHandler.LOGGER.warn("Player {} tried to attack an invalid entity", ServerPlayNetworkHandler.this.player.getName().getString());
							}
						}
					}
				);
			}
		}
	}

	@Override
	public void onClientStatus(ClientStatusC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ClientStatusC2SPacket.Mode mode = packet.getMode();
		switch (mode) {
			case PERFORM_RESPAWN:
				if (this.player.notInAnyWorld) {
					this.player.notInAnyWorld = false;
					this.player = this.server.getPlayerManager().respawnPlayer(this.player, true);
					Criteria.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().respawnPlayer(this.player, false);
					if (this.server.isHardcore()) {
						this.player.changeGameMode(GameMode.SPECTATOR);
						this.player.getWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS).set(false, this.server);
					}
				}
				break;
			case REQUEST_STATS:
				this.player.getStatHandler().sendStats(this.player);
		}
	}

	@Override
	public void onCloseHandledScreen(CloseHandledScreenC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.onHandledScreenClosed();
	}

	@Override
	public void onClickSlot(ClickSlotC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.currentScreenHandler.syncId == packet.getSyncId()) {
			if (this.player.isSpectator()) {
				this.player.currentScreenHandler.syncState();
			} else if (!this.player.currentScreenHandler.canUse(this.player)) {
				LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
			} else {
				int i = packet.getSlot();
				if (!this.player.currentScreenHandler.isValid(i)) {
					LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", this.player.getName(), i, this.player.currentScreenHandler.slots.size());
				} else {
					boolean bl = packet.getRevision() != this.player.currentScreenHandler.getRevision();
					this.player.currentScreenHandler.disableSyncing();
					this.player.currentScreenHandler.onSlotClick(i, packet.getButton(), packet.getActionType(), this.player);

					for (Entry<ItemStack> entry : Int2ObjectMaps.fastIterable(packet.getModifiedStacks())) {
						this.player.currentScreenHandler.setPreviousTrackedSlotMutable(entry.getIntKey(), (ItemStack)entry.getValue());
					}

					this.player.currentScreenHandler.setPreviousCursorStack(packet.getStack());
					this.player.currentScreenHandler.enableSyncing();
					if (bl) {
						this.player.currentScreenHandler.updateToClient();
					} else {
						this.player.currentScreenHandler.sendContentUpdates();
					}
				}
			}
		}
	}

	@Override
	public void onCraftRequest(CraftRequestC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (!this.player.isSpectator()
			&& this.player.currentScreenHandler.syncId == packet.getSyncId()
			&& this.player.currentScreenHandler instanceof AbstractRecipeScreenHandler) {
			if (!this.player.currentScreenHandler.canUse(this.player)) {
				LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
			} else {
				this.server
					.getRecipeManager()
					.get(packet.getRecipe())
					.ifPresent(recipe -> ((AbstractRecipeScreenHandler)this.player.currentScreenHandler).fillInputSlots(packet.shouldCraftAll(), recipe, this.player));
			}
		}
	}

	@Override
	public void onButtonClick(ButtonClickC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.currentScreenHandler.syncId == packet.getSyncId() && !this.player.isSpectator()) {
			if (!this.player.currentScreenHandler.canUse(this.player)) {
				LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
			} else {
				boolean bl = this.player.currentScreenHandler.onButtonClick(this.player, packet.getButtonId());
				if (bl) {
					this.player.currentScreenHandler.sendContentUpdates();
				}
			}
		}
	}

	@Override
	public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.interactionManager.isCreative()) {
			boolean bl = packet.getSlot() < 0;
			ItemStack itemStack = packet.getStack();
			if (!itemStack.isItemEnabled(this.player.getWorld().getEnabledFeatures())) {
				return;
			}

			NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
			if (!itemStack.isEmpty() && nbtCompound != null && nbtCompound.contains("x") && nbtCompound.contains("y") && nbtCompound.contains("z")) {
				BlockPos blockPos = BlockEntity.posFromNbt(nbtCompound);
				if (this.player.getWorld().canSetBlock(blockPos)) {
					BlockEntity blockEntity = this.player.getWorld().getBlockEntity(blockPos);
					if (blockEntity != null) {
						blockEntity.setStackNbt(itemStack);
					}
				}
			}

			boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getCount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				this.player.playerScreenHandler.getSlot(packet.getSlot()).setStack(itemStack);
				this.player.playerScreenHandler.sendContentUpdates();
			} else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
				this.creativeItemDropThreshold += 20;
				this.player.dropItem(itemStack, true);
			}
		}
	}

	@Override
	public void onUpdateSign(UpdateSignC2SPacket packet) {
		List<String> list = (List<String>)Stream.of(packet.getText()).map(Formatting::strip).collect(Collectors.toList());
		this.filterTexts(list).thenAcceptAsync(texts -> this.onSignUpdate(packet, texts), this.server);
	}

	private void onSignUpdate(UpdateSignC2SPacket packet, List<FilteredMessage> signText) {
		this.player.updateLastActionTime();
		ServerWorld serverWorld = this.player.getServerWorld();
		BlockPos blockPos = packet.getPos();
		if (serverWorld.isChunkLoaded(blockPos)) {
			if (!(serverWorld.getBlockEntity(blockPos) instanceof SignBlockEntity signBlockEntity)) {
				return;
			}

			signBlockEntity.tryChangeText(this.player, packet.isFront(), signText);
		}
	}

	@Override
	public void onUpdatePlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.getAbilities().flying = packet.isFlying() && this.player.getAbilities().allowFlying;
	}

	@Override
	public void onClientOptions(ClientOptionsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.setClientOptions(packet.options());
	}

	@Override
	public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2) || this.isHost()) {
			this.server.setDifficulty(packet.getDifficulty(), false);
		}
	}

	@Override
	public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2) || this.isHost()) {
			this.server.setDifficultyLocked(packet.isDifficultyLocked());
		}
	}

	@Override
	public void onPlayerSession(PlayerSessionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		PublicPlayerSession.Serialized serialized = packet.chatSession();
		PlayerPublicKey.PublicKeyData publicKeyData = this.session != null ? this.session.publicKeyData().data() : null;
		PlayerPublicKey.PublicKeyData publicKeyData2 = serialized.publicKeyData();
		if (!Objects.equals(publicKeyData, publicKeyData2)) {
			if (publicKeyData != null && publicKeyData2.expiresAt().isBefore(publicKeyData.expiresAt())) {
				this.disconnect(PlayerPublicKey.EXPIRED_PUBLIC_KEY_TEXT);
			} else {
				try {
					SignatureVerifier signatureVerifier = this.server.getServicesSignatureVerifier();
					if (signatureVerifier == null) {
						LOGGER.warn("Ignoring chat session from {} due to missing Services public key", this.player.getGameProfile().getName());
						return;
					}

					this.setSession(serialized.toSession(this.player.getGameProfile(), signatureVerifier));
				} catch (PlayerPublicKey.PublicKeyException var6) {
					LOGGER.error("Failed to validate profile key: {}", var6.getMessage());
					this.disconnect(var6.getMessageText());
				}
			}
		}
	}

	@Override
	public void onAcknowledgeReconfiguration(AcknowledgeReconfigurationC2SPacket packet) {
		if (!this.requestedReconfiguration) {
			throw new IllegalStateException("Client acknowledged config, but none was requested");
		} else {
			this.connection
				.setPacketListener(new ServerConfigurationNetworkHandler(this.server, this.connection, this.createClientData(this.player.getClientOptions())));
		}
	}

	@Override
	public void onAcknowledgeChunks(AcknowledgeChunksC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.chunkDataSender.onAcknowledgeChunks(packet.desiredChunksPerTick());
	}

	private void setSession(PublicPlayerSession session) {
		this.session = session;
		this.messageUnpacker = session.createUnpacker(this.player.getUuid());
		this.messageChainTaskQueue.append(() -> {
			this.player.setSession(session);
			this.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.INITIALIZE_CHAT), List.of(this.player)));
		});
	}

	@Override
	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	@FunctionalInterface
	interface Interaction {
		ActionResult run(ServerPlayerEntity player, Entity entity, Hand hand);
	}
}
