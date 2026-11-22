package com.emoniph.witchery.blocks;

import com.emoniph.witchery.util.ParticleEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSkullCandle extends BlockBaseContainer {

    public BlockSkullCandle() {
        super(Material.circuits, BlockSkullCandle.TileEntitySkullCandle.class);
        this.setHardness(1.0F);
        this.setResistance(2.0F);
        this.setLightLevel(0.9F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.9F, 0.75F);
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
        return Blocks.soul_sand.getBlockTextureFromSide(side);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!world.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        double px = (double)x + 0.5D;
        double py = (double)y + 0.88D;
        double pz = (double)z + 0.5D;
        world.spawnParticle(ParticleEffect.FLAME.toString(), px, py, pz, 0.0D, 0.0D, 0.0D);
        if (rand.nextInt(3) == 0) {
            world.spawnParticle(ParticleEffect.SMOKE.toString(), px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    public static class TileEntitySkullCandle extends TileEntity {
        @Override
        public boolean canUpdate() {
            return false;
        }
    }
}

