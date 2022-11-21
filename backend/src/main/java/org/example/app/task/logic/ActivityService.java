package org.example.app.task.logic;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Provides services for retrieving activity suggestions.
 */
@ApplicationScoped
public class ActivityService {

  @Inject
  @RestClient
  BoredApi boredApi;

  /**
   * Get a random activity suggestion.
   *
   * @return the activity description
   */
  public String getRandomActivity() {

    return this.boredApi.getRandomActivity().activity;
  }
}
