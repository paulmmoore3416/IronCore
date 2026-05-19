package com.ironcore.metrics.data.repository;

import com.ironcore.metrics.data.local.dao.WorkoutDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class WorkoutRepositoryImpl_Factory implements Factory<WorkoutRepositoryImpl> {
  private final Provider<WorkoutDao> workoutDaoProvider;

  public WorkoutRepositoryImpl_Factory(Provider<WorkoutDao> workoutDaoProvider) {
    this.workoutDaoProvider = workoutDaoProvider;
  }

  @Override
  public WorkoutRepositoryImpl get() {
    return newInstance(workoutDaoProvider.get());
  }

  public static WorkoutRepositoryImpl_Factory create(Provider<WorkoutDao> workoutDaoProvider) {
    return new WorkoutRepositoryImpl_Factory(workoutDaoProvider);
  }

  public static WorkoutRepositoryImpl newInstance(WorkoutDao workoutDao) {
    return new WorkoutRepositoryImpl(workoutDao);
  }
}
