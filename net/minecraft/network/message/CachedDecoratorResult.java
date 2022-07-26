/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * The cached result of {@link MessageDecorator}'s decorated message.
 * 
 * <p>The result is cached per player, and is consumed when the message is actually
 * sent. Caching allows the decorator's result to be not pure (e.g. uses externally
 * controlled variables), as decorators that produce different results on the second
 * execution at submission time do not affect the verification.
 */
public class CachedDecoratorResult {
    @Nullable
    private Cache cachedResult;

    /**
     * Sets the cached result to {@code preview} for {@code query}.
     * 
     * @see CachedDecoratorResult.Cache
     */
    public void setCachedResult(String query, Text preview) {
        this.cachedResult = new Cache(query, preview);
    }

    /**
     * Consumes the cached result if possible.
     * 
     * <p>The result can only be consumed if it exists and the cached query equals
     * {@code query}. After consuming, the cached result is set to {@code null}.
     * 
     * @return the cached result, or {@code null} if it cannot be consumed
     */
    @Nullable
    public Text tryConsume(String query) {
        Cache cache = this.cachedResult;
        if (cache != null && cache.queryEquals(query)) {
            this.cachedResult = null;
            return cache.preview();
        }
        return null;
    }

    record Cache(String query, Text preview) {
        public boolean queryEquals(String query) {
            return this.query.equals(query);
        }
    }
}

