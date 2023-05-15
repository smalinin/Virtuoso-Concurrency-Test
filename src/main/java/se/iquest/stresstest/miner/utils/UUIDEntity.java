package se.iquest.stresstest.miner.utils;

import java.util.UUID;
import java.util.regex.Pattern;

public interface UUIDEntity
{
    public UUID getUid();
    public void setUid(UUID uid);
    
    /**
     * Generate UUID for entity if it's null
     * @param entity
     * @return true if new UUID has been assigned to entity, false in otherwise
     */
    static boolean generateUUID(UUIDEntity entity)
    {
        if (entity.getUid() == null) {
            entity.setUid(UUID.randomUUID());
            return true;
        }
        return false;
    }
    
    static UUID generateUUID()
    {
        return UUID.randomUUID();
    }
    
    static boolean isValidUUID(String uuid)
    {
        if (uuid == null) {
            return false;
        }

        try {
            // Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
            Pattern p = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
            p.matcher(uuid).matches();
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
}
