package com.smashingmods.alchemylib.api.storage;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * This class is a wrapper around {@link FluidTank} that adds some helper methods.
 * for setting the fluid amount held in the tank.
 */
@SuppressWarnings("unused")
public class FluidStorageHandler extends FluidTank {

    public FluidStorageHandler(int capacity, FluidStack fluidStack) {
        super(capacity);
        fill(fluidStack, FluidAction.EXECUTE);
    }

    /**
     * Set the FluidStack of this tank. If the tank already had a fluid set, it will drain that fluid
     * to set the new fluid. Helper methods exist to set the FluidStack using a fluid with an optional amount.
     *
     * @param fluidStack {@link FluidStack}
     */
    public void setFluid(FluidStack fluidStack) {
        drain(capacity, FluidAction.EXECUTE);
        fill(fluidStack, FluidAction.EXECUTE);
    }

    public void setFluid(Fluid fluid, int amount) {
        setFluid(new FluidStack(fluid, amount));
    }

    public void setFluid(Fluid fluid) {
        setFluid(fluid, 0);
    }

    /**
     * Sets the fluid amount to the parameter value so long as that value is
     * higher than 0 and less than capacity.
     */
    public void setAmount(int amount) {
        fluid.setAmount(Math.clamp(amount, 0, capacity));
    }

    /**
     * Fills the fluid amount by the parameter value up to capacity.
     */
    public void fillAmount(int amount) {
        fluid.setAmount(Math.min(getFluidAmount() + amount, capacity));
    }

    /**
     * Drains the fluid amount by the parameter down to 0.
     */
    public void drainAmount(int amount) {
        fluid.setAmount(Math.max(getFluidAmount() - amount, 0));
    }

    public FluidStack getFluidStack() {
        return fluid;
    }
}
