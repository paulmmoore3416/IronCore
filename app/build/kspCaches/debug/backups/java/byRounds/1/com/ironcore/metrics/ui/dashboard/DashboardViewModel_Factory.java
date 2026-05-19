package com.ironcore.metrics.ui.dashboard;

import com.ironcore.metrics.data.health.HealthConnectManager;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<HealthConnectManager> healthConnectManagerProvider;

  public DashboardViewModel_Factory(Provider<HealthConnectManager> healthConnectManagerProvider) {
    this.healthConnectManagerProvider = healthConnectManagerProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(healthConnectManagerProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<HealthConnectManager> healthConnectManagerProvider) {
    return new DashboardViewModel_Factory(healthConnectManagerProvider);
  }

  public static DashboardViewModel newInstance(HealthConnectManager healthConnectManager) {
    return new DashboardViewModel(healthConnectManager);
  }
}
