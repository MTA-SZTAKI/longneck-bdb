package hu.sztaki.ilab.longneck.util.database;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class InterfaceTest {

    @Test
    public void testIsImplementsInterfaceTest() {
        Class bdbClass = BDBConfiguration.class;
        Class confClass = Configuration.class;

        Assert.assertTrue(confClass.isAssignableFrom(bdbClass));
    }
}
