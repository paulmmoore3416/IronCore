package com.ironcore.metrics.ui.workout;

import com.ironcore.metrics.domain.repository.WorkoutRepository;
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
public final class WorkoutViewModel_Factory implements Factory<WorkoutViewModel> {
  private final Provider<WorkoutRepository> repositoryProvider;

  public WorkoutViewModel_Factory(Provider<WorkoutRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public WorkoutViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static WorkoutViewModel_Factory create(Provider<WorkoutRepository> repositoryProvider) {
    return new WorkoutViewModel_Factory(repositoryProvider);
  }

  public static WorkoutViewModel newInstance(WorkoutRepository repository) {
    return new WorkoutViewModel(repository);
  }
}
