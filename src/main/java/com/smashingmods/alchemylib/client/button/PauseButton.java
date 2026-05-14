package com.smashingmods.alchemylib.client.button;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.common.network.TogglePauseButtonPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class PauseButton extends AbstractAlchemyButton {

    public PauseButton(AbstractProcessingScreen<?> parent) {
        super(parent, button -> {
            boolean togglePause = !parent.getBlockEntity().isProcessingPaused();
            parent.getBlockEntity().setPaused(!togglePause);
            parent.getBlockEntity().setChanged();
            AlchemyLib.getPacketHandler().sendToServer(new TogglePauseButtonPacket(parent.getBlockEntity().getBlockPos(), togglePause));
        });
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 25 + ((blockEntity.isProcessingPaused() ? 1 : 0) * 20), 20, width, height);
        renderButtonTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return blockEntity.isProcessingPaused() ?
                MutableComponent.create(new TranslatableContents("alchemylib.container.resume", "Resume", TranslatableContents.NO_ARGS))
                :
                MutableComponent.create(new TranslatableContents("alchemylib.container.pause", "Pause", TranslatableContents.NO_ARGS));
    }
}
