package com.smashingmods.alchemylib.api.blockentity.container.button;

import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Extend this class to make a Button to render to the screen using {@link AbstractProcessingScreen}.
 */
@SuppressWarnings("unused")
public abstract class AbstractAlchemyButton extends Button {

    protected final AbstractProcessingScreen<?> parent;
    protected final AbstractProcessingBlockEntity blockEntity;

    /**
     *  This convenience constructor sets up default values for creating a new button. Use
     *  the other constructor if you want to set your own values.
     *
     * @param parent {@link AbstractProcessingScreen}
     * @param onPress {@link Button#onPress}
     */
    public AbstractAlchemyButton(AbstractProcessingScreen<?> parent, Button.OnPress onPress) {
        this(0, 0, 20, 20, (MutableComponent) CommonComponents.EMPTY, parent, onPress);
    }

    /**
     *  Button widget optimized for rendering to AlchemyLib screens.
     *
     * @param x Starting X position on the screen for drawing this button.
     * @param y Starting Y position on the screen for drawing this button.
     * @param width Width of this button. Helper constructor defaults to 20.
     * @param height Height of this button. Helper constructor defaults to 20.
     * @param component {@link MutableComponent} for displaying on the button. By default, this is set to an empty string and no text is displayed.
     * @param parent {@link AbstractProcessingScreen} the screen that this button is rendered on.
     * @param onPress {@link Button#onPress} the callback method to be executed when the button is pressed.
     */
    public AbstractAlchemyButton(int x, int y, int width, int height, MutableComponent component, AbstractProcessingScreen<?> parent, Button.OnPress onPress) {
        super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
        this.parent = parent;
        this.blockEntity = parent.getBlockEntity();
    }

    /**
     * Renders the button's tooltip defined in its constructor to the parent screen.
     */
    public void renderButtonTooltip(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height) {
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(getMessage()), mouseX, mouseY);
        }
    }
}
