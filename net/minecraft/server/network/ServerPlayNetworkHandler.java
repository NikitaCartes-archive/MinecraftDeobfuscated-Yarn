/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
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
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.AcknowledgmentValidator;
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
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;
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
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
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
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerPlayNetworkHandler
implements EntityTrackingListener,
TickablePacketListener,
ServerPlayPacketListener {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int KEEP_ALIVE_INTERVAL = 15000;
    public static final double MAX_BREAK_SQUARED_DISTANCE = MathHelper.square(6.0);
    private static final int DEFAULT_SEQUENCE = -1;
    private static final int MAX_PENDING_ACKNOWLEDGMENTS = 4096;
    private static final Text CHAT_VALIDATION_FAILED_TEXT = Text.translatable("multiplayer.disconnect.chat_validation_failed");
    private final ClientConnection connection;
    private final MinecraftServer server;
    public ServerPlayerEntity player;
    private int ticks;
    private int sequence = -1;
    private long lastKeepAliveTime;
    private boolean waitingForKeepAlive;
    private long keepAliveId;
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
    private final AtomicReference<Instant> lastMessageTimestamp = new AtomicReference<Instant>(Instant.EPOCH);
    @Nullable
    private PublicPlayerSession session;
    private MessageChain.Unpacker messageUnpacker;
    private final AcknowledgmentValidator acknowledgmentValidator = new AcknowledgmentValidator(20);
    private final MessageSignatureStorage signatureStorage = MessageSignatureStorage.create();
    private final MessageChainTaskQueue messageChainTaskQueue;

    public ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        this.server = server;
        this.connection = connection;
        connection.setPacketListener(this);
        this.player = player;
        player.networkHandler = this;
        this.lastKeepAliveTime = Util.getMeasuringTimeMs();
        player.getTextStream().onConnect();
        this.messageUnpacker = server.shouldEnforceSecureProfile() ? MessageChain.Unpacker.NOT_INITIALIZED : MessageChain.Unpacker.unsigned(player.getUuid());
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
        ++this.ticks;
        this.lastTickMovePacketsCount = this.movePacketsCount;
        if (this.floating && !this.player.isSleeping() && !this.player.hasVehicle() && !this.player.isDead()) {
            if (++this.floatingTicks > 80) {
                LOGGER.warn("{} was kicked for floating too long!", (Object)this.player.getName().getString());
                this.disconnect(Text.translatable("multiplayer.disconnect.flying"));
                return;
            }
        } else {
            this.floating = false;
            this.floatingTicks = 0;
        }
        this.topmostRiddenEntity = this.player.getRootVehicle();
        if (this.topmostRiddenEntity == this.player || this.topmostRiddenEntity.getControllingPassenger() != this.player) {
            this.topmostRiddenEntity = null;
            this.vehicleFloating = false;
            this.vehicleFloatingTicks = 0;
        } else {
            this.lastTickRiddenX = this.topmostRiddenEntity.getX();
            this.lastTickRiddenY = this.topmostRiddenEntity.getY();
            this.lastTickRiddenZ = this.topmostRiddenEntity.getZ();
            this.updatedRiddenX = this.topmostRiddenEntity.getX();
            this.updatedRiddenY = this.topmostRiddenEntity.getY();
            this.updatedRiddenZ = this.topmostRiddenEntity.getZ();
            if (this.vehicleFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
                if (++this.vehicleFloatingTicks > 80) {
                    LOGGER.warn("{} was kicked for floating a vehicle too long!", (Object)this.player.getName().getString());
                    this.disconnect(Text.translatable("multiplayer.disconnect.flying"));
                    return;
                }
            } else {
                this.vehicleFloating = false;
                this.vehicleFloatingTicks = 0;
            }
        }
        this.server.getProfiler().push("keepAlive");
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastKeepAliveTime >= 15000L) {
            if (this.waitingForKeepAlive) {
                this.disconnect(Text.translatable("disconnect.timeout"));
            } else {
                this.waitingForKeepAlive = true;
                this.lastKeepAliveTime = l;
                this.keepAliveId = l;
                this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
            }
        }
        this.server.getProfiler().pop();
        if (this.messageCooldown > 0) {
            --this.messageCooldown;
        }
        if (this.creativeItemDropThreshold > 0) {
            --this.creativeItemDropThreshold;
        }
        if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
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
        return this.connection.isOpen();
    }

    private boolean isHost() {
        return this.server.isHost(this.player.getGameProfile());
    }

    public void disconnect(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason), PacketCallbacks.always(() -> this.connection.disconnect(reason)));
        this.connection.disableAutoRead();
        this.server.submitAndJoin(this.connection::handleDisconnection);
    }

    private <T, R> CompletableFuture<R> filterText(T text, BiFunction<TextStream, T, CompletableFuture<R>> filterer) {
        return filterer.apply(this.player.getTextStream(), (TextStream)text).thenApply(filtered -> {
            if (!this.isConnectionOpen()) {
                LOGGER.debug("Ignoring packet due to disconnection");
                throw new CancellationException("disconnected");
            }
            return filtered;
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
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
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
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (ServerPlayNetworkHandler.isMovementInvalid(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch())) {
            this.disconnect(Text.translatable("multiplayer.disconnect.invalid_vehicle_movement"));
            return;
        }
        Entity entity = this.player.getRootVehicle();
        if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.topmostRiddenEntity) {
            ServerWorld serverWorld = this.player.getWorld();
            double d = entity.getX();
            double e = entity.getY();
            double f = entity.getZ();
            double g = ServerPlayNetworkHandler.clampHorizontal(packet.getX());
            double h = ServerPlayNetworkHandler.clampVertical(packet.getY());
            double i = ServerPlayNetworkHandler.clampHorizontal(packet.getZ());
            float j = MathHelper.wrapDegrees(packet.getYaw());
            float k = MathHelper.wrapDegrees(packet.getPitch());
            double l = g - this.lastTickRiddenX;
            double m = h - this.lastTickRiddenY;
            double n = i - this.lastTickRiddenZ;
            double p = l * l + m * m + n * n;
            double o = entity.getVelocity().lengthSquared();
            if (p - o > 100.0 && !this.isHost()) {
                LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), l, m, n);
                this.connection.send(new VehicleMoveS2CPacket(entity));
                return;
            }
            boolean bl = serverWorld.isSpaceEmpty(entity, entity.getBoundingBox().contract(0.0625));
            l = g - this.updatedRiddenX;
            m = h - this.updatedRiddenY - 1.0E-6;
            n = i - this.updatedRiddenZ;
            boolean bl2 = entity.field_36331;
            entity.move(MovementType.PLAYER, new Vec3d(l, m, n));
            double q = m;
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
                this.connection.send(new VehicleMoveS2CPacket(entity));
                return;
            }
            this.player.getWorld().getChunkManager().updatePosition(this.player);
            this.player.increaseTravelMotionStats(this.player.getX() - d, this.player.getY() - e, this.player.getZ() - f);
            this.vehicleFloating = q >= -0.03125 && !bl2 && !this.server.isFlightEnabled() && !entity.hasNoGravity() && this.isEntityOnAir(entity);
            this.updatedRiddenX = entity.getX();
            this.updatedRiddenY = entity.getY();
            this.updatedRiddenZ = entity.getZ();
        }
    }

    private boolean isEntityOnAir(Entity entity) {
        return entity.world.getStatesInBox(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)).allMatch(AbstractBlock.AbstractBlockState::isAir);
    }

    @Override
    public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (packet.getTeleportId() == this.requestedTeleportId) {
            if (this.requestedTeleportPos == null) {
                this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
                return;
            }
            this.player.updatePositionAndAngles(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
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
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.server.getRecipeManager().get(packet.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
    }

    @Override
    public void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.getRecipeBook().setCategoryOptions(packet.getCategory(), packet.isGuiOpen(), packet.isFilteringCraftable());
    }

    @Override
    public void onAdvancementTab(AdvancementTabC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
            Identifier identifier = packet.getTabToOpen();
            Advancement advancement = this.server.getAdvancementLoader().get(identifier);
            if (advancement != null) {
                this.player.getAdvancementTracker().setDisplayTab(advancement);
            }
        }
    }

    @Override
    public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        StringReader stringReader = new StringReader(packet.getPartialCommand());
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
        this.server.getCommandManager().getDispatcher().getCompletionSuggestions(parseResults).thenAccept(suggestions -> this.connection.send(new CommandSuggestionsS2CPacket(packet.getCompletionId(), (Suggestions)suggestions)));
    }

    @Override
    public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.server.areCommandBlocksEnabled()) {
            this.player.sendMessage(Text.translatable("advMode.notEnabled"));
            return;
        }
        if (!this.player.isCreativeLevelTwoOp()) {
            this.player.sendMessage(Text.translatable("advMode.notAllowed"));
            return;
        }
        CommandBlockExecutor commandBlockExecutor = null;
        CommandBlockBlockEntity commandBlockBlockEntity = null;
        BlockPos blockPos = packet.getBlockPos();
        BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
        if (blockEntity instanceof CommandBlockBlockEntity) {
            commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
            commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
        }
        String string = packet.getCommand();
        boolean bl = packet.shouldTrackOutput();
        if (commandBlockExecutor != null) {
            CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
            BlockState blockState = this.player.world.getBlockState(blockPos);
            Direction direction = blockState.get(CommandBlock.FACING);
            BlockState blockState3 = (BlockState)((BlockState)(switch (packet.getType()) {
                case CommandBlockBlockEntity.Type.SEQUENCE -> Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
                case CommandBlockBlockEntity.Type.AUTO -> Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
                default -> Blocks.COMMAND_BLOCK.getDefaultState();
            }).with(CommandBlock.FACING, direction)).with(CommandBlock.CONDITIONAL, packet.isConditional());
            if (blockState3 != blockState) {
                this.player.world.setBlockState(blockPos, blockState3, Block.NOTIFY_LISTENERS);
                blockEntity.setCachedState(blockState3);
                this.player.world.getWorldChunk(blockPos).setBlockEntity(blockEntity);
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

    @Override
    public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.server.areCommandBlocksEnabled()) {
            this.player.sendMessage(Text.translatable("advMode.notEnabled"));
            return;
        }
        if (!this.player.isCreativeLevelTwoOp()) {
            this.player.sendMessage(Text.translatable("advMode.notAllowed"));
            return;
        }
        CommandBlockExecutor commandBlockExecutor = packet.getMinecartCommandExecutor(this.player.world);
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

    @Override
    public void onPickFromInventory(PickFromInventoryC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.getInventory().swapSlotWithHotbar(packet.getSlot());
        this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, this.player.getInventory().selectedSlot, this.player.getInventory().getStack(this.player.getInventory().selectedSlot)));
        this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, packet.getSlot(), this.player.getInventory().getStack(packet.getSlot())));
        this.player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.player.getInventory().selectedSlot));
    }

    @Override
    public void onRenameItem(RenameItemC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        ScreenHandler screenHandler = this.player.currentScreenHandler;
        if (screenHandler instanceof AnvilScreenHandler) {
            AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler)screenHandler;
            if (!anvilScreenHandler.canUse(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)anvilScreenHandler);
                return;
            }
            String string = SharedConstants.stripInvalidChars(packet.getName());
            if (string.length() <= 50) {
                anvilScreenHandler.setNewItemName(string);
            }
        }
    }

    @Override
    public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        ScreenHandler screenHandler = this.player.currentScreenHandler;
        if (screenHandler instanceof BeaconScreenHandler) {
            BeaconScreenHandler beaconScreenHandler = (BeaconScreenHandler)screenHandler;
            if (!this.player.currentScreenHandler.canUse(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.currentScreenHandler);
                return;
            }
            beaconScreenHandler.setEffects(packet.getPrimaryEffectId(), packet.getSecondaryEffectId());
        }
    }

    @Override
    public void onUpdateStructureBlock(UpdateStructureBlockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        BlockPos blockPos = packet.getPos();
        BlockState blockState = this.player.world.getBlockState(blockPos);
        BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
        if (blockEntity instanceof StructureBlockBlockEntity) {
            StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
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
                    } else if (structureBlockBlockEntity.loadStructure(this.player.getWorld())) {
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
            this.player.world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onUpdateJigsaw(UpdateJigsawC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        BlockPos blockPos = packet.getPos();
        BlockState blockState = this.player.world.getBlockState(blockPos);
        BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
        if (blockEntity instanceof JigsawBlockEntity) {
            JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
            jigsawBlockEntity.setName(packet.getName());
            jigsawBlockEntity.setTarget(packet.getTarget());
            jigsawBlockEntity.setPool(RegistryKey.of(RegistryKeys.TEMPLATE_POOL, packet.getPool()));
            jigsawBlockEntity.setFinalState(packet.getFinalState());
            jigsawBlockEntity.setJoint(packet.getJointType());
            jigsawBlockEntity.markDirty();
            this.player.world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        BlockPos blockPos = packet.getPos();
        BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
        if (blockEntity instanceof JigsawBlockEntity) {
            JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
            jigsawBlockEntity.generate(this.player.getWorld(), packet.getMaxDepth(), packet.shouldKeepJigsaws());
        }
    }

    @Override
    public void onSelectMerchantTrade(SelectMerchantTradeC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        int i = packet.getTradeId();
        ScreenHandler screenHandler = this.player.currentScreenHandler;
        if (screenHandler instanceof MerchantScreenHandler) {
            MerchantScreenHandler merchantScreenHandler = (MerchantScreenHandler)screenHandler;
            if (!merchantScreenHandler.canUse(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)merchantScreenHandler);
                return;
            }
            merchantScreenHandler.setRecipeIndex(i);
            merchantScreenHandler.switchTo(i);
        }
    }

    @Override
    public void onBookUpdate(BookUpdateC2SPacket packet) {
        int i = packet.getSlot();
        if (!PlayerInventory.isValidHotbarIndex(i) && i != 40) {
            return;
        }
        ArrayList<String> list = Lists.newArrayList();
        Optional<String> optional = packet.getTitle();
        optional.ifPresent(list::add);
        packet.getPages().stream().limit(100L).forEach(list::add);
        Consumer<List> consumer = optional.isPresent() ? texts -> this.addBook((FilteredMessage)texts.get(0), texts.subList(1, texts.size()), i) : texts -> this.updateBookContent((List<FilteredMessage>)texts, i);
        this.filterTexts(list).thenAcceptAsync(consumer, (Executor)this.server);
    }

    private void updateBookContent(List<FilteredMessage> pages, int slotId) {
        ItemStack itemStack = this.player.getInventory().getStack(slotId);
        if (!itemStack.isOf(Items.WRITABLE_BOOK)) {
            return;
        }
        this.setTextToBook(pages, UnaryOperator.identity(), itemStack);
    }

    private void addBook(FilteredMessage title, List<FilteredMessage> pages, int slotId) {
        ItemStack itemStack = this.player.getInventory().getStack(slotId);
        if (!itemStack.isOf(Items.WRITABLE_BOOK)) {
            return;
        }
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
        this.setTextToBook(pages, text -> Text.Serializer.toJson(Text.literal(text)), itemStack2);
        this.player.getInventory().setStack(slotId, itemStack2);
    }

    private void setTextToBook(List<FilteredMessage> messages, UnaryOperator<String> postProcessor, ItemStack book) {
        NbtList nbtList = new NbtList();
        if (this.player.shouldFilterText()) {
            messages.stream().map(message -> NbtString.of((String)postProcessor.apply(message.getString()))).forEach(nbtList::add);
        } else {
            NbtCompound nbtCompound = new NbtCompound();
            int j = messages.size();
            for (int i = 0; i < j; ++i) {
                FilteredMessage filteredMessage = messages.get(i);
                String string = filteredMessage.raw();
                nbtList.add(NbtString.of((String)postProcessor.apply(string)));
                if (!filteredMessage.isFiltered()) continue;
                nbtCompound.putString(String.valueOf(i), (String)postProcessor.apply(filteredMessage.getString()));
            }
            if (!nbtCompound.isEmpty()) {
                book.setSubNbt("filtered_pages", nbtCompound);
            }
        }
        book.setSubNbt("pages", nbtList);
    }

    @Override
    public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.player.hasPermissionLevel(2)) {
            return;
        }
        Entity entity = this.player.getWorld().getEntityById(packet.getEntityId());
        if (entity != null) {
            NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
            this.player.networkHandler.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
        }
    }

    @Override
    public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.player.hasPermissionLevel(2)) {
            return;
        }
        BlockEntity blockEntity = this.player.getWorld().getBlockEntity(packet.getPos());
        NbtCompound nbtCompound = blockEntity != null ? blockEntity.createNbt() : null;
        this.player.networkHandler.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
    }

    @Override
    public void onPlayerMove(PlayerMoveC2SPacket packet) {
        boolean bl;
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (ServerPlayNetworkHandler.isMovementInvalid(packet.getX(0.0), packet.getY(0.0), packet.getZ(0.0), packet.getYaw(0.0f), packet.getPitch(0.0f))) {
            this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
            return;
        }
        ServerWorld serverWorld = this.player.getWorld();
        if (this.player.notInAnyWorld) {
            return;
        }
        if (this.ticks == 0) {
            this.syncWithPlayerPosition();
        }
        if (this.requestedTeleportPos != null) {
            if (this.ticks - this.teleportRequestTick > 20) {
                this.teleportRequestTick = this.ticks;
                this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
            }
            return;
        }
        this.teleportRequestTick = this.ticks;
        double d = ServerPlayNetworkHandler.clampHorizontal(packet.getX(this.player.getX()));
        double e = ServerPlayNetworkHandler.clampVertical(packet.getY(this.player.getY()));
        double f = ServerPlayNetworkHandler.clampHorizontal(packet.getZ(this.player.getZ()));
        float g = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
        float h = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));
        if (this.player.hasVehicle()) {
            this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
            this.player.getWorld().getChunkManager().updatePosition(this.player);
            return;
        }
        double i = this.player.getX();
        double j = this.player.getY();
        double k = this.player.getZ();
        double l = this.player.getY();
        double m = d - this.lastTickX;
        double n = e - this.lastTickY;
        double o = f - this.lastTickZ;
        double p = this.player.getVelocity().lengthSquared();
        double q = m * m + n * n + o * o;
        if (this.player.isSleeping()) {
            if (q > 1.0) {
                this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
            }
            return;
        }
        ++this.movePacketsCount;
        int r = this.movePacketsCount - this.lastTickMovePacketsCount;
        if (r > 5) {
            LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", (Object)this.player.getName().getString(), (Object)r);
            r = 1;
        }
        if (!(this.player.isInTeleportationState() || this.player.getWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) && this.player.isFallFlying())) {
            float s;
            float f2 = s = this.player.isFallFlying() ? 300.0f : 100.0f;
            if (q - p > (double)(s * (float)r) && !this.isHost()) {
                LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), m, n, o);
                this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
                return;
            }
        }
        Box box = this.player.getBoundingBox();
        m = d - this.updatedX;
        n = e - this.updatedY;
        o = f - this.updatedZ;
        boolean bl2 = bl = n > 0.0;
        if (this.player.isOnGround() && !packet.isOnGround() && bl) {
            this.player.jump();
        }
        boolean bl22 = this.player.field_36331;
        this.player.move(MovementType.PLAYER, new Vec3d(m, n, o));
        double t = n;
        m = d - this.player.getX();
        n = e - this.player.getY();
        if (n > -0.5 || n < 0.5) {
            n = 0.0;
        }
        o = f - this.player.getZ();
        q = m * m + n * n + o * o;
        boolean bl3 = false;
        if (!this.player.isInTeleportationState() && q > 0.0625 && !this.player.isSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
            bl3 = true;
            LOGGER.warn("{} moved wrongly!", (Object)this.player.getName().getString());
        }
        this.player.updatePositionAndAngles(d, e, f, g, h);
        if (!this.player.noClip && !this.player.isSleeping() && (bl3 && serverWorld.isSpaceEmpty(this.player, box) || this.isPlayerNotCollidingWithBlocks(serverWorld, box))) {
            this.requestTeleport(i, j, k, g, h);
            this.player.handleFall(this.player.getY() - l, packet.isOnGround());
            return;
        }
        this.floating = t >= -0.03125 && !bl22 && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR && !this.server.isFlightEnabled() && !this.player.getAbilities().allowFlying && !this.player.hasStatusEffect(StatusEffects.LEVITATION) && !this.player.isFallFlying() && !this.player.isUsingRiptide() && this.isEntityOnAir(this.player);
        this.player.getWorld().getChunkManager().updatePosition(this.player);
        this.player.handleFall(this.player.getY() - l, packet.isOnGround());
        this.player.setOnGround(packet.isOnGround());
        if (bl) {
            this.player.onLanding();
        }
        this.player.increaseTravelMotionStats(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k);
        this.updatedX = this.player.getX();
        this.updatedY = this.player.getY();
        this.updatedZ = this.player.getZ();
    }

    private boolean isPlayerNotCollidingWithBlocks(WorldView world, Box box) {
        Iterable<VoxelShape> iterable = world.getCollisions(this.player, this.player.getBoundingBox().contract(1.0E-5f));
        VoxelShape voxelShape = VoxelShapes.cuboid(box.contract(1.0E-5f));
        for (VoxelShape voxelShape2 : iterable) {
            if (VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) continue;
            return true;
        }
        return false;
    }

    public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
        this.requestTeleport(x, y, z, yaw, pitch, Collections.emptySet());
    }

    public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<PositionFlag> set) {
        double d = set.contains((Object)PositionFlag.X) ? this.player.getX() : 0.0;
        double e = set.contains((Object)PositionFlag.Y) ? this.player.getY() : 0.0;
        double f = set.contains((Object)PositionFlag.Z) ? this.player.getZ() : 0.0;
        float g = set.contains((Object)PositionFlag.Y_ROT) ? this.player.getYaw() : 0.0f;
        float h = set.contains((Object)PositionFlag.X_ROT) ? this.player.getPitch() : 0.0f;
        this.requestedTeleportPos = new Vec3d(x, y, z);
        if (++this.requestedTeleportId == Integer.MAX_VALUE) {
            this.requestedTeleportId = 0;
        }
        this.teleportRequestTick = this.ticks;
        this.player.updatePositionAndAngles(x, y, z, yaw, pitch);
        this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x - d, y - e, z - f, yaw - g, pitch - h, set, this.requestedTeleportId));
    }

    @Override
    public void onPlayerAction(PlayerActionC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        BlockPos blockPos = packet.getPos();
        this.player.updateLastActionTime();
        PlayerActionC2SPacket.Action action = packet.getAction();
        switch (action) {
            case SWAP_ITEM_WITH_OFFHAND: {
                if (!this.player.isSpectator()) {
                    ItemStack itemStack = this.player.getStackInHand(Hand.OFF_HAND);
                    this.player.setStackInHand(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
                    this.player.setStackInHand(Hand.MAIN_HAND, itemStack);
                    this.player.clearActiveItem();
                }
                return;
            }
            case DROP_ITEM: {
                if (!this.player.isSpectator()) {
                    this.player.dropSelectedItem(false);
                }
                return;
            }
            case DROP_ALL_ITEMS: {
                if (!this.player.isSpectator()) {
                    this.player.dropSelectedItem(true);
                }
                return;
            }
            case RELEASE_USE_ITEM: {
                this.player.stopUsingItem();
                return;
            }
            case START_DESTROY_BLOCK: 
            case ABORT_DESTROY_BLOCK: 
            case STOP_DESTROY_BLOCK: {
                this.player.interactionManager.processBlockBreakingAction(blockPos, action, packet.getDirection(), this.player.world.getTopY(), packet.getSequence());
                this.player.networkHandler.updateSequence(packet.getSequence());
                return;
            }
        }
        throw new IllegalArgumentException("Invalid player action");
    }

    /**
     * Checks if a player can place a block or fluid from a bucket.
     * 
     * <p>For this to return true, the player must not be actively cooling down.
     */
    private static boolean canPlace(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return (item instanceof BlockItem || item instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(item);
    }

    @Override
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.networkHandler.updateSequence(packet.getSequence());
        ServerWorld serverWorld = this.player.getWorld();
        Hand hand = packet.getHand();
        ItemStack itemStack = this.player.getStackInHand(hand);
        if (!itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
            return;
        }
        BlockHitResult blockHitResult = packet.getBlockHitResult();
        Vec3d vec3d = blockHitResult.getPos();
        BlockPos blockPos = blockHitResult.getBlockPos();
        Vec3d vec3d2 = Vec3d.ofCenter(blockPos);
        if (this.player.getEyePos().squaredDistanceTo(vec3d2) > MAX_BREAK_SQUARED_DISTANCE) {
            return;
        }
        Vec3d vec3d3 = vec3d.subtract(vec3d2);
        double d = 1.0000001;
        if (!(Math.abs(vec3d3.getX()) < 1.0000001 && Math.abs(vec3d3.getY()) < 1.0000001 && Math.abs(vec3d3.getZ()) < 1.0000001)) {
            LOGGER.warn("Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.", this.player.getGameProfile().getName(), vec3d, blockPos);
            return;
        }
        Direction direction = blockHitResult.getSide();
        this.player.updateLastActionTime();
        int i = this.player.world.getTopY();
        if (blockPos.getY() < i) {
            if (this.requestedTeleportPos == null && this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0 && serverWorld.canPlayerModifyAt(this.player, blockPos)) {
                ActionResult actionResult = this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
                if (direction == Direction.UP && !actionResult.isAccepted() && blockPos.getY() >= i - 1 && ServerPlayNetworkHandler.canPlace(this.player, itemStack)) {
                    MutableText text = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
                    this.player.sendMessageToClient(text, true);
                } else if (actionResult.shouldSwingHand()) {
                    this.player.swingHand(hand, true);
                }
            }
        } else {
            MutableText text2 = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
            this.player.sendMessageToClient(text2, true);
        }
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
    }

    @Override
    public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.updateSequence(packet.getSequence());
        ServerWorld serverWorld = this.player.getWorld();
        Hand hand = packet.getHand();
        ItemStack itemStack = this.player.getStackInHand(hand);
        this.player.updateLastActionTime();
        if (itemStack.isEmpty() || !itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
            return;
        }
        ActionResult actionResult = this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
        if (actionResult.shouldSwingHand()) {
            this.player.swingHand(hand, true);
        }
    }

    @Override
    public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (this.player.isSpectator()) {
            for (ServerWorld serverWorld : this.server.getWorlds()) {
                Entity entity = packet.getTarget(serverWorld);
                if (entity == null) continue;
                this.player.teleport(serverWorld, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                return;
            }
        }
    }

    @Override
    public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (packet.getStatus() == ResourcePackStatusC2SPacket.Status.DECLINED && this.server.requireResourcePack()) {
            LOGGER.info("Disconnecting {} due to resource pack rejection", (Object)this.player.getName());
            this.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
        }
    }

    @Override
    public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        Entity entity = this.player.getControllingVehicle();
        if (entity instanceof BoatEntity) {
            BoatEntity boatEntity = (BoatEntity)entity;
            boatEntity.setPaddleMovings(packet.isLeftPaddling(), packet.isRightPaddling());
        }
    }

    @Override
    public void onPong(PlayPongC2SPacket packet) {
    }

    @Override
    public void onDisconnected(Text reason) {
        this.messageChainTaskQueue.close();
        LOGGER.info("{} lost connection: {}", (Object)this.player.getName().getString(), (Object)reason.getString());
        this.server.forcePlayerSampleUpdate();
        this.server.getPlayerManager().broadcast(Text.translatable("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW), false);
        this.player.onDisconnect();
        this.server.getPlayerManager().remove(this.player);
        this.player.getTextStream().onDisconnect();
        if (this.isHost()) {
            LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.stop(false);
        }
    }

    public void updateSequence(int sequence) {
        if (sequence < 0) {
            throw new IllegalArgumentException("Expected packet sequence nr >= 0");
        }
        this.sequence = Math.max(sequence, this.sequence);
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet<?> packet, @Nullable PacketCallbacks callbacks) {
        try {
            this.connection.send(packet, callbacks);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Sending packet");
            CrashReportSection crashReportSection = crashReport.addElement("Packet being sent");
            crashReportSection.add("Packet class", () -> packet.getClass().getCanonicalName());
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (packet.getSelectedSlot() < 0 || packet.getSelectedSlot() >= PlayerInventory.getHotbarSize()) {
            LOGGER.warn("{} tried to set an invalid carried item", (Object)this.player.getName().getString());
            return;
        }
        if (this.player.getInventory().selectedSlot != packet.getSelectedSlot() && this.player.getActiveHand() == Hand.MAIN_HAND) {
            this.player.clearActiveItem();
        }
        this.player.getInventory().selectedSlot = packet.getSelectedSlot();
        this.player.updateLastActionTime();
    }

    @Override
    public void onChatMessage(ChatMessageC2SPacket packet) {
        if (ServerPlayNetworkHandler.hasIllegalCharacter(packet.chatMessage())) {
            this.disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
            return;
        }
        Optional<LastSeenMessageList> optional = this.validateMessage(packet.chatMessage(), packet.timestamp(), packet.acknowledgment());
        if (optional.isPresent()) {
            this.server.submit(() -> {
                SignedMessage signedMessage;
                try {
                    signedMessage = this.getSignedMessage(packet, (LastSeenMessageList)optional.get());
                } catch (MessageChain.MessageChainException messageChainException) {
                    this.handleMessageChainException(messageChainException);
                    return;
                }
                CompletableFuture<FilteredMessage> completableFuture = this.filterText(signedMessage.getSignedContent());
                CompletableFuture<Text> completableFuture2 = this.server.getMessageDecorator().decorate(this.player, signedMessage.getContent());
                this.messageChainTaskQueue.append(executor -> CompletableFuture.allOf(completableFuture, completableFuture2).thenAcceptAsync(void_ -> {
                    SignedMessage signedMessage2 = signedMessage.withUnsignedContent((Text)completableFuture2.join()).withFilterMask(((FilteredMessage)completableFuture.join()).mask());
                    this.handleDecoratedMessage(signedMessage2);
                }, executor));
            });
        }
    }

    @Override
    public void onCommandExecution(CommandExecutionC2SPacket packet) {
        if (ServerPlayNetworkHandler.hasIllegalCharacter(packet.command())) {
            this.disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
            return;
        }
        Optional<LastSeenMessageList> optional = this.validateMessage(packet.command(), packet.timestamp(), packet.acknowledgment());
        if (optional.isPresent()) {
            this.server.submit(() -> {
                this.handleCommandExecution(packet, (LastSeenMessageList)optional.get());
                this.checkForSpam();
            });
        }
    }

    private void handleCommandExecution(CommandExecutionC2SPacket packet, LastSeenMessageList lastSeenMessages) {
        Map<String, SignedMessage> map;
        ParseResults<ServerCommandSource> parseResults = this.parse(packet.command());
        try {
            map = this.collectArgumentMessages(packet, SignedArgumentList.of(parseResults), lastSeenMessages);
        } catch (MessageChain.MessageChainException messageChainException) {
            this.handleMessageChainException(messageChainException);
            return;
        }
        SignedCommandArguments.Impl signedCommandArguments = new SignedCommandArguments.Impl(map);
        parseResults = CommandManager.withCommandSource(parseResults, source -> source.withSignedArguments(signedCommandArguments));
        this.server.getCommandManager().execute(parseResults, packet.command());
    }

    private void handleMessageChainException(MessageChain.MessageChainException exception) {
        if (exception.shouldDisconnect()) {
            this.disconnect(exception.getMessageText());
        } else {
            this.player.sendMessage(exception.getMessageText().copy().formatted(Formatting.RED));
        }
    }

    /**
     * {@return a map of argument name and value as signed messages}
     */
    private Map<String, SignedMessage> collectArgumentMessages(CommandExecutionC2SPacket packet, SignedArgumentList<?> arguments, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException {
        Object2ObjectOpenHashMap<String, SignedMessage> map = new Object2ObjectOpenHashMap<String, SignedMessage>();
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
     * <p>This disconnects the player if the message arrives in {@linkplain
     * #isInProperOrder improper order} or if chat is disabled.
     */
    private Optional<LastSeenMessageList> validateMessage(String message, Instant timestamp, LastSeenMessageList.Acknowledgment acknowledgment) {
        if (!this.isInProperOrder(timestamp)) {
            LOGGER.warn("{} sent out-of-order chat: '{}'", (Object)this.player.getName().getString(), (Object)message);
            this.disconnect(Text.translatable("multiplayer.disconnect.out_of_order_chat"));
            return Optional.empty();
        }
        Optional<LastSeenMessageList> optional = this.validateAcknowledgment(acknowledgment);
        if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
            this.sendPacket(new GameMessageS2CPacket(Text.translatable("chat.disabled.options").formatted(Formatting.RED), false));
            return Optional.empty();
        }
        this.player.updateLastActionTime();
        return optional;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Optional<LastSeenMessageList> validateAcknowledgment(LastSeenMessageList.Acknowledgment acknowledgment) {
        AcknowledgmentValidator acknowledgmentValidator = this.acknowledgmentValidator;
        synchronized (acknowledgmentValidator) {
            Optional<LastSeenMessageList> optional = this.acknowledgmentValidator.validate(acknowledgment);
            if (optional.isEmpty()) {
                LOGGER.warn("Failed to validate message acknowledgements from {}", (Object)this.player.getName().getString());
                this.disconnect(CHAT_VALIDATION_FAILED_TEXT);
            }
            return optional;
        }
    }

    /**
     * {@return whether the message sent at {@code timestamp} is received in proper order}
     * 
     * <p>If {@code false}, the message will be discarded.
     */
    private boolean isInProperOrder(Instant timestamp) {
        Instant instant;
        do {
            if (!timestamp.isBefore(instant = this.lastMessageTimestamp.get())) continue;
            return false;
        } while (!this.lastMessageTimestamp.compareAndSet(instant, timestamp));
        return true;
    }

    /**
     * {@return whether {@code message} contains an illegal character}
     * 
     * @see net.minecraft.SharedConstants#isValidChar(char)
     */
    private static boolean hasIllegalCharacter(String message) {
        for (int i = 0; i < message.length(); ++i) {
            if (SharedConstants.isValidChar(message.charAt(i))) continue;
            return true;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onMessageAcknowledgment(MessageAcknowledgmentC2SPacket packet) {
        AcknowledgmentValidator acknowledgmentValidator = this.acknowledgmentValidator;
        synchronized (acknowledgmentValidator) {
            if (!this.acknowledgmentValidator.removeUntil(packet.offset())) {
                LOGGER.warn("Failed to validate message acknowledgements from {}", (Object)this.player.getName().getString());
                this.disconnect(CHAT_VALIDATION_FAILED_TEXT);
            }
        }
    }

    @Override
    public void onHandSwing(HandSwingC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.updateLastActionTime();
        this.player.swingHand(packet.getHand());
    }

    @Override
    public void onClientCommand(ClientCommandC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.updateLastActionTime();
        switch (packet.getMode()) {
            case PRESS_SHIFT_KEY: {
                this.player.setSneaking(true);
                break;
            }
            case RELEASE_SHIFT_KEY: {
                this.player.setSneaking(false);
                break;
            }
            case START_SPRINTING: {
                this.player.setSprinting(true);
                break;
            }
            case STOP_SPRINTING: {
                this.player.setSprinting(false);
                break;
            }
            case STOP_SLEEPING: {
                if (!this.player.isSleeping()) break;
                this.player.wakeUp(false, true);
                this.requestedTeleportPos = this.player.getPos();
                break;
            }
            case START_RIDING_JUMP: {
                Entity entity = this.player.getControllingVehicle();
                if (!(entity instanceof JumpingMount)) break;
                JumpingMount jumpingMount = (JumpingMount)((Object)entity);
                int i = packet.getMountJumpHeight();
                if (!jumpingMount.canJump() || i <= 0) break;
                jumpingMount.startJumping(i);
                break;
            }
            case STOP_RIDING_JUMP: {
                Entity entity = this.player.getControllingVehicle();
                if (!(entity instanceof JumpingMount)) break;
                JumpingMount jumpingMount = (JumpingMount)((Object)entity);
                jumpingMount.stopJumping();
                break;
            }
            case OPEN_INVENTORY: {
                Entity entity = this.player.getVehicle();
                if (!(entity instanceof RideableInventory)) break;
                RideableInventory rideableInventory = (RideableInventory)((Object)entity);
                rideableInventory.openInventory(this.player);
                break;
            }
            case START_FALL_FLYING: {
                if (this.player.checkFallFlying()) break;
                this.player.stopFallFlying();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid client command!");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPendingAcknowledgment(SignedMessage message) {
        int i;
        MessageSignatureData messageSignatureData = message.signature();
        if (messageSignatureData == null) {
            return;
        }
        this.signatureStorage.add(message);
        AcknowledgmentValidator acknowledgmentValidator = this.acknowledgmentValidator;
        synchronized (acknowledgmentValidator) {
            this.acknowledgmentValidator.addPending(messageSignatureData);
            i = this.acknowledgmentValidator.getMessageCount();
        }
        if (i > 4096) {
            this.disconnect(Text.translatable("multiplayer.disconnect.too_many_pending_chats"));
        }
    }

    public void sendChatMessage(SignedMessage message, MessageType.Parameters params) {
        this.sendPacket(new ChatMessageS2CPacket(message.link().sender(), message.link().index(), message.signature(), message.signedBody().toSerialized(this.signatureStorage), message.unsignedContent(), message.filterMask(), params.toSerialized(this.player.world.getRegistryManager())));
        this.addPendingAcknowledgment(message);
    }

    public void sendProfilelessChatMessage(Text message, MessageType.Parameters params) {
        this.sendPacket(new ProfilelessChatMessageS2CPacket(message, params.toSerialized(this.player.world.getRegistryManager())));
    }

    public SocketAddress getConnectionAddress() {
        return this.connection.getAddress();
    }

    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        final ServerWorld serverWorld = this.player.getWorld();
        final Entity entity = packet.getEntity(serverWorld);
        this.player.updateLastActionTime();
        this.player.setSneaking(packet.isPlayerSneaking());
        if (entity != null) {
            if (!serverWorld.getWorldBorder().contains(entity.getBlockPos())) {
                return;
            }
            Box box = entity.getBoundingBox();
            if (box.squaredMagnitude(this.player.getEyePos()) < MAX_BREAK_SQUARED_DISTANCE) {
                packet.handle(new PlayerInteractEntityC2SPacket.Handler(){

                    private void processInteract(Hand hand, Interaction action) {
                        ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(hand);
                        if (!itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
                            return;
                        }
                        ItemStack itemStack2 = itemStack.copy();
                        ActionResult actionResult = action.run(ServerPlayNetworkHandler.this.player, entity, hand);
                        if (actionResult.isAccepted()) {
                            Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerPlayNetworkHandler.this.player, itemStack2, entity);
                            if (actionResult.shouldSwingHand()) {
                                ServerPlayNetworkHandler.this.player.swingHand(hand, true);
                            }
                        }
                    }

                    @Override
                    public void interact(Hand hand) {
                        this.processInteract(hand, PlayerEntity::interact);
                    }

                    @Override
                    public void interactAt(Hand hand2, Vec3d pos) {
                        this.processInteract(hand2, (player, entity, hand) -> entity.interactAt(player, pos, hand));
                    }

                    @Override
                    public void attack() {
                        if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof PersistentProjectileEntity || entity == ServerPlayNetworkHandler.this.player) {
                            ServerPlayNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                            LOGGER.warn("Player {} tried to attack an invalid entity", (Object)ServerPlayNetworkHandler.this.player.getName().getString());
                            return;
                        }
                        ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(Hand.MAIN_HAND);
                        if (!itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
                            return;
                        }
                        ServerPlayNetworkHandler.this.player.attack(entity);
                    }
                });
            }
        }
    }

    @Override
    public void onClientStatus(ClientStatusC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.updateLastActionTime();
        ClientStatusC2SPacket.Mode mode = packet.getMode();
        switch (mode) {
            case PERFORM_RESPAWN: {
                if (this.player.notInAnyWorld) {
                    this.player.notInAnyWorld = false;
                    this.player = this.server.getPlayerManager().respawnPlayer(this.player, true);
                    Criteria.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
                    break;
                }
                if (this.player.getHealth() > 0.0f) {
                    return;
                }
                this.player = this.server.getPlayerManager().respawnPlayer(this.player, false);
                if (!this.server.isHardcore()) break;
                this.player.changeGameMode(GameMode.SPECTATOR);
                this.player.getWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS).set(false, this.server);
                break;
            }
            case REQUEST_STATS: {
                this.player.getStatHandler().sendStats(this.player);
            }
        }
    }

    @Override
    public void onCloseHandledScreen(CloseHandledScreenC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.onHandledScreenClosed();
    }

    @Override
    public void onClickSlot(ClickSlotC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.updateLastActionTime();
        if (this.player.currentScreenHandler.syncId != packet.getSyncId()) {
            return;
        }
        if (this.player.isSpectator()) {
            this.player.currentScreenHandler.syncState();
            return;
        }
        if (!this.player.currentScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.currentScreenHandler);
            return;
        }
        int i = packet.getSlot();
        if (!this.player.currentScreenHandler.isValid(i)) {
            LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", this.player.getName(), i, this.player.currentScreenHandler.slots.size());
            return;
        }
        boolean bl = packet.getRevision() != this.player.currentScreenHandler.getRevision();
        this.player.currentScreenHandler.disableSyncing();
        this.player.currentScreenHandler.onSlotClick(i, packet.getButton(), packet.getActionType(), this.player);
        for (Int2ObjectMap.Entry entry : Int2ObjectMaps.fastIterable(packet.getModifiedStacks())) {
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

    @Override
    public void onCraftRequest(CraftRequestC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.updateLastActionTime();
        if (this.player.isSpectator() || this.player.currentScreenHandler.syncId != packet.getSyncId() || !(this.player.currentScreenHandler instanceof AbstractRecipeScreenHandler)) {
            return;
        }
        if (!this.player.currentScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.currentScreenHandler);
            return;
        }
        this.server.getRecipeManager().get(packet.getRecipe()).ifPresent(recipe -> ((AbstractRecipeScreenHandler)this.player.currentScreenHandler).fillInputSlots(packet.shouldCraftAll(), (Recipe<?>)recipe, this.player));
    }

    @Override
    public void onButtonClick(ButtonClickC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.updateLastActionTime();
        if (this.player.currentScreenHandler.syncId != packet.getSyncId() || this.player.isSpectator()) {
            return;
        }
        if (!this.player.currentScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.currentScreenHandler);
            return;
        }
        boolean bl = this.player.currentScreenHandler.onButtonClick(this.player, packet.getButtonId());
        if (bl) {
            this.player.currentScreenHandler.sendContentUpdates();
        }
    }

    @Override
    public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (this.player.interactionManager.isCreative()) {
            boolean bl3;
            BlockEntity blockEntity;
            BlockPos blockPos;
            boolean bl = packet.getSlot() < 0;
            ItemStack itemStack = packet.getItemStack();
            if (!itemStack.isItemEnabled(this.player.getWorld().getEnabledFeatures())) {
                return;
            }
            NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
            if (!itemStack.isEmpty() && nbtCompound != null && nbtCompound.contains("x") && nbtCompound.contains("y") && nbtCompound.contains("z") && this.player.world.canSetBlock(blockPos = BlockEntity.posFromNbt(nbtCompound)) && (blockEntity = this.player.world.getBlockEntity(blockPos)) != null) {
                blockEntity.setStackNbt(itemStack);
            }
            boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
            boolean bl4 = bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getCount() <= 64 && !itemStack.isEmpty();
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
        List<String> list = Stream.of(packet.getText()).map(Formatting::strip).collect(Collectors.toList());
        this.filterTexts(list).thenAcceptAsync(texts -> this.onSignUpdate(packet, (List<FilteredMessage>)texts), (Executor)this.server);
    }

    private void onSignUpdate(UpdateSignC2SPacket packet, List<FilteredMessage> signText) {
        this.player.updateLastActionTime();
        ServerWorld serverWorld = this.player.getWorld();
        BlockPos blockPos = packet.getPos();
        if (serverWorld.isChunkLoaded(blockPos)) {
            BlockState blockState = serverWorld.getBlockState(blockPos);
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            if (!(blockEntity instanceof SignBlockEntity)) {
                return;
            }
            SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
            if (!signBlockEntity.isEditable() || !this.player.getUuid().equals(signBlockEntity.getEditor())) {
                LOGGER.warn("Player {} just tried to change non-editable sign", (Object)this.player.getName().getString());
                return;
            }
            for (int i = 0; i < signText.size(); ++i) {
                FilteredMessage filteredMessage = signText.get(i);
                if (this.player.shouldFilterText()) {
                    signBlockEntity.setTextOnRow(i, Text.literal(filteredMessage.getString()));
                    continue;
                }
                signBlockEntity.setTextOnRow(i, Text.literal(filteredMessage.raw()), Text.literal(filteredMessage.getString()));
            }
            signBlockEntity.markDirty();
            serverWorld.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onKeepAlive(KeepAliveC2SPacket packet) {
        if (this.waitingForKeepAlive && packet.getId() == this.keepAliveId) {
            int i = (int)(Util.getMeasuringTimeMs() - this.lastKeepAliveTime);
            this.player.pingMilliseconds = (this.player.pingMilliseconds * 3 + i) / 4;
            this.waitingForKeepAlive = false;
        } else if (!this.isHost()) {
            this.disconnect(Text.translatable("disconnect.timeout"));
        }
    }

    @Override
    public void onUpdatePlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.getAbilities().flying = packet.isFlying() && this.player.getAbilities().allowFlying;
    }

    @Override
    public void onClientSettings(ClientSettingsC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        this.player.setClientSettings(packet);
    }

    @Override
    public void onCustomPayload(CustomPayloadC2SPacket packet) {
    }

    @Override
    public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.player.hasPermissionLevel(2) && !this.isHost()) {
            return;
        }
        this.server.setDifficulty(packet.getDifficulty(), false);
    }

    @Override
    public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (!this.player.hasPermissionLevel(2) && !this.isHost()) {
            return;
        }
        this.server.setDifficultyLocked(packet.isDifficultyLocked());
    }

    @Override
    public void onPlayerSession(PlayerSessionC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        PublicPlayerSession.Serialized serialized = packet.chatSession();
        PlayerPublicKey.PublicKeyData publicKeyData = this.session != null ? this.session.publicKeyData().data() : null;
        PlayerPublicKey.PublicKeyData publicKeyData2 = serialized.publicKeyData();
        if (Objects.equals(publicKeyData, publicKeyData2)) {
            return;
        }
        if (publicKeyData != null && publicKeyData2.expiresAt().isBefore(publicKeyData.expiresAt())) {
            this.disconnect(PlayerPublicKey.EXPIRED_PUBLIC_KEY_TEXT);
            return;
        }
        try {
            SignatureVerifier signatureVerifier = this.server.getServicesSignatureVerifier();
            this.setSession(serialized.toSession(this.player.getGameProfile(), signatureVerifier, Duration.ZERO));
        } catch (PlayerPublicKey.PublicKeyException publicKeyException) {
            LOGGER.error("Failed to validate profile key: {}", (Object)publicKeyException.getMessage());
            this.disconnect(publicKeyException.getMessageText());
        }
    }

    private void setSession(PublicPlayerSession session) {
        this.session = session;
        this.messageUnpacker = session.createUnpacker(this.player.getUuid());
        this.messageChainTaskQueue.append(executor -> {
            this.player.setSession(session);
            this.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.INITIALIZE_CHAT), List.of(this.player)));
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    @FunctionalInterface
    static interface Interaction {
        public ActionResult run(ServerPlayerEntity var1, Entity var2, Hand var3);
    }
}

