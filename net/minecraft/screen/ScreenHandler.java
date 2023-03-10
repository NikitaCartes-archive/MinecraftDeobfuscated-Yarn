/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * Manages lists of item stacks and properties between the server and the client for use
 * in a screen. They are usually used for synchronizing the screens of container blocks
 * such as chests and furnaces.
 * 
 * <p>On the client, screen handlers are coupled with a {@link
 * net.minecraft.client.gui.screen.ingame.HandledScreen}. Handled screens have a
 * reference to a client-sided screen handler that is exposed through the
 * {@link net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider} interface.
 * 
 * <h2 id="models">Models</h2>
 * <p>Screen handlers hold slots, properties, property delegates, and screen handler
 * contexts. This allows easy synchronization of states between the client and the
 * server, and prevents running code on the wrong side.
 * 
 * <p>{@link Slot} holds one item stack. The slots are usually controlled by the server,
 * and the changes to slots on the server are automatically synchronized to the client.
 * Slots can be backed by an inventory, allowing the changes to be reflected to the
 * persistent storage (like block entities) on the server. Clients manipulate the
 * slots by issuing a "slot click" packet. "Clicking" a slot includes actions like
 * picking up crafting result, shift-clicking stacks, swapping stacks between the
 * inventory and the hotbar, or dropping stacks.
 * 
 * <p>Screen handlers also contain a list of {@linkplain Property properties}
 * that are used for syncing integers (e.g. progress bars) from the server to the client.
 * Properties can also be used to sync an integer from the client to the server, although
 * it has to be manually performed. If a property relies on other objects, like
 * a value from a block entity instance, then the property can delegate its operations
 * using {@link PropertyDelegate}. The delegate is passed when creating the screen handler.
 * On the server, access to the property's value is delegated to the delegate (which in
 * turn delegates to another object like a block entity instance).
 * On the client, access to the property's value still uses the synced value.
 * 
 * <p>{@link ScreenHandlerContext} allows running code on the server side only. Screen
 * handlers are designed to be used on both sides; any action modifying the world has
 * to be wrapped in a call to the context. Like with the property delegate, a context
 * with the world is passed to the screen handler on creation on the server. On the
 * server, the context executes the function with the world and the position. On the
 * client, the context does nothing.
 * 
 * <h2 id="usage">How to use screen handlers</h2>
 * <h3 id="creation">Creation</h3>
 * <p>To create a new screen handler, subclass {@link ScreenHandler}, create and register
 * a new {@linkplain ScreenHandlerType screen handler type}, and associate it with
 * a handled screen.
 * 
 * <p>A subclass should have two constructors. One is for the server, and should take
 * the {@code syncId} and inventories, property delegates, or contexts that are used.
 * The {@link #syncId} is shared between the two sides. It is used to verify that a player
 * has a specific screen (handler) open so that they can move items, for example.
 * The inventories are used to back a slot so that any changes to a slot is reflected
 * on the backing inventory, and vice versa. Property delegates and contexts bridge
 * between the screen handler and other parts of the world; see above for more description.
 * 
 * <p>The constructor should {@linkplain #addSlot add slots}, {@link #addProperties
 * add properties from delegates}, and store the property delegates and screen handler
 * context in the instance fields.
 * 
 * <p>The other constructor is for the client. There, the only parameters allowed are the
 * {@code syncId} and the player inventory. This is because all other things are
 * unavailable at creation time and synced later. This constructor should call the
 * other constructor with {@linkplain net.minecraft.inventory.SimpleInventory
 * a new simple inventory of sufficient size}, {@linkplain ArrayPropertyDelegate
 * a new array property delegate}, and {@linkplain ScreenHandlerContext#EMPTY
 * an empty screen handler context}. Synced data then fills the inventory and property
 * delegate.
 * 
 * <p>The screen handler then has to be registered in a registry. Create a new instance of
 * {@link ScreenHandlerType} with the screen handler type factory (which can be a reference
 * to the client-side constructor; i.e. {@code MyScreenHandler::MyScreenHandler})
 * and register it to {@link net.minecraft.registry.Registries#SCREEN_HANDLER}.
 * 
 * <h3 id="opening">Opening</h3>
 * <p>Most of the screen handlers are associated with a block and opened by using the block.
 * Screen handlers are opened on the server and synced to the client. To open a
 * screen handler, use {@link PlayerEntity#openHandledScreen}. This takes a
 * {@link NamedScreenHandlerFactory}, which creates a screen handler. In vanilla,
 * block entity instances implement the interface, allowing them to be passed.
 * {@link SimpleNamedScreenHandlerFactory} is a screen handler factory implementation
 * for use cases that do not involve a block entity.
 * 
 * <p>The factory should create a new instance of a screen handler with the server-side
 * constructor (one that takes inventories, etc). If the screen handler requires
 * a property delegate or a context, create an instance and pass it here.
 * 
 * <p>As long as the screen handler only uses the slots and properties, there should not
 * be any need for external synchronization.
 * 
 * <h3 id="interaction">Interaction</h3>
 * <p>Screen handler interaction mainly involves "slot clicks" and "button clicks".
 * A {@linkplain #onSlotClick slot click} is, as mentioned before, an action manipulating
 * the slots' held item stacks. Slot clicks are implemented in this class and
 * {@link #quickMove}. To manipulate the stacks, get the slot via {@link #getSlot}
 * and call methods of it. Screen handlers also provide methods for common operations,
 * such as {@link #insertItem} that inserts a stack to the screen handler's available slots.
 * 
 * <p>The "cursor stack" is an item stack held by the cursor. When moving item stacks
 * between slots, the cursor stack can hold the stack temporarily. The cursor stack
 * is not held by any slots. When the screen handler is closed, the stack will be
 * inserted to the player inventory or dropped as an item entity.
 * 
 * <p>Some screen handlers also handle {@linkplain #onButtonClick button clicks}.
 * This is used to execute an action on the server as a response to clients sending a
 * button click packet. In most cases, this is triggered by a button in the screen
 * rendered by the client, hence the name. Inside screen handlers, buttons are identified
 * with an integer.
 * 
 * <p>Subclasses must implement two methods: {@link #canUse(PlayerEntity)} and {@link
 * #quickMove}. See the documentation of each method for more details.
 * 
 * <h3 id="closing">Closing</h3>
 * <p>Since a screen handler handles the client's screen, the screen must be closed at the
 * same time. To close the screen handler and the screen, call {@link
 * PlayerEntity#closeHandledScreen} on the server.
 * 
 * <p>Screen handlers should override {@link #onClosed}. In there, it should {@linkplain
 * #dropInventory drop contents} of all slots not backed by an inventory and call
 * {@link Inventory#onClose} on the backing inventory. See the documentation of
 * the method for more details.
 * 
 * @see ScreenHandlerType
 * @see ScreenHandlerFactory
 * @see Slot
 * @see Inventory
 * @see net.minecraft.client.gui.screen.ingame.HandledScreen
 */
public abstract class ScreenHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    /**
     * A special slot index value ({@value}) indicating that the player has clicked outside the main panel
     * of a screen. Used for dropping the cursor stack.
     */
    public static final int EMPTY_SPACE_SLOT_INDEX = -999;
    public static final int field_30731 = 0;
    public static final int field_30732 = 1;
    public static final int field_30733 = 2;
    public static final int field_30734 = 0;
    public static final int field_30735 = 1;
    public static final int field_30736 = 2;
    public static final int field_30737 = Integer.MAX_VALUE;
    /**
     * A list of item stacks that is used for tracking changes in {@link #sendContentUpdates()}.
     */
    private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
    public final DefaultedList<Slot> slots = DefaultedList.of();
    private final List<Property> properties = Lists.newArrayList();
    private ItemStack cursorStack = ItemStack.EMPTY;
    private final DefaultedList<ItemStack> previousTrackedStacks = DefaultedList.of();
    private final IntList trackedPropertyValues = new IntArrayList();
    private ItemStack previousCursorStack = ItemStack.EMPTY;
    private int revision;
    @Nullable
    private final ScreenHandlerType<?> type;
    public final int syncId;
    private int quickCraftButton = -1;
    private int quickCraftStage;
    private final Set<Slot> quickCraftSlots = Sets.newHashSet();
    private final List<ScreenHandlerListener> listeners = Lists.newArrayList();
    @Nullable
    private ScreenHandlerSyncHandler syncHandler;
    private boolean disableSync;

    protected ScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        this.type = type;
        this.syncId = syncId;
    }

    /**
     * {@return whether the screen handler can be used}
     * 
     * @apiNote This should be called inside {@link #canUse(PlayerEntity)}.
     * 
     * @implNote On the server, this checks that the block at the position is
     * {@code block} and the player is within 8 blocks from the block's center.
     * 
     * @see #canUse(PlayerEntity)
     */
    protected static boolean canUse(ScreenHandlerContext context, PlayerEntity player, Block block) {
        return context.get((world, pos) -> {
            if (!world.getBlockState((BlockPos)pos).isOf(block)) {
                return false;
            }
            return player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
        }, true);
    }

    /**
     * {@return the screen handler type}
     * 
     * <p>A screen handler must have associated screen handler type to open it.
     * 
     * @throws UnsupportedOperationException if the type is not passed in the constructor
     */
    public ScreenHandlerType<?> getType() {
        if (this.type == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.type;
    }

    /**
     * Checks that the size of the provided inventory is at least as large as the {@code expectedSize}.
     * 
     * @throws IllegalArgumentException if the inventory size is smaller than {@code expectedSize}
     */
    protected static void checkSize(Inventory inventory, int expectedSize) {
        int i = inventory.size();
        if (i < expectedSize) {
            throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + expectedSize);
        }
    }

    /**
     * Checks that the size of the {@code data} is at least as large as the {@code expectedCount}.
     * 
     * @throws IllegalArgumentException if the {@code data} has a smaller size than {@code expectedCount}
     */
    protected static void checkDataCount(PropertyDelegate data, int expectedCount) {
        int i = data.size();
        if (i < expectedCount) {
            throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + expectedCount);
        }
    }

    /**
     * {@return whether the given slot index is valid}
     * 
     * <p>This returns {@code true} for all added slots, {@value #EMPTY_SPACE_SLOT_INDEX},
     * and {@code -1}.
     */
    public boolean isValid(int slot) {
        return slot == -1 || slot == -999 || slot < this.slots.size();
    }

    /**
     * Adds {@code slot} to this screen handler. This must be called inside
     * the subclass's constructor.
     * 
     * @return the added slot
     */
    protected Slot addSlot(Slot slot) {
        slot.id = this.slots.size();
        this.slots.add(slot);
        this.trackedStacks.add(ItemStack.EMPTY);
        this.previousTrackedStacks.add(ItemStack.EMPTY);
        return slot;
    }

    /**
     * Adds {@code property} to this screen handler. This must be called inside the
     * subclass's constructor.
     * 
     * <p>If the property relies on external objects (such as a block entity instance),
     * it should instead use property delegates and {@link #addProperties}.
     * 
     * @return the added property
     * 
     * @see #addProperties
     */
    protected Property addProperty(Property property) {
        this.properties.add(property);
        this.trackedPropertyValues.add(0);
        return property;
    }

    /**
     * Adds properties of {@code propertyDelegate} to this screen handler.
     * This must be called inside the subclass's constructor.
     * 
     * @see #addProperty
     */
    protected void addProperties(PropertyDelegate propertyDelegate) {
        for (int i = 0; i < propertyDelegate.size(); ++i) {
            this.addProperty(Property.create(propertyDelegate, i));
        }
    }

    /**
     * Adds {@code listener} to the screen handler.
     * 
     * <p>Listeners are often used to listen to slot or property changes on the
     * client's screen.
     */
    public void addListener(ScreenHandlerListener listener) {
        if (this.listeners.contains(listener)) {
            return;
        }
        this.listeners.add(listener);
        this.sendContentUpdates();
    }

    public void updateSyncHandler(ScreenHandlerSyncHandler handler) {
        this.syncHandler = handler;
        this.syncState();
    }

    public void syncState() {
        int i;
        int j = this.slots.size();
        for (i = 0; i < j; ++i) {
            this.previousTrackedStacks.set(i, this.slots.get(i).getStack().copy());
        }
        this.previousCursorStack = this.getCursorStack().copy();
        j = this.properties.size();
        for (i = 0; i < j; ++i) {
            this.trackedPropertyValues.set(i, this.properties.get(i).get());
        }
        if (this.syncHandler != null) {
            this.syncHandler.updateState(this, this.previousTrackedStacks, this.previousCursorStack, this.trackedPropertyValues.toIntArray());
        }
    }

    /**
     * Removes {@code listener} from this screen handler.
     */
    public void removeListener(ScreenHandlerListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * {@return a list of all stacks of the screen handler's slot}
     * 
     * <p>This should not be used in most cases, and modifying the returned list
     * has no effect to the screen handler.
     */
    public DefaultedList<ItemStack> getStacks() {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (Slot slot : this.slots) {
            defaultedList.add(slot.getStack());
        }
        return defaultedList;
    }

    /**
     * Sends updates to listeners if any properties or slot stacks have changed.
     */
    public void sendContentUpdates() {
        int i;
        for (i = 0; i < this.slots.size(); ++i) {
            ItemStack itemStack = this.slots.get(i).getStack();
            com.google.common.base.Supplier<ItemStack> supplier = Suppliers.memoize(itemStack::copy);
            this.updateTrackedSlot(i, itemStack, supplier);
            this.checkSlotUpdates(i, itemStack, supplier);
        }
        this.checkCursorStackUpdates();
        for (i = 0; i < this.properties.size(); ++i) {
            Property property = this.properties.get(i);
            int j = property.get();
            if (property.hasChanged()) {
                this.notifyPropertyUpdate(i, j);
            }
            this.checkPropertyUpdates(i, j);
        }
    }

    public void updateToClient() {
        int i;
        for (i = 0; i < this.slots.size(); ++i) {
            ItemStack itemStack = this.slots.get(i).getStack();
            this.updateTrackedSlot(i, itemStack, itemStack::copy);
        }
        for (i = 0; i < this.properties.size(); ++i) {
            Property property = this.properties.get(i);
            if (!property.hasChanged()) continue;
            this.notifyPropertyUpdate(i, property.get());
        }
        this.syncState();
    }

    private void notifyPropertyUpdate(int index, int value) {
        for (ScreenHandlerListener screenHandlerListener : this.listeners) {
            screenHandlerListener.onPropertyUpdate(this, index, value);
        }
    }

    private void updateTrackedSlot(int slot, ItemStack stack, Supplier<ItemStack> copySupplier) {
        ItemStack itemStack = this.trackedStacks.get(slot);
        if (!ItemStack.areEqual(itemStack, stack)) {
            ItemStack itemStack2 = copySupplier.get();
            this.trackedStacks.set(slot, itemStack2);
            for (ScreenHandlerListener screenHandlerListener : this.listeners) {
                screenHandlerListener.onSlotUpdate(this, slot, itemStack2);
            }
        }
    }

    private void checkSlotUpdates(int slot, ItemStack stack, Supplier<ItemStack> copySupplier) {
        if (this.disableSync) {
            return;
        }
        ItemStack itemStack = this.previousTrackedStacks.get(slot);
        if (!ItemStack.areEqual(itemStack, stack)) {
            ItemStack itemStack2 = copySupplier.get();
            this.previousTrackedStacks.set(slot, itemStack2);
            if (this.syncHandler != null) {
                this.syncHandler.updateSlot(this, slot, itemStack2);
            }
        }
    }

    private void checkPropertyUpdates(int id, int value) {
        if (this.disableSync) {
            return;
        }
        int i = this.trackedPropertyValues.getInt(id);
        if (i != value) {
            this.trackedPropertyValues.set(id, value);
            if (this.syncHandler != null) {
                this.syncHandler.updateProperty(this, id, value);
            }
        }
    }

    private void checkCursorStackUpdates() {
        if (this.disableSync) {
            return;
        }
        if (!ItemStack.areEqual(this.getCursorStack(), this.previousCursorStack)) {
            this.previousCursorStack = this.getCursorStack().copy();
            if (this.syncHandler != null) {
                this.syncHandler.updateCursorStack(this, this.previousCursorStack);
            }
        }
    }

    public void setPreviousTrackedSlot(int slot, ItemStack stack) {
        this.previousTrackedStacks.set(slot, stack.copy());
    }

    public void setPreviousTrackedSlotMutable(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.previousTrackedStacks.size()) {
            LOGGER.debug("Incorrect slot index: {} available slots: {}", (Object)slot, (Object)this.previousTrackedStacks.size());
            return;
        }
        this.previousTrackedStacks.set(slot, stack);
    }

    public void setPreviousCursorStack(ItemStack stack) {
        this.previousCursorStack = stack.copy();
    }

    /**
     * Called when {@code player} clicks a button with {@code id}.
     * 
     * <p>"Button click" is an abstract concept; it does not have to be triggered by a
     * button. Examples of button clicks include selecting a recipe for a stonecutter,
     * turning a page of a lectern's book, or selecting an enchantment on an enchanting table.
     * Buttons are identified by an integer.
     * 
     * @implNote This is normally only called by the server; however, screens that use buttons
     * can call this on the client.
     * 
     * @return whether the button click is handled successfully
     */
    public boolean onButtonClick(PlayerEntity player, int id) {
        return false;
    }

    /**
     * {@return the slot with index {@code index}}
     */
    public Slot getSlot(int index) {
        return this.slots.get(index);
    }

    /**
     * Quick-moves the stack at {@code slot} to other
     * slots of the screen handler that belong to a different inventory or
     * another section of the same inventory. For example, items can be quick-moved
     * between a chest's slots and the player inventory or between the main player inventory
     * and the hotbar.
     * 
     * <p>Subclasses should call {@link #insertItem}, and if the insertion was successful,
     * clear the slot (if the stack is exhausted) or mark it as dirty. See the vanilla
     * subclasses for basic implementation.
     * 
     * <p>Quick-moving is also known as "shift-clicking" since it's usually triggered
     * using <kbd>Shift</kbd>+<kbd>left click</kbd>.
     * 
     * @return {@link ItemStack#EMPTY} when no stack can be transferred, otherwise
     * the original stack
     * 
     * @see #insertItem
     * 
     * @param slot the index of the slot to quick-move from
     */
    public abstract ItemStack quickMove(PlayerEntity var1, int var2);

    /**
     * Performs a slot click. This can behave in many different ways depending mainly on the action type.
     * 
     * @param actionType the type of slot click, check the docs for each {@link SlotActionType} value for details
     */
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        try {
            this.internalOnSlotClick(slotIndex, button, actionType, player);
        } catch (Exception exception) {
            CrashReport crashReport = CrashReport.create(exception, "Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> this.type != null ? Registries.SCREEN_HANDLER.getId(this.type).toString() : "<no type>");
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", slotIndex);
            crashReportSection.add("Button", button);
            crashReportSection.add("Type", (Object)actionType);
            throw new CrashException(crashReport);
        }
    }

    /**
     * The actual logic that handles a slot click. Called by {@link #onSlotClick
     * (int, int, SlotActionType, PlayerEntity)} in a try-catch block that wraps
     * exceptions from this method into a crash report.
     */
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        block39: {
            block50: {
                block46: {
                    ItemStack itemStack;
                    ItemStack itemStack2;
                    Slot slot3;
                    PlayerInventory playerInventory;
                    block49: {
                        block48: {
                            block47: {
                                block44: {
                                    ClickType clickType;
                                    block45: {
                                        block43: {
                                            block37: {
                                                block42: {
                                                    ItemStack itemStack3;
                                                    block41: {
                                                        block40: {
                                                            block38: {
                                                                playerInventory = player.getInventory();
                                                                if (actionType != SlotActionType.QUICK_CRAFT) break block37;
                                                                int i = this.quickCraftStage;
                                                                this.quickCraftStage = ScreenHandler.unpackQuickCraftStage(button);
                                                                if (i == 1 && this.quickCraftStage == 2 || i == this.quickCraftStage) break block38;
                                                                this.endQuickCraft();
                                                                break block39;
                                                            }
                                                            if (!this.getCursorStack().isEmpty()) break block40;
                                                            this.endQuickCraft();
                                                            break block39;
                                                        }
                                                        if (this.quickCraftStage != 0) break block41;
                                                        this.quickCraftButton = ScreenHandler.unpackQuickCraftButton(button);
                                                        if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftButton, player)) {
                                                            this.quickCraftStage = 1;
                                                            this.quickCraftSlots.clear();
                                                        } else {
                                                            this.endQuickCraft();
                                                        }
                                                        break block39;
                                                    }
                                                    if (this.quickCraftStage != 1) break block42;
                                                    Slot slot = this.slots.get(slotIndex);
                                                    if (!ScreenHandler.canInsertItemIntoSlot(slot, itemStack3 = this.getCursorStack(), true) || !slot.canInsert(itemStack3) || this.quickCraftButton != 2 && itemStack3.getCount() <= this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot)) break block39;
                                                    this.quickCraftSlots.add(slot);
                                                    break block39;
                                                }
                                                if (this.quickCraftStage == 2) {
                                                    if (!this.quickCraftSlots.isEmpty()) {
                                                        if (this.quickCraftSlots.size() == 1) {
                                                            int j = this.quickCraftSlots.iterator().next().id;
                                                            this.endQuickCraft();
                                                            this.internalOnSlotClick(j, this.quickCraftButton, SlotActionType.PICKUP, player);
                                                            return;
                                                        }
                                                        ItemStack itemStack22 = this.getCursorStack().copy();
                                                        int k = this.getCursorStack().getCount();
                                                        for (Slot slot2 : this.quickCraftSlots) {
                                                            ItemStack itemStack3 = this.getCursorStack();
                                                            if (slot2 == null || !ScreenHandler.canInsertItemIntoSlot(slot2, itemStack3, true) || !slot2.canInsert(itemStack3) || this.quickCraftButton != 2 && itemStack3.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) continue;
                                                            ItemStack itemStack4 = itemStack22.copy();
                                                            int l = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                                                            ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftButton, itemStack4, l);
                                                            int m = Math.min(itemStack4.getMaxCount(), slot2.getMaxItemCount(itemStack4));
                                                            if (itemStack4.getCount() > m) {
                                                                itemStack4.setCount(m);
                                                            }
                                                            k -= itemStack4.getCount() - l;
                                                            slot2.setStack(itemStack4);
                                                        }
                                                        itemStack22.setCount(k);
                                                        this.setCursorStack(itemStack22);
                                                    }
                                                    this.endQuickCraft();
                                                } else {
                                                    this.endQuickCraft();
                                                }
                                                break block39;
                                            }
                                            if (this.quickCraftStage == 0) break block43;
                                            this.endQuickCraft();
                                            break block39;
                                        }
                                        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE || button != 0 && button != 1) break block44;
                                        ClickType clickType2 = clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
                                        if (slotIndex != EMPTY_SPACE_SLOT_INDEX) break block45;
                                        if (this.getCursorStack().isEmpty()) break block39;
                                        if (clickType == ClickType.LEFT) {
                                            player.dropItem(this.getCursorStack(), true);
                                            this.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            player.dropItem(this.getCursorStack().split(1), true);
                                        }
                                        break block39;
                                    }
                                    if (actionType == SlotActionType.QUICK_MOVE) {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot = this.slots.get(slotIndex);
                                        if (!slot.canTakeItems(player)) {
                                            return;
                                        }
                                        ItemStack itemStack4 = this.quickMove(player, slotIndex);
                                        while (!itemStack4.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStack4)) {
                                            itemStack4 = this.quickMove(player, slotIndex);
                                        }
                                    } else {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot = this.slots.get(slotIndex);
                                        ItemStack itemStack5 = slot.getStack();
                                        ItemStack itemStack52 = this.getCursorStack();
                                        player.onPickupSlotClick(itemStack52, slot.getStack(), clickType);
                                        if (!this.handleSlotClick(player, clickType, slot, itemStack5, itemStack52)) {
                                            if (itemStack5.isEmpty()) {
                                                if (!itemStack52.isEmpty()) {
                                                    int n = clickType == ClickType.LEFT ? itemStack52.getCount() : 1;
                                                    this.setCursorStack(slot.insertStack(itemStack52, n));
                                                }
                                            } else if (slot.canTakeItems(player)) {
                                                if (itemStack52.isEmpty()) {
                                                    int n = clickType == ClickType.LEFT ? itemStack5.getCount() : (itemStack5.getCount() + 1) / 2;
                                                    Optional<ItemStack> optional = slot.tryTakeStackRange(n, Integer.MAX_VALUE, player);
                                                    optional.ifPresent(stack -> {
                                                        this.setCursorStack((ItemStack)stack);
                                                        slot.onTakeItem(player, (ItemStack)stack);
                                                    });
                                                } else if (slot.canInsert(itemStack52)) {
                                                    if (ItemStack.canCombine(itemStack5, itemStack52)) {
                                                        int n = clickType == ClickType.LEFT ? itemStack52.getCount() : 1;
                                                        this.setCursorStack(slot.insertStack(itemStack52, n));
                                                    } else if (itemStack52.getCount() <= slot.getMaxItemCount(itemStack52)) {
                                                        this.setCursorStack(itemStack5);
                                                        slot.setStack(itemStack52);
                                                    }
                                                } else if (ItemStack.canCombine(itemStack5, itemStack52)) {
                                                    Optional<ItemStack> optional2 = slot.tryTakeStackRange(itemStack5.getCount(), itemStack52.getMaxCount() - itemStack52.getCount(), player);
                                                    optional2.ifPresent(stack -> {
                                                        itemStack52.increment(stack.getCount());
                                                        slot.onTakeItem(player, (ItemStack)stack);
                                                    });
                                                }
                                            }
                                        }
                                        slot.markDirty();
                                    }
                                    break block39;
                                }
                                if (actionType != SlotActionType.SWAP) break block46;
                                slot3 = this.slots.get(slotIndex);
                                itemStack2 = playerInventory.getStack(button);
                                itemStack = slot3.getStack();
                                if (itemStack2.isEmpty() && itemStack.isEmpty()) break block39;
                                if (!itemStack2.isEmpty()) break block47;
                                if (!slot3.canTakeItems(player)) break block39;
                                playerInventory.setStack(button, itemStack);
                                slot3.onTake(itemStack.getCount());
                                slot3.setStack(ItemStack.EMPTY);
                                slot3.onTakeItem(player, itemStack);
                                break block39;
                            }
                            if (!itemStack.isEmpty()) break block48;
                            if (!slot3.canInsert(itemStack2)) break block39;
                            int o = slot3.getMaxItemCount(itemStack2);
                            if (itemStack2.getCount() > o) {
                                slot3.setStack(itemStack2.split(o));
                            } else {
                                playerInventory.setStack(button, ItemStack.EMPTY);
                                slot3.setStack(itemStack2);
                            }
                            break block39;
                        }
                        if (!slot3.canTakeItems(player) || !slot3.canInsert(itemStack2)) break block39;
                        int o = slot3.getMaxItemCount(itemStack2);
                        if (itemStack2.getCount() <= o) break block49;
                        slot3.setStack(itemStack2.split(o));
                        slot3.onTakeItem(player, itemStack);
                        if (playerInventory.insertStack(itemStack)) break block39;
                        player.dropItem(itemStack, true);
                        break block39;
                    }
                    playerInventory.setStack(button, itemStack);
                    slot3.setStack(itemStack2);
                    slot3.onTakeItem(player, itemStack);
                    break block39;
                }
                if (actionType != SlotActionType.CLONE || !player.getAbilities().creativeMode || !this.getCursorStack().isEmpty() || slotIndex < 0) break block50;
                Slot slot3 = this.slots.get(slotIndex);
                if (!slot3.hasStack()) break block39;
                ItemStack itemStack2 = slot3.getStack().copy();
                itemStack2.setCount(itemStack2.getMaxCount());
                this.setCursorStack(itemStack2);
                break block39;
            }
            if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                int j = button == 0 ? 1 : slot3.getStack().getCount();
                ItemStack itemStack = slot3.takeStackRange(j, Integer.MAX_VALUE, player);
                player.dropItem(itemStack, true);
            } else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                ItemStack itemStack2 = this.getCursorStack();
                if (!(itemStack2.isEmpty() || slot3.hasStack() && slot3.canTakeItems(player))) {
                    int k = button == 0 ? 0 : this.slots.size() - 1;
                    int o = button == 0 ? 1 : -1;
                    for (int n = 0; n < 2; ++n) {
                        for (int p = k; p >= 0 && p < this.slots.size() && itemStack2.getCount() < itemStack2.getMaxCount(); p += o) {
                            Slot slot4 = this.slots.get(p);
                            if (!slot4.hasStack() || !ScreenHandler.canInsertItemIntoSlot(slot4, itemStack2, true) || !slot4.canTakeItems(player) || !this.canInsertIntoSlot(itemStack2, slot4)) continue;
                            ItemStack itemStack6 = slot4.getStack();
                            if (n == 0 && itemStack6.getCount() == itemStack6.getMaxCount()) continue;
                            ItemStack itemStack7 = slot4.takeStackRange(itemStack6.getCount(), itemStack2.getMaxCount() - itemStack2.getCount(), player);
                            itemStack2.increment(itemStack7.getCount());
                        }
                    }
                }
            }
        }
    }

    private boolean handleSlotClick(PlayerEntity player, ClickType clickType, Slot slot, ItemStack stack, ItemStack cursorStack) {
        FeatureSet featureSet = player.getWorld().getEnabledFeatures();
        if (cursorStack.isItemEnabled(featureSet) && cursorStack.onStackClicked(slot, clickType, player)) {
            return true;
        }
        return stack.isItemEnabled(featureSet) && stack.onClicked(cursorStack, slot, clickType, player, this.getCursorStackReference());
    }

    /**
     * {@return a reference to the cursor's stack}
     */
    private StackReference getCursorStackReference() {
        return new StackReference(){

            @Override
            public ItemStack get() {
                return ScreenHandler.this.getCursorStack();
            }

            @Override
            public boolean set(ItemStack stack) {
                ScreenHandler.this.setCursorStack(stack);
                return true;
            }
        };
    }

    /**
     * {@return whether {@code stack} can be inserted to {@code slot}}
     * 
     * <p>Subclasses should override this to return {@code false} if the slot is
     * used for output.
     */
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return true;
    }

    /**
     * Called when this screen handler is closed.
     * 
     * <p>To close a screen handler, call {@link PlayerEntity#closeHandledScreen}
     * on the server instead of this method.
     * 
     * <p>This drops the cursor stack by default. Subclasses that have slots not backed
     * by a persistent inventory should call {@link #dropInventory} to drop the stacks.
     */
    public void onClosed(PlayerEntity player) {
        ItemStack itemStack;
        if (player instanceof ServerPlayerEntity && !(itemStack = this.getCursorStack()).isEmpty()) {
            if (!player.isAlive() || ((ServerPlayerEntity)player).isDisconnected()) {
                player.dropItem(itemStack, false);
            } else {
                player.getInventory().offerOrDrop(itemStack);
            }
            this.setCursorStack(ItemStack.EMPTY);
        }
    }

    protected void dropInventory(PlayerEntity player, Inventory inventory) {
        if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
            for (int i = 0; i < inventory.size(); ++i) {
                player.dropItem(inventory.removeStack(i), false);
            }
            return;
        }
        for (int i = 0; i < inventory.size(); ++i) {
            PlayerInventory playerInventory = player.getInventory();
            if (!(playerInventory.player instanceof ServerPlayerEntity)) continue;
            playerInventory.offerOrDrop(inventory.removeStack(i));
        }
    }

    /**
     * Called when a slot's content has changed.
     * 
     * <p>This is not called by default; subclasses that override this method
     * should also use a custom {@link Inventory} whose {@link Inventory#markDirty markDirty} method is
     * overridden to call this method as a backing inventory of the slot.
     * 
     * <p>This can be used to update the output slot when input changes.
     */
    public void onContentChanged(Inventory inventory) {
        this.sendContentUpdates();
    }

    public void setStackInSlot(int slot, int revision, ItemStack stack) {
        this.getSlot(slot).setStackNoCallbacks(stack);
        this.revision = revision;
    }

    public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getSlot(i).setStackNoCallbacks(stacks.get(i));
        }
        this.cursorStack = cursorStack;
        this.revision = revision;
    }

    /**
     * Sets the property with ID {@code id} to {@code value}.
     * 
     * <p>Subclasses can call {@link #sendContentUpdates} to manually sync the change
     * to the client.
     */
    public void setProperty(int id, int value) {
        this.properties.get(id).set(value);
    }

    /**
     * {@return whether the screen handler can be used}
     * 
     * <p>Subclasses should call #canUse(ScreenHandlerContext, PlayerEntity, Block)}
     * or implement the check itself. The implementation should check that the
     * player is near the screen handler's source position (e.g. block position) and
     * that the source (e.g. block) is not destroyed.
     */
    public abstract boolean canUse(PlayerEntity var1);

    /**
     * Tries to consume {@code stack} by inserting to slots from {@code startIndex}
     * to {@code endIndex - 1} (both inclusive) until the entire stack is used.
     * 
     * <p>If {@code fromLast} is {@code true}, this attempts the insertion in reverse
     * order; i.e. {@code endIndex - 1} to {@code startIndex} (both inclusive).
     * 
     * @return whether {@code stack} was decremented
     */
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        ItemStack itemStack;
        Slot slot;
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= stack.getMaxCount()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        slot.markDirty();
                        bl = true;
                    } else if (itemStack.getCount() < stack.getMaxCount()) {
                        stack.decrement(stack.getMaxCount() - itemStack.getCount());
                        itemStack.setCount(stack.getMaxCount());
                        slot.markDirty();
                        bl = true;
                    }
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        if (!stack.isEmpty()) {
            i = fromLast ? endIndex - 1 : startIndex;
            while (fromLast ? i >= startIndex : i < endIndex) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (itemStack.isEmpty() && slot.canInsert(stack)) {
                    if (stack.getCount() > slot.getMaxItemCount()) {
                        slot.setStack(stack.split(slot.getMaxItemCount()));
                    } else {
                        slot.setStack(stack.split(stack.getCount()));
                    }
                    slot.markDirty();
                    bl = true;
                    break;
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        return bl;
    }

    public static int unpackQuickCraftButton(int quickCraftData) {
        return quickCraftData >> 2 & 3;
    }

    public static int unpackQuickCraftStage(int quickCraftData) {
        return quickCraftData & 3;
    }

    public static int packQuickCraftData(int quickCraftStage, int buttonId) {
        return quickCraftStage & 3 | (buttonId & 3) << 2;
    }

    public static boolean shouldQuickCraftContinue(int stage, PlayerEntity player) {
        if (stage == 0) {
            return true;
        }
        if (stage == 1) {
            return true;
        }
        return stage == 2 && player.getAbilities().creativeMode;
    }

    protected void endQuickCraft() {
        this.quickCraftStage = 0;
        this.quickCraftSlots.clear();
    }

    public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
        boolean bl;
        boolean bl2 = bl = slot == null || !slot.hasStack();
        if (!bl && ItemStack.canCombine(stack, slot.getStack())) {
            return slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount();
        }
        return bl;
    }

    public static void calculateStackSize(Set<Slot> slots, int mode, ItemStack stack, int stackSize) {
        switch (mode) {
            case 0: {
                stack.setCount(MathHelper.floor((float)stack.getCount() / (float)slots.size()));
                break;
            }
            case 1: {
                stack.setCount(1);
                break;
            }
            case 2: {
                stack.setCount(stack.getItem().getMaxCount());
            }
        }
        stack.increment(stackSize);
    }

    public boolean canInsertIntoSlot(Slot slot) {
        return true;
    }

    public static int calculateComparatorOutput(@Nullable BlockEntity entity) {
        if (entity instanceof Inventory) {
            return ScreenHandler.calculateComparatorOutput((Inventory)((Object)entity));
        }
        return 0;
    }

    public static int calculateComparatorOutput(@Nullable Inventory inventory) {
        if (inventory == null) {
            return 0;
        }
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (itemStack.isEmpty()) continue;
            f += (float)itemStack.getCount() / (float)Math.min(inventory.getMaxCountPerStack(), itemStack.getMaxCount());
            ++i;
        }
        return MathHelper.floor((f /= (float)inventory.size()) * 14.0f) + (i > 0 ? 1 : 0);
    }

    public void setCursorStack(ItemStack stack) {
        this.cursorStack = stack;
    }

    public ItemStack getCursorStack() {
        return this.cursorStack;
    }

    public void disableSyncing() {
        this.disableSync = true;
    }

    public void enableSyncing() {
        this.disableSync = false;
    }

    public void copySharedSlots(ScreenHandler handler) {
        Slot slot;
        int i;
        HashBasedTable<Inventory, Integer, Integer> table = HashBasedTable.create();
        for (i = 0; i < handler.slots.size(); ++i) {
            slot = handler.slots.get(i);
            table.put(slot.inventory, slot.getIndex(), i);
        }
        for (i = 0; i < this.slots.size(); ++i) {
            slot = this.slots.get(i);
            Integer integer = (Integer)table.get(slot.inventory, slot.getIndex());
            if (integer == null) continue;
            this.trackedStacks.set(i, handler.trackedStacks.get(integer));
            this.previousTrackedStacks.set(i, handler.previousTrackedStacks.get(integer));
        }
    }

    public OptionalInt getSlotIndex(Inventory inventory, int index) {
        for (int i = 0; i < this.slots.size(); ++i) {
            Slot slot = this.slots.get(i);
            if (slot.inventory != inventory || index != slot.getIndex()) continue;
            return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    public int getRevision() {
        return this.revision;
    }

    public int nextRevision() {
        this.revision = this.revision + 1 & Short.MAX_VALUE;
        return this.revision;
    }
}

