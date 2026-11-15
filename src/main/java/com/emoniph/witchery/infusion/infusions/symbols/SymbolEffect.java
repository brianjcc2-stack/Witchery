package com.emoniph.witchery.infusion.infusions.symbols;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.util.Const;
import com.emoniph.witchery.util.TimeUtil;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
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

   public String getLocalizedName() {
      return Witchery.resource(this.unlocalisedName);
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

   public static String formatStrokesCompact(byte[] strokes) {
      if(strokes == null || strokes.length == 0) return "?";
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < strokes.length; i++) {
         if(i > 0) sb.append(" ");
         byte stroke = strokes[i];
         sb.append(getStrokeArrow(stroke));
         String strokeName = Witchery.resource("witchery.book.wands.stroke." + stroke);
         if(strokeName != null && !strokeName.isEmpty()) {
            if(strokeName.equalsIgnoreCase("izquierda")) strokeName = "Izq";
            else if(strokeName.equalsIgnoreCase("derecha")) strokeName = "Der";
            else if(strokeName.equalsIgnoreCase("arriba")) strokeName = "Arr";
            else if(strokeName.equalsIgnoreCase("abajo")) strokeName = "Abj";
            sb.append("(").append(strokeName).append(")");
         }
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
      if(tier == 2) return 6;
      if(tier == 3) return 14;
      return 24;
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
      sb.append(Witchery.resource(this.unlocalisedName));
      sb.append("§r");
      sb.append(Const.BOOK_NEWLINE);
      if(player != null) {
         if(isSpellMastered(player, this)) {
            sb.append("§2[✔ Hechizo Dominado]§0");
         } else {
            sb.append("§8[✦ Por dominar]§0");
         }
         sb.append(Const.BOOK_NEWLINE);
      }
      String descKey = this.unlocalisedName + ".info";
      String description = Witchery.resource(descKey);
      if(description != null && !description.isEmpty() && !description.equals(descKey)) {
         sb.append(description);
         sb.append(Const.BOOK_NEWLINE);
      }

      int tier = this.getSpellTier();
      if(tier == 1) {
         sb.append("§6[Tier 1: Aprendiz]§0");
      } else if(tier == 2) {
         sb.append("§b[Tier 2: Iniciado - Requiere 4 Dominados]§0");
      } else if(tier == 3) {
         sb.append("§d[Tier 3: Adepto - Requiere 10 Dominados]§0");
      } else {
         sb.append("§4[Tier 4: Artes Oscuras - Requiere 16 Dominados]§0");
      }
      sb.append(Const.BOOK_NEWLINE);

      if(this.requiresInfernalInfusion()) {
         sb.append("§4");
         sb.append(Witchery.resource("witchery.infuse.branch.reqinfernal"));
         sb.append("§0");
         sb.append(Const.BOOK_NEWLINE);
      }

      sb.append("§8");
      sb.append(Witchery.resource("witchery.book.wands.strokes"));
      sb.append(" §l§1");
      sb.append(formatStrokesArrowsOnly(this.defaultStrokes));
      sb.append("§r");
      sb.append(Const.BOOK_NEWLINE);
      if(this.defaultStrokes != null && this.defaultStrokes.length > 0) {
         sb.append("§8(");
         sb.append(formatStrokesCompact(this.defaultStrokes));
         sb.append(")§0");
         sb.append(Const.BOOK_NEWLINE);
      }

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
            if(player.getHeldItem() != null && player.getHeldItem().getItem() == Witchery.Items.MYSTIC_BRANCH) {
               if(com.emoniph.witchery.item.ItemMysticBranch.getWoodType(player.getHeldItem()) == 2) {
                  effectiveCooldown = Math.max(1L, (long)(effectiveCooldown * 0.65D));
               }
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
