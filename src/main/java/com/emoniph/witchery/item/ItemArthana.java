package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.blocks.BlockPlacedItem;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.entity.EntityBanshee;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.entity.EntityDemon;
import com.emoniph.witchery.entity.EntityNightmare;
import com.emoniph.witchery.entity.EntityPoltergeist;
import com.emoniph.witchery.entity.EntitySpectre;
import com.emoniph.witchery.entity.EntitySpirit;
import com.emoniph.witchery.util.CreatureUtil;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IOffhandWield;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.weapons.IBattlegearWeapon;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Optional.InterfaceList({
   @Optional.Interface(iface = "mods.battlegear2.api.weapons.IBattlegearWeapon", modid = "battlegear2", striprefs = true),
   @Optional.Interface(iface = "mods.battlegear2.api.IOffhandWield", modid = "battlegear2", striprefs = true)
})
public class ItemArthana extends ItemSword implements IBattlegearWeapon, IOffhandWield {

   public ItemArthana() {
      super(ToolMaterial.GOLD);
      this.setMaxDamage(ToolMaterial.IRON.getMaxUses());
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public static int getHiltType(ItemStack stack) {
      if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("HiltType")) {
         return stack.getTagCompound().getByte("HiltType") & 7;
      }
      return 0;
   }

   public static void setHiltType(ItemStack stack, int hiltType) {
      if (stack != null) {
         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }
         stack.getTagCompound().setByte("HiltType", (byte)(hiltType & 7));
      }
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      int hilt = getHiltType(stack);
      switch (hilt) {
         case 1: return StatCollector.translateToLocal("witchery.arthana.rowan.name");
         case 2: return StatCollector.translateToLocal("witchery.arthana.alder.name");
         case 3: return StatCollector.translateToLocal("witchery.arthana.hawthorn.name");
         case 4: return StatCollector.translateToLocal("witchery.arthana.demon.name");
         case 5: return StatCollector.translateToLocal("witchery.arthana.silver.name");
         default: return super.getItemStackDisplayName(stack);
      }
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack itemstack) {
      return getHiltType(itemstack) > 0 ? EnumRarity.rare : EnumRarity.uncommon;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      int hilt = getHiltType(stack);
      switch (hilt) {
         case 1:
            list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.arthana.tip.rowan"));
            break;
         case 2:
            list.add(EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("witchery.arthana.tip.alder"));
            break;
         case 3:
            list.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("witchery.arthana.tip.hawthorn"));
            break;
         case 4:
            list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("witchery.arthana.tip.demon"));
            break;
         case 5:
            list.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("witchery.arthana.tip.silver"));
            break;
      }
      list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("witchery.arthana.tip.supernatural"));
      list.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("witchery.arthana.tip.altar"));
   }

   @Override
   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      if (!target.worldObj.isRemote && attacker instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer) attacker;
         int hilt = getHiltType(stack);

         // 1. Efectos específicos según la empuñadura (balanceados y sutiles)
         switch (hilt) {
            case 1: // Rowan: Al matar a una bruja hostil, limpia un efecto negativo como recompensa
               if (target instanceof EntityWitch || target instanceof EntityCovenWitch) {
                  target.attackEntityFrom(DamageSource.causePlayerDamage(player).setDamageBypassesArmor().setMagicDamage(), 2.0F);
                  if (target.getHealth() <= 0.0F || target.isDead) {
                     Collection activeEffects = player.getActivePotionEffects();
                     if (activeEffects != null && !activeEffects.isEmpty()) {
                        for (Object obj : activeEffects) {
                           if (obj instanceof PotionEffect) {
                              PotionEffect pe = (PotionEffect) obj;
                              int pid = pe.getPotionID();
                              if (pid >= 0 && pid < Potion.potionTypes.length && Potion.potionTypes[pid] != null && Potion.potionTypes[pid].isBadEffect()) {
                                 player.removePotionEffect(pid);
                                 ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_ORB, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.4D, 0.5D, 8, 0x00FF88);
                                 break;
                              }
                           }
                        }
                     }
                  }
               }
               break;

            case 2: // Alder: Probabilidad del 25% de Slowness I por 2 segundos. Pequeña probabilidad (10%) de drop botánico en monstruos
               if (target.worldObj.rand.nextFloat() <= 0.25F) {
                  target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 0));
                  ParticleEffect.SPELL_COLORED.send(SoundEffect.DIG_CLOTH, target.worldObj, target.posX, target.posY + 0.5D, target.posZ, 0.3D, 0.3D, 6, 0x228B22);
               }
               if ((target.getHealth() <= 0.0F || target.isDead) && target instanceof EntityMob) {
                  if (target.worldObj.rand.nextFloat() <= 0.10F) {
                     ItemStack drop = null;
                     int roll = target.worldObj.rand.nextInt(4);
                     if (roll == 0) drop = Witchery.Items.GENERIC.itemAshWood.createStack();
                     else if (roll == 1) drop = Witchery.Items.GENERIC.itemRowanBerries.createStack();
                     else if (roll == 2) drop = new ItemStack(Witchery.Items.SEEDS_BELLADONNA);
                     else drop = Witchery.Items.GENERIC.itemBelladonnaFlower.createStack();

                     if (drop != null) {
                        target.worldObj.spawnEntityInWorld(new EntityItem(target.worldObj, target.posX, target.posY + 0.5D, target.posZ, drop));
                     }
                  }
               }
               break;

            case 3: // Hawthorn: Daño moderado (+2.5) a espíritus y 15% de dropear polvo espectral
               boolean isEthereal = target instanceof EntitySpectre || target instanceof EntityBanshee
                     || target instanceof EntityPoltergeist || target instanceof EntitySpirit
                     || target instanceof EntityNightmare;
               if (isEthereal) {
                  target.attackEntityFrom(DamageSource.causePlayerDamage(player).setDamageBypassesArmor().setMagicDamage(), 2.5F);
                  ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, target.worldObj, target.posX, target.posY + 1.0D, target.posZ, 0.3D, 0.3D, 6);
                  if ((target.getHealth() <= 0.0F || target.isDead) && target.worldObj.rand.nextFloat() <= 0.15F) {
                     target.worldObj.spawnEntityInWorld(new EntityItem(target.worldObj, target.posX, target.posY + 0.5D, target.posZ, Witchery.Items.GENERIC.itemSpectralDust.createStack()));
                  }
               }
               break;

            case 4: // Demon: 20% de probabilidad de Wither I por 2 segundos. Ligero bonus de decapitación (5%)
               if (target.worldObj.rand.nextFloat() <= 0.20F) {
                  target.addPotionEffect(new PotionEffect(Potion.wither.id, 50, 0));
                  ParticleEffect.FLAME.send(SoundEffect.FIRE_IGNITE, target.worldObj, target.posX, target.posY + 1.0D, target.posZ, 0.3D, 0.3D, 4);
               }
               if (target.getHealth() <= 0.0F || target.isDead) {
                  if (target instanceof EntitySkeleton && target.worldObj.rand.nextFloat() <= 0.05F) {
                     int skullType = ((EntitySkeleton) target).getSkeletonType();
                     target.worldObj.spawnEntityInWorld(new EntityItem(target.worldObj, target.posX, target.posY + 0.5D, target.posZ, new ItemStack(Items.skull, 1, skullType)));
                  } else if (target instanceof EntityZombie && target.worldObj.rand.nextFloat() <= 0.05F) {
                     target.worldObj.spawnEntityInWorld(new EntityItem(target.worldObj, target.posX, target.posY + 0.5D, target.posZ, new ItemStack(Items.skull, 1, 2)));
                  } else if (target instanceof EntityCreeper && target.worldObj.rand.nextFloat() <= 0.05F) {
                     target.worldObj.spawnEntityInWorld(new EntityItem(target.worldObj, target.posX, target.posY + 0.5D, target.posZ, new ItemStack(Items.skull, 1, 4)));
                  }
               }
               break;

            case 5: // Silver: +2.0 de daño a no-muertos, licántropos y vampiros (cuenta como arma de plata)
               boolean isSilverVulnerable = target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD
                     || CreatureUtil.isWerewolf(target, true) || CreatureUtil.isVampire(target);
               if (isSilverVulnerable) {
                  target.attackEntityFrom(DamageSource.causePlayerDamage(player).setDamageBypassesArmor().setFireDamage(), 2.0F);
                  ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_ORB, target.worldObj, target.posX, target.posY + 1.0D, target.posZ, 0.3D, 0.3D, 6, 0xE0F7FA);
               }
               break;
         }

         // 2. Daño adicional base a criaturas sobrenaturales (+2.0)
         boolean isSupernatural = target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD
               || target instanceof EntityWitch
               || target instanceof EntityCovenWitch
               || target instanceof EntityDemon
               || target instanceof EntitySpectre
               || target instanceof EntityBanshee
               || target instanceof EntityPoltergeist
               || target instanceof EntitySpirit
               || (target instanceof EntityPlayer && (ExtendedPlayer.get((EntityPlayer) target) != null && (ExtendedPlayer.get((EntityPlayer) target).isVampire() || ExtendedPlayer.get((EntityPlayer) target).getWerewolfLevel() > 0)));

         if (isSupernatural) {
            target.attackEntityFrom(DamageSource.causePlayerDamage(player).setDamageBypassesArmor().setMagicDamage(), 2.0F);
            ParticleEffect.CRIT.send(SoundEffect.NONE, target.worldObj, target.posX, target.posY + 1.0D, target.posZ, 0.3D, 0.3D, 4);
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

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean sheatheOnBack(ItemStack item) {
      return false;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
      return true;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean isOffhandWieldable(ItemStack offhandStack, EntityPlayer wielder) {
      return true;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean isOffhandHandDual(ItemStack off) {
      return true;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean offhandAttackEntity(PlayerEventChild.OffhandAttackEvent event, ItemStack mainhandItem, ItemStack offhandItem) {
      return true;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean offhandClickAir(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem) {
      return false;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean offhandClickBlock(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem) {
      if (event.entityPlayer.isSneaking()
            && event.face == 1
            && event.world.getBlock(event.x, event.y, event.z) == Witchery.Blocks.ALTAR
            && event.world.isAirBlock(event.x, event.y + 1, event.z)) {
         event.useItem = Event.Result.ALLOW;
         return true;
      }
      return false;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public void performPassiveEffects(Side effectiveSide, ItemStack mainhandItem, ItemStack offhandItem) {
   }
}
