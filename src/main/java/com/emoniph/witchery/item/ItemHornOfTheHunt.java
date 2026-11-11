package com.emoniph.witchery.item;

import com.emoniph.witchery.entity.EntityHornedHuntsman;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.InfusionInfernal;
import com.emoniph.witchery.item.ItemBase;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemHornOfTheHunt extends ItemBase {

   public ItemHornOfTheHunt() {
      super.autoGenerateTooltip = false;
      this.setMaxDamage(25);
      this.setMaxStackSize(1);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.horn.tip.hunt"));
      list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("witchery.horn.tip.boss"));
   }

   public EnumAction getItemUseAction(ItemStack itemstack) {
      return EnumAction.bow;
   }

   public int getMaxItemUseDuration(ItemStack itemstack) {
      return TimeUtil.secsToTicks(2);
   }

   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
      return stack;
   }

   public void onUsingTick(ItemStack stack, EntityPlayer player, int countdown) {
      if (!player.worldObj.isRemote && countdown == 1) {
         World world = player.worldObj;

         if (player.isSneaking()) {
            // Desafío al Cazador Cornudo (Boss)
            SoundEffect.WITCHERY_RANDOM_HORN.playAtPlayer(world, player, 1.0F, 0.8F);
            EntityCreature creature = InfusionInfernal.spawnCreature(world, EntityHornedHuntsman.class, (int) player.posX, (int) player.posY, (int) player.posZ, player, 2, 8, ParticleEffect.EXPLODE, SoundEffect.MOB_WITHER_SPAWN);
            if (creature != null) {
               EntityHornedHuntsman huntsman = (EntityHornedHuntsman) creature;
               huntsman.causeExplosiveEntrance();
               huntsman.func_110163_bv();
               huntsman.func_82206_m();
               stack.damageItem(5, player);
            }
         } else {
            // La Cacería Salvaje: Sabuesos y pánico
            SoundEffect.WITCHERY_RANDOM_HORN.playAtPlayer(world, player, 1.2F, 1.1F);

            // 1. Invocar 2 sabuesos de caza rabiosos vinculados
            int numHounds = 2;
            for (int i = 0; i < numHounds; ++i) {
               EntityCreature creature = Infusion.spawnCreature(world, EntityWolf.class, (int) player.posX, (int) player.posY, (int) player.posZ, player, 1, 3, ParticleEffect.FLAME, SoundEffect.WITCHERY_MOB_WOLFMAN_HOWL);
               if (creature instanceof EntityWolf) {
                  EntityWolf hound = (EntityWolf) creature;
                  hound.func_152115_b(player.getUniqueID().toString());
                  hound.setTamed(true);
                  hound.setCollarColor(14); // Rojo oscuro
                  hound.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 600, 1));
                  hound.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 600, 1));
                  hound.addPotionEffect(new PotionEffect(Potion.resistance.id, 600, 1));
               }
            }

            // 2. Provocar Pánico en enemigos y fauna cercana
            AxisAlignedBB panicBox = player.boundingBox.expand(16.0D, 8.0D, 16.0D);
            List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, panicBox);
            for (Object obj : entities) {
               if (obj instanceof EntityLivingBase && obj != player) {
                  EntityLivingBase living = (EntityLivingBase) obj;
                  if (living instanceof IMob || living instanceof EntityAnimal) {
                     living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 120, 2));
                     living.addPotionEffect(new PotionEffect(Potion.weakness.id, 120, 1));

                     // Impulso hacia afuera para simular huida aterrada
                     double dx = living.posX - player.posX;
                     double dz = living.posZ - player.posZ;
                     double dLen = Math.sqrt(dx * dx + dz * dz);
                     if (dLen > 0.1D) {
                        living.motionX += (dx / dLen) * 0.6D;
                        living.motionZ += (dz / dLen) * 0.6D;
                        living.velocityChanged = true;
                     }
                  }
               }
            }

            ParticleEffect.PORTAL.send(SoundEffect.NONE, player, 2.0D, 1.0D, 16);
            stack.damageItem(1, player);
         }
      }
   }
}
