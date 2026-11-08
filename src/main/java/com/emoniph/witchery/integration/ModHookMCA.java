package com.emoniph.witchery.integration;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.util.Log;
import cpw.mods.fml.common.Loader;
import java.util.List;
import mca.ai.AISleep;
import mca.data.PlayerMemory;
import mca.entity.EntityHuman;
import mca.enums.EnumRelation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ModHookMCA extends ModHook {

   private static boolean isPresent = false;

   @Override
   public String getModID() {
      return "MCA";
   }

   @Override
   protected void doInit() {
      isPresent = true;
      Log.instance().debug("MCA (Minecraft Comes Alive) integration initialized for Witchery.");
   }

   @Override
   protected void doPostInit() {}

   @Override
   protected void doReduceMagicPower(EntityLivingBase var1, float var2) {}

   public static boolean isMCAPresent() {
      return isPresent || Loader.isModLoaded("MCA");
   }

   public static boolean isMCAHuman(Entity entity) {
      if (!isMCAPresent() || entity == null) {
         return false;
      }
      return entity instanceof EntityHuman;
   }

   public static String getHumanName(Entity entity) {
      if (isMCAHuman(entity)) {
         return ((EntityHuman)entity).getName();
      }
      return entity != null ? entity.getCommandSenderName() : "";
   }

   public static boolean isSleeping(Entity entity) {
      if (!isMCAHuman(entity)) {
         return false;
      }
      try {
         EntityHuman human = (EntityHuman)entity;
         AISleep sleepAI = human.getAI(AISleep.class);
         return sleepAI != null && (sleepAI.getIsSleeping() || sleepAI.getIsInBed());
      } catch (Throwable t) {
         return false;
      }
   }

   public static int getHearts(Entity entity, EntityPlayer player) {
      if (!isMCAHuman(entity) || player == null) {
         return 0;
      }
      try {
         EntityHuman human = (EntityHuman)entity;
         PlayerMemory mem = human.getPlayerMemory(player);
         return mem != null ? mem.getHearts() : 0;
      } catch (Throwable t) {
         return 0;
      }
   }

   public static void modifyHearts(Entity entity, EntityPlayer player, int delta) {
      if (!isMCAHuman(entity) || player == null) {
         return;
      }
      try {
         EntityHuman human = (EntityHuman)entity;
         PlayerMemory mem = human.getPlayerMemory(player);
         if (mem != null) {
            mem.setHearts(mem.getHearts() + delta);
         }
      } catch (Throwable t) {
         Log.instance().debug("Failed to modify MCA hearts: " + t.getMessage());
      }
   }

   public static boolean isRelated(Entity entity, EntityPlayer player) {
      if (!isMCAHuman(entity) || player == null) {
         return false;
      }
      try {
         EntityHuman human = (EntityHuman)entity;
         PlayerMemory mem = human.getPlayerMemory(player);
         return mem != null && mem.isRelatedToPlayer();
      } catch (Throwable t) {
         return false;
      }
   }

   public static boolean isSpouse(Entity entity, EntityPlayer player) {
      if (!isMCAHuman(entity) || player == null) {
         return false;
      }
      try {
         EntityHuman human = (EntityHuman)entity;
         PlayerMemory mem = human.getPlayerMemory(player);
         if (mem != null) {
            EnumRelation rel = mem.getRelation();
            return rel == EnumRelation.HUSBAND || rel == EnumRelation.WIFE;
         }
      } catch (Throwable t) {
         return false;
      }
      return false;
   }

   public static void say(Entity entity, EntityPlayer player, String message) {
      if (!isMCAHuman(entity) || player == null) {
         return;
      }
      try {
         ((EntityHuman)entity).sayRaw(message, player);
      } catch (Throwable t) {
         player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "<" + getHumanName(entity) + "> " + message));
      }
   }

   /**
    * Handles vampire feeding reaction from MCA villagers.
    */
   public static void onVampireFeed(Entity entity, EntityPlayer player, boolean isHypnotized, boolean isSleeping) {
      if (!isMCAHuman(entity) || player == null) {
         return;
      }
      EntityHuman human = (EntityHuman)entity;
      String name = human.getName();

      if (isSleeping || isHypnotized) {
         // Clean, discreet feeding in the dark or under trance
         // Very minor chance of half-waking whisper
         if (isSleeping && player.worldObj.rand.nextInt(3) == 0) {
            say(human, player, "§7*murmura entre sueños* ...un escalofrío tan frío en el cuello...");
         }
      } else {
         // Violent discovered feeding!
         modifyHearts(human, player, -35);
         if (isSpouse(human, player)) {
            say(human, player, "§c¿" + player.getCommandSenderName() + "...? ¡¿Por qué me haces esto?! ¡Creí que me amabas!");
         } else if (isRelated(human, player)) {
            say(human, player, "§c¡Socorro! ¡¿Por qué nos haces esto?! ¡Eres un monstruo!");
         } else {
            String[] screams = new String[]{
               "§c¡Socorro! ¡Una criatura de la noche me está atacando!",
               "§c¡Aléjate de mí, monstruo! ¡Guardias, cazadores!",
               "§c¡Mis venas... me está drenando la vida!"
            };
            say(human, player, screams[player.worldObj.rand.nextInt(screams.length)]);
         }

         // Mark player with village terror tag
         player.getEntityData().setLong("WITCLastMCATerrorTick", player.worldObj.getTotalWorldTime());
         player.getEntityData().setInteger("WITCMCATerrorLevel", player.getEntityData().getInteger("WITCMCATerrorLevel") + 1);
      }
   }

   /**
    * Handles taglock snip attempt on an MCA human.
    */
   public static void onTaglockAttempt(Entity entity, EntityPlayer player, boolean success) {
      if (!isMCAHuman(entity) || player == null) {
         return;
      }
      EntityHuman human = (EntityHuman)entity;
      if (!success) {
         modifyHearts(human, player, -15);
         String[] angryLines = new String[]{
            "§e¡Oye! ¿Qué pretendes hacerme a mis espaldas con esas tijeras?",
            "§e¡Apártate! ¿Intentas cortarme el pelo para alguna brujería?",
            "§e¡No me toques! ¡Sé muy bien lo que hacen los brujos con muestras de sangre!"
         };
         say(human, player, angryLines[player.worldObj.rand.nextInt(angryLines.length)]);
         player.getEntityData().setLong("WITCLastMCATerrorTick", player.worldObj.getTotalWorldTime());
      }
   }

   /**
    * Applies anemia / weakness if the villager has been heavily bled.
    */
   public static void applyAnemia(Entity entity) {
      if (!isMCAHuman(entity)) {
         return;
      }
      EntityHuman human = (EntityHuman)entity;
      human.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 6000, 0));
      human.addPotionEffect(new PotionEffect(Potion.weakness.id, 6000, 0));
   }

   /**
    * Check if other MCA humans in the area witness violent blood drinking.
    */
   public static void checkWitnesses(EntityPlayer player, EntityLivingBase victim) {
      if (!isMCAPresent() || player == null || victim == null) {
         return;
      }
      AxisAlignedBB bounds = victim.boundingBox.expand(16.0D, 8.0D, 16.0D);
      List nearby = victim.worldObj.getEntitiesWithinAABB(EntityHuman.class, bounds);
      for (Object obj : nearby) {
         if (obj instanceof EntityHuman && obj != victim) {
            EntityHuman witness = (EntityHuman)obj;
            if (witness.getEntitySenses().canSee(victim)) {
               modifyHearts(witness, player, -25);
               say(witness, player, "§c¡Por los cielos! ¡" + player.getCommandSenderName() + " es un vampiro! ¡Cazadores, vengan rápido!");
               player.getEntityData().setLong("WITCLastMCATerrorTick", player.worldObj.getTotalWorldTime());
               player.getEntityData().setInteger("WITCMCATerrorLevel", player.getEntityData().getInteger("WITCMCATerrorLevel") + 2);
            }
         }
      }
   }

   /**
    * Returns true if nearby MCA humans fear or hate this player, summoning Witch Hunters.
    */
   public static boolean isTerrorizingVillage(EntityPlayer player) {
      if (!isMCAPresent() || player == null) {
         return false;
      }
      long lastTerror = player.getEntityData().getLong("WITCLastMCATerrorTick");
      long worldTime = player.worldObj.getTotalWorldTime();
      if (lastTerror > 0 && (worldTime - lastTerror) < 24000L) { // Within the last in-game day
         return true;
      }

      AxisAlignedBB bounds = player.boundingBox.expand(64.0D, 16.0D, 64.0D);
      List nearby = player.worldObj.getEntitiesWithinAABB(EntityHuman.class, bounds);
      int negativeCount = 0;
      for (Object obj : nearby) {
         if (obj instanceof EntityHuman) {
            if (getHearts((EntityHuman)obj, player) < -10) {
               negativeCount++;
               if (negativeCount >= 2) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * Handles interaction with MCA humans: Supreme Witch recognition and blessing reactions.
    */
   public static void onInteractHuman(EntityPlayer player, Entity entity) {
      if (!isMCAPresent() || player == null || !isMCAHuman(entity)) {
         return;
      }
      EntityHuman human = (EntityHuman)entity;

      // 1. Supreme Witch Recognition
      if (com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player)) {
         if (player.worldObj.rand.nextInt(3) == 0 && getHearts(human, player) >= 0) {
            String[] supremeReactions = new String[]{
               "§5*se inclina con respeto* §dSaludos a la Bruja Suprema. El aquelarre vela por nuestro pueblo.",
               "§5*te mira con asombro* §dDicen que tu poder supera al de cualquier hechicero en este reino...",
               "§5¡Es la Bruja Suprema! §dPor favor, ten misericordia de nuestras cosechas y familias."
            };
            say(human, player, supremeReactions[player.worldObj.rand.nextInt(supremeReactions.length)]);
            modifyHearts(human, player, 3);
         }
      }

      // 2. Vampire / Werewolf atmospheric perception
      com.emoniph.witchery.common.ExtendedPlayer playerEx = com.emoniph.witchery.common.ExtendedPlayer.get(player);
      if (playerEx != null) {
         if (playerEx.getVampireLevel() >= 5 && player.worldObj.rand.nextInt(4) == 0 && !isSleeping(human)) {
            say(human, player, "§7*retrocede un paso* §8Tu piel es fría como el mármol... y tus ojos tienen un brillo inquietante...");
         } else if (playerEx.getWerewolfLevel() >= 5 && player.worldObj.rand.nextInt(4) == 0 && !isSleeping(human)) {
            say(human, player, "§7*olfatea el aire* §6Hueles a bosque salvaje y luna llena... ¿qué eres realmente?");
         }
      }
   }
}
