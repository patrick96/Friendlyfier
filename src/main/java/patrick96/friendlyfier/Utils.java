package patrick96.friendlyfier;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String getOriginalName(Entity e) {
        String s = EntityList.getEntityString(e);

        if (s == null)
        {
            s = "generic";
        }

        return StatCollector.translateToLocal("entity." + s + ".name");
    }

    public static void log(Level l, String msg) {
        FMLLog.log(Friendlyfier.MODID, l, msg);
    }

    public static boolean isBoss(Entity entity) {
        return entity instanceof EntityWither || entity instanceof EntityDragon;
    }

    public static boolean canFriendlyfy(EntityCreature entity, boolean onSpawn) {
        return !isBoss(entity) && onSpawn == entity.getEntityData().getBoolean("friendlyfied");
    }

    public static boolean friendlyfy(EntityCreature entity) {
        return friendlyfy(entity, false);
    }

    public static boolean friendlyfy(EntityCreature entity, boolean onSpawn) {
        if(entity.worldObj.isRemote || !canFriendlyfy(entity, onSpawn)) {
            return false;
        }

        entity.getEntityData().setBoolean("friendlyfied", true);

        List<EntityAIBase> tasks = new ArrayList<>();
        List<EntityAIBase> targetTasks = new ArrayList<>();

        for(Object t : entity.tasks.taskEntries) {
            tasks.add(((EntityAITasks.EntityAITaskEntry) t).action);
        }

        for(Object t : entity.targetTasks.taskEntries) {
            targetTasks.add(((EntityAITasks.EntityAITaskEntry) t).action);
        }

        for(EntityAIBase b : tasks) {
            entity.tasks.removeTask(b);
        }

        for(EntityAIBase b : targetTasks) {
            entity.targetTasks.removeTask(b);
        }

        if(entity instanceof EntityCreeper) {
            // TODO config for this
            EntityCreeper creeper = (EntityCreeper) entity;
            try {
                creeper.setCreeperState(-1);
                Field watcher = ReflectionHelper.findField(Entity.class, "dataWatcher", "field_70180_af");
                ((DataWatcher) watcher.get(creeper)).updateObject(18, (byte) 0);
            } catch(IllegalAccessException e) {
                Utils.log(Level.ERROR, "Failed to defuse creeper. It's gonna blow!!!");
                e.printStackTrace();
            }
        }

        if(entity instanceof EntityPigZombie) {
            try {
                ReflectionHelper.findField(EntityPigZombie.class, "angerLevel", "field_70837_d").set(entity, 0);
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        entity.tasks.addTask(0, new EntityAISwimming(entity));
        entity.tasks.addTask(1, new EntityAIWander(entity, 1.0D));
        entity.tasks.addTask(2, new EntityAILookIdle(entity));

        try {
            ReflectionHelper.findField(Entity.class, "invulnerable", "field_83001_bt").set(entity, true);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }

        entity.setTarget(null);
        entity.setPathToEntity(null);
        entity.func_110163_bv();

        return true;
    }

}
