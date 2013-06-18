package hu.sztaki.ilab.longneck.util.database;

import com.sleepycat.je.*;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import java.io.File;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class SequenceTest {

    private File envHome;

    @Before
    public void setup() {
        envHome = new File("testdb");
        envHome.mkdirs();
    }

    @After
    public void cleanup() {
        for (File f : envHome.listFiles()) {
            f.delete();
        }
        envHome.delete();
        envHome = null;
    }

    @Test
    public void testSequenceCreation() throws ClassNotFoundException {
        EnvironmentConfig econf = EnvironmentConfig.DEFAULT.setAllowCreate(true);
        Environment env = new Environment(envHome, econf);

        StoreConfig sconf = StoreConfig.DEFAULT.setAllowCreate(true);
        EntityStore store = new EntityStore(env, "TestStore", sconf);

        store.setPrimaryConfig(FakeEntity.class, 
                DatabaseConfig.DEFAULT.setAllowCreate(true));
        store.setSequenceConfig("testSequence", SequenceConfig.DEFAULT.setAllowCreate(true));

        Sequence seq = store.getSequence("testSequence");
        Assert.assertEquals(0, seq.get(null, 1));
        Assert.assertEquals(1, seq.get(null, 1));
        Assert.assertEquals(2, seq.get(null, 1));

        store.sync();

        seq.close();
        store.close();
        env.close();
    }
}
