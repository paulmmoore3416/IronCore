package com.ironcore.metrics;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = IronCoreApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface IronCoreApplication_GeneratedInjector {
  void injectIronCoreApplication(IronCoreApplication ironCoreApplication);
}
