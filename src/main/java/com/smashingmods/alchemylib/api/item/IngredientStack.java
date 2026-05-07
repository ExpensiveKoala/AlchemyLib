package com.smashingmods.alchemylib.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An IngredientStack is a {@link ICustomIngredient} that is similar to a vanilla {@link Ingredient} with the difference being an
 * optional count. Ingredients extend Predicate&lt;ItemStack&gt; that hold values for potential items. For example, you can create an
 * Ingredient using {@link Ingredient#of(ItemLike...)}, {@link Ingredient#of(ItemStack...)}, or {@link Ingredient#of(TagKey)}.
 *
 * <p>IngredientStack has various static methods for instantiating. See: {@link IngredientStack#of(ItemStack)}, {@link IngredientStack#of(ItemLike)}, or {@link IngredientStack#of(Ingredient)}
 * and the overloads that take a count.</p>
 */
@SuppressWarnings("unused")
public class IngredientStack implements ICustomIngredient {

    public static final MapCodec<IngredientStack> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientStack::getIngredient),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientStack::getCount)
            ).apply(builder, IngredientStack::new)
    );

    private final Ingredient ingredient;
    private final int count;


    private IngredientStack(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = Math.min(count, 64);
    }

    /**
     * Pass-through for the Ingredient's test method. This is used to determine if an ItemStack matches the predicate of the
     * Ingredient. For example, if an Ingredient was made using the tag key "c:chests/wooden" and you tested an item
     * with the Resource Location "minecraft:chest", then it would match because "minecraft:chest" is contained within that tag.
     *
     * @param stack {@link ItemStack} to test against.
     */
    @Override
    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    /**
     * Convert this IngredientStack into a Stream of ItemStacks of each of the Ingredients items
     * with the count value set.
     *
     * @return Stream of ItemStacks.
     */
    @Override
    public Stream<ItemStack> getItems() {
        return Arrays.stream(ingredient.getItems()).peek(stack -> stack.setCount(this.count));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return null;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return ingredient.isEmpty();
    }

    public static Ingredient of(Ingredient ingredient,  int count) {
        return new IngredientStack(ingredient, count).toVanilla();
    }

    public static Ingredient of(Ingredient ingredient) {
        return of(ingredient, 1);
    }

    public static Ingredient of(ItemStack stack) {
        return of(stack, stack.getCount());
    }

    public static Ingredient of(ItemStack stack, int count) {
        return of(Ingredient.of(stack), count);
    }

    public static Ingredient of(ItemLike itemLike) {
        return of(itemLike, 1);
    }

    public static Ingredient of(ItemLike itemLike, int count) {
        return of(Ingredient.of(itemLike), count);
    }

    /**
     * Determines object equality of this IngredientStack against another object based on {@link Ingredient#equals(Object)}.
     *
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IngredientStack that)) return false;

        if (getCount() != that.getCount()) return false;
        return getIngredient().equals(that.getIngredient());
    }

    /**
     * Calculates the hash code for this IngredientStack based on its {@link Ingredient ingredient} hash code.
     *
     * @return int
     */
    @Override
    public int hashCode() {
        int result = getCount();
        result = 31 * result + getIngredient().hashCode();
        return result;
    }
}
