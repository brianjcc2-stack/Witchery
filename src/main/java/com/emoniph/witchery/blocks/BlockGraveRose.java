package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBaseBush;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockGraveRose extends BlockBaseBush {

   @SideOnly(Side.CLIENT)
   private IIcon fullIcon;

   public BlockGraveRose() {
      super(Material.plants);
      this.setHardness(0.0F);
      this.setStepSound(Block.soundTypeGrass);
      this.setTickRandomly(true);
      this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.8F, 0.7F);
   }

   @Override
   protected boolean canPlaceBlockOn(Block ground) {
      return ground == Blocks.grass || ground == Blocks.dirt || ground == Blocks.farmland ||
             ground == Blocks.mycelium || ground == Blocks.soul_sand || ground == Blocks.netherrack;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister iconRegister) {
      super.blockIcon = iconRegister.registerIcon(this.getTextureName());
      this.fullIcon = iconRegister.registerIcon(this.getTextureName() + "_full");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int meta) {
      return meta == 1 ? this.fullIcon : this.blockIcon;
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if(!world.isRemote && entity instanceof EntityLivingBase) {
         EntityLivingBase living = (EntityLivingBase)entity;
         living.attackEntityFrom(DamageSource.cactus, 1.5F);
         living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 0));
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      super.updateTick(world, x, y, z, rand);
      if(!world.isRemote && world.getBlockMetadata(x, y, z) == 0) {
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 4), (double)(y - 2), (double)(z - 4), (double)(x + 5), (double)(y + 3), (double)(z + 5));
         List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
         for(Object obj : entities) {
            EntityLivingBase living = (EntityLivingBase)obj;
            if(living.getHealth() <= 0.0F || living.isDead) {
               world.setBlockMetadataWithNotify(x, y, z, 1, 3);
               ParticleEffect.PORTAL.send(SoundEffect.WITCHERY_MOB_SPECTRE_SPECTRE_HIT, world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, 0.5D, 0.5D, 16);
               break;
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
      if(world.getBlockMetadata(x, y, z) == 1) {
         double px = (double)x + 0.3D + (double)(rand.nextFloat() * 0.4F);
         double py = (double)y + 0.6D + (double)(rand.nextFloat() * 0.3F);
         double pz = (double)z + 0.3D + (double)(rand.nextFloat() * 0.4F);
         ParticleEffect.SMOKE.send(SoundEffect.NONE, world, px, py, pz, 0.0D, 0.05D, 1);
      }
   }

   @Override
   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      if(world.getBlockMetadata(x, y, z) == 1) {
         ItemStack equipped = player.getCurrentEquippedItem();
         if(equipped != null && (equipped.getItem() instanceof ItemShears || (Witchery.Items.BOLINE != null && equipped.getItem() == Witchery.Items.BOLINE))) {
            if(!world.isRemote) {
               world.setBlockMetadataWithNotify(x, y, z, 0, 3);
               ItemStack drop = Witchery.Items.GENERIC.itemGraveyardDust.createStack(1 + world.rand.nextInt(2));
               EntityItem itemEntity = new EntityItem(world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, drop);
               world.spawnEntityInWorld(itemEntity);
               equipped.damageItem(1, player);
               ParticleEffect.SMOKE.send(SoundEffect.RANDOM_ORB, world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, 0.5D, 0.5D, 8);
            }
            return true;
         }
      }
      return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
   }

   @Override
   public int damageDropped(int meta) {
      return 0;
   }
}
