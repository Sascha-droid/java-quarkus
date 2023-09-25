package org.example.app.task.logic;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link UcAddRandomActivityTaskItem}
 */
@QuarkusTest
@QuarkusTestResource(WireMockTestResource.class)
public class ActivityServiceTest {

  @Inject
  ActivityService activityService;

  @Test
  void shouldRetrieveRandomActivityOnSuccess() {

    String randomActivity = this.activityService.getRandomActivity();

    then(randomActivity).isEqualTo("Learn a new programming language");
  }
}
