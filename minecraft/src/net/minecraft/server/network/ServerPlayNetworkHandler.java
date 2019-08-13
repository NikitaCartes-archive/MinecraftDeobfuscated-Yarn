package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection client;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int ticks;
	private long lastKeepAliveTime;
	private boolean waitingForKeepAlive;
	private long keepAliveId;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private final Int2ShortMap transactions = new Int2ShortOpenHashMap();
	private double lastTickX;
	private double lastTickY;
	private double lastTickZ;
	private double updatedX;
	private double updatedY;
	private double updatedZ;
	private Entity topmostRiddenEntity;
	private double lastTickRiddenX;
	private double lastTickRiddenY;
	private double lastTickRiddenZ;
	private double updatedRiddenX;
	private double updatedRiddenY;
	private double updatedRiddenZ;
	private Vec3d requestedTeleportPos;
	private int requestedTeleportId;
	private int teleportRequestTick;
	private boolean floating;
	private int floatingTicks;
	private boolean ridingEntity;
	private int vehicleFloatingTicks;
	private int movePacketsCount;
	private int lastTickMovePacketsCount;

	public ServerPlayNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.client = clientConnection;
		clientConnection.setPacketListener(this);
		this.player = serverPlayerEntity;
		serverPlayerEntity.networkHandler = this;
	}

	public void tick() {
		this.syncWithPlayerPosition();
		this.player.method_14226();
		this.player.setPositionAnglesAndUpdate(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.yaw, this.player.pitch);
		this.ticks++;
		this.lastTickMovePacketsCount = this.movePacketsCount;
		if (this.floating) {
			if (++this.floatingTicks > 80) {
				LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
				this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.floating = false;
			this.floatingTicks = 0;
		}

		this.topmostRiddenEntity = this.player.getRootVehicle();
		if (this.topmostRiddenEntity != this.player && this.topmostRiddenEntity.getPrimaryPassenger() == this.player) {
			this.lastTickRiddenX = this.topmostRiddenEntity.x;
			this.lastTickRiddenY = this.topmostRiddenEntity.y;
			this.lastTickRiddenZ = this.topmostRiddenEntity.z;
			this.updatedRiddenX = this.topmostRiddenEntity.x;
			this.updatedRiddenY = this.topmostRiddenEntity.y;
			this.updatedRiddenZ = this.topmostRiddenEntity.z;
			if (this.ridingEntity && this.player.getRootVehicle().getPrimaryPassenger() == this.player) {
				if (++this.vehicleFloatingTicks > 80) {
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
					this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
					return;
				}
			} else {
				this.ridingEntity = false;
				this.vehicleFloatingTicks = 0;
			}
		} else {
			this.topmostRiddenEntity = null;
			this.ridingEntity = false;
			this.vehicleFloatingTicks = 0;
		}

		this.server.getProfiler().push("keepAlive");
		long l = SystemUtil.getMeasuringTimeMs();
		if (l - this.lastKeepAliveTime >= 15000L) {
			if (this.waitingForKeepAlive) {
				this.disconnect(new TranslatableText("disconnect.timeout"));
			} else {
				this.waitingForKeepAlive = true;
				this.lastKeepAliveTime = l;
				this.keepAliveId = l;
				this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
			}
		}

		this.server.getProfiler().pop();
		if (this.messageCooldown > 0) {
			this.messageCooldown--;
		}

		if (this.creativeItemDropThreshold > 0) {
			this.creativeItemDropThreshold--;
		}

		if (this.player.getLastActionTime() > 0L
			&& this.server.getPlayerIdleTimeout() > 0
			&& SystemUtil.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.idling"));
		}
	}

	public void syncWithPlayerPosition() {
		this.lastTickX = this.player.x;
		this.lastTickY = this.player.y;
		this.lastTickZ = this.player.z;
		this.updatedX = this.player.x;
		this.updatedY = this.player.y;
		this.updatedZ = this.player.z;
	}

	@Override
	public ClientConnection getConnection() {
		return this.client;
	}

	private boolean isServerOwner() {
		return this.server.isOwner(this.player.getGameProfile());
	}

	public void disconnect(Text text) {
		this.client.send(new DisconnectS2CPacket(text), future -> this.client.disconnect(text));
		this.client.disableAutoRead();
		this.server.executeSync(this.client::handleDisconnection);
	}

	@Override
	public void onPlayerInput(PlayerInputC2SPacket playerInputC2SPacket) {
		NetworkThreadUtils.forceMainThread(playerInputC2SPacket, this, this.player.getServerWorld());
		this.player
			.method_14218(playerInputC2SPacket.getSideways(), playerInputC2SPacket.getForward(), playerInputC2SPacket.isJumping(), playerInputC2SPacket.isSneaking());
	}

	private static boolean validatePlayerMove(PlayerMoveC2SPacket playerMoveC2SPacket) {
		return Doubles.isFinite(playerMoveC2SPacket.getX(0.0))
				&& Doubles.isFinite(playerMoveC2SPacket.getY(0.0))
				&& Doubles.isFinite(playerMoveC2SPacket.getZ(0.0))
				&& Floats.isFinite(playerMoveC2SPacket.getPitch(0.0F))
				&& Floats.isFinite(playerMoveC2SPacket.getYaw(0.0F))
			? Math.abs(playerMoveC2SPacket.getX(0.0)) > 3.0E7 || Math.abs(playerMoveC2SPacket.getY(0.0)) > 3.0E7 || Math.abs(playerMoveC2SPacket.getZ(0.0)) > 3.0E7
			: true;
	}

	private static boolean validateVehicleMove(VehicleMoveC2SPacket vehicleMoveC2SPacket) {
		return !Doubles.isFinite(vehicleMoveC2SPacket.getX())
			|| !Doubles.isFinite(vehicleMoveC2SPacket.getY())
			|| !Doubles.isFinite(vehicleMoveC2SPacket.getZ())
			|| !Floats.isFinite(vehicleMoveC2SPacket.getPitch())
			|| !Floats.isFinite(vehicleMoveC2SPacket.getYaw());
	}

	@Override
	public void onVehicleMove(VehicleMoveC2SPacket vehicleMoveC2SPacket) {
		NetworkThreadUtils.forceMainThread(vehicleMoveC2SPacket, this, this.player.getServerWorld());
		if (validateVehicleMove(vehicleMoveC2SPacket)) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			Entity entity = this.player.getRootVehicle();
			if (entity != this.player && entity.getPrimaryPassenger() == this.player && entity == this.topmostRiddenEntity) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.x;
				double e = entity.y;
				double f = entity.z;
				double g = vehicleMoveC2SPacket.getX();
				double h = vehicleMoveC2SPacket.getY();
				double i = vehicleMoveC2SPacket.getZ();
				float j = vehicleMoveC2SPacket.getYaw();
				float k = vehicleMoveC2SPacket.getPitch();
				double l = g - this.lastTickRiddenX;
				double m = h - this.lastTickRiddenY;
				double n = i - this.lastTickRiddenZ;
				double o = entity.getVelocity().lengthSquared();
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && !this.isServerOwner()) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), l, m, n);
					this.client.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				boolean bl = serverWorld.doesNotCollide(entity, entity.getBoundingBox().contract(0.0625));
				l = g - this.updatedRiddenX;
				m = h - this.updatedRiddenY - 1.0E-6;
				n = i - this.updatedRiddenZ;
				entity.move(MovementType.field_6305, new Vec3d(l, m, n));
				l = g - entity.x;
				m = h - entity.y;
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.z;
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					LOGGER.warn("{} moved wrongly!", entity.getName().getString());
				}

				entity.setPositionAnglesAndUpdate(g, h, i, j, k);
				boolean bl3 = serverWorld.doesNotCollide(entity, entity.getBoundingBox().contract(0.0625));
				if (bl && (bl2 || !bl3)) {
					entity.setPositionAnglesAndUpdate(d, e, f, j, k);
					this.client.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				this.player.getServerWorld().method_14178().updateCameraPosition(this.player);
				this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
				this.ridingEntity = m >= -0.03125
					&& !this.server.isFlightEnabled()
					&& !serverWorld.isAreaNotEmpty(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
				this.updatedRiddenX = entity.x;
				this.updatedRiddenY = entity.y;
				this.updatedRiddenZ = entity.z;
			}
		}
	}

	@Override
	public void onTeleportConfirm(TeleportConfirmC2SPacket teleportConfirmC2SPacket) {
		NetworkThreadUtils.forceMainThread(teleportConfirmC2SPacket, this, this.player.getServerWorld());
		if (teleportConfirmC2SPacket.getTeleportId() == this.requestedTeleportId) {
			this.player
				.setPositionAnglesAndUpdate(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
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
	public void onRecipeBookData(RecipeBookDataC2SPacket recipeBookDataC2SPacket) {
		NetworkThreadUtils.forceMainThread(recipeBookDataC2SPacket, this, this.player.getServerWorld());
		if (recipeBookDataC2SPacket.getMode() == RecipeBookDataC2SPacket.Mode.field_13011) {
			this.server.getRecipeManager().get(recipeBookDataC2SPacket.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
		} else if (recipeBookDataC2SPacket.getMode() == RecipeBookDataC2SPacket.Mode.field_13010) {
			this.player.getRecipeBook().setGuiOpen(recipeBookDataC2SPacket.isGuiOpen());
			this.player.getRecipeBook().setFilteringCraftable(recipeBookDataC2SPacket.isFilteringCraftable());
			this.player.getRecipeBook().setFurnaceGuiOpen(recipeBookDataC2SPacket.isFurnaceGuiOpen());
			this.player.getRecipeBook().setFurnaceFilteringCraftable(recipeBookDataC2SPacket.isFurnaceFilteringCraftable());
			this.player.getRecipeBook().setBlastFurnaceGuiOpen(recipeBookDataC2SPacket.isBlastFurnaceGuiOpen());
			this.player.getRecipeBook().setBlastFurnaceFilteringCraftable(recipeBookDataC2SPacket.isBlastFurnaceFilteringCraftable());
			this.player.getRecipeBook().setSmokerGuiOpen(recipeBookDataC2SPacket.isSmokerGuiOpen());
			this.player.getRecipeBook().setSmokerFilteringCraftable(recipeBookDataC2SPacket.isSmokerGuiFilteringCraftable());
		}
	}

	@Override
	public void onAdvancementTab(AdvancementTabC2SPacket advancementTabC2SPacket) {
		NetworkThreadUtils.forceMainThread(advancementTabC2SPacket, this, this.player.getServerWorld());
		if (advancementTabC2SPacket.getAction() == AdvancementTabC2SPacket.Action.field_13024) {
			Identifier identifier = advancementTabC2SPacket.getTabToOpen();
			Advancement advancement = this.server.getAdvancementManager().get(identifier);
			if (advancement != null) {
				this.player.getAdvancementManager().setDisplayTab(advancement);
			}
		}
	}

	@Override
	public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket requestCommandCompletionsC2SPacket) {
		NetworkThreadUtils.forceMainThread(requestCommandCompletionsC2SPacket, this, this.player.getServerWorld());
		StringReader stringReader = new StringReader(requestCommandCompletionsC2SPacket.getPartialCommand());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
		this.server
			.getCommandManager()
			.getDispatcher()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.client.send(new CommandSuggestionsS2CPacket(requestCommandCompletionsC2SPacket.getCompletionId(), suggestions)));
	}

	@Override
	public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket updateCommandBlockC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateCommandBlockC2SPacket, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = updateCommandBlockC2SPacket.getBlockPos();
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = updateCommandBlockC2SPacket.getCommand();
			boolean bl = updateCommandBlockC2SPacket.shouldTrackOutput();
			if (commandBlockExecutor != null) {
				Direction direction = this.player.world.getBlockState(blockPos).get(CommandBlock.FACING);
				switch (updateCommandBlockC2SPacket.getType()) {
					case field_11922: {
						BlockState blockState = Blocks.field_10395.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos,
								blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(updateCommandBlockC2SPacket.isConditional())),
								2
							);
						break;
					}
					case field_11923: {
						BlockState blockState = Blocks.field_10263.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos,
								blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(updateCommandBlockC2SPacket.isConditional())),
								2
							);
						break;
					}
					case field_11924:
					default: {
						BlockState blockState = Blocks.field_10525.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos,
								blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(updateCommandBlockC2SPacket.isConditional())),
								2
							);
					}
				}

				blockEntity.validate();
				this.player.world.setBlockEntity(blockPos, blockEntity);
				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.shouldTrackOutput(bl);
				if (!bl) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockBlockEntity.setAuto(updateCommandBlockC2SPacket.isAlwaysActive());
				commandBlockExecutor.markDirty();
				if (!ChatUtil.isEmpty(string)) {
					this.player.sendMessage(new TranslatableText("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket updateCommandBlockMinecartC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateCommandBlockMinecartC2SPacket, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = updateCommandBlockMinecartC2SPacket.getMinecartCommandExecutor(this.player.world);
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(updateCommandBlockMinecartC2SPacket.getCommand());
				commandBlockExecutor.shouldTrackOutput(updateCommandBlockMinecartC2SPacket.shouldTrackOutput());
				if (!updateCommandBlockMinecartC2SPacket.shouldTrackOutput()) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockExecutor.markDirty();
				this.player.sendMessage(new TranslatableText("advMode.setCommand.success", updateCommandBlockMinecartC2SPacket.getCommand()));
			}
		}
	}

	@Override
	public void onPickFromInventory(PickFromInventoryC2SPacket pickFromInventoryC2SPacket) {
		NetworkThreadUtils.forceMainThread(pickFromInventoryC2SPacket, this, this.player.getServerWorld());
		this.player.inventory.swapSlotWithHotbar(pickFromInventoryC2SPacket.getSlot());
		this.player
			.networkHandler
			.sendPacket(new GuiSlotUpdateS2CPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getInvStack(this.player.inventory.selectedSlot)));
		this.player
			.networkHandler
			.sendPacket(new GuiSlotUpdateS2CPacket(-2, pickFromInventoryC2SPacket.getSlot(), this.player.inventory.getInvStack(pickFromInventoryC2SPacket.getSlot())));
		this.player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(this.player.inventory.selectedSlot));
	}

	@Override
	public void onRenameItem(RenameItemC2SPacket renameItemC2SPacket) {
		NetworkThreadUtils.forceMainThread(renameItemC2SPacket, this, this.player.getServerWorld());
		if (this.player.container instanceof AnvilContainer) {
			AnvilContainer anvilContainer = (AnvilContainer)this.player.container;
			String string = SharedConstants.stripInvalidChars(renameItemC2SPacket.getItemName());
			if (string.length() <= 35) {
				anvilContainer.setNewItemName(string);
			}
		}
	}

	@Override
	public void onUpdateBeacon(UpdateBeaconC2SPacket updateBeaconC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateBeaconC2SPacket, this, this.player.getServerWorld());
		if (this.player.container instanceof BeaconContainer) {
			((BeaconContainer)this.player.container).setEffects(updateBeaconC2SPacket.getPrimaryEffectId(), updateBeaconC2SPacket.getSecondaryEffectId());
		}
	}

	@Override
	public void onStructureBlockUpdate(UpdateStructureBlockC2SPacket updateStructureBlockC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateStructureBlockC2SPacket, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = updateStructureBlockC2SPacket.getPos();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof StructureBlockBlockEntity) {
				StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
				structureBlockBlockEntity.setMode(updateStructureBlockC2SPacket.getMode());
				structureBlockBlockEntity.setStructureName(updateStructureBlockC2SPacket.getStructureName());
				structureBlockBlockEntity.setOffset(updateStructureBlockC2SPacket.getOffset());
				structureBlockBlockEntity.setSize(updateStructureBlockC2SPacket.getSize());
				structureBlockBlockEntity.setMirror(updateStructureBlockC2SPacket.getMirror());
				structureBlockBlockEntity.setRotation(updateStructureBlockC2SPacket.getRotation());
				structureBlockBlockEntity.setMetadata(updateStructureBlockC2SPacket.getMetadata());
				structureBlockBlockEntity.setIgnoreEntities(updateStructureBlockC2SPacket.getIgnoreEntities());
				structureBlockBlockEntity.setShowAir(updateStructureBlockC2SPacket.shouldShowAir());
				structureBlockBlockEntity.setShowBoundingBox(updateStructureBlockC2SPacket.shouldShowBoundingBox());
				structureBlockBlockEntity.setIntegrity(updateStructureBlockC2SPacket.getIntegrity());
				structureBlockBlockEntity.setSeed(updateStructureBlockC2SPacket.getSeed());
				if (structureBlockBlockEntity.hasStructureName()) {
					String string = structureBlockBlockEntity.getStructureName();
					if (updateStructureBlockC2SPacket.getAction() == StructureBlockBlockEntity.Action.field_12110) {
						if (structureBlockBlockEntity.saveStructure()) {
							this.player.addChatMessage(new TranslatableText("structure_block.save_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableText("structure_block.save_failure", string), false);
						}
					} else if (updateStructureBlockC2SPacket.getAction() == StructureBlockBlockEntity.Action.field_12109) {
						if (!structureBlockBlockEntity.isStructureAvailable()) {
							this.player.addChatMessage(new TranslatableText("structure_block.load_not_found", string), false);
						} else if (structureBlockBlockEntity.loadStructure()) {
							this.player.addChatMessage(new TranslatableText("structure_block.load_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableText("structure_block.load_prepare", string), false);
						}
					} else if (updateStructureBlockC2SPacket.getAction() == StructureBlockBlockEntity.Action.field_12106) {
						if (structureBlockBlockEntity.detectStructureSize()) {
							this.player.addChatMessage(new TranslatableText("structure_block.size_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableText("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player.addChatMessage(new TranslatableText("structure_block.invalid_structure_name", updateStructureBlockC2SPacket.getStructureName()), false);
				}

				structureBlockBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void onJigsawUpdate(UpdateJigsawC2SPacket updateJigsawC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateJigsawC2SPacket, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = updateJigsawC2SPacket.getPos();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof JigsawBlockEntity) {
				JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
				jigsawBlockEntity.setAttachmentType(updateJigsawC2SPacket.getAttachmentType());
				jigsawBlockEntity.setTargetPool(updateJigsawC2SPacket.getTargetPool());
				jigsawBlockEntity.setFinalState(updateJigsawC2SPacket.getFinalState());
				jigsawBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void onVillagerTradeSelect(SelectVillagerTradeC2SPacket selectVillagerTradeC2SPacket) {
		NetworkThreadUtils.forceMainThread(selectVillagerTradeC2SPacket, this, this.player.getServerWorld());
		int i = selectVillagerTradeC2SPacket.method_12431();
		Container container = this.player.container;
		if (container instanceof MerchantContainer) {
			MerchantContainer merchantContainer = (MerchantContainer)container;
			merchantContainer.setRecipeIndex(i);
			merchantContainer.switchTo(i);
		}
	}

	@Override
	public void onBookUpdate(BookUpdateC2SPacket bookUpdateC2SPacket) {
		ItemStack itemStack = bookUpdateC2SPacket.getBook();
		if (!itemStack.isEmpty()) {
			if (WritableBookItem.isValid(itemStack.getTag())) {
				ItemStack itemStack2 = this.player.getStackInHand(bookUpdateC2SPacket.getHand());
				if (itemStack.getItem() == Items.field_8674 && itemStack2.getItem() == Items.field_8674) {
					if (bookUpdateC2SPacket.wasSigned()) {
						ItemStack itemStack3 = new ItemStack(Items.field_8360);
						CompoundTag compoundTag = itemStack2.getTag();
						if (compoundTag != null) {
							itemStack3.setTag(compoundTag.method_10553());
						}

						itemStack3.putSubTag("author", new StringTag(this.player.getName().getString()));
						itemStack3.putSubTag("title", new StringTag(itemStack.getTag().getString("title")));
						ListTag listTag = itemStack.getTag().getList("pages", 8);

						for (int i = 0; i < listTag.size(); i++) {
							String string = listTag.getString(i);
							Text text = new LiteralText(string);
							string = Text.Serializer.toJson(text);
							listTag.method_10606(i, new StringTag(string));
						}

						itemStack3.putSubTag("pages", listTag);
						this.player.setStackInHand(bookUpdateC2SPacket.getHand(), itemStack3);
					} else {
						itemStack2.putSubTag("pages", itemStack.getTag().getList("pages", 8));
					}
				}
			}
		}
	}

	@Override
	public void onQueryEntityNbt(QueryEntityNbtC2SPacket queryEntityNbtC2SPacket) {
		NetworkThreadUtils.forceMainThread(queryEntityNbtC2SPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			Entity entity = this.player.getServerWorld().getEntityById(queryEntityNbtC2SPacket.getEntityId());
			if (entity != null) {
				CompoundTag compoundTag = entity.toTag(new CompoundTag());
				this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(queryEntityNbtC2SPacket.getTransactionId(), compoundTag));
			}
		}
	}

	@Override
	public void onQueryBlockNbt(QueryBlockNbtC2SPacket queryBlockNbtC2SPacket) {
		NetworkThreadUtils.forceMainThread(queryBlockNbtC2SPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			BlockEntity blockEntity = this.player.getServerWorld().getBlockEntity(queryBlockNbtC2SPacket.getPos());
			CompoundTag compoundTag = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
			this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(queryBlockNbtC2SPacket.getTransactionId(), compoundTag));
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket playerMoveC2SPacket) {
		NetworkThreadUtils.forceMainThread(playerMoveC2SPacket, this, this.player.getServerWorld());
		if (validatePlayerMove(playerMoveC2SPacket)) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
			if (!this.player.notInAnyWorld) {
				if (this.ticks == 0) {
					this.syncWithPlayerPosition();
				}

				if (this.requestedTeleportPos != null) {
					if (this.ticks - this.teleportRequestTick > 20) {
						this.teleportRequestTick = this.ticks;
						this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
					}
				} else {
					this.teleportRequestTick = this.ticks;
					if (this.player.hasVehicle()) {
						this.player
							.setPositionAnglesAndUpdate(
								this.player.x, this.player.y, this.player.z, playerMoveC2SPacket.getYaw(this.player.yaw), playerMoveC2SPacket.getPitch(this.player.pitch)
							);
						this.player.getServerWorld().method_14178().updateCameraPosition(this.player);
					} else {
						double d = this.player.x;
						double e = this.player.y;
						double f = this.player.z;
						double g = this.player.y;
						double h = playerMoveC2SPacket.getX(this.player.x);
						double i = playerMoveC2SPacket.getY(this.player.y);
						double j = playerMoveC2SPacket.getZ(this.player.z);
						float k = playerMoveC2SPacket.getYaw(this.player.yaw);
						float l = playerMoveC2SPacket.getPitch(this.player.pitch);
						double m = h - this.lastTickX;
						double n = i - this.lastTickY;
						double o = j - this.lastTickZ;
						double p = this.player.getVelocity().lengthSquared();
						double q = m * m + n * n + o * o;
						if (this.player.isSleeping()) {
							if (q > 1.0) {
								this.requestTeleport(
									this.player.x, this.player.y, this.player.z, playerMoveC2SPacket.getYaw(this.player.yaw), playerMoveC2SPacket.getPitch(this.player.pitch)
								);
							}
						} else {
							this.movePacketsCount++;
							int r = this.movePacketsCount - this.lastTickMovePacketsCount;
							if (r > 5) {
								LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), r);
								r = 1;
							}

							if (!this.player.isInTeleportationState()
								&& (!this.player.getServerWorld().getGameRules().getBoolean(GameRules.field_19404) || !this.player.isFallFlying())) {
								float s = this.player.isFallFlying() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && !this.isServerOwner()) {
									LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), m, n, o);
									this.requestTeleport(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
									return;
								}
							}

							boolean bl = this.method_20630(serverWorld);
							m = h - this.updatedX;
							n = i - this.updatedY;
							o = j - this.updatedZ;
							if (this.player.onGround && !playerMoveC2SPacket.isOnGround() && n > 0.0) {
								this.player.jump();
							}

							this.player.move(MovementType.field_6305, new Vec3d(m, n, o));
							this.player.onGround = playerMoveC2SPacket.isOnGround();
							m = h - this.player.x;
							n = i - this.player.y;
							if (n > -0.5 || n < 0.5) {
								n = 0.0;
							}

							o = j - this.player.z;
							q = m * m + n * n + o * o;
							boolean bl2 = false;
							if (!this.player.isInTeleportationState()
								&& q > 0.0625
								&& !this.player.isSleeping()
								&& !this.player.interactionManager.isCreative()
								&& this.player.interactionManager.getGameMode() != GameMode.field_9219) {
								bl2 = true;
								LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
							}

							this.player.setPositionAnglesAndUpdate(h, i, j, k, l);
							this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
							if (!this.player.noClip && !this.player.isSleeping()) {
								boolean bl3 = this.method_20630(serverWorld);
								if (bl && (bl2 || !bl3)) {
									this.requestTeleport(d, e, f, k, l);
									return;
								}
							}

							this.floating = n >= -0.03125
								&& this.player.interactionManager.getGameMode() != GameMode.field_9219
								&& !this.server.isFlightEnabled()
								&& !this.player.abilities.allowFlying
								&& !this.player.hasStatusEffect(StatusEffects.field_5902)
								&& !this.player.isFallFlying()
								&& !serverWorld.isAreaNotEmpty(this.player.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
							this.player.onGround = playerMoveC2SPacket.isOnGround();
							this.player.getServerWorld().method_14178().updateCameraPosition(this.player);
							this.player.method_14207(this.player.y - g, playerMoveC2SPacket.isOnGround());
							this.updatedX = this.player.x;
							this.updatedY = this.player.y;
							this.updatedZ = this.player.z;
						}
					}
				}
			}
		}
	}

	private boolean method_20630(ViewableWorld viewableWorld) {
		return viewableWorld.doesNotCollide(this.player, this.player.getBoundingBox().contract(1.0E-5F));
	}

	public void requestTeleport(double d, double e, double f, float g, float h) {
		this.teleportRequest(d, e, f, g, h, Collections.emptySet());
	}

	public void teleportRequest(double d, double e, double f, float g, float h, Set<PlayerPositionLookS2CPacket.Flag> set) {
		double i = set.contains(PlayerPositionLookS2CPacket.Flag.field_12400) ? this.player.x : 0.0;
		double j = set.contains(PlayerPositionLookS2CPacket.Flag.field_12398) ? this.player.y : 0.0;
		double k = set.contains(PlayerPositionLookS2CPacket.Flag.field_12403) ? this.player.z : 0.0;
		float l = set.contains(PlayerPositionLookS2CPacket.Flag.field_12401) ? this.player.yaw : 0.0F;
		float m = set.contains(PlayerPositionLookS2CPacket.Flag.field_12397) ? this.player.pitch : 0.0F;
		this.requestedTeleportPos = new Vec3d(d, e, f);
		if (++this.requestedTeleportId == Integer.MAX_VALUE) {
			this.requestedTeleportId = 0;
		}

		this.teleportRequestTick = this.ticks;
		this.player.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(d - i, e - j, f - k, g - l, h - m, set, this.requestedTeleportId));
	}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket playerActionC2SPacket) {
		NetworkThreadUtils.forceMainThread(playerActionC2SPacket, this, this.player.getServerWorld());
		BlockPos blockPos = playerActionC2SPacket.getPos();
		this.player.updateLastActionTime();
		PlayerActionC2SPacket.Action action = playerActionC2SPacket.getAction();
		switch (action) {
			case field_12969:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.getStackInHand(Hand.field_5810);
					this.player.setStackInHand(Hand.field_5810, this.player.getStackInHand(Hand.field_5808));
					this.player.setStackInHand(Hand.field_5808, itemStack);
				}

				return;
			case field_12975:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(false);
				}

				return;
			case field_12970:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(true);
				}

				return;
			case field_12974:
				this.player.stopUsingItem();
				return;
			case field_12968:
			case field_12971:
			case field_12973:
				this.player.interactionManager.method_14263(blockPos, action, playerActionC2SPacket.getDirection(), this.server.getWorldHeight());
				return;
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractBlockC2SPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = playerInteractBlockC2SPacket.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		BlockHitResult blockHitResult = playerInteractBlockC2SPacket.getHitY();
		BlockPos blockPos = blockHitResult.getBlockPos();
		Direction direction = blockHitResult.getSide();
		this.player.updateLastActionTime();
		if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.field_11036 && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.requestedTeleportPos == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& serverWorld.canPlayerModifyAt(this.player, blockPos)) {
				this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
			}
		} else {
			Text text = new TranslatableText("build.tooHigh", this.server.getWorldHeight()).formatted(Formatting.field_1061);
			this.player.networkHandler.sendPacket(new ChatMessageS2CPacket(text, MessageType.field_11733));
		}

		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
	}

	@Override
	public void onPlayerInteractItem(PlayerInteractItemC2SPacket playerInteractItemC2SPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractItemC2SPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = playerInteractItemC2SPacket.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.updateLastActionTime();
		if (!itemStack.isEmpty()) {
			this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
		}
	}

	@Override
	public void onSpectatorTeleport(SpectatorTeleportC2SPacket spectatorTeleportC2SPacket) {
		NetworkThreadUtils.forceMainThread(spectatorTeleportC2SPacket, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			for (ServerWorld serverWorld : this.server.getWorlds()) {
				Entity entity = spectatorTeleportC2SPacket.getTarget(serverWorld);
				if (entity != null) {
					this.player.teleport(serverWorld, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
					return;
				}
			}
		}
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusC2SPacket resourcePackStatusC2SPacket) {
	}

	@Override
	public void onBoatPaddleState(BoatPaddleStateC2SPacket boatPaddleStateC2SPacket) {
		NetworkThreadUtils.forceMainThread(boatPaddleStateC2SPacket, this, this.player.getServerWorld());
		Entity entity = this.player.getVehicle();
		if (entity instanceof BoatEntity) {
			((BoatEntity)entity).setPaddleMovings(boatPaddleStateC2SPacket.isLeftPaddling(), boatPaddleStateC2SPacket.isRightPaddling());
		}
	}

	@Override
	public void onDisconnected(Text text) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), text.getString());
		this.server.forcePlayerSampleUpdate();
		this.server.getPlayerManager().sendToAll(new TranslatableText("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.field_1054));
		this.player.method_14231();
		this.server.getPlayerManager().remove(this.player);
		if (this.isServerOwner()) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stop(false);
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.sendPacket(packet, null);
	}

	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (packet instanceof ChatMessageS2CPacket) {
			ChatMessageS2CPacket chatMessageS2CPacket = (ChatMessageS2CPacket)packet;
			ChatVisibility chatVisibility = this.player.getClientChatVisibility();
			if (chatVisibility == ChatVisibility.HIDDEN && chatMessageS2CPacket.getLocation() != MessageType.field_11733) {
				return;
			}

			if (chatVisibility == ChatVisibility.SYSTEM && !chatMessageS2CPacket.isNonChat()) {
				return;
			}
		}

		try {
			this.client.send(packet, genericFutureListener);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Sending packet");
			CrashReportSection crashReportSection = crashReport.addElement("Packet being sent");
			crashReportSection.add("Packet class", (CrashCallable<String>)(() -> packet.getClass().getCanonicalName()));
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateSelectedSlotC2SPacket, this, this.player.getServerWorld());
		if (updateSelectedSlotC2SPacket.getSelectedSlot() >= 0 && updateSelectedSlotC2SPacket.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
			this.player.inventory.selectedSlot = updateSelectedSlotC2SPacket.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
		}
	}

	@Override
	public void onChatMessage(ChatMessageC2SPacket chatMessageC2SPacket) {
		NetworkThreadUtils.forceMainThread(chatMessageC2SPacket, this, this.player.getServerWorld());
		if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
			this.sendPacket(new ChatMessageS2CPacket(new TranslatableText("chat.cannotSend").formatted(Formatting.field_1061)));
		} else {
			this.player.updateLastActionTime();
			String string = chatMessageC2SPacket.getChatMessage();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!SharedConstants.isValidChar(string.charAt(i))) {
					this.disconnect(new TranslatableText("multiplayer.disconnect.illegal_characters"));
					return;
				}
			}

			if (string.startsWith("/")) {
				this.executeCommand(string);
			} else {
				Text text = new TranslatableText("chat.type.text", this.player.getDisplayName(), string);
				this.server.getPlayerManager().broadcastChatMessage(text, false);
			}

			this.messageCooldown += 20;
			if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
				this.disconnect(new TranslatableText("disconnect.spam"));
			}
		}
	}

	private void executeCommand(String string) {
		this.server.getCommandManager().execute(this.player.getCommandSource(), string);
	}

	@Override
	public void onHandSwing(HandSwingC2SPacket handSwingC2SPacket) {
		NetworkThreadUtils.forceMainThread(handSwingC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		this.player.swingHand(handSwingC2SPacket.getHand());
	}

	@Override
	public void onClientCommand(ClientCommandC2SPacket clientCommandC2SPacket) {
		NetworkThreadUtils.forceMainThread(clientCommandC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		switch (clientCommandC2SPacket.getMode()) {
			case field_12979:
				this.player.setSneaking(true);
				break;
			case field_12984:
				this.player.setSneaking(false);
				break;
			case field_12981:
				this.player.setSprinting(true);
				break;
			case field_12985:
				this.player.setSprinting(false);
				break;
			case field_12986:
				if (this.player.isSleeping()) {
					this.player.wakeUp(false, true, true);
					this.requestedTeleportPos = new Vec3d(this.player.x, this.player.y, this.player.z);
				}
				break;
			case field_12987:
				if (this.player.getVehicle() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getVehicle();
					int i = clientCommandC2SPacket.getMountJumpHeight();
					if (jumpingMount.canJump() && i > 0) {
						jumpingMount.startJumping(i);
					}
				}
				break;
			case field_12980:
				if (this.player.getVehicle() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getVehicle();
					jumpingMount.stopJumping();
				}
				break;
			case field_12988:
				if (this.player.getVehicle() instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.getVehicle()).openInventory(this.player);
				}
				break;
			case field_12982:
				if (!this.player.onGround && this.player.getVelocity().y < 0.0 && !this.player.isFallFlying() && !this.player.isInsideWater()) {
					ItemStack itemStack = this.player.getEquippedStack(EquipmentSlot.field_6174);
					if (itemStack.getItem() == Items.field_8833 && ElytraItem.isUsable(itemStack)) {
						this.player.method_14243();
					}
				} else {
					this.player.method_14229();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractEntityC2SPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Entity entity = playerInteractEntityC2SPacket.getEntity(serverWorld);
		this.player.updateLastActionTime();
		if (entity != null) {
			boolean bl = this.player.canSee(entity);
			float f = this.player.method_21753(1.0F) + 0.25F + entity.getWidth() * 0.5F;
			double d = (double)(f * f);
			if (!bl) {
				d = 9.0;
			}

			if (this.player.squaredDistanceTo(entity) < d) {
				if (playerInteractEntityC2SPacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.field_12876) {
					Hand hand = playerInteractEntityC2SPacket.getHand();
					this.player.interact(entity, hand);
				} else if (playerInteractEntityC2SPacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.field_12873) {
					Hand hand = playerInteractEntityC2SPacket.getHand();
					entity.interactAt(this.player, playerInteractEntityC2SPacket.getHitPosition(), hand);
				} else if (playerInteractEntityC2SPacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.field_12875) {
					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ProjectileEntity || entity == this.player) {
						this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_entity_attacked"));
						this.server.warn("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
						return;
					}

					this.player.attack(entity);
				}
			}
		} else {
			this.player.method_21750();
		}
	}

	@Override
	public void onClientStatus(ClientStatusC2SPacket clientStatusC2SPacket) {
		NetworkThreadUtils.forceMainThread(clientStatusC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ClientStatusC2SPacket.Mode mode = clientStatusC2SPacket.getMode();
		switch (mode) {
			case field_12774:
				if (this.player.notInAnyWorld) {
					this.player.notInAnyWorld = false;
					this.player = this.server.getPlayerManager().respawnPlayer(this.player, DimensionType.field_13072, true);
					Criterions.CHANGED_DIMENSION.handle(this.player, DimensionType.field_13078, DimensionType.field_13072);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().respawnPlayer(this.player, DimensionType.field_13072, false);
					if (this.server.isHardcore()) {
						this.player.setGameMode(GameMode.field_9219);
						this.player.getServerWorld().getGameRules().get(GameRules.field_19402).set(false, this.server);
					}
				}
				break;
			case field_12775:
				this.player.getStatHandler().sendStats(this.player);
		}
	}

	@Override
	public void onGuiClose(GuiCloseC2SPacket guiCloseC2SPacket) {
		NetworkThreadUtils.forceMainThread(guiCloseC2SPacket, this, this.player.getServerWorld());
		this.player.method_14247();
	}

	@Override
	public void onClickWindow(ClickWindowC2SPacket clickWindowC2SPacket) {
		NetworkThreadUtils.forceMainThread(clickWindowC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.container.syncId == clickWindowC2SPacket.getSyncId() && this.player.container.isRestricted(this.player)) {
			if (this.player.isSpectator()) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.of();

				for (int i = 0; i < this.player.container.slotList.size(); i++) {
					defaultedList.add(((Slot)this.player.container.slotList.get(i)).getStack());
				}

				this.player.onContainerRegistered(this.player.container, defaultedList);
			} else {
				ItemStack itemStack = this.player
					.container
					.onSlotClick(clickWindowC2SPacket.getSlot(), clickWindowC2SPacket.getButton(), clickWindowC2SPacket.getActionType(), this.player);
				if (ItemStack.areEqualIgnoreDamage(clickWindowC2SPacket.getStack(), itemStack)) {
					this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(clickWindowC2SPacket.getSyncId(), clickWindowC2SPacket.getTransactionId(), true));
					this.player.field_13991 = true;
					this.player.container.sendContentUpdates();
					this.player.method_14241();
					this.player.field_13991 = false;
				} else {
					this.transactions.put(this.player.container.syncId, clickWindowC2SPacket.getTransactionId());
					this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(clickWindowC2SPacket.getSyncId(), clickWindowC2SPacket.getTransactionId(), false));
					this.player.container.setPlayerRestriction(this.player, false);
					DefaultedList<ItemStack> defaultedList2 = DefaultedList.of();

					for (int j = 0; j < this.player.container.slotList.size(); j++) {
						ItemStack itemStack2 = ((Slot)this.player.container.slotList.get(j)).getStack();
						defaultedList2.add(itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
					}

					this.player.onContainerRegistered(this.player.container, defaultedList2);
				}
			}
		}
	}

	@Override
	public void onCraftRequest(CraftRequestC2SPacket craftRequestC2SPacket) {
		NetworkThreadUtils.forceMainThread(craftRequestC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (!this.player.isSpectator()
			&& this.player.container.syncId == craftRequestC2SPacket.getSyncId()
			&& this.player.container.isRestricted(this.player)
			&& this.player.container instanceof CraftingContainer) {
			this.server
				.getRecipeManager()
				.get(craftRequestC2SPacket.getRecipe())
				.ifPresent(recipe -> ((CraftingContainer)this.player.container).fillInputSlots(craftRequestC2SPacket.shouldCraftAll(), recipe, this.player));
		}
	}

	@Override
	public void onButtonClick(ButtonClickC2SPacket buttonClickC2SPacket) {
		NetworkThreadUtils.forceMainThread(buttonClickC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.container.syncId == buttonClickC2SPacket.getSyncId() && this.player.container.isRestricted(this.player) && !this.player.isSpectator()) {
			this.player.container.onButtonClick(this.player, buttonClickC2SPacket.getButtonId());
			this.player.container.sendContentUpdates();
		}
	}

	@Override
	public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
		NetworkThreadUtils.forceMainThread(creativeInventoryActionC2SPacket, this, this.player.getServerWorld());
		if (this.player.interactionManager.isCreative()) {
			boolean bl = creativeInventoryActionC2SPacket.getSlot() < 0;
			ItemStack itemStack = creativeInventoryActionC2SPacket.getItemStack();
			CompoundTag compoundTag = itemStack.getSubTag("BlockEntityTag");
			if (!itemStack.isEmpty() && compoundTag != null && compoundTag.containsKey("x") && compoundTag.containsKey("y") && compoundTag.containsKey("z")) {
				BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
				BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
					compoundTag2.remove("x");
					compoundTag2.remove("y");
					compoundTag2.remove("z");
					itemStack.putSubTag("BlockEntityTag", compoundTag2);
				}
			}

			boolean bl2 = creativeInventoryActionC2SPacket.getSlot() >= 1 && creativeInventoryActionC2SPacket.getSlot() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getCount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				if (itemStack.isEmpty()) {
					this.player.playerContainer.setStackInSlot(creativeInventoryActionC2SPacket.getSlot(), ItemStack.EMPTY);
				} else {
					this.player.playerContainer.setStackInSlot(creativeInventoryActionC2SPacket.getSlot(), itemStack);
				}

				this.player.playerContainer.setPlayerRestriction(this.player, true);
				this.player.playerContainer.sendContentUpdates();
			} else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
				this.creativeItemDropThreshold += 20;
				ItemEntity itemEntity = this.player.dropItem(itemStack, true);
				if (itemEntity != null) {
					itemEntity.setCreativeDespawnTime();
				}
			}
		}
	}

	@Override
	public void onConfirmTransaction(GuiActionConfirmC2SPacket guiActionConfirmC2SPacket) {
		NetworkThreadUtils.forceMainThread(guiActionConfirmC2SPacket, this, this.player.getServerWorld());
		int i = this.player.container.syncId;
		if (i == guiActionConfirmC2SPacket.getWindowId()
			&& this.transactions.getOrDefault(i, (short)(guiActionConfirmC2SPacket.getSyncId() + 1)) == guiActionConfirmC2SPacket.getSyncId()
			&& !this.player.container.isRestricted(this.player)
			&& !this.player.isSpectator()) {
			this.player.container.setPlayerRestriction(this.player, true);
		}
	}

	@Override
	public void onSignUpdate(UpdateSignC2SPacket updateSignC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateSignC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = updateSignC2SPacket.getPos();
		if (serverWorld.isBlockLoaded(blockPos)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
			if (!(blockEntity instanceof SignBlockEntity)) {
				return;
			}

			SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
			if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
				this.server.warn("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
				return;
			}

			String[] strings = updateSignC2SPacket.getText();

			for (int i = 0; i < strings.length; i++) {
				signBlockEntity.setTextOnRow(i, new LiteralText(Formatting.strip(strings[i])));
			}

			signBlockEntity.markDirty();
			serverWorld.updateListeners(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void onKeepAlive(KeepAliveC2SPacket keepAliveC2SPacket) {
		if (this.waitingForKeepAlive && keepAliveC2SPacket.getId() == this.keepAliveId) {
			int i = (int)(SystemUtil.getMeasuringTimeMs() - this.lastKeepAliveTime);
			this.player.pingMilliseconds = (this.player.pingMilliseconds * 3 + i) / 4;
			this.waitingForKeepAlive = false;
		} else if (!this.isServerOwner()) {
			this.disconnect(new TranslatableText("disconnect.timeout"));
		}
	}

	@Override
	public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket updatePlayerAbilitiesC2SPacket) {
		NetworkThreadUtils.forceMainThread(updatePlayerAbilitiesC2SPacket, this, this.player.getServerWorld());
		this.player.abilities.flying = updatePlayerAbilitiesC2SPacket.isFlying() && this.player.abilities.allowFlying;
	}

	@Override
	public void onClientSettings(ClientSettingsC2SPacket clientSettingsC2SPacket) {
		NetworkThreadUtils.forceMainThread(clientSettingsC2SPacket, this, this.player.getServerWorld());
		this.player.setClientSettings(clientSettingsC2SPacket);
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket customPayloadC2SPacket) {
	}

	@Override
	public void onUpdateDifficulty(UpdateDifficultyC2SPacket updateDifficultyC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateDifficultyC2SPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2) || this.isServerOwner()) {
			this.server.setDifficulty(updateDifficultyC2SPacket.getDifficulty(), false);
		}
	}

	@Override
	public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket updateDifficultyLockC2SPacket) {
		NetworkThreadUtils.forceMainThread(updateDifficultyLockC2SPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2) || this.isServerOwner()) {
			this.server.setDifficultyLocked(updateDifficultyLockC2SPacket.isDifficultyLocked());
		}
	}
}
