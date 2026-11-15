package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.world.World;

public class ContainerHerbalistDesk extends ContainerWorkbench {

    private final World worldObj;
    private final int posX;
    private final int posY;
    private final int posZ;

    public ContainerHerbalistDesk(InventoryPlayer playerInv, World world, int x, int y, int z) {
        super(playerInv, world, x, y, z);
        this.worldObj = world;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (this.worldObj.getBlock(this.posX, this.posY, this.posZ) != Witchery.Blocks.HERBALIST_DESK) {
            return false;
        }
        return player.getDistanceSq((double)this.posX + 0.5D, (double)this.posY + 0.5D, (double)this.posZ + 0.5D) <= 64.0D;
    }
}

