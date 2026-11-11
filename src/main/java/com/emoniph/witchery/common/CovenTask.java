package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.integration.ModHookMCA;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Random;

public class CovenTask {

    public enum TaskCategory {
        BREWING("Alquimia y Caldero", "§d"),
        HUNTING("Caza y Sombras", "§c"),
        RITUAL("Ritos y Círculos", "§5"),
        HARVEST("Cosecha y Rarezas", "§a"),
        MCA_VILLAGER("Aldea y Almas Humanas", "§6");

        public final String displayName;
        public final String colorPrefix;

        TaskCategory(String name, String color) {
            this.displayName = name;
            this.colorPrefix = color;
        }
    }

    private String id;
    private TaskCategory category;
    private String title;
    private String description;
    private ItemStack requiredItem;
    private int requiredAmount;
    private int masteryReward;
    private String blessingName;

    public CovenTask(String id, TaskCategory category, String title, String description, ItemStack requiredItem, int requiredAmount, int masteryReward, String blessingName) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.requiredItem = requiredItem;
        this.requiredAmount = requiredAmount;
        this.masteryReward = masteryReward;
        this.blessingName = blessingName;
    }

    public String getId() {
        return id;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getRequiredItem() {
        return requiredItem;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public int getMasteryReward() {
        return masteryReward;
    }

    public String getBlessingName() {
        return blessingName;
    }

    public boolean canComplete(EntityPlayer player) {
        if (player == null || requiredItem == null) return false;

        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == requiredItem.getItem() &&
                    (requiredItem.getItemDamage() == 32767 || stack.getItemDamage() == requiredItem.getItemDamage())) {
                count += stack.stackSize;
            }
        }
        return count >= requiredAmount;
    }

    public boolean completeAndReward(EntityPlayer player, String witchName) {
        if (!canComplete(player)) {
            return false;
        }

        // Consume required items
        int needed = requiredAmount;
        for (int i = 0; i < player.inventory.mainInventory.length && needed > 0; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == requiredItem.getItem() &&
                    (requiredItem.getItemDamage() == 32767 || stack.getItemDamage() == requiredItem.getItemDamage())) {
                int take = Math.min(needed, stack.stackSize);
                stack.stackSize -= take;
                needed -= take;
                if (stack.stackSize <= 0) {
                    player.inventory.mainInventory[i] = null;
                }
            }
        }

        Random rand = player.worldObj.rand;

        // 1. Roll loot items (Alijo del Aquelarre)
        ItemStack bonusReward = rollLoot(rand);
        if (bonusReward != null) {
            if (!player.inventory.addItemStackToInventory(bonusReward)) {
                player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY + 1.0D, player.posZ, bonusReward));
            }
        }

        // 2. Apply Coven Blessing
        applyBlessing(player);

        // 3. Audio & Visuals
        SoundEffect.RANDOM_LEVELUP.playAtPlayer(player.worldObj, player, 0.8F, 1.0F);
        ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_ORB, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 1.0D, 1.5D, 24);

        // 4. Update Coven Task Counter & Infusion Recharge
        NBTTagCompound nbtPlayer = com.emoniph.witchery.infusion.Infusion.getNBT(player);
        int totalDone = 1;
        if (nbtPlayer != null) {
            totalDone = nbtPlayer.getInteger("WITCCovenTasksCompleted") + 1;
            nbtPlayer.setInteger("WITCCovenTasksCompleted", totalDone);
        }
        int curEnergy = com.emoniph.witchery.infusion.Infusion.getCurrentEnergy(player);
        int maxEnergy = com.emoniph.witchery.infusion.Infusion.getMaxEnergy(player);
        if (curEnergy < maxEnergy) {
            com.emoniph.witchery.infusion.Infusion.setCurrentEnergy(player, Math.min(maxEnergy, curEnergy + 50));
        }

        // 5. Update Supreme Witch Status
        SupremeWitchData.updateSupremeStatus(player.worldObj);

        // 6. Notify player in chat
        player.addChatMessage(new ChatComponentText("§2§m-----------------------------------------------------"));
        player.addChatMessage(new ChatComponentText("§a§l✔ [Misión del Aquelarre Completada - #" + totalDone + "]"));
        player.addChatMessage(new ChatComponentText("§f<" + witchName + "> §d\"¡Magnífico trabajo, hermana! Tu lealtad fortalece nuestras raíces.\""));
        if (bonusReward != null) {
            player.addChatMessage(new ChatComponentText("§6🎁 Alijo Místico del Aquelarre: §f" + bonusReward.getDisplayName() + " x" + bonusReward.stackSize));
        }
        player.addChatMessage(new ChatComponentText("§d✨ Bendición Activa: §b" + blessingName + " §a(+50 Poder de Infusión)"));
        player.addChatMessage(new ChatComponentText("§5★ Puntos de Maestría otorgados: §6+" + masteryReward + " pts"));
        player.addChatMessage(new ChatComponentText("§2§m-----------------------------------------------------"));

        return true;
    }

    private void applyBlessing(EntityPlayer player) {
        if (player == null) return;
        // Apply 12 minutes (14400 ticks) of atmospheric buffs
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 14400, 1));
        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 14400, 0));
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 14400, 0));

        // MCA village friendship boost
        if (ModHookMCA.isMCAPresent()) {
            player.getEntityData().setLong("WITCLastMCATerrorTick", 0L); // Clear any village fear
            player.getEntityData().setInteger("WITCMCATerrorLevel", 0);
        }
    }

    private ItemStack rollLoot(Random rand) {
        int roll = rand.nextInt(100);
        if (roll < 15) {
            // Koboldite items
            return Witchery.Items.GENERIC.itemKobolditeNugget.createStack(rand.nextInt(3) + 1);
        } else if (roll < 30) {
            // Tears of the goddess or high distillates
            return Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack(rand.nextInt(2) + 1);
        } else if (roll < 45) {
            // Refined Evil or Diamond Vapour
            return Witchery.Items.GENERIC.itemRefinedEvil.createStack(rand.nextInt(3) + 1);
        } else if (roll < 60) {
            // Breath of Goddess or Attuned Stone
            return Witchery.Items.GENERIC.itemBreathOfTheGoddess.createStack(rand.nextInt(3) + 1);
        } else if (roll < 75) {
            // Tarro de Boticario o Libro de Simbología
            return rand.nextBoolean() ?
                    new ItemStack(Witchery.Blocks.APOTHECARY_JAR, 2) :
                    Witchery.Items.GENERIC.itemBookWands.createStack(1);
        } else if (roll < 90) {
            // Flores mágicas raras de la expansión
            int flowerRoll = rand.nextInt(5);
            switch (flowerRoll) {
                case 0: return new ItemStack(Witchery.Blocks.MOON_ROSE, 2);
                case 1: return new ItemStack(Witchery.Blocks.SUN_ROSE, 2);
                case 2: return new ItemStack(Witchery.Blocks.GRAVE_ROSE, 2);
                case 3: return new ItemStack(Witchery.Blocks.BANSHEE_ROSE, 1);
                default: return new ItemStack(Witchery.Blocks.HECATES_TEARS, 1);
            }
        } else {
            // Demon heart, Subdued Spirit o Lingote de Koboldita
            int ultraRoll = rand.nextInt(3);
            if (ultraRoll == 0) return Witchery.Items.GENERIC.itemDemonHeart.createStack(1);
            else if (ultraRoll == 1) return Witchery.Items.GENERIC.itemSubduedSpirit.createStack(1);
            else return Witchery.Items.GENERIC.itemKobolditeIngot.createStack(1);
        }
    }
}
