package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockPlacedItem;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.item.ItemBase;
import com.emoniph.witchery.network.PacketSpellPrepared;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandListener;
import mods.battlegear2.api.IOffhandWield;
import mods.battlegear2.api.IUsableItem;
import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Optional.InterfaceList({
   @Optional.Interface(iface = "mods.battlegear2.api.IUsableItem", modid = "battlegear2", striprefs = true),
   @Optional.Interface(iface = "mods.battlegear2.api.IOffhandWield", modid = "battlegear2", striprefs = true),
   @Optional.Interface(iface = "mods.battlegear2.api.IAllowItem", modid = "battlegear2", striprefs = true),
   @Optional.Interface(iface = "mods.battlegear2.api.IOffhandListener", modid = "battlegear2", striprefs = true)
})
public class ItemMysticBranch extends ItemBase implements IUsableItem, IOffhandWield, IAllowItem, IOffhandListener {

   private static final float THRESHOLD_ORTHOGONAL = 7.0F;
   private static final int MAX_STROKES = 15;


   public ItemMysticBranch() {
      this.setMaxStackSize(1);
      this.setFull3D();
   }

   public static int getWoodType(ItemStack stack) {
      if(stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("WoodType")) {
         return stack.getTagCompound().getByte("WoodType") & 3;
      }
      return 2;
   }

