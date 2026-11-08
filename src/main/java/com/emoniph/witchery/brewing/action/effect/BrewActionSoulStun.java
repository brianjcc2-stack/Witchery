package com.emoniph.witchery.brewing.action.effect;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.brewing.AltarPower;
import com.emoniph.witchery.brewing.BrewItemKey;
import com.emoniph.witchery.brewing.BrewNamePart;
import com.emoniph.witchery.brewing.EffectLevel;
import com.emoniph.witchery.brewing.ModifiersEffect;
import com.emoniph.witchery.brewing.Probability;
import com.emoniph.witchery.brewing.action.BrewActionEffect;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BrewActionSoulStun extends BrewActionEffect {

    public BrewActionSoulStun(BrewItemKey itemKey, BrewNamePart namePart, AltarPower powerCost, Probability baseProbability, EffectLevel effectLevel) {
        super(itemKey, namePart, powerCost, baseProbability, effectLevel);
    }

    @Override
    protected void doApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack) {
        if (!world.isRemote && targetEntity != null) {
            int duration = 140 + modifiers.getStrength() * 60;
            targetEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, duration, 6));
            targetEntity.addPotionEffect(new PotionEffect(Potion.weakness.id, duration, 4));
            targetEntity.addPotionEffect(new PotionEffect(Witchery.Potions.PARALYSED.id, 100 + modifiers.getStrength() * 40, 1));
            targetEntity.motionX = 0;
            targetEntity.motionY = 0;
            targetEntity.motionZ = 0;
            ParticleEffect.PORTAL.send(SoundEffect.RANDOM_POP, targetEntity, 1.0D, 1.5D, 20);
        }
    }
}

