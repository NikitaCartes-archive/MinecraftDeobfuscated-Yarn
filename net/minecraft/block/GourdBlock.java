/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.StemBlock;

public abstract class GourdBlock
extends Block {
    public GourdBlock(Block.Settings settings) {
        super(settings);
    }

    public abstract StemBlock getStem();

    public abstract AttachedStemBlock getAttachedStem();
}

