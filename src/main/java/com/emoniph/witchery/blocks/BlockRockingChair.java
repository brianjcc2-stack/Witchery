package com.emoniph.witchery.blocks;

import com.emoniph.witchery.entity.EntitySeat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockRockingChair extends BlockBaseContainer {

    public BlockRockingChair() {
        super(Material.wood, BlockRockingChair.TileEntityRockingChair.class);
        this.setHardness(2.0F);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.1F, 0.875F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            List<EntitySeat> seats = world.getEntitiesWithinAABB(EntitySeat.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
            if (!seats.isEmpty()) {
                EntitySeat seat = seats.get(0);
                if (seat.riddenByEntity == null) {
                    player.mountEntity(seat);
                }
                return true;
            }

            EntitySeat newSeat = new EntitySeat(world, (double)x + 0.5D, (double)y + 0.1D, (double)z + 0.5D);
            world.spawnEntityInWorld(newSeat);
            player.mountEntity(newSeat);
        }
        return true;
    }

    public static class TileEntityRockingChair extends TileEntity {
        @Override
        public boolean canUpdate() {
            return false;
        }
    }
}

