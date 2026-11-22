package com.emoniph.witchery.entity;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.infusion.Infusion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySeat extends Entity {

    public EntitySeat(World world) {
        super(world);
        this.setSize(0.01F, 0.01F);
        this.noClip = true;
    }

    public EntitySeat(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void entityInit() {}

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public double getMountedYOffset() {
        return 0.15D;
    }

    @Override
    public double getYOffset() {
        return 0.0D;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            if (this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityPlayer)) {
                this.setDead();
            } else {
                int bx = (int) Math.floor(this.posX);
                int by = (int) Math.floor(this.posY);
                int bz = (int) Math.floor(this.posZ);
                if (this.worldObj.getBlock(bx, by, bz) != Witchery.Blocks.ROCKING_CHAIR) {
                    this.setDead();
                } else if (this.ticksExisted % 60 == 0) {
                    EntityPlayer rider = (EntityPlayer) this.riddenByEntity;
                    if (Infusion.getInfusionID(rider) > 0) {
                        int cur = Infusion.getCurrentEnergy(rider);
                        int max = Infusion.getMaxEnergy(rider);
                        if (cur < max) {
                            Infusion.setCurrentEnergy(rider, Math.min(max, cur + 2));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {}
}

