package com.ironcore.metrics.ui.nutrition;

import com.ironcore.metrics.data.remote.HomelabApiService;
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
public final class NutritionViewModel_Factory implements Factory<NutritionViewModel> {
  private final Provider<HomelabApiService> apiServiceProvider;

  public NutritionViewModel_Factory(Provider<HomelabApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public NutritionViewModel get() {
    return newInstance(apiServiceProvider.get());
  }

  public static NutritionViewModel_Factory create(Provider<HomelabApiService> apiServiceProvider) {
    return new NutritionViewModel_Factory(apiServiceProvider);
  }

  public static NutritionViewModel newInstance(HomelabApiService apiService) {
    return new NutritionViewModel(apiService);
  }
}
