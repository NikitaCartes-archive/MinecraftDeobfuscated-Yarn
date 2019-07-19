/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SuffixArray<T> {
    private static final boolean PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
    private static final boolean PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
    private static final Logger LOGGER = LogManager.getLogger();
    protected final List<T> objects = Lists.newArrayList();
    private final IntList field_5505 = new IntArrayList();
    private final IntList field_5509 = new IntArrayList();
    private IntList field_5504 = new IntArrayList();
    private IntList field_5506 = new IntArrayList();
    private int maxTextLength;

    public void add(T object, String string) {
        this.maxTextLength = Math.max(this.maxTextLength, string.length());
        int i = this.objects.size();
        this.objects.add(object);
        this.field_5509.add(this.field_5505.size());
        for (int j = 0; j < string.length(); ++j) {
            this.field_5504.add(i);
            this.field_5506.add(j);
            this.field_5505.add(string.charAt(j));
        }
        this.field_5504.add(i);
        this.field_5506.add(string.length());
        this.field_5505.add(-1);
    }

    public void sort() {
        int j2;
        int i2 = this.field_5505.size();
        int[] is = new int[i2];
        final int[] js = new int[i2];
        final int[] ks = new int[i2];
        int[] ls = new int[i2];
        IntComparator intComparator = new IntComparator(){

            @Override
            public int compare(int i, int j) {
                if (js[i] == js[j]) {
                    return Integer.compare(ks[i], ks[j]);
                }
                return Integer.compare(js[i], js[j]);
            }

            @Override
            public int compare(Integer integer, Integer integer2) {
                return this.compare((int)integer, (int)integer2);
            }
        };
        Swapper swapper = (i, j) -> {
            if (i != j) {
                int k = js[i];
                is[i] = js[j];
                is[j] = k;
                k = ks[i];
                js[i] = ks[j];
                js[j] = k;
                k = ls[i];
                ks[i] = ls[j];
                ks[j] = k;
            }
        };
        for (j2 = 0; j2 < i2; ++j2) {
            is[j2] = this.field_5505.getInt(j2);
        }
        j2 = 1;
        int k = Math.min(i2, this.maxTextLength);
        while (j2 * 2 < k) {
            int l;
            for (l = 0; l < i2; ++l) {
                js[l] = is[l];
                ks[l] = l + j2 < i2 ? is[l + j2] : -2;
                ls[l] = l;
            }
            it.unimi.dsi.fastutil.Arrays.quickSort(0, i2, intComparator, swapper);
            for (l = 0; l < i2; ++l) {
                is[ls[l]] = l > 0 && js[l] == js[l - 1] && ks[l] == ks[l - 1] ? is[ls[l - 1]] : l;
            }
            j2 *= 2;
        }
        IntList intList = this.field_5504;
        IntList intList2 = this.field_5506;
        this.field_5504 = new IntArrayList(intList.size());
        this.field_5506 = new IntArrayList(intList2.size());
        for (int m = 0; m < i2; ++m) {
            int n = ls[m];
            this.field_5504.add(intList.getInt(n));
            this.field_5506.add(intList2.getInt(n));
        }
        if (PRINT_ARRAY) {
            this.printArray();
        }
    }

    private void printArray() {
        for (int i = 0; i < this.field_5504.size(); ++i) {
            LOGGER.debug("{} {}", (Object)i, (Object)this.method_4808(i));
        }
        LOGGER.debug("");
    }

    private String method_4808(int i) {
        int j = this.field_5506.getInt(i);
        int k = this.field_5509.getInt(this.field_5504.getInt(i));
        StringBuilder stringBuilder = new StringBuilder();
        int l = 0;
        while (k + l < this.field_5505.size()) {
            int m;
            if (l == j) {
                stringBuilder.append('^');
            }
            if ((m = this.field_5505.get(k + l).intValue()) == -1) break;
            stringBuilder.append((char)m);
            ++l;
        }
        return stringBuilder.toString();
    }

    private int method_4805(String string, int i) {
        int j = this.field_5509.getInt(this.field_5504.getInt(i));
        int k = this.field_5506.getInt(i);
        for (int l = 0; l < string.length(); ++l) {
            char d;
            int m = this.field_5505.getInt(j + k + l);
            if (m == -1) {
                return 1;
            }
            char c = string.charAt(l);
            if (c < (d = (char)m)) {
                return -1;
            }
            if (c <= d) continue;
            return 1;
        }
        return 0;
    }

    public List<T> findAll(String string) {
        int m;
        int l;
        int i = this.field_5504.size();
        int j = 0;
        int k = i;
        while (j < k) {
            l = j + (k - j) / 2;
            m = this.method_4805(string, l);
            if (PRINT_COMPARISONS) {
                LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", (Object)string, (Object)l, (Object)this.method_4808(l), (Object)m);
            }
            if (m > 0) {
                j = l + 1;
                continue;
            }
            k = l;
        }
        if (j < 0 || j >= i) {
            return Collections.emptyList();
        }
        l = j;
        k = i;
        while (j < k) {
            m = j + (k - j) / 2;
            int n = this.method_4805(string, m);
            if (PRINT_COMPARISONS) {
                LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", (Object)string, (Object)m, (Object)this.method_4808(m), (Object)n);
            }
            if (n >= 0) {
                j = m + 1;
                continue;
            }
            k = m;
        }
        m = j;
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (int o = l; o < m; ++o) {
            intSet.add(this.field_5504.getInt(o));
        }
        int[] is = intSet.toIntArray();
        Arrays.sort(is);
        LinkedHashSet<T> set = Sets.newLinkedHashSet();
        for (int p : is) {
            set.add(this.objects.get(p));
        }
        return Lists.newArrayList(set);
    }
}

