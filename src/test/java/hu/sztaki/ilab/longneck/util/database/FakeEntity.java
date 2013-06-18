package hu.sztaki.ilab.longneck.util.database;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
@Entity
public class FakeEntity {

    @PrimaryKey(sequence = "primaryindex")
    public long primaryId;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    public long secondaryId;
}
