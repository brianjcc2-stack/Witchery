package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.familiar.Familiar;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.network.PacketSyncSupremeWitch;
import com.emoniph.witchery.util.Coord;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class SupremeWitchData extends WorldSavedData {

    public static final String DATA_KEY = "witchery_supreme_witch";

    private String supremeUUID = "";
    private String supremeName = "";
    private boolean auraEnabled = true;
    private int auraColor = 0x33CC00; // Verde Naturaleza / Altar de Witchery por defecto
    private int auraAmount = 2; // Cantidad / densidad (1 a 10, por defecto 2)

    // Client-side cache
    public static String clientSupremeUUID = "";
    public static String clientSupremeName = "";
    public static boolean clientAuraEnabled = true;
    public static int clientAuraColor = 0x33CC00;
    public static int clientAuraAmount = 2;

    public SupremeWitchData(String key) {
        super(key);
    }

    public SupremeWitchData() {
        super(DATA_KEY);
    }

    public static SupremeWitchData get(World world) {
        if (world == null) {
            world = MinecraftServer.getServer() != null ? MinecraftServer.getServer().worldServerForDimension(0) : null;
        }
        World overworld = (world != null && world.provider.dimensionId == 0) ? world :
                (MinecraftServer.getServer() != null ? MinecraftServer.getServer().worldServerForDimension(0) : world);
        if (overworld == null || overworld.mapStorage == null) {
            return null;
        }
        SupremeWitchData data = (SupremeWitchData) overworld.mapStorage.loadData(SupremeWitchData.class, DATA_KEY);
        if (data == null) {
            data = new SupremeWitchData();
            overworld.mapStorage.setData(DATA_KEY, data);
        }
        return data;
    }

    public boolean hasSupreme() {
        return supremeUUID != null && !supremeUUID.trim().isEmpty();
    }

    public String getSupremeUUID() {
        return supremeUUID;
    }

    public String getSupremeName() {
        return supremeName;
    }

    public void setSupreme(EntityPlayer player) {
        if (player != null) {
            this.supremeUUID = player.getUniqueID().toString();
            this.supremeName = player.getCommandSenderName();
            this.markDirty();
            syncToAll();

            if (!player.worldObj.isRemote) {
                if (Infusion.getInfusionID(player) <= 0) {
                    Infusion.switchInfusionForSupreme(player, 4);
                } else {
                    Infusion.setCurrentEnergy(player, 3000);
                }
            }
        }
    }

    public void clearSupreme() {
        this.supremeUUID = "";
        this.supremeName = "";
        this.markDirty();
        syncToAll();
    }

    public boolean isSupreme(EntityPlayer player) {
        if (player == null) return false;
        if (player.worldObj != null && player.worldObj.isRemote) {
            return (clientSupremeUUID != null && !clientSupremeUUID.isEmpty() && clientSupremeUUID.equals(player.getUniqueID().toString()))
                    || (clientSupremeName != null && !clientSupremeName.isEmpty() && clientSupremeName.equalsIgnoreCase(player.getCommandSenderName()));
        }
        return hasSupreme() && supremeUUID.equals(player.getUniqueID().toString());
    }

    public static boolean isPlayerSupreme(EntityPlayer player) {
        if (player == null) return false;
        if (player.worldObj != null && player.worldObj.isRemote) {
            return (clientSupremeUUID != null && !clientSupremeUUID.isEmpty() && clientSupremeUUID.equals(player.getUniqueID().toString()))
                    || (clientSupremeName != null && !clientSupremeName.isEmpty() && clientSupremeName.equalsIgnoreCase(player.getCommandSenderName()));
        }
        SupremeWitchData data = get(player.worldObj);
        return data != null && data.isSupreme(player);
    }

    public boolean isAuraEnabled() {
        return this.auraEnabled;
    }

    public void setAuraEnabled(boolean enabled) {
        this.auraEnabled = enabled;
        this.markDirty();
        syncToAll();
    }

    public int getAuraColor() {
        return this.auraColor;
    }

    public void setAuraColor(int color) {
        this.auraColor = color;
        this.markDirty();
        syncToAll();
    }

    public int getAuraAmount() {
        return this.auraAmount;
    }

    public void setAuraAmount(int amount) {
        this.auraAmount = Math.max(1, Math.min(10, amount));
        this.markDirty();
        syncToAll();
    }

    public static boolean isSupremeAuraActive(EntityPlayer player) {
        if (player == null) return false;
        if (!com.emoniph.witchery.util.Config.instance().renderSupremeAnimation) return false;
        if (!isPlayerSupreme(player)) return false;
        if (player.worldObj != null && player.worldObj.isRemote) {
            return clientAuraEnabled;
        }
        SupremeWitchData data = get(player.worldObj);
        return data != null && data.isAuraEnabled();
    }

    public static int getSupremeAuraColor(EntityPlayer player) {
        if (player != null && player.worldObj != null && player.worldObj.isRemote) {
            return clientAuraColor;
        }
        if (player != null) {
            SupremeWitchData data = get(player.worldObj);
            if (data != null) return data.getAuraColor();
        }
        return 0x33CC00;
    }

    public static int getSupremeAuraAmount(EntityPlayer player) {
        if (player != null && player.worldObj != null && player.worldObj.isRemote) {
            return clientAuraAmount > 0 ? clientAuraAmount : 2;
        }
        if (player != null) {
            SupremeWitchData data = get(player.worldObj);
            if (data != null) return data.getAuraAmount();
        }
        return 2;
    }

    public static int parseColor(String str) {
        if (str == null) return -1;
        String s = str.trim().toLowerCase();
        if (s.startsWith("#")) s = s.substring(1);
        else if (s.startsWith("0x")) s = s.substring(2);

        if (s.equals("verde") || s.equals("green") || s.equals("altar") || s.equals("naturaleza") || s.equals("nature")) {
            return 0x33CC00; // Verde Altar Witchery
        } else if (s.equals("morado") || s.equals("purple") || s.equals("violeta") || s.equals("amatista")) {
            return 0x9933FF; // Violeta místico
        } else if (s.equals("azul") || s.equals("blue") || s.equals("celeste") || s.equals("cyan")) {
            return 0x00CCFF; // Azul etéreo
        } else if (s.equals("rojo") || s.equals("red") || s.equals("carmesi") || s.equals("fuego")) {
            return 0xFF2222; // Rojo carmesí
        } else if (s.equals("dorado") || s.equals("gold") || s.equals("amarillo") || s.equals("yellow")) {
            return 0xFFCC00; // Oro solar
        } else if (s.equals("blanco") || s.equals("white") || s.equals("espectral") || s.equals("luz")) {
            return 0xFFFFFF; // Blanco espectral
        } else if (s.equals("rosa") || s.equals("pink") || s.equals("magenta")) {
            return 0xFF66CC; // Rosa místico
        } else if (s.equals("naranja") || s.equals("orange")) {
            return 0xFF7700; // Naranja
        } else if (s.equals("negro") || s.equals("black") || s.equals("sombra") || s.equals("vacio")) {
            return 0x222222; // Sombra oscura
        }

        try {
            return (int) Long.parseLong(s, 16);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    public static String getColorName(int color) {
        int c = color & 0xFFFFFF;
        if (c == 0x33CC00) return "Verde Naturaleza (Altar)";
        if (c == 0x9933FF) return "Morado Místico";
        if (c == 0x00CCFF) return "Azul Etéreo";
        if (c == 0xFF2222) return "Rojo Carmesí";
        if (c == 0xFFCC00) return "Dorado Solar";
        if (c == 0xFFFFFF) return "Blanco Espectral";
        if (c == 0xFF66CC) return "Rosa Mágico";
        if (c == 0xFF7700) return "Naranja";
        if (c == 0x222222) return "Sombra Oscura";
        return String.format("#%06X", c);
    }

    public void syncTo(EntityPlayerMP player) {
        if (player != null) {
            Witchery.packetPipeline.sendTo((IMessage) new PacketSyncSupremeWitch(this.supremeUUID, this.supremeName, this.auraEnabled, this.auraColor, this.auraAmount), (EntityPlayer) player);
        }
    }

    public void syncToAll() {
        Witchery.packetPipeline.sendToAll((IMessage) new PacketSyncSupremeWitch(this.supremeUUID, this.supremeName, this.auraEnabled, this.auraColor, this.auraAmount));
    }

    public static class MasteryScoreBreakdown {
        public int spiritScore;
        public int covenScore;
        public int familiarBaseScore;
        public int familiarActiveScore;
        public int infusionBaseScore;
        public int infusionEnergyBonus;
        public int spellScore;
        public int babaYagaScore;
        public int horrocruxScore;
        public int wondersScore;
        public int tasksScore;
        public int bossScore;
        public int bossCount;
        public int brewingScore;
        public int altarScore;
        public int poppetScore;
        public int creatureScore;

        // Penalizaciones por descuido y daño
        public int energyPenalty;
        public int curseCount;
        public int cursePenalty;
        public int deathPenalty;
        public int familiarDeathPenalty;
        public int stagnationPenalty;
        public int covenTaskNeglectPenalty;

        public int getTotalPositive() {
            return spiritScore + covenScore + familiarBaseScore + familiarActiveScore
                    + infusionBaseScore + infusionEnergyBonus + spellScore + babaYagaScore
                    + horrocruxScore + wondersScore + tasksScore + bossScore + brewingScore + altarScore
                    + poppetScore + creatureScore;
        }

        public int getTotalPenalties() {
            return energyPenalty + cursePenalty + deathPenalty + familiarDeathPenalty + stagnationPenalty + covenTaskNeglectPenalty;
        }

        public int getNetScore() {
            return Math.max(0, getTotalPositive() - getTotalPenalties());
        }
    }

    public static boolean hasActiveProtectedPoppet(EntityPlayer player) {
        if (player == null || player.inventory == null) return false;
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == Witchery.Items.POPPET) {
                if (Witchery.Items.TAGLOCK_KIT.containsTaglockForEntity(stack, player, 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static MasteryScoreBreakdown getMasteryScoreBreakdown(EntityPlayer player) {
        MasteryScoreBreakdown b = new MasteryScoreBreakdown();
        if (player == null) return b;

        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        NBTTagCompound nbtPlayer = Infusion.getNBT(player);

        // 1. Sintonía Espiritual: hasta Nivel 3 = 300 pts (100 pts por nivel)
        if (playerEx != null) {
            b.spiritScore = Math.min(3, playerEx.getSpiritLevel()) * 100;
        }

        // 2. Aquelarre Vivo: 50 pts por cada bruja reclutada viva (hasta 300 pts)
        int covenSize = EntityCovenWitch.getCovenSize(player);
        b.covenScore = covenSize * 50;

        // 3. Familiar Vivo y Vinculado: 40 pts vínculo formal, +40 pts convocado en el mundo
        String familiar = Familiar.getFamiliarName(player);
        if (familiar != null && !familiar.isEmpty()) {
            b.familiarBaseScore = 40;
            if (Familiar.getActiveFamiliarType(player) > 0) {
                b.familiarActiveScore = 40;
            }
        }

        // 4. Infusión Arcana y Reserva de Energía
        if (Infusion.getInfusionID(player) > 0) {
            b.infusionBaseScore = 50;
            int curEnergy = Infusion.getCurrentEnergy(player);
            int maxEnergy = Infusion.getMaxEnergy(player);
            if (curEnergy == 0) {
                b.energyPenalty = 40; // Descuido grave: infusión agotada sin recargar
            } else if (maxEnergy > 0 && curEnergy >= (maxEnergy / 2)) {
                b.infusionEnergyBonus = 30; // Reserva mantenida con vigor
            }
        }

        // 5. Hechizos dominados en la Rama Mística (10 pts cada uno)
        int masteredSpells = SymbolEffect.getMasteredSpellCount(player);
        b.spellScore = masteredSpells * 10;

        // 6. Derrotar a Baba Yaga (250 pts)
        if (nbtPlayer != null && nbtPlayer.getBoolean("WITCDefeatedBabaYaga")) {
            b.babaYagaScore = 250;
        }

        // 7. Horrocrux consagrado (100 pts)
        if (nbtPlayer != null && nbtPlayer.getBoolean("WITCHorrocrux")) {
            b.horrocruxScore = 100;
        }

        // 8. Las Siete Maravillas consumadas (50 pts cada una, hasta 350 pts)
        b.wondersScore = getCompletedWondersCount(player) * 50;

        // 9. Misiones de Aquelarre (crecimiento continuo: 15 pts c/u hasta 20 tareas = 300 pts, y +5 pts por tarea adicional)
        int tasksCompleted = nbtPlayer != null ? nbtPlayer.getInteger("WITCCovenTasksCompleted") : 0;
        if (tasksCompleted > 0) {
            if (tasksCompleted <= 20) {
                b.tasksScore = tasksCompleted * 15;
            } else {
                b.tasksScore = 300 + (tasksCompleted - 20) * 5;
            }
        }

        // 10. Conquista y Golpe Final a Jefes (Bosses de Witchery y Minecraft)
        if (nbtPlayer != null && nbtPlayer.hasKey("WITCBossDefeats")) {
            NBTTagCompound nbtBosses = nbtPlayer.getCompoundTag("WITCBossDefeats");
            int bScore = 0;
            int bCount = 0;
            if (nbtBosses.getInteger("ender_dragon") > 0) { bScore += 80; bCount++; }
            if (nbtBosses.getInteger("wither") > 0) { bScore += 70; bCount++; }
            if (nbtBosses.getInteger("death") > 0) { bScore += 100; bCount++; }
            if (nbtBosses.getInteger("lord_of_torment") > 0) { bScore += 90; bCount++; }
            if (nbtBosses.getInteger("lilith") > 0) { bScore += 90; bCount++; }
            if (nbtBosses.getInteger("leonard") > 0) { bScore += 90; bCount++; }
            if (nbtBosses.getInteger("horned_huntsman") > 0) { bScore += 80; bCount++; }
            if (nbtBosses.getInteger("baba_yaga") > 0) { bScore += 100; bCount++; }
            if (nbtBosses.getInteger("reflection") > 0) { bScore += 60; bCount++; }
            if (nbtBosses.getInteger("gulg") > 0) { bScore += 50; bCount++; }
            if (nbtBosses.getInteger("mog") > 0) { bScore += 50; bCount++; }
            b.bossScore = bScore;
            b.bossCount = bCount;
        }

        // 11. Alquimia y Pociones en Caldero (Embotellado y Lanzamiento, hasta 100 pts)
        if (playerEx != null) {
            b.brewingScore = Math.min(50, playerEx.getSkillPotionBottling() * 5) + Math.min(50, playerEx.getSkillPotionThrowing() * 5);
        }

        // 11. Sintonía con Altar y Santuario Cercano
        if (player.worldObj != null && !player.worldObj.isRemote) {
            IPowerSource power = PowerSources.findClosestPowerSource(player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            if (power != null && !power.isPowerInvalid()) {
                Coord loc = power.getLocation();
                if (loc != null) {
                    double dx = player.posX - (loc.x + 0.5D);
                    double dy = player.posY - (loc.y + 0.5D);
                    double dz = player.posZ - (loc.z + 0.5D);
                    double distSq = dx * dx + dy * dy + dz * dz;
                    double range = (double) power.getRange();
                    if (distSq <= range * range) {
                        float p = power.getCurrentPower();
                        if (p >= 5000.0F) {
                            b.altarScore = 60;
                        } else if (p >= 2000.0F) {
                            b.altarScore = 30;
                        }
                    }
                }
            }
        }

        // 12. Muñeco Poppet de Protección Vinculado en Inventario (30 pts)
        if (hasActiveProtectedPoppet(player)) {
            b.poppetScore = 30;
        }

        // 13. Linaje Sobrenatural (Vampirismo / Licantropía viva, hasta 70 pts)
        if (playerEx != null) {
            int beastLevel = Math.max(playerEx.getVampireLevel(), playerEx.getWerewolfLevel());
            b.creatureScore = Math.min(70, beastLevel * 7);
        }

        // --- PENALIZACIONES POR DESCUIDO ---

        // A. Maleficios Activos Padecidos (-50 pts por cada maldición activa sin purificar)
        if (nbtPlayer != null) {
            int curses = 0;
            if (nbtPlayer.hasKey("witcheryCursed")) curses++;
            if (nbtPlayer.hasKey("witcheryInsanity")) curses++;
            if (nbtPlayer.hasKey("witcherySinking")) curses++;
            if (nbtPlayer.hasKey("witcheryOverheating")) curses++;
            if (nbtPlayer.hasKey("witcheryWakingNightmare")) curses++;
            b.curseCount = curses;
            b.cursePenalty = curses * 50;
        }

        // B. Trauma de Muerte Reciente (-80 pts si murió en las últimas 24.000 ticks / 1 día de Minecraft)
        if (nbtPlayer != null && nbtPlayer.hasKey("WITCLastDeathTime") && player.worldObj != null) {
            long elapsed = player.worldObj.getTotalWorldTime() - nbtPlayer.getLong("WITCLastDeathTime");
            if (elapsed >= 0 && elapsed < 24000L) {
                b.deathPenalty = 80;
            }
        }

        // C. Luto de Familiar Roto (-60 pts si su familiar murió en las últimas 24.000 ticks)
        if (nbtPlayer != null && nbtPlayer.hasKey("WITCLastFamiliarDeath") && player.worldObj != null) {
            long elapsed = player.worldObj.getTotalWorldTime() - nbtPlayer.getLong("WITCLastFamiliarDeath");
            if (elapsed >= 0 && elapsed < 24000L) {
                b.familiarDeathPenalty = 60;
            }
        }

        // D. Atrofia Mágica por Inactividad (-50 pts > 3 días MC, -100 pts > 5 días MC sin hacer magia)
        if (nbtPlayer != null && nbtPlayer.hasKey("WITCLastArcaneActivity") && player.worldObj != null) {
            long idle = player.worldObj.getTotalWorldTime() - nbtPlayer.getLong("WITCLastArcaneActivity");
            if (idle > 120000L) {
                b.stagnationPenalty = 100;
            } else if (idle > 72000L) {
                b.stagnationPenalty = 50;
            }
        }

        // E. Negligencia Semanal en Tareas del Aquelarre (-60 pts > 1 semana MC [168.000 ticks] sin cumplir misión, -120 pts > 2 semanas)
        if (nbtPlayer != null && (covenSize > 0 || tasksCompleted > 0) && player.worldObj != null) {
            long lastTaskTime = nbtPlayer.hasKey("WITCLastCovenTaskTime") ? nbtPlayer.getLong("WITCLastCovenTaskTime") : 0L;
            if (lastTaskTime > 0L) {
                long idleTask = player.worldObj.getTotalWorldTime() - lastTaskTime;
                if (idleTask > 336000L) {
                    b.covenTaskNeglectPenalty = 120;
                } else if (idleTask > 168000L) {
                    b.covenTaskNeglectPenalty = 60;
                }
            }
        }

        return b;
    }

    public static int calculateWitcheryMasteryScore(EntityPlayer player) {
        if (player == null) return 0;
        return getMasteryScoreBreakdown(player).getNetScore();
    }

    public static final String WONDER_TELEKINESIS = "Telekinesis";
    public static final String WONDER_CONCILIUM = "Concilium";
    public static final String WONDER_THERMOKINESIS = "Thermokinesis";
    public static final String WONDER_DESCENSUM = "Descensum";
    public static final String WONDER_VITALUM_VITALIS = "VitalumVitalis";
    public static final String WONDER_TRANSMUTATION = "Transmutation";
    public static final String WONDER_DIVINATION = "Divination";

    public static final String[] ALL_WONDERS = new String[]{
        WONDER_TELEKINESIS,
        WONDER_CONCILIUM,
        WONDER_THERMOKINESIS,
        WONDER_DESCENSUM,
        WONDER_VITALUM_VITALIS,
        WONDER_TRANSMUTATION,
        WONDER_DIVINATION
    };

    public static boolean hasCompletedWonder(EntityPlayer player, String wonderKey) {
        if (player == null || wonderKey == null) return false;
        NBTTagCompound nbtPlayer = Infusion.getNBT(player);
        if (nbtPlayer != null && nbtPlayer.hasKey("WITCSevenWonders")) {
            return nbtPlayer.getCompoundTag("WITCSevenWonders").getBoolean(wonderKey);
        }
        return false;
    }

    public static int getCompletedWondersCount(EntityPlayer player) {
        if (player == null) return 0;
        int count = 0;
        for (String w : ALL_WONDERS) {
            if (hasCompletedWonder(player, w)) {
                count++;
            }
        }
        return count;
    }

    public static void completeWonder(EntityPlayer player, String wonderKey) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote || wonderKey == null) return;
        NBTTagCompound nbtPlayer = Infusion.getNBT(player);
        if (nbtPlayer == null) return;

        if (!nbtPlayer.hasKey("WITCSevenWonders")) {
            nbtPlayer.setTag("WITCSevenWonders", new NBTTagCompound());
        }
        NBTTagCompound nbtWonders = nbtPlayer.getCompoundTag("WITCSevenWonders");
        if (nbtWonders.getBoolean(wonderKey)) {
            return; // Already completed
        }

        nbtWonders.setBoolean(wonderKey, true);
        int count = getCompletedWondersCount(player);

        String title = wonderKey;
        String lore = "";
        if (WONDER_TELEKINESIS.equals(wonderKey)) {
            title = "I. Telekinesis";
            lore = "«La materia inerte se inclina ante tu mente invisible.»";
        } else if (WONDER_CONCILIUM.equals(wonderKey)) {
            title = "II. Concilium";
            lore = "«Has doblegado una voluntad ajena bajo el peso de tu voz.»";
        } else if (WONDER_THERMOKINESIS.equals(wonderKey)) {
            title = "III. Thermokinesis";
            lore = "«El fuego primordial arde al compás de tu sangre.»";
        } else if (WONDER_DESCENSUM.equals(wonderKey)) {
            title = "IV. Descensum";
            lore = "«Tu alma ha cruzado el velo del Más Allá y ha regresado intacta.»";
        } else if (WONDER_VITALUM_VITALIS.equals(wonderKey)) {
            title = "V. Vitalum Vitalis";
            lore = "«Has equilibrado la balanza sagrada entre la vida y la muerte.»";
        } else if (WONDER_TRANSMUTATION.equals(wonderKey)) {
            title = "VI. Transmutation";
            lore = "«El espacio y la materia se pliegan bajo tus pasos.»";
        } else if (WONDER_DIVINATION.equals(wonderKey)) {
            title = "VII. Divination";
            lore = "«Ningún misterio permanece oculto ante tu segunda vista.»";
        }

        com.emoniph.witchery.util.SoundEffect.RANDOM_LEVELUP.playAtPlayer(player.worldObj, player);
        com.emoniph.witchery.util.ParticleEffect.PORTAL.send(com.emoniph.witchery.util.SoundEffect.RANDOM_ORB, player, 1.0D, 2.0D, 24);
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "✦ " + EnumChatFormatting.GOLD + "[Las Siete Maravillas - " + count + "/7] " + EnumChatFormatting.LIGHT_PURPLE + "Has superado " + EnumChatFormatting.YELLOW + title + EnumChatFormatting.LIGHT_PURPLE + " (+50 Maestría)"));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "   " + lore));

        if (count == 7) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔══════════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "  ★ ¡HAS CONSUMADO LAS SIETE MARAVILLAS! ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "  «Las antiguas brujas susurran tu nombre en el viento."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "   Usa " + EnumChatFormatting.YELLOW + "/suprema maravillas" + EnumChatFormatting.LIGHT_PURPLE + " para reclamar tu ascensión.»"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚══════════════════════════════════════════════════╝"));
        }

        updateSupremeStatus(player.worldObj);
    }

    public static final int SUPREME_MASTERY_THRESHOLD = 1000;

    public static void updateSupremeStatus(World world) {
        if (world == null || world.isRemote) return;
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.getConfigurationManager() == null) return;

        SupremeWitchData data = get(world);
        if (data == null) return;

        java.util.List players = server.getConfigurationManager().playerEntityList;
        if (players == null || players.isEmpty()) return;

        EntityPlayerMP bestCandidate = null;
        int highestScore = 0;

        for (Object obj : players) {
            if (obj instanceof EntityPlayerMP) {
                EntityPlayerMP p = (EntityPlayerMP) obj;
                int score = calculateWitcheryMasteryScore(p);
                if (score > highestScore) {
                    highestScore = score;
                    bestCandidate = p;
                }
            }
        }

        String currentUUID = data.getSupremeUUID();
        EntityPlayerMP currentSupremePlayer = null;
        int currentSupremeScore = 0;

        if (data.hasSupreme()) {
            for (Object obj : players) {
                if (obj instanceof EntityPlayerMP) {
                    EntityPlayerMP p = (EntityPlayerMP) obj;
                    if (p.getUniqueID().toString().equals(currentUUID)) {
                        currentSupremePlayer = p;
                        currentSupremeScore = calculateWitcheryMasteryScore(p);
                        break;
                    }
                }
            }
        }

        // 1. ¿La Bruja Suprema actual descuidó su poder vivo y cayó por debajo del umbral de 1,000 pts?
        if (data.hasSupreme() && currentSupremePlayer != null && currentSupremeScore < SUPREME_MASTERY_THRESHOLD) {
            // Si nadie alcanza 1,000 pts tampoco, el trono queda completamente VACANTE
            if (highestScore < SUPREME_MASTERY_THRESHOLD || bestCandidate == null) {
                String oldName = data.getSupremeName();
                data.clearSupreme();

                currentSupremePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "[Bruja Suprema] " + EnumChatFormatting.YELLOW + "Has descuidado tu poder vivo (Poder actual: " + currentSupremeScore + " pts, mínimo requerido: " + SUPREME_MASTERY_THRESHOLD + "). ¡Has sido destronada y el Manto Sagrado queda VACANTE!"));

                server.getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "★ [Witchery] " + EnumChatFormatting.RED + "¡La Bruja Suprema " + EnumChatFormatting.GOLD + oldName + EnumChatFormatting.RED + " ha descuidado su poder arcano (Poder actual: " + currentSupremeScore + " pts) y ha caído del trono! El Aquelarre busca una nueva soberana."));
                return;
            }
        }

        // 2. Si ningún jugador alcanza el umbral de 1,000 pts
        if (highestScore < SUPREME_MASTERY_THRESHOLD || bestCandidate == null) {
            return;
        }

        String candidateUUID = bestCandidate.getUniqueID().toString();

        // 3. Si el mejor candidato supera al actual o asciende al trono
        if (!candidateUUID.equals(currentUUID)) {
            data.setSupreme(bestCandidate);

            bestCandidate.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "[Bruja Suprema] " + EnumChatFormatting.LIGHT_PURPLE + "¡Tu maestría viva en Witchery (" + EnumChatFormatting.GOLD + highestScore + " pts" + EnumChatFormatting.LIGHT_PURPLE + ") te ha elevado como la " + EnumChatFormatting.GOLD + "Bruja Suprema" + EnumChatFormatting.LIGHT_PURPLE + "!"));

            if (currentSupremePlayer != null && currentSupremePlayer != bestCandidate) {
                currentSupremePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "[Bruja Suprema] " + EnumChatFormatting.YELLOW + bestCandidate.getCommandSenderName() + " te ha superado en poder vivo (" + highestScore + " vs " + currentSupremeScore + " pts) y ha tomado el manto. ¡Atiende tus componentes arcanos para recuperarlo!"));
            }

            server.getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "★ [Witchery] " + EnumChatFormatting.LIGHT_PURPLE + "¡" + EnumChatFormatting.GOLD + bestCandidate.getCommandSenderName() + EnumChatFormatting.LIGHT_PURPLE + " ha ascendido como la nueva " + EnumChatFormatting.GOLD + "Bruja Suprema" + EnumChatFormatting.LIGHT_PURPLE + " con un poder vivo de " + EnumChatFormatting.GOLD + highestScore + " pts" + EnumChatFormatting.LIGHT_PURPLE + "!"));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.supremeUUID = nbt.getString("SupremeUUID");
        this.supremeName = nbt.getString("SupremeName");
        this.auraEnabled = !nbt.hasKey("AuraEnabled") || nbt.getBoolean("AuraEnabled");
        this.auraColor = nbt.hasKey("AuraColor") ? nbt.getInteger("AuraColor") : 0x33CC00;
        this.auraAmount = nbt.hasKey("AuraAmount") ? Math.max(1, Math.min(10, nbt.getInteger("AuraAmount"))) : 2;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("SupremeUUID", this.supremeUUID != null ? this.supremeUUID : "");
        nbt.setString("SupremeName", this.supremeName != null ? this.supremeName : "");
        nbt.setBoolean("AuraEnabled", this.auraEnabled);
        nbt.setInteger("AuraColor", this.auraColor);
        nbt.setInteger("AuraAmount", this.auraAmount);
    }
}
