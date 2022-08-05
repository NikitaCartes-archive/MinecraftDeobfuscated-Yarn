/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.narration;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.Narration;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

/**
 * A builder for narration messages.
 * 
 * <p>Narration messages consist of multiple sections known as
 * {@linkplain NarrationPart parts}. Each narration message can
 * contain only one narration per part.
 * 
 * <p>You can create a <em>submessage</em> by calling {@link #nextMessage()}.
 * Each submessage can have its own set of narrations for the different
 * narration parts.
 * 
 * <p id="ordering">The narrations added to a message will be ordered by their part
 * first, in {@link NarrationPart}'s natural ordering. If there are multiple
 * narrations for a part added through submessages, they will be ordered earliest
 * submessage first.
 */
@Environment(value=EnvType.CLIENT)
public interface NarrationMessageBuilder {
    /**
     * Adds a {@link Text} narration to this message builder.
     * 
     * <p>If a narration already exists for the specified narration part,
     * it is replaced by the new narration.
     * 
     * @implSpec The default implementation behaves like
     * {@code put(part, text.getString())}.
     * 
     * @param text the message for the narration
     */
    default public void put(NarrationPart part, Text text) {
        this.put(part, Narration.string(text.getString()));
    }

    /**
     * Adds a string narration to this message builder.
     * 
     * <p>If a narration already exists for the specified narration part,
     * it is replaced by the new narration.
     * 
     * @param string the message for the narration
     */
    default public void put(NarrationPart part, String string) {
        this.put(part, Narration.string(string));
    }

    /**
     * Adds an array of {@link Text} narrations to this message builder.
     * 
     * <p>Each {@link Text} in the input array will be its own sentence
     * as described in {@link Narration#texts}.
     * 
     * <p>If a narration already exists for the specified narration part,
     * it is replaced by the new narration.
     * 
     * @implSpec The default implementation creates a {@link Narration} for the
     * texts using {@link Narration#texts}.
     * 
     * @param texts the messages for the narration
     */
    default public void put(NarrationPart part, Text ... texts) {
        this.put(part, Narration.texts(ImmutableList.copyOf(texts)));
    }

    /**
     * Adds a narration to this message builder.
     * 
     * <p>If a narration already exists for the specified narration part,
     * it is replaced by the new narration.
     * 
     * @see #put(NarrationPart, Text)
     * @see #put(NarrationPart, String)
     * @see #put(NarrationPart, Text...)
     */
    public void put(NarrationPart var1, Narration<?> var2);

    /**
     * Creates a narration message builder for a submessage.
     * 
     * <p>Submessages can have their own set of narrations for the narration parts,
     * which are merged with the "parent" message's narrations
     * <a href="#ordering">as described above</a>.
     * 
     * @apiNote All returned builder instances are equivalent and refer to the same
     * submessage. If you want to add yet another set of narrations, call this method
     * again on the first submessage builder to obtain a "nested" submessage builder.
     * 
     * @return the created builder
     */
    public NarrationMessageBuilder nextMessage();
}

