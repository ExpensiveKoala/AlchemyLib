package com.smashingmods.alchemylib.api.blockentity.container.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 *  This abstract class defines getters and setters for shared fields for all {@link DisplayData} implementers.
 *
 * @see EnergyDisplayData
 * @see FluidDisplayData
 * @see ProgressDisplayData
 */
public abstract class AbstractDisplayData implements DisplayData {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    /**
     * Set the x/y coordinates of child classes to be drawn on the screen. DisplayData renders from
     * the left and top respectively.
     *
     * <p>The width and height passed here will determine the total area that is used to render the data.</p>
     *
     * @param x integer representing the x position on the screen.
     * @param y integer representing the y position on the screen.
     * @param width integer value of the width of the display data.
     * @param height integer value of the height of the display data.
     */
    public AbstractDisplayData(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public MutableComponent toTextComponent() {
        String temp = "";
        if (this.toString() != null) {
            temp = this.toString();
        }
        return Component.literal(temp);
    }
}
