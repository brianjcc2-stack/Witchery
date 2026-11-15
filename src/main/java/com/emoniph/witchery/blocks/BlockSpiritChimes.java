package com.emoniph.witchery.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockSpiritChimes extends BlockBaseContainer {

    public BlockSpiritChimes() {
        super(Material.iron, BlockSpiritChimes.TileEntitySpiritChimes.class);
        this.setHardness(1.5F);
        this.setResistance(2.0F);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
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
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN) || World.doesBlockHaveSolidTopSurface(world, x, y + 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(4) == 0) {
            double px = (double)x + 0.35D + (rand.nextDouble() * 0.3D);
            double py = (double)y + 0.1D + (rand.nextDouble() * 0.3D);
            double pz = (double)z + 0.35D + (rand.nextDouble() * 0.3D);
            world.spawnParticle("enchantmenttable", px, py, pz, (rand.nextDouble() - 0.5D) * 0.1D, 0.05D, (rand.nextDouble() - 0.5D) * 0.1D);
        }
    }

    public static class TileEntitySpiritChimes extends TileEntity {
        @Override
        public boolean canUpdate() {
            return false;
        }
    }
}

