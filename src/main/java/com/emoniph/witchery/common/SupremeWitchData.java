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

    // Client-side cache
    public static String clientSupremeUUID = "";
    public static String clientSupremeName = "";

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

    public void syncTo(EntityPlayerMP player) {
        if (player != null) {
            Witchery.packetPipeline.sendTo((IMessage) new PacketSyncSupremeWitch(this.supremeUUID, this.supremeName), (EntityPlayer) player);
        }
    }

    public void syncToAll() {
        Witchery.packetPipeline.sendToAll((IMessage) new PacketSyncSupremeWitch(this.supremeUUID, this.supremeName));
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

        return score;
    }

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

        // Minimum threshold: need at least some real progress in Witchery (at least 50 points)
        if (highestScore < 50 || bestCandidate == null) {
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
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("SupremeUUID", this.supremeUUID != null ? this.supremeUUID : "");
        nbt.setString("SupremeName", this.supremeName != null ? this.supremeName : "");
    }
}
