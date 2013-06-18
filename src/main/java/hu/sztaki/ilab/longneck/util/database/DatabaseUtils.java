package hu.sztaki.ilab.longneck.util.database;

import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;
import com.sleepycat.je.SequenceNotFoundException;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.EntityStore;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class DatabaseUtils {

    /**
     * Returns the highest key value in the index greater than zero.
     * 
     * This method will return at least 0.
     * 
     * @param index The index to scan.
     * @return The highest key, but at least 0.
     */
    public static long getMaxId(EntityIndex<Long, ?> index) {
        long maxId = 0;
        for (long id : index.keys()) {
            if (id > maxId) {
                maxId = id;
            }
        }

        return maxId;
    }

    /**
     * Opens or creates a sequence.
     * 
     * This method tries to open an existing sequence or create a new sequence with starting value
     * of the next available id number in the specified index.
     * 
     * @param entityStore The store that contains the sequence.
     * @param index The index which is used to initialize the starting value.
     * @param sequenceName The name of the sequence.
     * @return The opened or created sequence.
     */
    public static Sequence openSequence(EntityStore entityStore, EntityIndex<Long, ?> index, 
            String sequenceName) {
        Sequence seq = null;
        try {
            seq = entityStore.getSequence(sequenceName);
        } catch (SequenceNotFoundException ex) {
            // Establish current value
            long initialValue = DatabaseUtils.getMaxId(index) +  1;

            entityStore.setSequenceConfig(sequenceName, 
                    SequenceConfig.DEFAULT.setAllowCreate(true).setInitialValue(initialValue));
            seq = entityStore.getSequence(sequenceName);
        } finally {
            if (seq == null) {
                throw new RuntimeException("Could not open sequence localIdSequence.");
            }

            return seq;
        }
    }
}
