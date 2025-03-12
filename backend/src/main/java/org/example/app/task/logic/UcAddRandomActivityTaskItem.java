package org.example.app.task.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.example.app.task.common.TaskItemEto;
import org.example.app.task.dataaccess.TaskItemEntity;
import org.example.app.task.dataaccess.TaskItemRepository;

import java.util.List;

/**
 * Use-Case to add one or multiple {@link org.example.app.task.common.TaskItem task items} with a random activity that
 * is generated by a locally running Ollama.
 *
 * @see <a href="https://ollama.com/">Ollama LLM</a>
 */
@ApplicationScoped
@Named
@Transactional
public class UcAddRandomActivityTaskItem {

  @Inject
  TaskItemRepository taskItemRepository;

  @Inject
  ActivityService activityService;

  @Inject
  TaskItemMapper taskItemMapper;

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

  /**
   * Adds multiple random tasks to the specified {@link org.example.app.task.dataaccess.TaskListEntity}.
   *
   * @param taskListId the {@link org.example.app.task.dataaccess.TaskListEntity#getId() primary key} of the
   *                   {@link org.example.app.task.dataaccess.TaskListEntity} to which random tasks will be added.
   */
  public void addMultipleRandom(Long taskListId) {
    List<TaskItemEto> taskItems = this.activityService.getMultipleRandomActivities();

    List<TaskItemEntity> entities = taskItems.stream()
            .map(eto -> {
                eto.setTaskListId(taskListId);
                return taskItemMapper.toEntity(eto);
            })
            .toList();

    this.taskItemRepository.saveAll(entities);
  }

  /**
   * Extracts ingredients from a given recipe and adds them as tasks to the specified
   * {@link org.example.app.task.dataaccess.TaskListEntity}.
   *
   * @param taskListId the {@link org.example.app.task.dataaccess.TaskListEntity#getId() primary key} of the
   *                   {@link org.example.app.task.dataaccess.TaskListEntity} to which the extracted ingredients will be added as tasks.
   * @param recipe     the recipe from which to extract ingredients.
   */
  public void addExtractedIngredients(Long taskListId, String recipe) {
    List<TaskItemEto> ingredients = this.activityService.getExtractedIngredients(recipe);

    List<TaskItemEntity> entities = ingredients.stream()
            .map(eto -> {
                eto.setTaskListId(taskListId);
                return taskItemMapper.toEntity(eto);
            })
            .toList();

    this.taskItemRepository.saveAll(entities);
  }

}
