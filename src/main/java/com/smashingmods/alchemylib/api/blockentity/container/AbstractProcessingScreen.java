package com.smashingmods.alchemylib.api.blockentity.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.data.*;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.codehaus.plexus.util.dag.Vertex;

import java.util.LinkedList;
import java.util.List;

import static com.smashingmods.alchemylib.api.blockentity.container.Direction2D.*;

/**
 * This abstract class does the heavy lifting setting up the methods for rendering a {@link AbstractProcessingMenu} to the screen.
 *
 * @param <M> The menu that this screen represents.
 */
@SuppressWarnings("unused")
public abstract class AbstractProcessingScreen<M extends AbstractProcessingMenu> extends AbstractContainerScreen<M> {

    private final AbstractProcessingBlockEntity blockEntity;
    protected final LinkedList<AbstractWidget> widgets = new LinkedList<>();

    public AbstractProcessingScreen(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 184;
        this.imageHeight = 162;
        this.blockEntity = menu.getBlockEntity();
    }

    /**
     * This method initializes the vaues for leftPos and topPos for the screen. Extenders can override this method to set
     * their own values or to initialize other objects. For example, this is where you should add buttons.
     */
    @Override
    protected void init() {
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
        super.init();
    }

    /**
     * Implementers should override this method and call super. This sets up the background color overlay, calls renderBG
     * which is set up by extenders, and renders all widgets added to the widgets field.
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (int index = 0; index < widgets.size(); index++) {
            renderWidget(widgets.get(index), leftPos - 24, topPos + (index * 24));
        }
    }

    /**
     * If an extending screen adds FluidDisplayData to the list of DisplayData field, this will be called to render a fluid
     * tank to the screen. Display data provides a FluidStack for getting the still texture, color, and other values for
     * rendering.
     *
     * @param data {@link FluidDisplayData}
     *
     * @see AbstractProcessingScreen#renderDisplayData(List, GuiGraphics, int, int)
     */
    public void drawFluidTank(FluidDisplayData data) {
        if (data.getValue() > 0) {
            FluidStack fluidStack = data.getFluidHandler().getFluidStack();
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            setShaderColor(fluidTypeExtensions.getTintColor());
            TextureAtlasSprite icon = getResourceTexture(fluidTypeExtensions.getStillTexture());
            drawTexture(data, icon, leftPos + data.getX(), topPos + data.getY());
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    /**
     * This method is similar to using blit but manually interacts with the rendering engine to draw a texture to the screen.
     *
     * @param data {@link AbstractDisplayData} Can pass any implementer of {@link DisplayData}.
     * @param sprite {@link TextureAtlasSprite}
     * @param textureX Integer for the X position of the screen to render from.
     * @param textureY Integer for the Y position of the screen to render from.
     */

    //TODO: Discover why FluidStack textures become invisible when picking up an inventory item.
    public void drawTexture(AbstractDisplayData data, TextureAtlasSprite sprite, int textureX, int textureY) {

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        int renderAmount = Math.max(Math.min(data.getHeight(), data.getValue() * data.getHeight() / data.getMaxValue()), 1);
        int posY = textureY + data.getHeight() - renderAmount;

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        for (int width = 0; width < data.getWidth(); width++) {
            for (int height = 0; height < data.getHeight(); height++) {

                int drawHeight = Math.min(renderAmount - height, 16);
                int drawWidth = Math.min(data.getWidth() - width, 16);

                int x1 = textureX + width;
                float x2 = x1 + drawWidth;
                int y1 = posY + height;
                float y2 = y1 + drawHeight;

                float scaleV = minV + (maxV - minV) * drawHeight / 16f;
                float scaleU = minU + (maxU - minU) * drawWidth / 16f;

                float blitOffset = 0;

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                bufferBuilder.addVertex(x1, y2, blitOffset).setUv(minU, scaleV);
                bufferBuilder.addVertex(x2, y2, blitOffset).setUv(scaleU, scaleV);
                bufferBuilder.addVertex(x2, y1, blitOffset).setUv(scaleU, minV);
                bufferBuilder.addVertex(x1, y1, blitOffset).setUv(minU, minV);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

                height += 15;
            }
            width += 16;
        }
    }

    /**
     * Gets a texture from {@link InventoryMenu#BLOCK_ATLAS} by the passed ResourceLocation.
     *
     * @param resourceLocation {@link ResourceLocation}
     * @return {@link TextureAtlasSprite}
     */
    public static TextureAtlasSprite getResourceTexture(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(resourceLocation);
    }

    /**
     * Sets the RenderSystem's shader color by converting an integer color to an RGBA value using bit shifting. The shader
     * color needs to be set back to default (1.0F, 1.0F, 1.0F, 1.0F) after rendering the color.
     *
     * @param color Interger color value.
     */
    public static void setShaderColor(int color) {
        float alpha = (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    /**
     * @param pixels Integer value of the height of the area being drawn to. For example, if you are drawing an energy
     *                bar to the screen inside a 60 pixel height area, you pass 60 here.
     * @param value {@link DisplayData#getValue()}
     * @param maxValue {@link DisplayData#getMaxValue()}
     * @return Integer for the scaled hieght to draw.
     */
    public static int getScaled(int pixels, int value, int maxValue) {
        if (value > 0 && maxValue > 0) {
            return value * pixels / maxValue;
        } else {
            return 0;
        }
    }

    /**
     * This helper method sets up the values from EnergyDisplayData to call directional blit.
     *
     * @param data {@link EnergyDisplayData}
     */
    public void drawEnergyBar(GuiGraphics guiGraphics, EnergyDisplayData data) {
        int x = data.getX() + (this.width - this.imageWidth) / 2;
        int y = data.getY() + (this.height - this.imageHeight) / 2;
        directionalBlit(guiGraphics, x, y + data.getHeight(), 0, 0, data.getWidth(), data.getHeight(), data.getValue(), data.getMaxValue(), UP, true);
    }

    /**
     * Overload for directionalBlit with scale offset set to false.
     */
    private void directionalBlit(GuiGraphics guiGraphics, int x, int y, int uOffset, int vOffset, int u, int v, int value, int maxValue, Direction2D direction2D) {
        directionalBlit(guiGraphics, x, y, uOffset, vOffset, u, v, value, maxValue, direction2D, false);
    }

    /**
     * Renders a texture from a texture atlas to the screen using a blit method.
     *
     * @param x Integer value of X position to render to the screen.
     * @param y Integer value of Y position to render to the screen.
     * @param uOffset Integer value of uOffset (left to right) for a texture atlas. Correlates to width.
     * @param vOffset Integer value of vOffset (top to bottom) for a texture atlas. Correlates to height.
     * @param u Integer value of U for a texture atlas.
     * @param v Integer value of V for a teture atlas.
     * @param value Integer for current value typically of DisplayData. For example, an energy bar displaying 10,000 FE.
     * @param maxValue Integer for max value typically of DisplayData. For example, an energy bar with a capacity of 100,000 FE.
     * @param direction2D {@link Direction2D} for the direction to blit to the screen. Direction is toward, so if Left,
     *                                        blitting will start on the right and move toward the left.
     * @param scaleOffset Calculated value for offsetting the scale. This is used primarily for changing the color of the
     *                     energy bar based on its value. If the energy bar has a lot of energy, it will be greener, and if
     *                     it's low on energy, it will be redder. The actual texture covers the entire spectrum and this value
     *                     is changing where on the texture is being rendered to the screen.
     */
    private void directionalBlit(GuiGraphics guiGraphics, int x, int y, int uOffset, int vOffset, int u, int v, int value, int maxValue, Direction2D direction2D, boolean scaleOffset) {

        int resultX = x;
        int resultY = y;
        int resultUOffset = uOffset;
        int resultVOffset = vOffset;
        int uWidth = u;
        int vHeight = v;

        int vScaled = getScaled(v, value, maxValue);
        int vScalePercent = (int) ((v * 1.8f) - (vScaled * 1.8f));
        int finalVOffset = scaleOffset ? vScalePercent : vOffset + v - vScaled;

        switch (direction2D) {
            case LEFT -> {
                resultX = x -vScaled;
                resultUOffset = u - vScaled;
                uWidth = vScaled;
            }
            case UP -> {
                resultY = y - vScaled;
                resultVOffset = finalVOffset;
                vHeight = vScaled;
            }
            case RIGHT -> {
                uWidth = vScaled;
                vHeight = u;
            }
            case DOWN -> vHeight = vScaled;
        }
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "textures/gui/widgets.png"), resultX, resultY, resultUOffset, resultVOffset, uWidth, vHeight);
    }

    /**
     * {@link ProgressDisplayData} holds a {@link Direction2D} for defining the direction on the screen to render the
     * progress arrow. Progress arrows are hard coded such that their height and width are 9/30 respectively or rotated
     * based on that. Arrow textures live on the widgets.png starting at uOffset 0 and vOffset 100.
     *
     * @param data {@link ProgressDisplayData}
     */
    public void directionalArrow(GuiGraphics guiGraphics, int x, int y, ProgressDisplayData data) {
        int uOffset = 0;
        int vOffset = 99;
        int width = 0;
        int height = 0;
        switch (data.getDirection()) {
            case LEFT -> {
                height = 9;
                width = 30;
            }
            case UP -> {
                vOffset = vOffset + 18;
                height = 9;
                width = 30;
            }
            case RIGHT -> {
                vOffset = vOffset + 9;
                height = 9;
                width = 30;
            }
            case DOWN -> {
                uOffset = uOffset + 9;
                vOffset = vOffset + 18;
                height = 9;
                width = 30;
            }
        }
        directionalBlit(guiGraphics, x + data.getX(), y + data.getY(), uOffset, vOffset, height, width, data.getValue(), data.getMaxValue(), data.getDirection());
    }

    /**
     * Calls the relevant rendering method depending on the type of {@link AbstractDisplayData}.
     */
    public void renderDisplayData(List<AbstractDisplayData> displayData, GuiGraphics guiGraphics, int x, int y) {
        displayData.forEach(data -> {
            if (data instanceof ProgressDisplayData progressData) {
                directionalArrow(guiGraphics, x, y, progressData);
            }
            if (data instanceof EnergyDisplayData energyData) {
                drawEnergyBar(guiGraphics, energyData);
            }
            if (data instanceof FluidDisplayData fluidData) {
                drawFluidTank(fluidData);
            }
        });
    }

    /**
     * Tests the location of the mouse position against the X/Y positions of all display data objects held by this screen
     * to render a tooltip calling {@link DisplayData#toTextComponent()}.
     */
    public void renderDisplayTooltip(List<AbstractDisplayData> displayData, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        displayData.stream().filter(data ->
                mouseX >= data.getX() + x &&
                        mouseX <= data.getX() + x + data.getWidth() &&
                        mouseY >= data.getY() + y &&
                        mouseY <= data.getY() + y + data.getHeight()
        ).forEach(data -> {
            if (!(data instanceof ProgressDisplayData)) {
                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(data.toTextComponent()), mouseX, mouseY);
            }
        });
    }

    /**
     * Helper method that calls {@link RecipeDisplayUtil#getItemTooltipComponent(ItemStack, MutableComponent)} for a given ItemStack
     * and MutableComponent to render to the screen.
     */
    public void renderItemTooltip(GuiGraphics guiGraphics, ItemStack itemStack, MutableComponent component, int mouseX, int mouseY) {
        guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, RecipeDisplayUtil.getItemTooltipComponent(itemStack, component), mouseX, mouseY);
    }

    /**
     *  Call this method to add a widget (button) to the list of renderable widgets. This makes sure that a widget is
     *  only added once as adding multiple copies of the same widget will cause them all to be rendered even if you can't
     *  see them which is very bad for game FPS.
     *
     *  <p>Extenders of {@link AbstractWidget} can have their X/Y screen positions set while other types of widgets might
     *  handle positioning differently.</p>
     *
     * @param widget Extends {@link GuiEventListener} &amp; {@link Renderable} &amp; {@link NarratableEntry}
     * @param x Integer for X position on the screen.
     * @param y Integer for Y position on the screen.
     * @param <W> extends GuiEventListener &amp; Widget &amp; NarratableEntry
     *
     * @see AbstractWidget
     * @see com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton AbstractAlchemyButton
     */
    public <W extends GuiEventListener & Renderable & NarratableEntry> void renderWidget(W widget, int x, int y) {
        if (!renderables.contains(widget)) {
            if (widget instanceof AbstractWidget w) {
                w.setX(x);
                w.setY(y);
            }
            addRenderableWidget(widget);
        }
    }

    /**
     * @return A reference to the menu's reference to the block entity.
     */
    public AbstractProcessingBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
