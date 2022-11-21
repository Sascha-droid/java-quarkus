package org.example.app.task.logic;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.example.app.task.dataaccess.TaskItemEntity;
import org.example.app.task.dataaccess.TaskItemRepository;

/**
 * Use-Case to add a {@link org.example.app.task.common.TaskItem} with a random activity grabbed from
 * <a href="https://www.boredapi.com/">The Bored API</a>.
 *
 * @see <a href="https://www.boredapi.com/">The Bored API</a>
 */
@ApplicationScoped
@Named
@Transactional
public class UcAddRandomActivityTaskItem {

  @Inject
  TaskItemRepository taskItemRepository;

  @Inject
  ActivityService activityService;

  /**
   * @param taskListId id the {@link org.example.app.task.dataaccess.TaskListEntity#getId() primary key} of the
   *        {@link org.example.app.task.dataaccess.TaskListEntity} for which to add a random task.
   * @return the {@link TaskItemEntity#getId() primary key} of the newly added {@link TaskItemEntity}.
   */
  // @RolesAllowed(ApplicationAccessControlConfig.PERMISSION_SAVE_TASK_ITEM)
  public Long addRandom(Long taskListId) {

    TaskItemEntity entity = new TaskItemEntity();
    entity.setTaskListId(taskListId);
    entity.setTitle(this.activityService.getRandomActivity());

    entity = this.taskItemRepository.save(entity);
    return entity.getId();
  }

}
