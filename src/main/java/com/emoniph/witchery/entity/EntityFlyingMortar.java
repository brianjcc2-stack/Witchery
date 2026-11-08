package com.emoniph.witchery.entity;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFlyingMortar extends EntityBroom {

   private int mortarCooldown = 0;

   public EntityFlyingMortar(World world) {
      super(world);
      this.setSize(1.4F, 0.9F);
   }

   public EntityFlyingMortar(World world, double x, double y, double z) {
      super(world, x, y, z);
      this.setSize(1.4F, 0.9F);
   }

   @Override
   public double getMountedYOffset() {
      return 0.4D;
   }

   @Override
   public boolean interactFirst(EntityPlayer player) {
      if (player.isSneaking()) {
         if (!this.worldObj.isRemote) {
            ItemStack stack = new ItemStack(Witchery.Items.FLYING_MORTAR);
            this.entityDropItem(stack, 0.0F);
            this.setDead();
         }
         return true;
      }
      return super.interactFirst(player);
   }

   @Override
   public void onUpdate() {
      super.onUpdate();

      if (this.mortarCooldown > 0) {
         --this.mortarCooldown;
      }

      // Heavy stone engine smoke particles
      if (this.worldObj.isRemote && this.riddenByEntity != null) {
         if (this.rand.nextInt(2) == 0) {
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.2D, this.posZ, 0.0D, -0.05D, 0.0D);
         }
      }
   }

   public boolean fireMortarShell(EntityPlayer rider) {
      if (this.mortarCooldown > 0 || this.worldObj.isRemote) {
         return false;
      }

      this.mortarCooldown = 25; // 1.25s cooldown between shots

      Vec3 look = rider.getLookVec();
      double speed = 1.6D;
      double vx = look.xCoord * speed;
      double vy = look.yCoord * speed + 0.2D;
      double vz = look.zCoord * speed;

      // Spawn an explosive arcane projectile
      EntitySpellEffect projectile = new EntitySpellEffect(this.worldObj, rider, vx, vy, vz, com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry.instance().getEffect(1), 1);
      projectile.setPosition(this.posX + look.xCoord * 1.2D, this.posY + 0.8D, this.posZ + look.zCoord * 1.2D);
      this.worldObj.spawnEntityInWorld(projectile);

      SoundEffect.RANDOM_EXPLODE.playAt((net.minecraft.entity.Entity)this, 1.0F, 1.6F);
      ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_FIZZ, this.worldObj, projectile.posX, projectile.posY, projectile.posZ, 0.8D, 0.8D, 16, 0xFF6600);
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode) {
         this.setDead();
         return true;
      }
      return super.attackEntityFrom(source, amount);
   }
}
