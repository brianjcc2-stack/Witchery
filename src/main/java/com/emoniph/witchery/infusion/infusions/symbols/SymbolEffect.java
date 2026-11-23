package com.emoniph.witchery.infusion.infusions.symbols;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.util.Const;
import com.emoniph.witchery.util.TimeUtil;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class SymbolEffect {

   private final int effectID;
   private final String unlocalisedName;
   private final int chargeCost;
   private final boolean curse;
   private final boolean fallsToEarth;
   private final String knowledgeKey;
   private final boolean isVisible;
   private byte[] defaultStrokes;
   private final int cooldownTicks;
   private int requiredInfusionID = 0;


   public SymbolEffect(int effectID, String unlocalisedName) {
      this(effectID, unlocalisedName, 1, false, false, (String)null, 0, true);
   }

   public SymbolEffect(int effectID, String unlocalisedName, int spellCost, boolean curse, boolean fallsToEarth, String knowledgeKey, int cooldown) {
      this(effectID, unlocalisedName, spellCost, curse, fallsToEarth, knowledgeKey, cooldown, true);
   }

   public SymbolEffect(int effectID, String unlocalisedName, int spellCost, boolean curse, boolean fallsToEarth, String knowledgeKey, int cooldown, boolean isVisible) {
      this.effectID = effectID;
      this.unlocalisedName = unlocalisedName;
      this.chargeCost = spellCost;
      this.curse = curse;
      this.fallsToEarth = fallsToEarth;
      this.knowledgeKey = knowledgeKey;
      this.cooldownTicks = cooldown;
      this.isVisible = isVisible;
   }

   public int getEffectID() {
      return this.effectID;
   }

   public int getDisplayColor() {
      if(this instanceof SymbolEffectProjectile) {
         return ((SymbolEffectProjectile)this).getColor();
      }

      return this.curse?13382297:11645183;
   }

   public boolean isCurse() {
      return this.curse;
   }

   public boolean isUnforgivable() {
      return this.curse && this.knowledgeKey == null;
   }

   public String getUnlocalisedName() {
      return this.unlocalisedName;
   }

   public String getLocalizedName() {
      return Witchery.resource(this.unlocalisedName);
   }

   public String getSpanishName() {
      switch(this.effectID) {
         case 14: return "Lumos Maxima";
         case 18: return "Wingardium Leviosa";
         case 29: return "Piertotum Locomotor";
         case 32: return "Arania Exumai";
         case 34: return "Protego Maxima";
         case 43: return "Caelum (Urna: Arriba)";
         case 44: return "Baratrum (Urna: Abajo)";
         case 45: return "Ortus (Urna: Este)";
         case 46: return "Occasus (Urna: Oeste)";
         case 48: return "Flagrate Ritualis";
         case 49: return "Flagrate Aureus";
         case 50: return "Flagrate Alibi";
         case 51: return "Revelio Potionis";
         case 53: return "Expecto Patronum";
         case 72: return "Maleficio de Mechón (Taglock Hex)";
         case 73: return "Rayo de Fundición (Smelt Ray)";
         case 74: return "Pilar de Tierra (Earth Pillar)";
         case 75: return "Transmutación";
         case 76: return "Excavación";
         case 77: return "Invocar Escoba";
         case 78: return "Salto de Sapo (Toad Leap)";
         case 79: return "Bóveda Etérea (Ethereal Vault)";
         case 99: return "Bombarda Maxima";
         default: return this.getLocalizedName();
      }
   }

   public String getSpanishInfo() {
      switch(this.effectID) {
         case 1: return "Atrae objetos caídos y entidades cercanas hacia ti.";
         case 2: return "Conjura agua en el impacto o llena calderos y hervidores.";
         case 3: return "Abre puertas cerradas (incluyendo puertas de serbal y aliso).";
         case 4: return "§4Prohibido§r: Maleficio asesino de muerte instantánea.";
         case 5: return "Fortalece bloques (tierra/madera a piedra, puertas de madera a hierro).";
         case 6: return "Sella una puerta transformándola en una puerta de serbal cerrada.";
         case 7: return "Borra la memoria del objetivo (las criaturas pierden el agro; confunde jugadores).";
         case 8: return "Desorienta y causa confusión severa al objetivo.";
         case 9: return "§4Prohibido§r: Tortura al objetivo con agonía progresiva (/crucio 1-10).";
         case 10: return "Excava y rompe bloques de tierra, piedra, arena o arcilla a distancia.";
         case 11: return "Provoca una explosión ígnea al impactar.";
         case 12: return "Reanima y contrarresta los efectos de aturdimiento (Stupefy).";
         case 13: return "Curación menor instantánea (consume hambre para restaurar salud).";
         case 14: return "Ilumina una gran área conjurando esferas de luz flotantes.";
         case 15: return "Desarma al objetivo haciendo caer el objeto que sostiene.";
         case 16: return "Dibuja una runa infernal con la Rama Mística.";
         case 17: return "Empuja violentamente al objetivo hacia atrás (en nivel II/III empuja en área).";
         case 18: return "Levita y mueve al objetivo por el aire (agáchate al lanzarlo para impulsarte).";
         case 19: return "Ralentiza severamente el movimiento del objetivo.";
         case 20: return "§4Prohibido§r: Somete la mente del objetivo bajo tu control (/imperio).";
         case 21: return "Prende fuego al objetivo o al terreno en el punto de impacto.";
         case 22: return "Crea una fuente de luz mágica (agáchate para que la luz te siga).";
         case 23: return "Despeja el clima, deteniendo la lluvia y las tormentas.";
         case 24: return "Provoca una explosión moderada en el punto de impacto.";
         case 25: return "Invoca tres búhos mágicos que atacan a tu objetivo.";
         case 26: return "Extingue todas las fuentes de luz cercanas y disipa Lumos.";
         case 27: return "Invoca tres lobos feroces que atacan a tu objetivo.";
         case 28: return "Golpea al objetivo causándole daño y drenando parte de su energía de infusión.";
         case 29: return "Anima un Gólem de Hierro leal para que luche por ti.";
         case 30: return "Desintegra bloques y objetos sueltos en el área de impacto.";
         case 31: return "Conjura un escudo protector frente a ti (apuntando al suelo).";
         case 32: return "Repele arañas violentamente y les inflige daño letal.";
         case 33: return "Silencia al objetivo impidiéndole canalizar hechizos.";
         case 34: return "Crea una cúpula esférica de fuerza protectora a tu alrededor.";
         case 35: return "Petrifica al objetivo, dejándolo inmóvil y ciego temporalmente.";
         case 36: return "Aturde y paraliza temporalmente al objetivo.";
         case 37: return "Congela al objetivo, convierte agua en hielo y apaga el fuego cercano.";
         case 39: return "Fuego del alma: inflige dolor equivalente al daño que tú has sufrido.";
         case 40: return "Banquete de carne: restaura tu hambre a costa de tu propia sangre.";
         case 41: return "Conjura la Marca Oscura en el cielo, aterrorizando a las criaturas.";
         case 42: return "Envía a la víctima a la dimensión del Tormento Eterno.";
         case 43: return "Libera la poción concentrada en la Urna de Leonard hacia arriba (cielo).";
         case 44: return "Libera la poción concentrada en la Urna de Leonard hacia abajo (profundidades).";
         case 45: return "Libera la poción concentrada en la Urna de Leonard hacia el este.";
         case 46: return "Libera la poción concentrada en la Urna de Leonard hacia el oeste.";
         case 48: return "Dibuja un glifo de Tiza Ritual en el bloque al que miras.";
         case 49: return "Dibuja un glifo central de Tiza Dorada en el bloque al que miras.";
         case 50: return "Dibuja un glifo de Tiza del Más Allá en el bloque al que miras.";
         case 51: return "Proyectil que revela entidades invisibles y desencanta bloques malditos.";
         case 52: return "Repara los objetos y herramientas dañados de tu inventario.";
         case 53: return "Ahuyenta y daña a los no-muertos y creepers cercanos con un destello de luz.";
         case 54: return "Maldición cortante profunda que causa gran daño, sangrado y Wither.";
         case 55: return "Teletransporte instantáneo de corto alcance al punto donde impacta.";
         case 56: return "Desata Fuego Demoníaco maldito e invoca Blazes marchitos.";
         case 57: return "Onda expansiva que lanza por los aires a las entidades cercanas.";
         case 58: return "Potencia al objetivo o a ti mismo con Aumento de Salud y Fuerza.";
         case 59: return "Agáchate para purgar tus efectos negativos, o lánzalo para disipar magia.";
         case 60: return "Hace crecer instantáneamente los cultivos y plantas cercanas.";
         case 62: return "Otorga resistencia al fuego, respiración acuática y resistencia al daño.";
         case 63: return "Conjura un refugio temporal de bloques de fuerza (agáchate para disiparlo).";
         case 64: return "Marca un punto de retorno personal; lánzalo de nuevo para volver a él.";
         case 65: return "Crea una Piedra Guía Traslador vinculada a tu posición actual.";
         case 72: return "Extrae a distancia una muestra de sangre (Taglock) del objetivo.";
         case 73: return "Funde o cocina instantáneamente el bloque o ítem caído al que impacta.";
         case 74: return "Eleva una columna de tierra bajo el bloque objetivo (te eleva si estás encima).";
         case 75: return "Transmuta bloques en materiales afines (tierra/arena, piedra/adoquín, troncos/tablones).";
         case 76: return "Excava instantáneamente un cubo de 3x3x3 bloques soltándolos como ítems.";
         case 77: return "Invoca tu escoba encantada desde tu inventario, cofre de ender o mundo.";
         case 78: return "Te impulsa a gran altura en el aire y anula el daño por caída.";
         case 79: return "Abre tu Cofre de Ender portátilmente desde cualquier lugar.";
         case 80: return "Proyectil básico de fuerza arcana; encadena impactos para aumentar el daño.";
         case 81: return "Hace brotar hierba y flores silvestres sobre el terreno objetivo.";
         case 82: return "Crea una densa cortina de humo (agáchate para soltarla a tus pies).";
         case 83: return "Atrapa e inmoviliza al objetivo envolviéndolo en telarañas.";
         case 84: return "Dispara una bengala roja brillante al cielo como señal.";
         case 85: return "Otorga visión nocturna, destruye ilusiones y revela invisibles cercanos.";
         case 86: return "Intercambia instantáneamente tu posición con la de la entidad objetivo.";
         case 99: return "Provoca una explosión masiva que destruye el terreno.";
         case 101: return "Un corte lacerante que hiere y desangra al objetivo.";
         case 102: return "Estrella violentamente al objetivo contra el suelo inmovilizándolo.";
         case 103: return "Duplica objetos menores en el suelo (como Piedras Guía o Kits de Mechón).";
         default: {
            String descKey = this.unlocalisedName + ".info";
            String desc = Witchery.resource(descKey);
            return (desc != null && !desc.equals(descKey)) ? desc : "";
         }
      }
   }

   public abstract void perform(World var1, EntityPlayer var2, int var3);

   public int getChargeCost(World world, EntityPlayer player, int level) {
      double multiplier = 1.0D + Math.max(0, level - 1) * 0.5D;
      int baseCost = MathHelper.floor_double(multiplier * (double)this.chargeCost);
      if(player != null) {
         if(com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player)) {
            baseCost = Math.max(1, baseCost / 2);
         }
         if(this.isCurse() && com.emoniph.witchery.familiar.Familiar.hasActiveCurseMasteryFamiliar(player)) {
            baseCost = Math.max(1, (int)Math.floor(baseCost * 0.75D));
         }
         if(this.isWindOrMovementSpell() && com.emoniph.witchery.familiar.Familiar.hasActiveBroomMasteryFamiliar(player)) {
            baseCost = Math.max(1, (int)Math.floor(baseCost * 0.75D));
         }
      }
      return baseCost;
   }

   public boolean isWindOrMovementSpell() {
      return this.effectID == 1 || this.effectID == 15 || this.effectID == 44
          || this.effectID == 55 || this.effectID == 56 || this.effectID == 58
          || this.effectID == 60 || this.effectID == 86 || this.effectID == 94;
   }

   public int getBaseChargeCost() {
      return this.chargeCost;
   }

   public int getCooldownTicks() {
      return this.cooldownTicks;
   }

   public byte[] getDefaultStrokes() {
      return this.defaultStrokes;
   }

   public static String getStrokeArrow(byte stroke) {
      switch(stroke) {
         case 0: return "↑";
         case 1: return "↓";
         case 2: return "→";
         case 3: return "←";
         case 4: return "↗";
         case 5: return "↙";
         case 6: return "↖";
         case 7: return "↘";
         default: return "?";
      }
   }

   public static String getStrokeShortEs(byte stroke) {
      switch(stroke) {
         case 0: return "Arr";
         case 1: return "Abj";
         case 2: return "Der";
         case 3: return "Izq";
         case 4: return "Arr-Der";
         case 5: return "Abj-Izq";
         case 6: return "Arr-Izq";
         case 7: return "Abj-Der";
         default: return "?";
      }
   }

   public static String getStrokeFullEs(byte stroke) {
      switch(stroke) {
         case 0: return "Arriba";
         case 1: return "Abajo";
         case 2: return "Derecha";
         case 3: return "Izquierda";
         case 4: return "Arriba-Derecha";
         case 5: return "Abajo-Izquierda";
         case 6: return "Arriba-Izquierda";
         case 7: return "Abajo-Derecha";
         default: return "?";
      }
   }

   public static String formatStrokesCompact(byte[] strokes) {
      if(strokes == null || strokes.length == 0) return "?";
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < strokes.length; i++) {
         if(i > 0) sb.append(" ");
         byte stroke = strokes[i];
         sb.append(i + 1).append(".").append(getStrokeArrow(stroke));
      }
      return sb.toString();
   }

   public static String formatStrokesStepByStep(byte[] strokes) {
      if(strokes == null || strokes.length == 0) return "?";
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < strokes.length; i++) {
         if(i > 0) sb.append(" ➔ ");
         byte stroke = strokes[i];
         sb.append(i + 1).append(".").append(getStrokeArrow(stroke));
      }
      return sb.toString();
   }

   public static String formatStrokesArrowsOnly(byte[] strokes) {
      if(strokes == null || strokes.length == 0) return "?";
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < strokes.length; i++) {
         if(i > 0) sb.append(" ");
         sb.append(getStrokeArrow(strokes[i]));
      }
      return sb.toString();
   }

   public static boolean isFlagrateFamily(int id) {
      return id == 16 || id == 48 || id == 49 || id == 50;
   }

   private static int getSortGroupLength(SymbolEffect eff) {
      if(isFlagrateFamily(eff.getEffectID())) {
         return 4; // Une todos los Flagrate (16, 48, 49, 50) juntos en el grupo de 4 trazos
      }
      return eff.getDefaultStrokes() != null ? eff.getDefaultStrokes().length : 0;
   }

   private static int getSortOrderId(SymbolEffect eff) {
      int id = eff.getEffectID();
      if(id == 16) return 47; // Coloca Flagrate justo al inicio de Flagrate Ritualis (48), Aureus (49) y Alibi (50)
      return id;
   }

   public static void sortByComplexity(java.util.List<SymbolEffect> list) {
      java.util.Collections.sort(list, new java.util.Comparator<SymbolEffect>() {
         @Override
         public int compare(SymbolEffect a, SymbolEffect b) {
            int tierA = a.getSpellTier();
            int tierB = b.getSpellTier();
            if(tierA != tierB) return tierA - tierB;

            boolean supA = a.requiresInfernalInfusion() || a.isCurse();
            boolean supB = b.requiresInfernalInfusion() || b.isCurse();
            if(supA != supB) return supA ? 1 : -1;

            int lenA = getSortGroupLength(a);
            int lenB = getSortGroupLength(b);
            if(lenA != lenB) return lenA - lenB;

            boolean flagA = isFlagrateFamily(a.getEffectID());
            boolean flagB = isFlagrateFamily(b.getEffectID());
            if(flagA && flagB) {
               return getSortOrderId(a) - getSortOrderId(b);
            }

            int costA = a.getBaseChargeCost();
            int costB = b.getBaseChargeCost();
            if(costA != costB) return costA - costB;

            return getSortOrderId(a) - getSortOrderId(b);
         }
      });
   }

   public static String getUnifiedFlagrateBookPage(EntityPlayer player) {
      StringBuilder sb = new StringBuilder();
      sb.append("§nFlagrate (Glifos de Tiza)§r\n");
      boolean m16 = isSpellMastered(player, EffectRegistry.Flagrate);
      boolean m48 = isSpellMastered(player, EffectRegistry.FlagrateRitualis);
      boolean m49 = isSpellMastered(player, EffectRegistry.FlagrateAureus);
      boolean m50 = isSpellMastered(player, EffectRegistry.FlagrateAlibi);
      int domCount = (m16 ? 1 : 0) + (m48 ? 1 : 0) + (m49 ? 1 : 0) + (m50 ? 1 : 0);
      if(domCount == 4) {
         sb.append("§2[✔ 4/4 Dominados]§0 §6[Tier 1]§0\n");
      } else {
         sb.append("§8[✦ ").append(domCount).append("/4 Dom.]§0 §6[Tier 1]§0\n");
      }
      sb.append("Dibuja glifos de tiza en el suelo mirando al bloque:\n");
      sb.append("§7• §0Ritual (Blanca):\n  §l§1").append(formatStrokesCompact(EffectRegistry.FlagrateRitualis.getDefaultStrokes())).append("§r\n");
      sb.append("§6• §0Aureus (Dorada):\n  §l§1").append(formatStrokesCompact(EffectRegistry.FlagrateAureus.getDefaultStrokes())).append("§r\n");
      sb.append("§5• §0Alibi (Más Allá):\n  §l§1").append(formatStrokesCompact(EffectRegistry.FlagrateAlibi.getDefaultStrokes())).append("§r\n");
      sb.append("§4• §0Infernal (Roja):\n  §l§1").append(formatStrokesCompact(EffectRegistry.Flagrate.getDefaultStrokes())).append("§r");
      return sb.toString();
   }

   public boolean fallsToEarth() {
      return this.fallsToEarth;
   }

   public SymbolEffect setRequiredInfusion(int infusionID) {
      this.requiredInfusionID = infusionID;
      return this;
   }

   public boolean requiresInfernalInfusion() {
      return this.requiredInfusionID == 2 || this.isCurse() || this.getSpellTier() == 4;
   }

   public int getRequiredInfusionID() {
      if(this.requiresInfernalInfusion()) {
         return 2;
      }
      return 0;
   }

   public boolean hasValidInfusion(EntityPlayer player, int infusionID) {
      if(player.capabilities.isCreativeMode || (player != null && com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player))) {
         return true;
      }
      if(infusionID <= 0) {
         return false;
      }
      if(this.requiresInfernalInfusion()) {
         return infusionID == 2;
      }
      return true;
   }

      public int getSpellTier() {
      int id = this.getEffectID();
      // Tier 4: Artes Oscuras y Maleficios Prohibidos
      if(this.isCurse() || id == 4 || id == 9 || id == 20 || id == 39 || id == 40 || id == 41 || id == 42 || id == 43 || id == 44 || id == 45 || id == 46 || id == 56 || id == 99) {
         return 4;
      }
      // Tier 3: Magister (Alta Magia, Destrucción, Invocación y Espacio)
      if(id == 5 || id == 7 || id == 11 || id == 23 || id == 24 || id == 27 || id == 29 || id == 30 || id == 34 || id == 53 || id == 54 || id == 55 || id == 57 || id == 58 || id == 59 || id == 64 || id == 65 || id == 74 || id == 75 || id == 76 || id == 77 || id == 79 || id == 103) {
         return 3;
      }
      // Tier 2: Adepto (Duelo táctico, movilidad, control y defensa)
      if(id == 8 || id == 10 || id == 14 || id == 15 || id == 19 || id == 25 || id == 28 || id == 31 || id == 32 || id == 33 || id == 35 || id == 36 || id == 37 || id == 60 || id == 62 || id == 63 || id == 72 || id == 73 || id == 78 || id == 83 || id == 85 || id == 86 || id == 101) {
         return 2;
      }
      // Tier 1: Iniciado (Hechizos básicos, cotidianos y trucos iniciales)
      return 1;
   }

   public int getRequiredMasteryCount() {
      int tier = this.getSpellTier();
      if(tier == 1) return 0;
      if(tier == 2) return 4;
      if(tier == 3) return 10;
      return 16;
   }

   public static int getMasteredSpellCount(EntityPlayer player) {
      if(player == null) return 0;
      NBTTagCompound nbt = Infusion.getNBT(player);
      if(nbt == null || !nbt.hasKey("WITCSpellBook")) return 0;
      NBTTagCompound nbtSpells = nbt.getCompoundTag("WITCSpellBook");
      int count = 0;
      Iterator it = EffectRegistry.instance().getEffects().iterator();
      while(it.hasNext()) {
         SymbolEffect eff = (SymbolEffect)it.next();
         if(eff != null) {
            boolean mastered = nbtSpells.getBoolean("Mastered_" + eff.getEffectID()) || (eff.knowledgeKey != null && nbtSpells.getBoolean(eff.knowledgeKey));
            if(mastered) {
               count++;
            }
         }
      }
      return count;
   }

   public boolean isVisible(EntityPlayer player) {
      if(player == null || player.capabilities.isCreativeMode || isPlayerSupremeOrSynced(player)) {
         return true;
      }
      if(!this.isVisible) {
         if(this.knowledgeKey != null) {
            NBTTagCompound nbt = Infusion.getNBT(player);
            if(nbt != null && nbt.hasKey("WITCSpellBook")) {
               return nbt.getCompoundTag("WITCSpellBook").getBoolean(this.knowledgeKey);
            }
         }
         return false;
      }
      return true;
   }

   private static boolean isPlayerSupremeOrSynced(EntityPlayer player) {
      if(player == null) return false;
      if(com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player)) return true;
      NBTTagCompound nbtPlayer = Infusion.getNBT(player);
      if(nbtPlayer != null && nbtPlayer.hasKey("WITCSpellBook")) {
         return nbtPlayer.getCompoundTag("WITCSpellBook").getBoolean("_IsSupreme");
      }
      return false;
   }

   public String getDescription() {
      return this.getDescription((EntityPlayer)null);
   }

   public String getDescription(EntityPlayer player) {
      StringBuffer sb = new StringBuffer();
      sb.append("§n");
      sb.append(this.getSpanishName());
      sb.append("§r\n");
      if(player != null) {
         if(isSpellMastered(player, this)) {
            sb.append("§2[✔ Dominado]§0 ");
         } else {
            sb.append("§8[✦ Por dominar]§0 ");
         }
      }
      int tier = this.getSpellTier();
      if(tier == 1) {
         sb.append("§6[Tier 1]§0\n");
      } else if(tier == 2) {
         sb.append("§b[Tier 2: 4 Dom.]§0\n");
      } else if(tier == 3) {
         sb.append("§d[Tier 3: 10 Dom.]§0\n");
      } else {
         sb.append("§4[Tier 4: 16 Dom.]§0\n");
      }

      String description = this.getSpanishInfo();
      if(description != null && !description.isEmpty()) {
         sb.append(description);
         sb.append("\n");
      }

      if(this.requiresInfernalInfusion()) {
         sb.append("§4");
         sb.append(Witchery.resource("witchery.infuse.branch.reqinfernal"));
         sb.append("§0\n");
      }

      sb.append("§8");
      sb.append(Witchery.resource("witchery.book.wands.strokes"));
      sb.append(": §l§1");
      sb.append(formatStrokesCompact(this.defaultStrokes));
      sb.append("§r");

      return sb.toString();
   }

   public void setDefaultStrokes(byte[] strokes) {
      this.defaultStrokes = strokes;
   }

   public boolean hasValidKnowledge(EntityPlayer player, NBTTagCompound nbtPlayer) {
      if(player.capabilities.isCreativeMode || isPlayerSupremeOrSynced(player)) {
         return true;
      }
      int tier = this.getSpellTier();
      int mastered = getMasteredSpellCount(player);
      if(tier == 2 && mastered < 4) {
         return false;
      }
      if(tier == 3 && mastered < 10) {
         return false;
      }
      if(tier == 4 && mastered < 16) {
         return false;
      }
      if(this.knowledgeKey != null) {
         if(nbtPlayer != null && nbtPlayer.hasKey("WITCSpellBook")) {
            NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
            return nbtSpells.getBoolean(this.knowledgeKey);
         } else {
            return false;
         }
      }
      return true;
   }

   public void acquireKnowledge(EntityPlayer player) {
      if(this.knowledgeKey != null) {
         NBTTagCompound nbtPlayer = Infusion.getNBT(player);
         if(nbtPlayer != null) {
            if(!nbtPlayer.hasKey("WITCSpellBook")) {
               nbtPlayer.setTag("WITCSpellBook", new NBTTagCompound());
            }

            NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
            nbtSpells.setBoolean(this.knowledgeKey, true);
            nbtSpells.setBoolean("Discovered_" + this.effectID, true);
            if(player.worldObj != null && !player.worldObj.isRemote) {
               Infusion.syncPlayer(player.worldObj, player);
            }
         }
      }

   }

   public static boolean studyBookOfSymbology(EntityPlayer player) {
      if(player == null) return false;
      player.addChatMessage(new net.minecraft.util.ChatComponentText(
         net.minecraft.util.EnumChatFormatting.GOLD + "✦ [Brujería: Simbología] " +
         net.minecraft.util.EnumChatFormatting.GREEN + "Todos los hechizos y trazos se consultan directamente en las páginas de tu libro de Simbología."
      ));
      return true;
   }

   public static String getKnowledge(EntityPlayer player) {
      StringBuilder sb = new StringBuilder();
      NBTTagCompound nbtPlayer = Infusion.getNBT(player);
      if(nbtPlayer != null && nbtPlayer.hasKey("WITCSpellBook")) {
         NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
         Iterator i$ = EffectRegistry.instance().getEffects().iterator();

         while(i$.hasNext()) {
            SymbolEffect effect = (SymbolEffect)i$.next();
            boolean known = (effect.knowledgeKey != null && nbtSpells.getBoolean(effect.knowledgeKey))
                || nbtSpells.getBoolean("Mastered_" + effect.effectID);
            if(known) {
               if(sb.length() > 0) {
                  sb.append(", ");
               }

               sb.append(effect.getLocalizedName());
            }
         }
      }

      return sb.toString();
   }

   public static boolean isSpellMastered(EntityPlayer player, SymbolEffect effect) {
      if(player == null || effect == null) return false;
      if(player.capabilities.isCreativeMode || isPlayerSupremeOrSynced(player)) return true;
      NBTTagCompound nbtPlayer = Infusion.getNBT(player);
      if(nbtPlayer != null && nbtPlayer.hasKey("WITCSpellBook")) {
         NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
         return nbtSpells.getBoolean("Mastered_" + effect.getEffectID())
             || (effect.knowledgeKey != null && nbtSpells.getBoolean(effect.knowledgeKey));
      }
      return false;
   }

   public static boolean isSpellDiscovered(EntityPlayer player, SymbolEffect effect) {
      if(effect == null) return false;
      return effect.isVisible(player);
   }

   public void onSpellCastSuccess(EntityPlayer player) {
      if(player == null || player.worldObj == null || player.worldObj.isRemote) return;
      NBTTagCompound nbtPlayer = Infusion.getNBT(player);
      if(nbtPlayer == null) return;
      nbtPlayer.setLong("WITCLastArcaneActivity", player.worldObj.getTotalWorldTime());
      if(!nbtPlayer.hasKey("WITCSpellBook")) {
         nbtPlayer.setTag("WITCSpellBook", new NBTTagCompound());
      }
      NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
      String castKey = "CastCount_" + this.effectID;
      int castCount = nbtSpells.getInteger(castKey) + 1;
      nbtSpells.setInteger(castKey, castCount);
      nbtSpells.setBoolean("Discovered_" + this.effectID, true);

      String masteredKey = "Mastered_" + this.effectID;
      boolean wasMastered = nbtSpells.getBoolean(masteredKey);
      int requiredCasts = 3;

      if(!wasMastered) {
         if(castCount >= requiredCasts) {
            nbtSpells.setBoolean(masteredKey, true);
            player.addChatMessage(new net.minecraft.util.ChatComponentText(net.minecraft.util.EnumChatFormatting.GOLD + "✦ ¡Has Dominado " + this.getLocalizedName() + "! (+10 Maestría)"));
            com.emoniph.witchery.util.SoundEffect.RANDOM_LEVELUP.playAtPlayer(player.worldObj, player);
            com.emoniph.witchery.common.SupremeWitchData.updateSupremeStatus(player.worldObj);
         } else {
            player.addChatMessage(new net.minecraft.util.ChatComponentText(net.minecraft.util.EnumChatFormatting.AQUA + "✦ Progreso con " + this.getLocalizedName() + ": " + net.minecraft.util.EnumChatFormatting.YELLOW + castCount + "/" + requiredCasts + net.minecraft.util.EnumChatFormatting.GRAY + " lanzamientos"));
         }
      }

      Infusion.syncPlayer(player.worldObj, player);
   }

   private String getCooldownKey() {
      return this.knowledgeKey != null ? this.knowledgeKey : ("spell_" + this.effectID);
   }

   public long cooldownRemaining(EntityPlayer player, NBTTagCompound nbtPlayer) {
      if(this.cooldownTicks > 0 && nbtPlayer != null && nbtPlayer.hasKey("WITCSpellBook")) {
         NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
         long lastUseTime = nbtSpells.getLong(this.getCooldownKey() + "LastUse");
         long timeNow = TimeUtil.getServerTimeInTicks();
         long effectiveCooldown = (long)this.cooldownTicks;
         if(player != null) {
            if(com.emoniph.witchery.common.SupremeWitchData.isPlayerSupreme(player)) {
               effectiveCooldown = Math.max(1L, effectiveCooldown / 2L);
            }
            if(com.emoniph.witchery.familiar.Familiar.hasActiveBroomMasteryFamiliar(player)) {
               effectiveCooldown = Math.max(1L, (long)(effectiveCooldown * 0.75D));
            }
            ItemStack branchStack = Witchery.modHooks.getHeldOrOffhandItem(player, Witchery.Items.MYSTIC_BRANCH);
            if(branchStack != null && com.emoniph.witchery.item.ItemMysticBranch.getWoodType(branchStack) == 2) {
               effectiveCooldown = Math.max(1L, (long)(effectiveCooldown * 0.65D));
            }
         }
         if(timeNow < lastUseTime + effectiveCooldown) {
            return lastUseTime + effectiveCooldown - timeNow;
         }
      }

      return 0L;
   }

   public void setOnCooldown(EntityPlayer player) {
      if(this.cooldownTicks > 0 && !player.capabilities.isCreativeMode) {
         NBTTagCompound nbtPlayer = Infusion.getNBT(player);
         if(nbtPlayer != null) {
            if(!nbtPlayer.hasKey("WITCSpellBook")) {
               nbtPlayer.setTag("WITCSpellBook", new NBTTagCompound());
            }
            NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
            nbtSpells.setLong(this.getCooldownKey() + "LastUse", TimeUtil.getServerTimeInTicks());
         }
      }

   }

   public boolean equals(Object obj) {
      if(obj != null && this.getClass() == obj.getClass()) {
         if(obj == this) {
            return true;
         } else {
            SymbolEffect other = (SymbolEffect)obj;
            return other.effectID == this.effectID;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      byte result = 17;
      int result1 = 37 * result + this.effectID;
      return result1;
   }
}
