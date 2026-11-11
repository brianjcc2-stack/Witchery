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
      int chunkX = ((int)player.posX) >> 4;
      int chunkZ = ((int)player.posZ) >> 4;
      if (player.getEntityData().getBoolean("WITCVillageHero_" + chunkX + "_" + chunkZ)) {
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

      // 0. Mind-Controlled Village Trance check
      if (isVillageMindControlled(human)) {
         String[] tranceLines = new String[]{
            "§5*con mirada perdida* §dGloria a la Gran Ama del Caldero... sus guisos huelen tan bien... todos debemos entregarle nuestras papas...",
            "§5*ojos vidriosos* §dNo pienses... solo entrega tus tributos a Ella... Ella nos prometió pastel en la otra vida...",
            "§5*sonrisa congelada* §d¿Quién eres tú? ¿Vienes a interrumpir la hora de la sopa? La Señora dice que los forasteros son buen ingrediente...",
            "§5*murmura en trance* §dUno con el caldero... todos somos fideos en su caldo eterno...",
            "§5*baila en círculos* §dBurbujas en el agua, ranas en el tejado... la Ama es bondadosa, la Ama nos ama..."
         };
         say(human, player, tranceLines[player.worldObj.rand.nextInt(tranceLines.length)]);
         return;
      }

      // 1. Flying on Broom Reaction
      if (player.ridingEntity instanceof com.emoniph.witchery.entity.EntityBroom) {
         if (human.getProfessionEnum() == mca.enums.EnumProfession.Child) {
            say(human, player, "§b¡Mamá, mira, alguien olvidó cómo funciona la gravedad!");
         } else {
            say(human, player, "§e¡Bájate de esa escoba antes de que me rompas las tejas del tejado!");
         }
         return;
      }

      // 2. Creature Transformations (Werewolf, Bat, Toad)
      com.emoniph.witchery.common.ExtendedPlayer playerEx = com.emoniph.witchery.common.ExtendedPlayer.get(player);
      if (playerEx != null) {
         com.emoniph.witchery.util.TransformCreature creature = playerEx.getCreatureType();
         if (creature == com.emoniph.witchery.util.TransformCreature.WOLF || creature == com.emoniph.witchery.util.TransformCreature.WOLFMAN) {
            String[] wolfScreams = new String[]{
               "§c¡UN HOMBRE LOBO! ¡CIERREN LAS PUERTAS Y ESCONDAN LAS OVEJAS!",
               "§c¡Por los cielos, la bestia de la luna llena nos va a devorar! ¡Socorro!",
               "§c¡No te comas a mi perro, por favor!"
            };
            say(human, player, wolfScreams[player.worldObj.rand.nextInt(wolfScreams.length)]);
            human.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200, 1));
            return;
         } else if (creature == com.emoniph.witchery.util.TransformCreature.BAT) {
            String[] batLines = new String[]{
               "§7¡Qué murciélago tan gordo y raro! ¡Que alguien traiga una escoba para espantarlo!",
               "§7¡Fuera de aquí, bicho con alas! ¡Aléjate de mi huerto de frutas!"
            };
            say(human, player, batLines[player.worldObj.rand.nextInt(batLines.length)]);
            return;
         } else if (creature == com.emoniph.witchery.util.TransformCreature.TOAD) {
            say(human, player, "§a¿Croac? ...Qué sapo tan misterioso. Si te beso, ¿te conviertes en príncipe o en verruga?");
            return;
         }
      }

      // 3. Held Items (Poppets, Seeds)
      net.minecraft.item.ItemStack held = player.getHeldItem();
      if (held != null) {
         if (held.getItem() instanceof com.emoniph.witchery.item.ItemPoppet || held.getItem() instanceof com.emoniph.witchery.item.ItemTaglockKit) {
            if (player.worldObj.rand.nextInt(2) == 0) {
               String[] poppetLines = new String[]{
                  "§e¡Oye! ¿Por qué ese muñeco de paja tiene mi misma ropa? ¡No me estés pinchando con alfileres!",
                  "§e¡Aparta esa aguja de ese muñeco! ¡Sentí un escalofrío nada más verte!",
                  "§e¿Eso es arcilla con pelo humano? ¡Qué perturbador!"
               };
               say(human, player, poppetLines[player.worldObj.rand.nextInt(poppetLines.length)]);
               return;
            }
         }

         if (held.getItem() == Witchery.Items.SEEDS_BELLADONNA || held.getItem() == Witchery.Items.SEEDS_MANDRAKE || held.getItem() == Witchery.Items.SEEDS_WOLFSBANE || held.getItem() == Witchery.Items.SEEDS_ARTICHOKE || held.getItem() == Witchery.Items.SEEDS_SNOWBELL) {
            mca.enums.EnumProfession prof = human.getProfessionEnum();
            mca.enums.EnumPersonality pers = human.getPersonality();
            if (prof == mca.enums.EnumProfession.Librarian || pers == mca.enums.EnumPersonality.CURIOUS || pers == mca.enums.EnumPersonality.ODD) {
               if (!player.capabilities.isCreativeMode) {
                  --held.stackSize;
                  if (held.stackSize <= 0) {
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                  }
               }
               modifyHearts(human, player, 8);
               say(human, player, "§d¡Hierbas raras del aquelarre! Las estudiaré para mis experimentos secretos... ¡prometo no hacer explotar el granero!");
               human.worldObj.spawnParticle("happyVillager", human.posX, human.posY + 1.2D, human.posZ, 0.0D, 0.1D, 0.0D);
               return;
            } else if (prof == mca.enums.EnumProfession.Farmer) {
               if (!player.capabilities.isCreativeMode) {
                  --held.stackSize;
                  if (held.stackSize <= 0) {
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                  }
               }
               modifyHearts(human, player, 5);
               say(human, player, "§2¡Vaya semilla más extraña! La plantaré en mi mejor parcela, ¡muchas gracias!");
               human.worldObj.spawnParticle("happyVillager", human.posX, human.posY + 1.2D, human.posZ, 0.0D, 0.1D, 0.0D);
               return;
            }
         }
      }

      // 4. Supreme Witch Recognition
      if (com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player)) {
         if (player.worldObj.rand.nextInt(3) == 0 && getHearts(human, player) >= 0) {
            String[] supremeReactions = new String[]{
               "§5*se inclina con respeto* §dSaludos a la Bruja Suprema. El aquelarre vela por nuestro pueblo.",
               "§5*te mira con asombro* §dDicen que tu poder supera al de cualquier hechicero en este reino...",
               "§5¡Es la Bruja Suprema! §dPor favor, ten misericordia de nuestras cosechas y familias."
            };
            say(human, player, supremeReactions[player.worldObj.rand.nextInt(supremeReactions.length)]);
            modifyHearts(human, player, 3);
            return;
         }
      }

      // 5. Vampire / Werewolf atmospheric perception
      if (playerEx != null) {
         if (playerEx.getVampireLevel() >= 5 && player.worldObj.rand.nextInt(4) == 0 && !isSleeping(human)) {
            say(human, player, "§7*retrocede un paso* §8Tu piel es fría como el mármol... y tus ojos tienen un brillo inquietante...");
            return;
         } else if (playerEx.getWerewolfLevel() >= 5 && player.worldObj.rand.nextInt(4) == 0 && !isSleeping(human)) {
            say(human, player, "§7*olfatea el aire* §6Hueles a bosque salvaje y luna llena... ¿qué eres realmente?");
            return;
         }
      }

      // 6. Personality & Profession Flavor Dialogues
      mca.enums.EnumProfession prof = human.getProfessionEnum();
      mca.enums.EnumPersonality pers = human.getPersonality();

      if (prof == mca.enums.EnumProfession.Cleric) {
         int chunkX = ((int)human.posX) >> 4;
         int chunkZ = ((int)human.posZ) >> 4;
         boolean isHero = player.getEntityData().getBoolean("WITCVillageHero_" + chunkX + "_" + chunkZ);
         if (isHero) {
            if (player.worldObj.rand.nextInt(3) == 0) {
               say(human, player, "§a¡Que la luz bendiga tus pasos, protector de nuestro pueblo! Gracias a ti la magia ya no nos asusta tanto.");
            }
         } else {
            int hearts = getHearts(human, player);
            if (hearts < 0) {
               if (hearts < -10 && player.worldObj.rand.nextInt(3) == 0) {
                  say(human, player, "§c¡Por las sagradas escrituras! ¡Cazador de Brujas, auxilio, hay hechicería impía en la plaza!");
                  summonWitchHunterNear(human, player);
               } else {
                  say(human, player, "§c¡Atrás, practicante de las artes oscuras! ¡Te lanzaré sal bendita y ristras de ajo si te acercas!");
               }
            } else if (player.worldObj.rand.nextInt(4) == 0) {
               say(human, player, "§6Rezo todos los días para que las brujas del bosque no se roben las campanas de mi iglesia...");
            }
         }
      } else if (prof == mca.enums.EnumProfession.Librarian || pers == mca.enums.EnumPersonality.CURIOUS || pers == mca.enums.EnumPersonality.ODD) {
         if (player.worldObj.rand.nextInt(3) == 0) {
            String[] bookLines = new String[]{
               "§b¡Psst! Oye... ¿es cierto que puedes convertir a mi suegra en un sapo? ¡Tengo esmeraldas si me enseñas el truco!",
               "§b¿Ese es un libro de sombras? Déjame echarle un vistazo... ¡prometo no pronunciar nada en latín al revés!",
               "§bDicen que si dejas una ofrenda en una encrucijada a medianoche, una bruja te concede un deseo. ¿Es verdad o solo cuento de taberna?",
               "§b¿Crees en la nigromancia? A mí me vendría genial un esqueleto asistente que ordene los estantes superiores..."
            };
            say(human, player, bookLines[player.worldObj.rand.nextInt(bookLines.length)]);
         }
      } else if (prof == mca.enums.EnumProfession.Farmer) {
         if (player.worldObj.rand.nextInt(3) == 0) {
            String[] farmLines = new String[]{
               "§2Don hechicero, mis lechugas tienen plaga... ¿no tiene un encantamiento que no involucre invocar un demonio en mi huerto?",
               "§2Por favor no hagas llover sapos este martes, ¡acabo de terminar de barrer el tejado!",
               "§2Dicen que las brujas hacen crecer las calabazas al triple de tamaño... si me ayuda con mi cosecha, le regalo media tarta."
            };
            say(human, player, farmLines[player.worldObj.rand.nextInt(farmLines.length)]);
         }
      } else if (pers == mca.enums.EnumPersonality.FLIRTY) {
         if (player.worldObj.rand.nextInt(3) == 0) {
            String[] flirtyLines = new String[]{
               "§d¿Me estás lanzando una poción de amor o es que eres naturalmente hechizante?",
               "§dDicen que las brujas roban corazones... pero el mío te lo llevaste sin necesidad de calderos ni rituales.",
               "§dCon esa mirada tuya, no necesitas varita para embrujar a nadie..."
            };
            say(human, player, flirtyLines[player.worldObj.rand.nextInt(flirtyLines.length)]);
         }
      } else if (prof == mca.enums.EnumProfession.Guard || prof == mca.enums.EnumProfession.Warrior) {
         if (player.worldObj.rand.nextInt(3) == 0) {
            say(human, player, "§6Tengo mi espada afilada y lista. No hagas trucos raros con calderos en la plaza del pueblo y nos llevaremos en paz.");
         }
      }
   }

   public static boolean isVillageMindControlled(EntityHuman human) {
      if (human == null) {
         return false;
      }
      if (human.getEntityData().getBoolean("WITCMindControlled")) {
         return true;
      }
      AxisAlignedBB searchBox = human.boundingBox.expand(48.0D, 24.0D, 48.0D);
      List witches = human.worldObj.getEntitiesWithinAABB(com.emoniph.witchery.entity.EntityCovenWitch.class, searchBox);
      for (Object obj : witches) {
         if (obj instanceof com.emoniph.witchery.entity.EntityCovenWitch) {
            com.emoniph.witchery.entity.EntityCovenWitch w = (com.emoniph.witchery.entity.EntityCovenWitch)obj;
            if (w.isEntityAlive() && w.getEntityData().getBoolean("WITCPuppeteerWitch")) {
               human.getEntityData().setBoolean("WITCMindControlled", true);
               return true;
            }
         }
      }
      return false;
   }

   public static void summonWitchHunterNear(EntityHuman human, EntityPlayer player) {
      if (human == null || player == null || human.worldObj.isRemote) {
         return;
      }
      long lastSummon = human.getEntityData().getLong("WITCLastHunterCall");
      long time = human.worldObj.getTotalWorldTime();
      if (time - lastSummon < 2400L) {
         return;
      }
      human.getEntityData().setLong("WITCLastHunterCall", time);

      double angle = human.worldObj.rand.nextDouble() * Math.PI * 2.0;
      double dist = 16.0 + human.worldObj.rand.nextDouble() * 10.0;
      double spawnX = human.posX + Math.cos(angle) * dist;
      double spawnZ = human.posZ + Math.sin(angle) * dist;
      int spawnY = human.worldObj.getTopSolidOrLiquidBlock((int)spawnX, (int)spawnZ);

      com.emoniph.witchery.entity.EntityWitchHunter hunter = new com.emoniph.witchery.entity.EntityWitchHunter(human.worldObj);
      hunter.setLocationAndAngles(spawnX, (double)spawnY, spawnZ, human.worldObj.rand.nextFloat() * 360.0F, 0.0F);
      hunter.onSpawnWithEgg(null);
      hunter.setAttackTarget(player);
      human.worldObj.spawnEntityInWorld(hunter);
   }

   public static void triggerPuppeteerBattle(EntityLivingBase witch, EntityPlayer player) {
      if (witch == null || player == null || witch.worldObj.isRemote) {
         return;
      }
      if (witch instanceof com.emoniph.witchery.entity.EntityCovenWitch) {
         com.emoniph.witchery.entity.EntityCovenWitch covenWitch = (com.emoniph.witchery.entity.EntityCovenWitch)witch;
         covenWitch.setRevengeTarget(player);
         covenWitch.setAttackTarget(player);
         covenWitch.setTarget(player);
         covenWitch.setAggressive(true);
      }

      if (!witch.getEntityData().getBoolean("WITCBattleStarted")) {
         witch.getEntityData().setBoolean("WITCBattleStarted", true);
         com.emoniph.witchery.util.ChatUtil.sendPlain(EnumChatFormatting.DARK_PURPLE, player, "<§5" + witch.getCommandSenderName() + "§r> §c¡¿Un intruso en mi aldea?! ¡Mis títeres me pertenecen, nadie arruina mi nuevo aquelarre doméstico!");

         for (int i = 0; i < 2; i++) {
            com.emoniph.witchery.entity.EntityIllusionSpider spider = new com.emoniph.witchery.entity.EntityIllusionSpider(witch.worldObj);
            spider.setLocationAndAngles(witch.posX + (witch.worldObj.rand.nextDouble() - 0.5D) * 3.0D, witch.posY, witch.posZ + (witch.worldObj.rand.nextDouble() - 0.5D) * 3.0D, 0.0F, 0.0F);
            spider.setAttackTarget(player);
            witch.worldObj.spawnEntityInWorld(spider);
         }

         com.emoniph.witchery.entity.EntityToad toad = new com.emoniph.witchery.entity.EntityToad(witch.worldObj);
         toad.setLocationAndAngles(witch.posX + (witch.worldObj.rand.nextDouble() - 0.5D) * 2.0D, witch.posY, witch.posZ + (witch.worldObj.rand.nextDouble() - 0.5D) * 2.0D, 0.0F, 0.0F);
         witch.worldObj.spawnEntityInWorld(toad);

         witch.worldObj.playSoundAtEntity(witch, "mob.witch.throw", 1.0F, 0.8F);
      }
   }

   public static void onPuppeteerWitchDefeated(EntityLivingBase witch, EntityPlayer killer) {
      if (witch == null || witch.worldObj.isRemote) {
         return;
      }

      AxisAlignedBB bounds = witch.boundingBox.expand(64.0D, 32.0D, 64.0D);
      List nearby = witch.worldObj.getEntitiesWithinAABB(EntityHuman.class, bounds);

      int chunkX = ((int)witch.posX) >> 4;
      int chunkZ = ((int)witch.posZ) >> 4;

      if (killer != null) {
         killer.getEntityData().setBoolean("WITCVillageHero_" + chunkX + "_" + chunkZ, true);
         com.emoniph.witchery.util.ChatUtil.sendPlain(EnumChatFormatting.GOLD, killer, "§6§l[Aldea Liberada] §e¡Has roto el embrujo de la Bruja Manipuladora! Los aldeanos vuelven en sí.");
         com.emoniph.witchery.util.SoundEffect.RANDOM_LEVELUP.playOnlyTo(killer);
      }

      String[] wakeLines = new String[]{
         "§a¡Puaj! ¿Por qué tengo la boca llena de tierra de cementerio? ¡Esa anciana me dijo que era canela!",
         "§a¿Qué rayos pasó? ¿Por qué estaba bailando descalzo en el pozo? ¡Dime que nadie me vio...!",
         "§a¡Por todos los santos! ¡Nos salvaste de esa lunática! ¡Toma mi agradecimiento y bendiciones!",
         "§a¡Mi cabeza! Sentía como si un sapo me estuviera cantando al oído durante tres días seguidos...",
         "§a¡Somos libres! ¡El caldero ya no nos susurra órdenes!"
      };

      for (Object obj : nearby) {
         if (obj instanceof EntityHuman) {
            EntityHuman human = (EntityHuman)obj;
            human.getEntityData().removeTag("WITCMindControlled");
            if (killer != null) {
               modifyHearts(human, killer, 60);
               say(human, killer, wakeLines[human.worldObj.rand.nextInt(wakeLines.length)]);
            }
            human.worldObj.spawnParticle("happyVillager", human.posX, human.posY + 1.2D, human.posZ, 0.0D, 0.2D, 0.0D);
         }
      }

      if (killer != null) {
         witch.dropItem(net.minecraft.init.Items.emerald, 4 + witch.worldObj.rand.nextInt(5));
         witch.dropItem(net.minecraft.init.Items.cake, 1);
      }
   }
}
