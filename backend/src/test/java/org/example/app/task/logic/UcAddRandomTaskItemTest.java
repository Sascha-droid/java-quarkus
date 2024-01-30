package org.example.app.task.logic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;

import org.assertj.core.api.BDDAssertions;
import org.example.app.task.dataaccess.TaskItemEntity;
import org.example.app.task.dataaccess.TaskItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link UcAddRandomActivityTaskItem}
 */
@QuarkusTest
public class UcAddRandomTaskItemTest {

  @Inject
  UcAddRandomActivityTaskItem ucAddRandomTaskItem;

  @InjectMock
  private ActivityService activityServiceMock;

  @InjectMock
  private TaskItemRepository taskItemRepositoryMock;

  @Test
  void testAddRandomTaskItemUseCase() {

    given(this.activityServiceMock.getRandomActivity()).willReturn("Water my plants");
    given(this.taskItemRepositoryMock.save(any())).willReturn(new TaskItemEntity());

    this.ucAddRandomTaskItem.addRandom(1l);

    ArgumentCaptor<TaskItemEntity> taskItemCaptor = ArgumentCaptor.forClass(TaskItemEntity.class);
    then(this.taskItemRepositoryMock).should().save(taskItemCaptor.capture());

    BDDAssertions.then(taskItemCaptor.getValue().getTitle()).isEqualTo("Water my plants");
  }
}