      public static int getMaxAllowedChargeLevel(EntityPlayer player, SymbolEffect effect) {
      if (player == null) return 1;
      if (player.capabilities.isCreativeMode || com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player)) {
         return 3;
      }
      int masteredCount = SymbolEffect.getMasteredSpellCount(player);
      // Rango 1 (Iniciado: 0-5 dominados): Solo Nivel I
      if (masteredCount < 6) {
         return 1;
      }
      // Para sobrecargar, el hechizo específico debe estar dominado (3 lanzamientos exitosos)
      if (effect != null && !SymbolEffect.isSpellMastered(player, effect)) {
         return 1;
      }
      // Rango 2 (Adepto: 6-13 dominados): Hasta Nivel II
      if (masteredCount < 14) {
         return 2;
      }
      // Rango 3 y 4 (Magister / Maestro: 14+ dominados): Hasta Nivel III
      return 3;
   }

   public static void setWoodType(ItemStack stack, int woodType) {
      if(stack != null) {
         if(!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }
         stack.getTagCompound().setByte("WoodType", (byte)(woodType & 3));
      }
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      int wood = getWoodType(stack);
      String key = wood == 0 ? "witchery.branch.rowan.name" : (wood == 1 ? "witchery.branch.alder.name" : "witchery.branch.hawthorn.name");
      return net.minecraft.util.StatCollector.translateToLocal(key);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, java.util.List list, boolean advanced) {
      int wood = getWoodType(stack);
      if(wood == 0) {
         list.add(EnumChatFormatting.GOLD + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.rowan.desc1"));
         list.add(EnumChatFormatting.GRAY + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.rowan.desc2"));
         list.add(EnumChatFormatting.DARK_GREEN + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.rowan.desc3"));
      } else if(wood == 1) {
         list.add(EnumChatFormatting.RED + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.alder.desc1"));
         list.add(EnumChatFormatting.GRAY + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.alder.desc2"));
         list.add(EnumChatFormatting.DARK_RED + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.alder.desc3"));
      } else {
         list.add(EnumChatFormatting.AQUA + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.hawthorn.desc1"));
         list.add(EnumChatFormatting.GRAY + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.hawthorn.desc2"));
         list.add(EnumChatFormatting.DARK_PURPLE + net.minecraft.util.StatCollector.translateToLocal("witchery.branch.hawthorn.desc3"));
      }

      list.add(EnumChatFormatting.DARK_GRAY + "(Al castear, presiona Shift para potenciar la carga I - II - III)");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public int getColorFromItemStack(ItemStack stack, int pass) {
      int wood = getWoodType(stack);
      if(wood == 0) {
         return 0xD2691E;
      } else if(wood == 1) {
         return 0x8B4513;
      } else {
         return 0xE6DFD3;
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void getSubItems(net.minecraft.item.Item item, net.minecraft.creativetab.CreativeTabs tab, java.util.List itemList) {
      for(int wood = 0; wood < 3; ++wood) {
         ItemStack branch = new ItemStack(item, 1, 0);
         setWoodType(branch, wood);
         itemList.add(branch);
      }
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   @SideOnly(Side.CLIENT)
   public boolean isFull3D() {
      return true;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.block;
   }

   public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
      return super.onDroppedByPlayer(item, player);
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return '\u8ca0';
   }

   public boolean hasEffect(ItemStack par1ItemStack, int pass) {
      return true;
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) {}

   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (player.getEntityData().getLong("witchery_silenced_until") > world.getTotalWorldTime()) {
         SoundEffect.NOTE_SNARE.playOnlyTo(player, 1.0F, 0.5F);
         return stack;
      }

      if (player.getEntityData().getInteger("WITCLeviosaTicks") > 0) {
         if (!world.isRemote) {
            float currentDist = player.getEntityData().hasKey("WITCLeviosaDistance") ? player.getEntityData().getFloat("WITCLeviosaDistance") : 5.0f;
            if (player.isSneaking()) {
               currentDist = Math.min(30.0f, currentDist + 2.0f);
            } else {
               currentDist = Math.max(1.5f, currentDist - 2.0f);
            }
            player.getEntityData().setFloat("WITCLeviosaDistance", currentDist);
            SoundEffect.NOTE_PLING.playOnlyTo(player, 1.0F, 0.6F + (currentDist / 30.0F) * 0.8F);
            ChatUtil.sendTranslated(EnumChatFormatting.AQUA, player, "witchery.pott.wingardiumleviosa.dist", new Object[]{String.format("%.1f", currentDist)});
         }
         return stack;
      }

      NBTTagCompound nbtTag = player.getEntityData();
      if(!player.worldObj.isRemote) {
         nbtTag.removeTag("WITCSpellEffectID");
         nbtTag.removeTag("WITCSpellEffectEnhanced");
      }

      nbtTag.setInteger("WITCSpellChargeLevel", 1);
      nbtTag.setBoolean("WITCUsingSneak", player.isSneaking());
      nbtTag.setByteArray("Strokes", new byte[0]);
      nbtTag.setFloat("startPitch", player.rotationPitch);
      nbtTag.setFloat("startYaw", player.rotationYawHead);
      player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
      return stack;
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      if(world.getBlock(x, y, z) == Witchery.Blocks.ALTAR && side == 1 && world.getBlock(x, y + 1, z) == Blocks.air) {
         BlockPlacedItem.placeItemInWorld(stack, player, world, x, y + 1, z);
         player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
         return !world.isRemote;
      } else {
         return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
      }
   }

   public void onUsingTick(ItemStack stack, EntityPlayer player, int countdown) {
      if(player.worldObj.isRemote) {
         NBTTagCompound nbtTag = player.getEntityData();
         if(nbtTag == null) {
            return;
         }

         boolean wasSneaking = nbtTag.getBoolean("WITCUsingSneak");
         boolean isSneaking = player.isSneaking();
         if (isSneaking && !wasSneaking) {
            byte[] currentStrokes = nbtTag.getByteArray("Strokes");
            SymbolEffect currentEffect = EffectRegistry.instance().getEffect(currentStrokes);
            int maxAllowed = getMaxAllowedChargeLevel(player, currentEffect);

            int charge = nbtTag.getInteger("WITCSpellChargeLevel");
            if (charge < 1 || charge > 3) charge = 1;

            if (maxAllowed <= 1) {
               SoundEffect.NOTE_SNARE.playOnlyTo(player, 1.0F, 0.8F);
               if (currentEffect != null && !SymbolEffect.isSpellMastered(player, currentEffect)) {
                  player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "✦ [Canalización] Debes dominar este hechizo (3 lanzamientos) antes de poder sobrecargarlo a Nivel II."));
               } else {
                  player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "✦ [Canalización] Aún eres un Iniciado. Domina al menos 6 hechizos previos para desbloquear la sobrecarga a Nivel II."));
               }
            } else {
               int nextCharge = charge + 1;
               if (nextCharge > maxAllowed) {
                  nextCharge = 1;
               }
               nbtTag.setInteger("WITCSpellChargeLevel", nextCharge);

               float pitch = nextCharge == 1 ? 0.8F : (nextCharge == 2 ? 1.2F : 1.6F);
               SoundEffect.NOTE_HARP.playOnlyTo(player, 1.0F, pitch);
               String lvlRoman = nextCharge == 1 ? "I" : (nextCharge == 2 ? "II" : "III");
               EnumChatFormatting lvlColor = nextCharge == 1 ? EnumChatFormatting.WHITE : (nextCharge == 2 ? EnumChatFormatting.AQUA : EnumChatFormatting.GOLD);
               player.addChatMessage(new ChatComponentText(lvlColor + "✦ Carga del Hechizo: Nivel " + lvlRoman));

               if (currentEffect != null) {
                  int drawnLevel = EffectRegistry.instance().getLevel(currentStrokes);
                  int finalLvl = Math.min(maxAllowed, Math.max(nextCharge, drawnLevel));
                  Witchery.packetPipeline.sendToServer(new PacketSpellPrepared(currentEffect, finalLvl));
               }
            }
         }
         nbtTag.setBoolean("WITCUsingSneak", isSneaking);

         float yawDiff = nbtTag.getFloat("startYaw") - player.rotationYawHead;
         float pitchDiff = nbtTag.getFloat("startPitch") - player.rotationPitch;
         byte[] strokes = nbtTag.getByteArray("Strokes");
         int strokesStart = strokes.length;
         if(!EffectRegistry.instance().contains(strokes) && strokesStart <= 15) {
            if(pitchDiff >= 7.0F) {
               strokes = this.addNewStroke(nbtTag, strokes, (byte)0);
            } else if(pitchDiff <= -7.0F) {
               strokes = this.addNewStroke(nbtTag, strokes, (byte)1);
            } else if(yawDiff <= -7.0F) {
               strokes = this.addNewStroke(nbtTag, strokes, (byte)2);
            } else if(yawDiff >= 7.0F) {
               strokes = this.addNewStroke(nbtTag, strokes, (byte)3);
            }

            if(strokes.length > strokesStart) {
               nbtTag.setFloat("startPitch", player.rotationPitch);
               nbtTag.setFloat("startYaw", player.rotationYawHead);

               SymbolEffect effect = EffectRegistry.instance().getEffect(strokes);
               if(effect != null) {
                  int maxAllowed = getMaxAllowedChargeLevel(player, effect);
                  int charge = nbtTag.getInteger("WITCSpellChargeLevel");
                  if (charge < 1 || charge > 3) charge = 1;
                  if (charge > maxAllowed) charge = maxAllowed;
                  int drawnLevel = EffectRegistry.instance().getLevel(strokes);
                  int level = Math.min(maxAllowed, Math.max(charge, drawnLevel));
                  Witchery.packetPipeline.sendToServer(new PacketSpellPrepared(effect, level));
               }
            }
         }
      }

   }

   public byte[] addNewStroke(NBTTagCompound nbtTag, byte[] strokes, byte stroke) {
      byte[] newStrokes = new byte[strokes.length + 1];
      System.arraycopy(strokes, 0, newStrokes, 0, strokes.length);
      newStrokes[newStrokes.length - 1] = stroke;
      nbtTag.setByteArray("Strokes", newStrokes);
      return newStrokes;
   }

   public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int countdown) {
      NBTTagCompound nbtTag = player.getEntityData();
      if(nbtTag != null) {
         nbtTag.removeTag("WITCUsingSneak");
         nbtTag.removeTag("WITCSpellChargeLevel");
         if(!world.isRemote) {
            int effectID = nbtTag.getInteger("WITCSpellEffectID");
            SymbolEffect effectPre = EffectRegistry.instance().getEffect(effectID);
            int maxAllowed = getMaxAllowedChargeLevel(player, effectPre);
            int level = 1;
            if(nbtTag.hasKey("WITCSpellEffectEnhanced")) {
               level = Math.min(maxAllowed, Math.max(1, nbtTag.getInteger("WITCSpellEffectEnhanced")));
               nbtTag.removeTag("WITCSpellEffectEnhanced");
            }

            nbtTag.removeTag("WITCSpellEffectID");
            com.emoniph.witchery.common.ExtendedPlayer exPlayer = com.emoniph.witchery.common.ExtendedPlayer.get(player);
            if(exPlayer != null && exPlayer.isSilenced() && !player.capabilities.isCreativeMode) {
               long secLeft = (exPlayer.getSilencedUntil() - System.currentTimeMillis()) / 1000L + 1;
               ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.silenced", new Object[]{Long.valueOf(secLeft)});
               SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
               return;
            }
            SymbolEffect effect = EffectRegistry.instance().getEffect(effectID);
            NBTTagCompound nbtPerm = Infusion.getNBT(player);
            boolean isSupreme = com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player);
            if(isSupreme && nbtPerm != null && (!nbtPerm.hasKey("witcheryInfusionID") || nbtPerm.getInteger("witcheryInfusionID") <= 0)) {
               Infusion.switchInfusionForSupreme(player, 4);
            }
            if(effect != null) {
               if(!player.capabilities.isCreativeMode && !isSupreme && (nbtPerm == null || !nbtPerm.hasKey("witcheryInfusionID") || !nbtPerm.hasKey("witcheryInfusionCharges"))) {
                  ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.infusionrequired", new Object[0]);
                  SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
               } else if(effect.hasValidInfusion(player, nbtPerm != null ? nbtPerm.getInteger("witcheryInfusionID") : 0)) {
                  if(effect.hasValidKnowledge(player, nbtPerm)) {
                     long ticksRemaining = effect.cooldownRemaining(player, nbtPerm);
                     if(ticksRemaining > 0L && !player.capabilities.isCreativeMode) {
                        ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.effectoncooldown", new Object[]{Long.valueOf(TimeUtil.ticksToSecs(ticksRemaining)).toString()});
                        SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                     } else {
                        int wood = getWoodType(stack);
                        int effectiveLevel = level;
                        int cost = effect.getChargeCost(world, player, level);
                        if(wood == 0) {
                           if(!effect.isCurse()) {
                              cost = Math.max(1, (int)(cost * 0.70F));
                           }
                        } else if(wood == 1) {
                           if(effect.isCurse()) {
                              effectiveLevel = level + 1;
                              cost = Math.max(1, (int)(cost * 0.75F));
                           }
                        } else if(wood == 2) {
                           if(effect.isWindOrMovementSpell() || effect.getRequiredInfusionID() == 4 || effect.getRequiredInfusionID() == 3) {
                              cost = Math.max(1, (int)(cost * 0.70F));
                           }
                        }
                        int currentCharges = nbtPerm != null ? nbtPerm.getInteger("witcheryInfusionCharges") : 0;
                        boolean canCast = player.capabilities.isCreativeMode || currentCharges >= cost;
                        if (!canCast) {
                           int deficit = cost - currentCharges;
                           canCast = Infusion.attemptOvercharge(world, player, deficit);
                        }

                        if (!canCast) {
                           ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.nocharges", new Object[0]);
                           SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                        } else {
                           effect.perform(world, player, effectiveLevel);
                           effect.setOnCooldown(player);
                           effect.onSpellCastSuccess(player);
                           this.announceCastSpell(player, effect);
                           if(!player.capabilities.isCreativeMode && nbtPerm != null) {
                              if(com.emoniph.witchery.familiar.Familiar.hasActiveBrewMasteryFamiliar(player) && world.rand.nextFloat() < 0.25F) {
                                 player.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GREEN + "✦ [Sapo Familiar] Tu familiar canalizó la humedad arcana y ahorró la energía del hechizo."));
                              } else {
                                 Infusion.setCurrentEnergy(player, Math.max(0, nbtPerm.getInteger("witcheryInfusionCharges") - cost));
                              }
                           }
                        }
                     }
                  } else {
                     int tier = effect.getSpellTier();
                     int mastered = SymbolEffect.getMasteredSpellCount(player);
                     int needed = effect.getRequiredMasteryCount();
                     ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.tierlocked", new Object[]{Integer.valueOf(tier), Integer.valueOf(needed), Integer.valueOf(mastered)});
                     SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
                  }
               } else {
                  ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.infernalrequired", new Object[0]);
                  SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
               }
            } else {
               ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.infuse.branch.unknownsymbol", new Object[0]);
               SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
            }
          } else {
            nbtTag.removeTag("Strokes");
            nbtTag.removeTag("startYaw");
            nbtTag.removeTag("startPitch");
         }
      }

   }

   private void announceCastSpell(EntityPlayer player, SymbolEffect effect) {
      // Chat broadcast disabled per request
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean isUsedOverAttack(ItemStack itemStack) {
      return true;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean isOffhandWieldable(ItemStack offhandStack, EntityPlayer wielder) {
      return true;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
      return true;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public Event.Result onClickBlock(PlayerInteractEvent event, ItemStack mainhandStack, ItemStack offhandStack, boolean isOffhand) {
      return isOffhand ? Event.Result.ALLOW : Event.Result.DEFAULT;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public Event.Result onClickAir(EntityPlayer player, ItemStack mainhandStack, ItemStack offhandStack) {
      return Event.Result.ALLOW;
   }

   @Optional.Method(modid = "battlegear2")
   @Override
   public void onAttackEntity(PlayerEventChild.OffhandAttackEvent event, boolean isOffhand) {
      if (isOffhand) {
         event.swingOffhand = false;
         event.shouldAttack = false;
         event.cancelParent = false;
      }
   }
}
