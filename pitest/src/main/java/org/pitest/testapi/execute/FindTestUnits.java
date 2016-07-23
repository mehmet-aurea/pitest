package org.pitest.testapi.execute;

import org.pitest.testapi.Configuration;
import org.pitest.testapi.TestUnit;
import org.pitest.util.Log;

import java.util.*;
import java.util.logging.Logger;

/**
 * Scans classes to discover TestUnits
 *
 */
public class FindTestUnits {

  private final Configuration config;
    private static final Logger LOG = Log.getLogger();

  public FindTestUnits(final Configuration config) {
    this.config = config;
  }

  public List<TestUnit> findTestUnitsForAllSuppliedClasses(
      final Iterable<Class<?>> classes) {
    final List<TestUnit> testUnits = new ArrayList<TestUnit>();

    for (final Class<?> c : classes) {
        try {
            LOG.info("Finding Test Units for class : " + c.getCanonicalName());
            final Collection<TestUnit> testUnitsFromClass = getTestUnits(c);
            testUnits.addAll(testUnitsFromClass);
        } catch (Exception ex) {
            LOG.info("Caught Exception while finding Test Units for Class" + ex);
        }
    }

    return testUnits;

  }

  private Collection<TestUnit> getTestUnits(final Class<?> suiteClass) {
    final List<TestUnit> tus = new ArrayList<TestUnit>();
    final Set<Class<?>> visitedClasses = new HashSet<Class<?>>();
    findTestUnits(tus, visitedClasses, suiteClass);
    return tus;
  }

  private void findTestUnits(final List<TestUnit> tus,
      final Set<Class<?>> visitedClasses, final Class<?> suiteClass) {
    visitedClasses.add(suiteClass);
    final Collection<Class<?>> tcs = this.config.testSuiteFinder().apply(
        suiteClass);

    for (final Class<?> tc : tcs) {
      if (!visitedClasses.contains(tc)) {
        findTestUnits(tus, visitedClasses, tc);
      }
    }

    final List<TestUnit> testsInThisClass = this.config.testUnitFinder()
        .findTestUnits(suiteClass);
    if (!testsInThisClass.isEmpty()) {
      tus.addAll(testsInThisClass);
    }

  }

}
