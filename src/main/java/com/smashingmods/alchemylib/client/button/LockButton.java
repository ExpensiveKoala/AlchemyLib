package com.smashingmods.alchemylib.client.button;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.common.network.ToggleLockButtonPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class LockButton extends AbstractAlchemyButton {

    public LockButton(AbstractProcessingScreen<?> parent) {
        super(parent, button -> {
            boolean toggleLock = !parent.getBlockEntity().isRecipeLocked();
            parent.getBlockEntity().setRecipeLocked(toggleLock);
            parent.getBlockEntity().setChanged();
            AlchemyLib.getPacketHandler().sendToServer(new ToggleLockButtonPacket(parent.getBlockEntity().getBlockPos(), toggleLock));
        });
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 25 + ((blockEntity.isRecipeLocked() ? 0 : 1) * 20), 0, width, height);
        renderButtonTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return blockEntity.isRecipeLocked() ?
                MutableComponent.create(new TranslatableContents("alchemylib.container.unlock_recipe", "Unlock recipe", TranslatableContents.NO_ARGS))
                :
                MutableComponent.create(new TranslatableContents("alchemylib.container.lock_recipe", "Lock recipe", TranslatableContents.NO_ARGS));
    }
}
