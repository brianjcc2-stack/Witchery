package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.familiar.Familiar;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.network.PacketSyncSupremeWitch;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    public static int calculateWitcheryMasteryScore(EntityPlayer player) {
        if (player == null) return 0;
        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        int score = 0;

        // Spirit Level: 100 points per level (up to 1000)
        if (playerEx != null) {
            score += playerEx.getSpiritLevel() * 100;
        }

        // Coven witches recruited: 80 points per witch (up to 480)
        int covenSize = EntityCovenWitch.getCovenSize(player);
        score += covenSize * 80;

        // Bound familiar: 70 points
        String familiar = Familiar.getFamiliarName(player);
        if (familiar != null && !familiar.isEmpty()) {
            score += 70;
        }

        // Active infusion: 50 points
        if (Infusion.getInfusionID(player) > 0) {
            score += 50;
        }

        // Mystic Branch spell knowledge: 10 points per spell known
        String knowledge = SymbolEffect.getKnowledge(player);
        if (knowledge != null && !knowledge.isEmpty()) {
            String[] spells = knowledge.split(",");
            score += spells.length * 10;
        }

        // Defeated Baba Yaga: 300 mastery points
        NBTTagCompound nbtPlayer = Infusion.getNBT(player);
        if (nbtPlayer != null && nbtPlayer.getBoolean("WITCDefeatedBabaYaga")) {
            score += 300;
        }

        // Horrocrux active: 100 mastery points
        if (nbtPlayer != null && nbtPlayer.getBoolean("WITCHorrocrux")) {
            score += 100;
        }

        // Seven Wonders completed: 50 points per Wonder (up to 350)
        score += getCompletedWondersCount(player) * 50;

        // Coven Tasks completed: 15 points per task (up to 300)
        if (nbtPlayer != null) {
            score += Math.min(300, nbtPlayer.getInteger("WITCCovenTasksCompleted") * 15);
        }

        return score;
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

    public static final int SUPREME_MASTERY_THRESHOLD = 900;

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

        // Minimum threshold: requires true end-game Witchery mastery (900+ points)
        if (highestScore < SUPREME_MASTERY_THRESHOLD || bestCandidate == null) {
            return;
        }

        String currentUUID = data.getSupremeUUID();
        String candidateUUID = bestCandidate.getUniqueID().toString();

        if (!candidateUUID.equals(currentUUID)) {
            EntityPlayerMP previousSupreme = null;
            if (data.hasSupreme()) {
                for (Object obj : players) {
                    if (obj instanceof EntityPlayerMP) {
                        EntityPlayerMP p = (EntityPlayerMP) obj;
                        if (p.getUniqueID().toString().equals(currentUUID)) {
                            previousSupreme = p;
                            break;
                        }
                    }
                }
            }

            data.setSupreme(bestCandidate);

            bestCandidate.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "[Bruja Suprema] " + EnumChatFormatting.LIGHT_PURPLE + "¡Tu maestría en Witchery (" + EnumChatFormatting.GOLD + highestScore + " pts" + EnumChatFormatting.LIGHT_PURPLE + ") te ha elevado como la " + EnumChatFormatting.GOLD + "Bruja Suprema" + EnumChatFormatting.LIGHT_PURPLE + "!"));

            if (previousSupreme != null && previousSupreme != bestCandidate) {
                previousSupreme.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "[Bruja Suprema] " + EnumChatFormatting.YELLOW + bestCandidate.getCommandSenderName() + " te ha superado en maestría (" + highestScore + " pts) y ha tomado el manto. ¡Sigue progresando para recuperarlo!"));
            }

            server.getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "★ [Witchery] " + EnumChatFormatting.LIGHT_PURPLE + "¡" + EnumChatFormatting.GOLD + bestCandidate.getCommandSenderName() + EnumChatFormatting.LIGHT_PURPLE + " ha sido reconocida por las fuerzas del aquelarre como la nueva " + EnumChatFormatting.GOLD + "Bruja Suprema" + EnumChatFormatting.LIGHT_PURPLE + " (Maestría: " + highestScore + " pts)!"));
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
