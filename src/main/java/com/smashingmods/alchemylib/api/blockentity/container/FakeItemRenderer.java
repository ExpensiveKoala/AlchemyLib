package com.smashingmods.alchemylib.api.blockentity.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class FakeItemRenderer {

    public static void renderFakeItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y) {
        renderFakeItem(guiGraphics, itemStack, x, y, true, false);
    }

    public static void renderFakeItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, boolean drawItemDecorations) {
        renderFakeItem(guiGraphics, itemStack, x, y, true, drawItemDecorations);
    }

    public static void renderFakeItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, boolean semiTransparent, boolean drawItemDecorations) {
        guiGraphics.renderFakeItem(itemStack, x, y);
        if (semiTransparent) {
            guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), x, y, x + 16, y + 16, 0x88888888);
        }

        if (drawItemDecorations) {
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, itemStack, x, y);
        }
    }
}
