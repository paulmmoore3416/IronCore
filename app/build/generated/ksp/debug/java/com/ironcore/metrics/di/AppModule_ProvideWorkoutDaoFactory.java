package com.ironcore.metrics.di;

import com.ironcore.metrics.data.local.IronCoreDatabase;
import com.ironcore.metrics.data.local.dao.WorkoutDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideWorkoutDaoFactory implements Factory<WorkoutDao> {
  private final Provider<IronCoreDatabase> databaseProvider;

  public AppModule_ProvideWorkoutDaoFactory(Provider<IronCoreDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public WorkoutDao get() {
    return provideWorkoutDao(databaseProvider.get());
  }

  public static AppModule_ProvideWorkoutDaoFactory create(
      Provider<IronCoreDatabase> databaseProvider) {
    return new AppModule_ProvideWorkoutDaoFactory(databaseProvider);
  }

  public static WorkoutDao provideWorkoutDao(IronCoreDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWorkoutDao(database));
  }
}
