package com.ironcore.metrics.di;

import com.ironcore.metrics.data.remote.HomelabApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideHomelabApiServiceFactory implements Factory<HomelabApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideHomelabApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public HomelabApiService get() {
    return provideHomelabApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideHomelabApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideHomelabApiServiceFactory(retrofitProvider);
  }

  public static HomelabApiService provideHomelabApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideHomelabApiService(retrofit));
  }
}
