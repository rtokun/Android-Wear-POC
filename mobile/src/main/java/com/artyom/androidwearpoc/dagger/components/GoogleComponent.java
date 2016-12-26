package com.artyom.androidwearpoc.dagger.components;

import com.google.android.gms.common.api.GoogleApiClient;

import com.artyom.androidwearpoc.shared.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.wear.connectivity.WearConnectivityService;

import dagger.Component;

/**
 * Created by Artyom on 24/12/2016.
 */
@Component(modules = GoogleApiModule.class)
public interface GoogleComponent {

    GoogleApiClient googleApiClient();

}
