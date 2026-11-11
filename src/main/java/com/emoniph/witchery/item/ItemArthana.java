package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.blocks.BlockPlacedItem;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.entity.EntityBanshee;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.entity.EntityDemon;
import com.emoniph.witchery.entity.EntityPoltergeist;
import com.emoniph.witchery.entity.EntitySpectre;
import com.emoniph.witchery.entity.EntitySpirit;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemArthana extends ItemSword {

   public ItemArthana() {
      super(ToolMaterial.GOLD);
      this.setMaxDamage(ToolMaterial.IRON.getMaxUses());
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
      return EnumRarity.uncommon;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.arthana.tip.dispel"));
      list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("witchery.arthana.tip.supernatural"));
      list.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("witchery.arthana.tip.altar"));
   }

   @Override
   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      if (!target.worldObj.isRemote && attacker instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer) attacker;

         // 1. Spellbreaker: Disipar buffs mágicos benéficos del objetivo
         Collection activeEffects = target.getActivePotionEffects();
         if (activeEffects != null && !activeEffects.isEmpty()) {
            List<Integer> dispellable = new ArrayList<Integer>();
            for (Object obj : activeEffects) {
               if (obj instanceof PotionEffect) {
                  int id = ((PotionEffect) obj).getPotionID();
                  if (id == Potion.regeneration.id || id == Potion.damageBoost.id || id == Potion.resistance.id
                        || id == Potion.invisibility.id || id == Potion.moveSpeed.id || id == Potion.fireResistance.id) {
                     dispellable.add(id);
                  }
               }
            }

            if (!dispellable.isEmpty() && target.worldObj.rand.nextFloat() <= 0.45F) {
               int removedId = dispellable.get(target.worldObj.rand.nextInt(dispellable.size()));
               target.removePotionEffect(removedId);
               ParticleEffect.MAGIC_CRIT.send(SoundEffect.RANDOM_ORB, target.worldObj, target.posX, target.posY + 1.0D, target.posZ, 0.5D, 0.5D, 10);
               ChatUtil.sendTranslated(EnumChatFormatting.GOLD, player, "witchery.arthana.dispel", target.getCommandSenderName());
            }
         }

         // 2. Daño adicional a criaturas sobrenaturales
         boolean isSupernatural = target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD
               || target instanceof EntityWitch
               || target instanceof EntityCovenWitch
               || target instanceof EntityDemon
               || target instanceof EntitySpectre
               || target instanceof EntityBanshee
               || target instanceof EntityPoltergeist
               || target instanceof EntitySpirit;

         if (!isSupernatural && target instanceof EntityPlayer) {
            ExtendedPlayer ep = ExtendedPlayer.get((EntityPlayer) target);
            if (ep != null && (ep.isVampire() || ep.getWerewolfLevel() > 0)) {
               isSupernatural = true;
            }
         }

         if (isSupernatural) {
            target.attackEntityFrom(DamageSource.causePlayerDamage(player).setDamageBypassesArmor().setMagicDamage(), 4.0F);
            ParticleEffect.CRIT.send(SoundEffect.NONE, target.worldObj, target.posX, target.posY + 1.0D, target.posZ, 0.4D, 0.4D, 6);
         }
      }

      return super.hitEntity(stack, target, attacker);
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      if(world.getBlock(x, y, z) == Witchery.Blocks.ALTAR && side == 1 && world.getBlock(x, y + 1, z) == Blocks.air) {
         BlockPlacedItem.placeItemInWorld(stack, player, world, x, y + 1, z);
         player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
      }

      return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
   }
}
