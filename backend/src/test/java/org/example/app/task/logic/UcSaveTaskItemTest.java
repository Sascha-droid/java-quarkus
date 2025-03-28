package org.example.app.task.logic;

import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.example.app.task.common.TaskItemEto;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link UcSaveTaskItem}.
 */
@QuarkusTest
public class UcSaveTaskItemTest extends Assertions {

  @Inject
  private UcFindTaskItem ucFindTaskItem;

  @Inject
  private UcSaveTaskItem ucSaveTaskItem;

  @Test
  public void saveTaskItem() {

    TaskItemEto item = this.ucFindTaskItem.findById(11L);
    // save without any change
    this.ucSaveTaskItem.save(item);
    TaskItemEto item2 = this.ucFindTaskItem.findById(11L);
    assertThat(item.getVersion()).isEqualTo(0L);
    assertThat(item2.getVersion()).isEqualTo(1L);
    // test that relation from item to list is still present and not lost.
    assertThat(item2.getTaskListId()).isEqualTo(item.getTaskListId()).isEqualTo(1L);
  }

}
