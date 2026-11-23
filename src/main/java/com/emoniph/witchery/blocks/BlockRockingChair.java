package com.emoniph.witchery.blocks;

import com.emoniph.witchery.entity.EntitySeat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getBlockTextureFromSide(side);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox((double)x + 0.125D, (double)y, (double)z + 0.125D, (double)x + 0.875D, (double)y + 0.55D, (double)z + 0.875D);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    private float getSeatYaw(int meta) {
        switch (meta & 3) {
            case 0: return 180.0F;
            case 1: return 270.0F;
            case 2: return 0.0F;
            case 3: return 90.0F;
            default: return 0.0F;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            List<EntitySeat> seats = world.getEntitiesWithinAABB(EntitySeat.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
            if (!seats.isEmpty()) {
                EntitySeat seat = seats.get(0);
                if (seat.riddenByEntity == null) {
                    seat.rotationYaw = this.getSeatYaw(world.getBlockMetadata(x, y, z));
                    player.mountEntity(seat);
                }
                return true;
            }

            EntitySeat newSeat = new EntitySeat(world, (double)x + 0.5D, (double)y + 0.35D, (double)z + 0.5D);
            newSeat.rotationYaw = this.getSeatYaw(world.getBlockMetadata(x, y, z));
            newSeat.setPendingRider(player);
            world.spawnEntityInWorld(newSeat);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            List<EntitySeat> seats = world.getEntitiesWithinAABB(EntitySeat.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
            for (EntitySeat seat : seats) {
                if (seat.riddenByEntity != null) {
                    seat.riddenByEntity.mountEntity((net.minecraft.entity.Entity)null);
                }
                seat.setDead();
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static class TileEntityRockingChair extends TileEntity {
        @Override
        public boolean canUpdate() {
            return false;
        }
    }
}

