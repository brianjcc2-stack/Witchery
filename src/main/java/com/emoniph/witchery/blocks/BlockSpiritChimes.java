package com.emoniph.witchery.blocks;

import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.dimension.WorldProviderDreamWorld;
import com.emoniph.witchery.entity.EntityBanshee;
import com.emoniph.witchery.entity.EntityLostSoul;
import com.emoniph.witchery.entity.EntityNightmare;
import com.emoniph.witchery.entity.EntityPoltergeist;
import com.emoniph.witchery.entity.EntitySpectre;
import com.emoniph.witchery.entity.EntitySpirit;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.TransformCreature;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.iron_block.getBlockTextureFromSide(side);
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            boolean spiritNearby = TileEntitySpiritChimes.hasNearbySpirits(world, x, y, z, 16.0D);
            float pitch = spiritNearby ? 0.65F : 1.5F + world.rand.nextFloat() * 0.4F;
            world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.orb", 0.5F, pitch);
            if (spiritNearby) {
                ParticleEffect.SPELL_COLORED.send(com.emoniph.witchery.util.SoundEffect.NONE, world, (double)x + 0.5D, (double)y + 0.4D, (double)z + 0.5D, 0.5D, 0.5D, 16, 0x66FFFF);
            }
        }
        return true;
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
        private int ticks = 0;

        @Override
        public boolean canUpdate() {
            return true;
        }

        @Override
        public void updateEntity() {
            ++this.ticks;
            if (!this.worldObj.isRemote && this.ticks % 80 == 0) {
                if (hasNearbySpirits(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 16.0D)) {
                    this.worldObj.playSoundEffect((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, "random.orb", 0.45F, 1.6F + this.worldObj.rand.nextFloat() * 0.35F);
                    ParticleEffect.SPELL_COLORED.send(com.emoniph.witchery.util.SoundEffect.NONE, this.worldObj, (double)this.xCoord + 0.5D, (double)this.yCoord + 0.35D, (double)this.zCoord + 0.5D, 0.45D, 0.45D, 16, 0x66FFFF);
                }
            }
        }

        public static boolean hasNearbySpirits(World world, int x, int y, int z, double range) {
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)x - range, (double)y - range, (double)z - range, (double)x + 1.0D + range, (double)y + 1.0D + range, (double)z + 1.0D + range);
            List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds);
            for (int i = 0; i < entities.size(); ++i) {
                EntityLivingBase e = (EntityLivingBase) entities.get(i);
                if (e instanceof EntityBanshee || e instanceof EntitySpectre || e instanceof EntityPoltergeist || e instanceof EntitySpirit || e instanceof EntityNightmare || e instanceof EntityLostSoul) {
                    return true;
                }
                if (e instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) e;
                    ExtendedPlayer ep = ExtendedPlayer.get(p);
                    if (ep != null && (ep.isAstralProjecting() || ep.getCreatureType() == TransformCreature.SPIRIT || ep.getCreatureType() == TransformCreature.SUBDUED_SPIRIT)) {
                        return true;
                    }
                    if (WorldProviderDreamWorld.getPlayerIsGhost(Infusion.getNBT(p))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}

